package com.sharshar.scratchdatatools.utils;

import com.sharshar.scratchdatatools.beans.PriceData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Help build out the graph
 *
 * Created by lsharshar on 5/7/2018.
 */
public class GraphUtils {
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

	public static List<TimeGraph> getIntervals(List<PriceData> pd, int interval) {
		List<PriceData> data = pd.stream().sorted(Comparator.comparing(PriceData::getUpdateTime)).collect(Collectors.toList());
		List<TimeGraph> tg = new ArrayList<>();
		List<PriceData> tmpList = new ArrayList<>();
		for (int i=0; i<data.size(); i++) {
			if (i % interval == 0 && i > 0) {
				tg.add(createTimeGraph(tmpList));
				tmpList.clear();
			}
			tmpList.add(data.get(i));
		}
		if (tmpList.size() % interval == 0) {
			tg.add(createTimeGraph(tmpList));
		}
		return tg;
	}

	public static TimeGraph createTimeGraph(List<PriceData> pd) {
		if (pd == null || pd.size() == 0) {
			return null;
		}
		long totalTime = 0;
		double totalPrice = 0;
		for (PriceData p : pd) {
			totalTime += p.getUpdateTime().getTime();
			totalPrice += p.getPrice();
		}
		GraphUtils g = new GraphUtils();
		return g.new TimeGraph(new Date(totalTime/pd.size()), totalPrice/pd.size());
	}
}
