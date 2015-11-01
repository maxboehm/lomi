package com.maximilian_boehm.lod.tools;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.LoggerFactory;

public class FileWorker {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(FileWorker.class);


	public void work(File f, Worker w) throws Exception{
		work(f, null, w);
	}
	public void work(File fInput, FilenameFilter validator, Worker w) throws Exception{

		if(!fInput.exists())
			throw new Exception("File "+fInput.getAbsolutePath()+" does not exist");

		if(fInput.isFile()){
			logger.info("Work on "+fInput.getName());
			w.work(fInput);
			return;
		}

		logger.info("Working Directory: "+fInput.getAbsolutePath());

		if(validator==null) validator = new SimpleFileNameValidator();
		int nFiles = count(fInput.listFiles(validator));

		int nCounter = 0;

		for(File f:fInput.listFiles(validator))
			if(f.isFile() && !f.getName().equals("Thumbs.db") && !f.getName().equals(".DS_Store")){
				nCounter++;
				logger.info(nCounter+"/"+nFiles+": Work on "+f.getName());
				w.work(f);
			}
	}

	public interface Worker{
		public void work(File f) throws Exception;
	}

	class SimpleFileNameValidator implements FilenameFilter{

		@Override
		public boolean accept(File dir, String name) {
			return true;
		}

	}

	private int count(File[] fFiles){
		int nFiles = 0;
		for(File f:fFiles)
			if(f.isFile() && !f.getName().equals("Thumbs.db") && !f.getName().equals(".DS_Store"))
				nFiles++;

		return nFiles;
	}

}
