package com.maximilian_boehm.lod.tools.reader.lazy;

import org.junit.Test;

public class TestHierarchy {

	@Test
	public void test() {
		DBpediaHierarchy.printClasses("http://dbpedia.org/ontology/BodyOfWater");
		DBpediaHierarchy.printClasses("http://dbpedia.org/ontology/BodyOfWater");
		DBpediaHierarchy.printClasses("http://dbpedia.org/ontology/BodyOfWater");
		DBpediaHierarchy.printClasses("http://dbpedia.org/ontology/BodyOfWater");
	}

}
