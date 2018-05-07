package com.sharshar.scratchdatatools.utils;

/**
 * Reused constants for the application that don't need to be in a configuration file
 *
 * Created by lsharshar on 3/19/2018.
 */
public class ScratchConstants {
	private ScratchConstants() {  /* No need to instantiate this */}

	public static final short BINANCE = 1;
	public static final String[] EXCHANGES = {"", "Binance"};

	// The maximum amount of time this api can be down
	// before the data is stale is 30 second
	public static final long MAX_EXCHANGE_DOWN_TIME = 1000L * 30;

	// The maximum amount of time this service can be down
	// before the data in the database can't be trusted
	public static final long MAX_TIME_BEFORE_RELOAD_REQUIRED =  1000L * 30;
}
