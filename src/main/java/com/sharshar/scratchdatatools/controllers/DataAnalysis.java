package com.sharshar.scratchdatatools.controllers;

import com.sharshar.scratchdatatools.beans.PriceData;
import com.sharshar.scratchdatatools.repository.PriceDataES;
import com.sharshar.scratchdatatools.utils.GenUtils;
import com.sharshar.scratchdatatools.utils.GraphUtils;
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
	public List<GraphUtils.TimeGraph> getPriceDataForGraph(@PathVariable String ticker, @RequestParam String startDate,
				@RequestParam String endDate, @PathVariable short exchange, @RequestParam int interval) throws Exception {
		Date startDateVal = GenUtils.parseDate(startDate, sdf);
		Date endDateVal = GenUtils.parseDate(endDate, sdf);
		if (startDateVal == null || endDateVal == null) {
			return new ArrayList<>();
		}
		List<PriceData> data = priceDataEs.findByTimeRange(ticker, startDateVal, endDateVal, exchange);
		if (data != null) {
			return GraphUtils.getIntervals(data, interval);
		}
		return new ArrayList<>();
	}

	/**
	 * Get the ratios of GAS to NEO
	 *
	 * @param startDate - date to start
	 * @param endDate - date to end
	 * @param exchange - the exchange to do it for
	 * @param interval - the sample rate (it combines the interval number of items into 2 by averaging values)
	 * @return the graph data
	 * @throws Exception if there is an issue getting the data
	 */
	@CrossOrigin
	@GetMapping("/data/history/{exchange}/neoratio/graph")
	public List<GraphUtils.TimeGraph> getPriceDataForGraph(@RequestParam String startDate,
				@RequestParam String endDate, @PathVariable short exchange, @RequestParam int interval) throws Exception {

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
		List<GraphUtils.TimeGraph> ratios = new ArrayList<>();
		GraphUtils g = new GraphUtils();
		List<GraphUtils.TimeGraph> neoGraphs = GraphUtils.getIntervals(neoPrices, interval);
		List<GraphUtils.TimeGraph> gasGraphs = GraphUtils.getIntervals(gasPrices, interval);
		for (int i=0; i<neoGraphs.size(); i++) {
			if (i < gasGraphs.size()) {
				double pricesNeo = neoGraphs.get(i).getY();
				double pricesGas = gasGraphs.get(i).getY();
				ratios.add(g.new TimeGraph(neoGraphs.get(i).getT(), pricesGas/pricesNeo));
			}
		}
		return ratios;
	}
}
