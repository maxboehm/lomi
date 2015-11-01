package com.maximilian_boehm.lod.deduper.model;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.maximilian_boehm.lod.tools.KeyValueDatabase;
import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;

public class MapHashToURI {

	/**
	 * Stores which URI has which Hash
	 * e.g.
	 * URI -> HASH
	 * 1 -> a
	 * 2 -> b
	 * 3 -> a
	 * Later, 1 and 3 will be merged to one instance and instead the Hash will be used as URI
	 */
	private Map<String, String> kvHash2URI = null;


	/**
	 * @param sName
	 */
	public MapHashToURI(String sName) {
		kvHash2URI =  new KeyValueDatabase<String, String>().getDB("hash2uri-"+FilenameUtils.getBaseName(sName), true);
	}

	/**
	 * Add an instance to the map
	 * @param instance
	 */
	public void addInstance(ExtendedInstance instance){
		String sHash = instance.getHash();
		// Just save the reference in mapMD5ToURI
		addURIbyHash(instance.getURI(), sHash);
	}

	/**
	 * @param sURI
	 * @param sHash
	 */
	private void addURIbyHash(String sURI, String sHash){
		kvHash2URI.put(sURI, sHash);
	}

	/**
	 * @param sURI
	 * @return
	 */
	public String getHashByURI(String sURI){
		return kvHash2URI.get(sURI);
	}

}