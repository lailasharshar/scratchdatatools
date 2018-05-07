package com.sharshar.scratchdatatools.beans;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.security.SecureRandom;
import java.util.Date;

/**
 * Bean for price data
 *
 * Created by lsharshar on 3/6/2018.
 */
@Document(indexName = "pricedata", type="_doc")
public class PriceData {
	@Id
	private Long _id;

	private String ticker;

	@Field(type = FieldType.Float)
	private Double price;

	//@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "EEE MMM dd HH:mm:ss zzz yyyy")
	private Date updateTime;

	@Field(type = FieldType.Long)
	private short exchange;

	public PriceData() {
		long unsignedValue;
		try {
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			int val = prng.nextInt();
			unsignedValue = val;
			if (val < 0) {
				unsignedValue = val & 0xffffffffl;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			int val = (int) (Math.random() * Integer.MAX_VALUE);
			unsignedValue = val;
			if (val < 0) {
				unsignedValue = val & 0xffffffffl;
			}
		}
		_id = unsignedValue;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public short getExchange() {
		return exchange;
	}

	public void setExchange(short exchange) {
		this.exchange = exchange;
	}
}
