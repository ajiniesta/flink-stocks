package com.iniesta.flink.stocks;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import scala.util.Random;

public class StockTradeSource extends RichSourceFunction<StockTrade> {

	private static final long serialVersionUID = 1794443692481772571L;
	private transient Random random;

	public StockTradeSource() {
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);

	}

	@Override
	public void cancel() {
		System.err.println("Canceling...");
	}

	@Override
	public void run(SourceContext<StockTrade> ctx) throws Exception {
		random = new Random();
		StocksValuesParser stocksValuesParser = new StocksValuesParser("nasdaq");
		while (true) {
			ctx.collect(stocksValuesParser.getStockTrade());
			Thread.sleep(random.nextInt(10));
		}
	}

}
