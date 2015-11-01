package com.maximilian_boehm.lod.main;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

import com.maximilian_boehm.lod.deduper.model.Deduper;
import com.maximilian_boehm.lod.tools.FileWorker;
import com.maximilian_boehm.lod.tools.FileWorker.Worker;
import com.maximilian_boehm.lod.tools.GZipFile;
import com.maximilian_boehm.lod.tools.Settings;

public class A0_Deduper {

	/**
	 * Take the files from the input-directory (Defined in settings.java) which are starting with a "d_"
	 * and dedupe them! The files will be automatically unzipped
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Settings.initSettings();
		new A0_Deduper().run();
		System.exit(0);
	}

	public void run() throws Exception{
		new FileWorker().work(Settings.getInputDir(), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !Settings.getOutputFile("d_"+name).exists();
			}
		}, new Worker() {

			@Override
			public void work(File f) throws Exception {
				File fUnzipped = Settings.getTmpFile(FilenameUtils.getBaseName(f.getName())+"_Unzipped.nq");
				if(!fUnzipped.exists())
					fUnzipped = new GZipFile().gunzipIt(f);

				// Remove duplicates
				File fDeduped = new Deduper().dedupeFile(fUnzipped,false, false);

				new GZipFile().compressFile(fDeduped, "d_"+f.getName());
			}
		});

	}
}
