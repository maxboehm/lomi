package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.instance_matcher.model.measure.MatchMeasure;
import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.Settings;

public class A3_MeasureResult {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(A3_MeasureResult.class);

	/**
	 * MeasureResult takes the mapping of the InstanceMatcher and predefined Gold Standards
	 * and calculates Precision and Recall
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A3_MeasureResult().run();
		System.exit(0);
	}

	public void run() throws Exception{

		new FileWorker().work(Settings.getInputDir(), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith("txt") || Settings.getMappingFile("Gold"+name).length()<10;
			}
		}, new Worker() {

			@Override
			public void work(File f) throws Exception {
				Pair<Double,Double> pair = new MatchMeasure().getMeasure(f, Settings.getMappingFile("Gold"+f.getName()));
				logger.info("Precision: "+pair.getLeft());
				logger.info("Recall: "+pair.getRight());

			}
		});
	}
}
