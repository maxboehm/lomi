package com.maximilian_boehm.lod.tools;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.std.TextFileSorter;

public class GZipFile {

	/**
	 * GunZip it
	 */
	public File gunzipIt(File fInput) {
		return gunzipIt(fInput, Settings.getTmpFile(FilenameUtils.getBaseName(fInput.getName())+"_Unzipped.nq"));
	}

	public File gunzipIt(File fInput, File fOutput) {

		try {
			// 2 GB
			TextFileSorter sorter = new TextFileSorter(
					new SortConfig()
					.withMaxMemoryUsage(5 * 1000 * 1000 * 100)
					//						.withMaxMemoryUsage(20 * 1000 * 1000 * 100)
					);
			sorter.sort(new GZIPInputStream(new FileInputStream(fInput)), new FileOutputStream(fOutput));
			sorter.close();

		} catch (IOException ex) {ex.printStackTrace();}

		return fOutput;
	}

	public File compressFile(File fInput, String sName){
		File fOutput = Settings.getOutputFile(sName);

		byte[] buffer = new byte[2048];

		try{

			GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(fOutput));

			FileInputStream in = new FileInputStream(fInput);

			int len;
			while ((len = in.read(buffer)) > 0) {
				gzos.write(buffer, 0, len);
			}

			in.close();

			gzos.finish();
			gzos.close();

		}catch(IOException ex){
			ex.printStackTrace();
		}
		return fOutput;
	}
}