package com.maximilian_boehm.lod.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.maximilian_boehm.lod.deduper.test.TestDedupe;
import com.maximilian_boehm.lod.deduper.test.TestReferences;
import com.maximilian_boehm.lod.tools.reader.test.TestInstanceType;
import com.maximilian_boehm.lod.tools.test.TestCountMapping;
import com.maximilian_boehm.lod.transformer.model.test.TestMapper;
import com.maximilian_boehm.lod.transformer.test.TestTransformer;



/**
 * The test suite. Execute it to ensure the correctness of your system
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestDedupe.class,
	TestReferences.class,
	TestTransformer.class,
	TestInstanceType.class,
	TestCountMapping.class,
	TestMapper.class
})public class LomiTestSuite {

}
