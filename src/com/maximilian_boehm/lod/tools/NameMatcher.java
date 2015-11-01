package com.maximilian_boehm.lod.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.text.Normalizer;

import org.junit.Test;

public class NameMatcher {

	public String encode(String sInput){
		sInput  = Normalizer.normalize(sInput, Normalizer.Form.NFD);
		sInput = sInput.replaceAll("[^\\p{ASCII}]", "");
		sInput = sInput.replaceAll("\\p{M}", "");
		sInput = sInput.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		List<String> listWords = new ArrayList<>();
		for(String s:sInput.split(" "))
			listWords.add(s.trim());

		Collections.sort(listWords);
		sInput = "";
		for(String s:listWords)
			sInput+=s;

		return sInput.trim();
	}


	@Test
	public void testEncode(){
		encode("ITT Technical Institute-Earth City");
		encode("Technical College");
		encode("Smith & Staggs, LLP");
		encode("Sean M. Houlihan");
		encode("Massachusetts Institute of Technology (MIT)");
	}

}
