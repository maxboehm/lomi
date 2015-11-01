package com.maximilian_boehm.lod.tools.reader.counter;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.maximilian_boehm.lod.tools.reader.statement.StatementReader;


public class StatementCounter extends CounterBase{

	public StatementCounter(String sFile) {
		super(sFile);
	}

	private long countedStatements = 0;

	/**
	 * @return number of quads
	 * @throws Exception
	 */
	public long countStatements() throws Exception {
		new StatementReader().openFile(getFile(), new RDFHandlerBase() {

			@Override
			public void handleStatement(Statement arg0) throws RDFHandlerException {
				countedStatements++;
			}
		});
		return countedStatements;
	}


}
