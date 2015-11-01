package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.SortFile;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class A3_DirStatistics {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(A3_DirStatistics.class);

	/**
	 * DirStatistics counts the number of lines, instances and the filesize of all files within one directory
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A3_DirStatistics().countDir("input");
		new A3_DirStatistics().countDir("used_transformed_files");
		new A3_DirStatistics().countDir("used_deduped_files");
		new A3_DirStatistics().countDir("output-matching");
		System.exit(0);
	}

	long lLines = 0;
	long lInstances = 0;
	long lFileSize = 0;
	public void countDir(String sDir) throws Exception{

		new FileWorker().work(Settings.getDir(sDir), new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".nt");
			}
		}, new Worker() {
			@Override
			public void work(File f) throws Exception{
				File fOut = File.createTempFile("suffix", "prefix");
				new SortFile().sort(f, fOut);
				oneFile(fOut);
			}
		});


		new FileWorker().work(Settings.getDir(sDir), new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".gz");
			}
		}, new Worker() {
			@Override
			public void work(File f) throws Exception {
				oneFile(new GZipFile().gunzipIt(f));
			}
		});

		logger.info(sDir);
		logger.info("Lines: "+lLines);
		logger.info("Instances: "+lInstances);
		logger.info("FileSize in Megabyte: "+(lFileSize/1000));
		logger.info("--------");
	}
	private void oneFile(File f) throws Exception{
		lFileSize += f.length();
		new InstanceReader(f.getAbsolutePath()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				lLines += instance.getStatements().size();
				lInstances++;
			}
		});
	}
}
