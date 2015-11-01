package com.maximilian_boehm.lod.main;

import java.io.File;

import org.junit.Test;

import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.Settings;
import com.maximilian_boehm.lod.tools.TestBase;

public class DirectoryCompressor extends TestBase {

	/**
	 * Compress each file in a directory
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		Settings.initSettings();

		new FileWorker().work(Settings.getDir("tmp"), new Worker() {
			@Override
			public void work(File f) {
				new GZipFile().compressFile(f, f.getName()+".gz");
			}
		});
	}


}
