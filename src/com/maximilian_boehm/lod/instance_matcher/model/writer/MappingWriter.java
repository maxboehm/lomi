package com.maximilian_boehm.lod.instance_matcher.model.writer;

import java.io.File;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.maximilian_boehm.lod.instance_matcher.model.Match;
import com.maximilian_boehm.lod.instance_matcher.model.Result;
import com.maximilian_boehm.lod.tools.PersistentMap;

public class MappingWriter implements ResultWriter {

	@Override
	public void write(File f, Result result) throws Exception {
		// Create a new MULTIMAP
		Multimap<String, String> map = ArrayListMultimap.create();
		// Iterate over results
		for(Match m:result.getMatches())
			// Put both instances into multimap
			map.put(m.getDBPInstance().getURI(), m.getSCOInstance().getURI());

		// write multimap into a file
		PersistentMap.writeMultiMap(f, map);
	}

}
