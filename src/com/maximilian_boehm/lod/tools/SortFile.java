package com.maximilian_boehm.lod.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.std.TextFileSorter;

public class SortFile {

	public void sort(File fIn, File fOut)throws Exception{
		// 2GB
		try(TextFileSorter sorter = new TextFileSorter(new SortConfig().withMaxMemoryUsage(256 * 1024 * 1024));) {
			sorter.sort(new FileInputStream(fIn), new FileOutputStream(fOut));
		} 
	}

}
