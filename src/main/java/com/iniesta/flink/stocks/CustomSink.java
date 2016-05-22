package com.iniesta.flink.stocks;

import java.io.File;
import java.io.FileWriter;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class CustomSink extends RichSinkFunction<Tuple2<String, Double>> {

	private static final long serialVersionUID = -917705023394144419L;
	private FileWriter fileWriter;

	@Override
	public void close() throws Exception {
		super.close();
		fileWriter.flush();
		fileWriter.close();
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
		fileWriter = new FileWriter(new File("output."+System.currentTimeMillis()+".txt"), true);
	}

	@Override
	public void invoke(Tuple2<String, Double> tuple) throws Exception {
		fileWriter.write(tuple.toString()+"\n");
		fileWriter.flush();
	}

}
