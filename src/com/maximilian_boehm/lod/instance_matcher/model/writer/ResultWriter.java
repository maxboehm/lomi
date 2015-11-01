package com.maximilian_boehm.lod.instance_matcher.model.writer;

import java.io.File;

import com.maximilian_boehm.lod.instance_matcher.model.Result;

/**
 * Interface for different output possibilities
 */
public interface ResultWriter {
	public void write(File f, Result result) throws Exception;
}