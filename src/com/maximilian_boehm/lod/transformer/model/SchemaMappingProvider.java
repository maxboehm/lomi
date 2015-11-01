package com.maximilian_boehm.lod.transformer.model;

import java.util.Map;

import java.io.File;

import com.maximilian_boehm.lod.tools.PersistentMap;
import com.maximilian_boehm.lod.tools.Settings;

public class SchemaMappingProvider {

	private static File fFileMapClasses = Settings.getMappingFile("map-classes.txt");
	private static File fFileMapProperties = Settings.getMappingFile("map-properties.txt");

	public static Map<String, String> getClassMapping() throws Exception{
		if(!fFileMapClasses.exists())
			throw new Exception("File "+fFileMapClasses.getAbsolutePath()+" does not exist!");
		return PersistentMap.read(fFileMapClasses, true);
	}
	public static Map<String, String> getPropertyMapping() throws Exception{
		if(!fFileMapProperties.exists())
			throw new Exception("File "+fFileMapProperties.getAbsolutePath()+" does not exist!");
		return PersistentMap.read(fFileMapProperties, true);
	}

	public static void writeClassMapping(Map<String, String> map) throws Exception{
		PersistentMap.write(fFileMapClasses, map);
	}
	public static void writePropertyMapping(Map<String, String> map) throws Exception{
		PersistentMap.write(fFileMapProperties, map);
	}

	public static void rewriteAndOrdnerMappingFiles() throws Exception{
		SchemaMappingProvider.writePropertyMapping(SchemaMappingProvider.getPropertyMapping());
		SchemaMappingProvider.writeClassMapping(SchemaMappingProvider.getClassMapping());
	}
}
