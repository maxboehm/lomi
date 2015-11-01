package com.maximilian_boehm.lod.tools.reader.instance;

import org.openrdf.model.Statement;


public interface ExtendedInstanceHandler extends InstanceHandler {

	public boolean skipStatement(Statement st);
}
