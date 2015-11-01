package com.maximilian_boehm.lod.deduper.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.maximilian_boehm.lod.deduper.model.Deduper;
import com.maximilian_boehm.lod.tools.TestBase;
import com.maximilian_boehm.lod.tools.reader.counter.InstanceCounter;

public class TestDedupe extends TestBase{


	@Test
	public void testReferences()  throws Exception{
		File fTestFile = getFile("Deduper_References.nq", this.getClass());
		File fDedupedFile = dedupeFile(fTestFile);
		testTripleFile(fTestFile, 4);
		testTripleFile(fDedupedFile, 2);
	}

	@Test
	public void testDuplicateOrdering()  throws Exception{
		File fTestFile = getFile("Deduper_Duplicate_Ordering.nq", this.getClass());
		File fDedupedFile = dedupeFile(fTestFile);
		testTripleFile(fTestFile, 4);
		testTripleFile(fDedupedFile, 2);
	}

	@Test
	public void testDuplicate()  throws Exception{
		File fTestFile = getFile("Deduper_Duplicate.nq", this.getClass());
		File fDedupedFile = dedupeFile(fTestFile);
		testTripleFile(fTestFile, 3);
		testTripleFile(fDedupedFile, 2);
	}



	// ##########
	// HELPER
	// ##########
	/**
	 * @param fTestFile
	 * @return
	 * @throws Exception
	 */
	private File dedupeFile(File fTestFile) throws Exception{
		File fDedupedFile = new Deduper().dedupeFile(fTestFile, true, false);
		addFile2Delete(fDedupedFile);
		return fDedupedFile;
	}


	/**
	 * @param f
	 * @param nInstances
	 * @throws Exception
	 */
	private void testTripleFile(File f, int nInstances)  throws Exception{
		Assert.assertEquals(f.exists(), true);

		InstanceCounter counterOriginal = new InstanceCounter(f.getAbsolutePath());
		Assert.assertEquals(nInstances, counterOriginal.countInstances());
		Assert.assertEquals(nInstances, counterOriginal.countInstancesWithTypeDeclaration());
		// Instances without type will be ignored
		Assert.assertEquals(0, counterOriginal.countInstancesWithoutTypeDeclaration());
	}

}
