package com.sharshar;

import com.sharshar.scratchdatatools.beans.PriceData;
import com.sharshar.scratchdatatools.utils.GraphUtils;
import com.sharshar.scratchdatatools.utils.ScratchConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by lsharshar on 5/7/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {
	@Test
	public void testCreateTimeGraph() {
		int interval = 1000 * 60 * 60 * 24;
		Date d1 = new Date();
		Date d2 = new Date(d1.getTime() + interval);
		Date d3 = new Date(d2.getTime() + interval);
		PriceData p1 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(5.0).setTicker("ETHBTC").setUpdateTime(d1);
		PriceData p2 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(20.0).setTicker("ETHBTC").setUpdateTime(d2);
		PriceData p3 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(35.0).setTicker("ETHBTC").setUpdateTime(d3);
		GraphUtils.TimeGraph tg = GraphUtils.createTimeGraph(Arrays.asList(new PriceData[]{p1, p2, p3}));
		assertEquals(20.0, tg.getY(), 0.00001);
		assertEquals(d2.getTime(), tg.getT().getTime());
		GraphUtils.TimeGraph tg2 = GraphUtils.createTimeGraph(null);
		assertNull(tg2);
	}

	@Test
	public void testGetIntervals() {
		int interval = 1000 * 60 * 60 * 24;
		Date d1 = new Date();
		Date d2 = new Date(d1.getTime() + interval);
		Date d3 = new Date(d2.getTime() + interval);
		Date d4 = new Date(d3.getTime() + interval);
		Date d5 = new Date(d4.getTime() + interval);
		Date d6 = new Date(d5.getTime() + interval);
		Date d7 = new Date(d6.getTime() + interval);
		PriceData p1 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(5.0).setTicker("ETHBTC").setUpdateTime(d1);
		PriceData p2 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(20.0).setTicker("ETHBTC").setUpdateTime(d2);
		PriceData p3 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(35.0).setTicker("ETHBTC").setUpdateTime(d3);
		PriceData p4 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(15.0).setTicker("ETHBTC").setUpdateTime(d4);
		PriceData p5 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(22.0).setTicker("ETHBTC").setUpdateTime(d5);
		PriceData p6 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(38.0).setTicker("ETHBTC").setUpdateTime(d6);
		PriceData p7 = new PriceData().setExchange(ScratchConstants.BINANCE).setPrice(40.0).setTicker("ETHBTC").setUpdateTime(d7);
		List<PriceData> list = Arrays.asList(new PriceData[]{p3, p4, p1, p7, p6, p2, p5});
		List<GraphUtils.TimeGraph> sumData = GraphUtils.getIntervals(list, 2);
		assertEquals(3, sumData.size());
		GraphUtils.TimeGraph t1 = sumData.get(0);
		GraphUtils.TimeGraph t2 = sumData.get(1);
		GraphUtils.TimeGraph t3 = sumData.get(2);
		assertEquals(d1.getTime() + (interval/2), t1.getT().getTime());
		assertEquals(d3.getTime() + (interval/2), t2.getT().getTime());
		assertEquals(d5.getTime() + (interval/2), t3.getT().getTime());
		assertEquals(12.5, t1.getY(), 0.00001);
		assertEquals(25.0, t2.getY(), 0.00001);
		assertEquals(30.0, t3.getY(), 0.00001);
	}
}
