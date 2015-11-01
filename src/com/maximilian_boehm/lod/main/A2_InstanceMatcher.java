package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import com.maximilian_boehm.lod.instance_matcher.model.InstanceMatcher;
import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.Settings;

public class A2_InstanceMatcher {

	private InstanceMatcher mapper = null;

	/**
	 * The instance matcher takes the files from the transformer and matches
	 * them with the current dbpedia instances
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A2_InstanceMatcher().run();
		System.exit(0);
	}

	public void run() throws Exception{
		mapper = new InstanceMatcher();

		new FileWorker().work(Settings.getOutputDir(), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("t_");
			}
		}, new Worker() {

			@Override
			public void work(File f) throws Exception {
				// Unzip
				File fUnzipped = new GZipFile().gunzipIt(f);
				mapper.mapInstances(fUnzipped);
			}
		});
	}
}
