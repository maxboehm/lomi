package com.maximilian_boehm.lod.tools;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Hash {

	public static String calculateHash(String s) {
		try {
			MessageDigest m=MessageDigest.getInstance("MD5");
			m.update(s.getBytes(),0,s.length());
			return new BigInteger(1,m.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
