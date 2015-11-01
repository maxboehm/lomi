package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.transformer.model.MappingCounter;
import com.maximilian_boehm.lod.transformer.model.Transformer;

public class A1_Transformer {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(A3_MeasureResult.class);


	/**
	 * The Transformer takes files from the deduper and transform the schema.org vocabulary
	 * to the dbpedia ontology (As far as it is possible)
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A1_Transformer().run();
		System.exit(0);
	}

	public void run() throws Exception{
		new FileWorker().work(Settings.getOutputDir(), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if(Settings.getOutputFile("t_"+name).exists())
					return false;
				return name.startsWith("d_") || name.endsWith(".gz");
			}
		}, new Worker() {

			@Override
			public void work(File f) throws Exception {
				// Unzip
				File fUnzipped = new GZipFile().gunzipIt(f);

				File fTransformed = new Transformer().transformFile(fUnzipped);
				new GZipFile().compressFile(fTransformed, "t_"+f.getName());
				MappingCounter.outputMappingStatistics();

			}
		});

		logger.info("--------------------");
		logger.info("--------------------");
		logger.info("--------------------");
		MappingCounter.outputMappingStatistics();
	}
}
