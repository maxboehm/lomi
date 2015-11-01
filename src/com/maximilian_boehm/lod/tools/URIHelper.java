package com.maximilian_boehm.lod.tools;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

public class URIHelper {
	
	public static String getURI(Statement st){
		return getURI(st.getSubject().toString(), st.getContext());
	}
	public static String getURI(String sURI, Resource context){
		if(sURI.contains("-") && (sURI.startsWith("genid") || sURI.startsWith("_:genid")))
			sURI = sURI.substring(sURI.lastIndexOf("-")+1);
		
		return context!=null ? sURI+context : sURI;
	}

}
