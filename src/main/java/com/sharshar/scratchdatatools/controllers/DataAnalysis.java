package com.sharshar.scratchdatatools.controllers;

import com.sharshar.scratchdatatools.beans.PriceData;
import com.sharshar.scratchdatatools.repository.PriceDataES;
import com.sharshar.scratchdatatools.utils.GenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to return data that can be analyzed. This is data from the database
 *
 * Created by lsharshar on 3/16/2018.
 */
@RestController
public class DataAnalysis {

	public class TimeGraph {
		private Date t;
		private double y;

		public TimeGraph(Date t, double y) {
			this.t = t;
			this.y = y;
		}

		public Date getT() {
			return t;
		}

		public void setT(Date t) {
			this.t = t;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}
	}

	public class PriceDataLight {
		private String ticker;
		private double price;
		private Date updateDate;

		private PriceDataLight(String ticker, double price, Date updateDate) {
			this.ticker = ticker;
			this.price = price;
			this.updateDate = updateDate;
		}

		public String getTicker() {
			return ticker;
		}

		public double getPrice() {
			return price;
		}

		public Date getUpdateDate() {
			return updateDate;
		}

	}

	@Resource
	private PriceDataES priceDataEs;

	private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

	@GetMapping("/data/history/{ticker}/first")
	public List<PriceDataLight> getPriceData(@PathVariable String ticker, short exchange) {
		List<PriceData> data = priceDataEs.findByTicker(ticker, exchange);
		if (data != null) {
			return data.stream().map(d -> new PriceDataLight(d.getTicker(), d.getPrice(), d.getUpdateTime()))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@GetMapping("/data/history/{ticker}/{exchange}/firstText")
	public String getPriceDataText(@PathVariable String ticker, @PathVariable short exchange) {
		List<PriceData> data = priceDataEs.findByTicker(ticker, exchange);
		StringBuilder result = new StringBuilder();
		result.append(ticker).append("\n\n");
		if (data != null) {
			for (PriceData pd : data) {
				result.append("   ").append(sdf2.format(pd.getUpdateTime()))
						.append(": ").append(String.format("%2.10f", pd.getPrice())).append("\n");
			}
			return result.toString();
		}
		return null;
	}

	@CrossOrigin
	@GetMapping("/data/history/{ticker}/{exchange}")
	public List<PriceDataLight> getPriceData(@PathVariable String ticker, @RequestParam String startDate,
											 @RequestParam String endDate, @PathVariable short exchange) throws Exception {
		Date startDateVal = GenUtils.parseDate(startDate, sdf);
		Date endDateVal = GenUtils.parseDate(endDate, sdf);
		if (startDateVal == null || endDateVal == null) {
			return new ArrayList<>();
		}
		List<PriceData> data = priceDataEs.findByTimeRange(ticker, startDateVal, endDateVal, exchange);
		if (data != null) {
			data = data.stream().sorted(Comparator.comparing(PriceData::getUpdateTime)).collect(Collectors.toList());
			return data.stream().map(d -> new PriceDataLight(d.getTicker(), d.getPrice(), d.getUpdateTime()))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@CrossOrigin
	@GetMapping("/data/history/{ticker}/{exchange}/graph")
	public List<TimeGraph> getPriceDataForGraph(@PathVariable String ticker, @RequestParam String startDate,
												@RequestParam String endDate, @PathVariable short exchange,
												@RequestParam int interval) throws Exception {
		Date startDateVal = GenUtils.parseDate(startDate, sdf);
		Date endDateVal = GenUtils.parseDate(endDate, sdf);
		if (startDateVal == null || endDateVal == null) {
			return new ArrayList<>();
		}
		List<PriceData> data = priceDataEs.findByTimeRange(ticker, startDateVal, endDateVal, exchange);
		if (data != null) {
		    return getSummaryTGList(data, interval);
		}
		return new ArrayList<>();
	}

	private List<TimeGraph> getSummaryTGList(List<PriceData> pd, int interval) {
		List<PriceData> subList = new ArrayList<>();
		List<TimeGraph> tgList = new ArrayList<>();
		List<PriceData> data = pd.stream().sorted(Comparator.comparing(PriceData::getUpdateTime)).collect(Collectors.toList());
		for (int i=0; i<data.size(); i++) {
			if (i % interval == 0 && i > 0) {
				TimeGraph tg = getSummaryTG(subList);
				if (tg != null) {
					tgList.add(tg);
					subList.clear();
				}
			}
			subList.add(data.get(i));
		}
		return tgList;
	}

	private TimeGraph getSummaryTG(List<PriceData> pd) {
		if (pd == null || pd.size() == 0) {
			return null;
		}
		long totalTime = 0;
		double totalPrice = 0;
		for (PriceData p : pd) {
			totalTime += p.getUpdateTime().getTime();
			totalPrice += p.getPrice();
		}
		return new TimeGraph(new Date(totalTime/pd.size()), totalPrice/pd.size());
	}

	@CrossOrigin
	@GetMapping("/data/history/{exchange}/neoration/graph")
	public List<TimeGraph> getPriceDataForGraph(@RequestParam String startDate,
												@RequestParam String endDate, @PathVariable short exchange) throws Exception {

		Date startDateVal = GenUtils.parseDate(startDate, sdf);
		Date endDateVal = GenUtils.parseDate(endDate, sdf);
		if (startDateVal == null || endDateVal == null) {
			return new ArrayList<>();
		}
		List<PriceData> neoPrices = priceDataEs.findByTimeRange("NEOBTC", startDateVal, endDateVal, exchange);
		List<PriceData> gasPrices = priceDataEs.findByTimeRange("GASBTC", startDateVal, endDateVal, exchange);
		if (neoPrices == null || gasPrices == null) {
			return new ArrayList<>();
		}
		if (neoPrices.size() != gasPrices.size()) {
			System.out.println("Mismatched data");
		}
		neoPrices = neoPrices.stream().sorted(Comparator.comparing(PriceData::getUpdateTime)).collect(Collectors.toList());
		gasPrices = gasPrices.stream().sorted(Comparator.comparing(PriceData::getUpdateTime)).collect(Collectors.toList());
		List<TimeGraph> ratios = new ArrayList<>();
		for (int i=0; i<neoPrices.size(); i++) {
			double priceNeo = neoPrices.get(i).getPrice();
			if (i < gasPrices.size()) {
				double priceGas = gasPrices.get(i).getPrice();
				ratios.add(new TimeGraph(neoPrices.get(i).getUpdateTime(), priceGas / priceNeo));
			}
		}
		return ratios;
	}
}
