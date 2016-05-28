package com.iniesta.flink.stocks;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class StockStreaming {

	@SuppressWarnings("serial")
	private static final class FilterSymbol implements FilterFunction<Tuple2<String, Double>> {
		private String symbol;

		public FilterSymbol(String symbol) {
			this.symbol = symbol;
		}

		@Override
		public boolean filter(Tuple2<String, Double> input) throws Exception {
			return symbol.equals(input.f0);
		}
	}

	@SuppressWarnings("serial")
	private static final class CountTrades
			implements WindowFunction<Tuple2<String, Double>, Integer, Tuple, TimeWindow> {
		@Override
		public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple2<String, Double>> it,
				Collector<Integer> collector) throws Exception {
			int count = 0;
			for (Tuple2<String, Double> t : it) {
				count++;
			}
			collector.collect(count);
		}
	}

	@SuppressWarnings("serial")
	private static final class MapStockToTuple implements MapFunction<StockTrade, Tuple2<String, Double>> {
		@Override
		public Tuple2<String, Double> map(StockTrade trade) throws Exception {
			return new Tuple2<>(trade.getSymbol(), trade.getPrice());
		}
	}

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<StockTrade> stocks = env.addSource(new StockTradeSource());

		stocks.
			map(new MapStockToTuple()).
			keyBy(0).
			timeWindow(Time.seconds(30)).
			sum(1).
			print();
//			addSink(new CustomSink());
		
//		DataStream<Tuple2<String, Double>> sumtocks = window.sum(1);
//		sumtocks.print();
//
//		window.apply(new CountTrades()).print();

//		stocks.map(new MapStockToTuple()).filter(new FilterSymbol("GOOGL")).keyBy(0).timeWindow(Time.seconds(30)).sum(1).addSink(new CustomSink());
		
		env.execute("Stock exchange trade");
	}
}
