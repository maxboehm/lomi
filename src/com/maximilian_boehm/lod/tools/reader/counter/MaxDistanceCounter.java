package com.maximilian_boehm.lod.tools.reader.counter;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.maximilian_boehm.lod.tools.reader.statement.StatementReader;


public class MaxDistanceCounter extends CounterBase{

	public MaxDistanceCounter(String sFile) {
		super(sFile);
	}

	private int nDistance = 0;
	private int nLineCounter = 0;

	/**
	 * Determine the max distance between statements with the same URI
	 * Was used to determine, if it is necessary to sort the files alphabetically
	 * @return
	 * @throws Exception
	 */
	public int countMaxDistance() throws Exception {
		new StatementReader().openFile(getFile(), new RDFHandlerBase() {

			private Map<String, Integer> mapSubject2LastOccurence = new HashMap<String, Integer>();

			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				nLineCounter++;

				String sSubject = st.getSubject().stringValue();

				if(mapSubject2LastOccurence.containsKey(sSubject)){
					int nLastLine = mapSubject2LastOccurence.get(sSubject);
					int nDifference = nLineCounter - nLastLine;

					if(nDifference > nDistance)
						nDistance = nDifference;
				}


				mapSubject2LastOccurence.put(sSubject, nLineCounter);
			}
		});
		return nDistance;
	}


}
