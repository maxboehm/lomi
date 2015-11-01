package com.maximilian_boehm.lod.tools.reader.instance;

import java.util.Set;

import org.openrdf.model.Statement;

public interface Instance {

	public String getURI();
	public Set<String> getNames();
	public Set<String> getTypes();
	public Set<String> getExtendedTypeHierarchy();
	public Set<Statement> getStatements();
	public boolean isOfType(String sURIType);


}
