package com.maximilian_boehm.lod.transformer.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.code.externalsorting.ExternalSort;
import com.maximilian_boehm.lod.tools.TestBase;
import com.maximilian_boehm.lod.transformer.model.Transformer;

public class TestTransformer extends TestBase{


	@Test
	public void testRecursion()  throws Exception{	 testAndCompare("Transformer_Recursion");}
	
	@Test
	public void testSimple()  throws Exception{		 testAndCompare("Transformer_Simple");}
	
	@Test
	public void testTransformer()  throws Exception{ testAndCompare("Transformer_References_0");}
	
	@Test
	public void testTransformer1()  throws Exception{testAndCompare("Transformer_References_1");}
	
	@Test
	public void testTransformer2()  throws Exception{testAndCompare("Transformer_References_2");}
	
    
    
    
    // ##########
    // HELPER
    // ##########
    
	private File transformFile(File fTestFile) throws Exception{
		File fTransformedFile = new Transformer().transformFile(fTestFile);
		addFile2Delete(fTransformedFile);
		return fTransformedFile;
	}
	
	public void testAndCompare(String sFilename) throws Exception{
		File fTestFile = getFile(sFilename+".nq", this.getClass());
		File fOutput = transformFile(fTestFile);
		File fResult = getFile(sFilename+"_RESULT"+".nq", this.getClass());
		
		ExternalSort.sort(fOutput, fOutput);
		
		assertEquals(true, FileUtils.contentEquals(fResult, fOutput));
		
	}
	
}
