package com.maximilian_boehm.lod.tools.reader.instance;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.slf4j.LoggerFactory;

import com.maximilian_boehm.lod.tools.URIHelper;
import com.maximilian_boehm.lod.tools.reader.statement.StatementReader;

public class InstanceReader {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(InstanceReader.class);
	private String sFile = null;


	public InstanceReader(String sFile){
		this.sFile = sFile;
	}

	public void readInstances(boolean bIgnoreInstancesWithoutType, InstanceHandler ir) throws Exception{

		new StatementReader().openFile(sFile, new RDFHandlerBase(){

			private ExtendedInstance 	instance 	 = new ExtendedInstance();
			private long 		lTimeComplete		 	 = System.currentTimeMillis();
			private long 		lTimeWork		 	 = 0;
			private long 		lLineCounter = 0;

			@Override
			public void endRDF() throws RDFHandlerException {
				// also save last instance
				if(!bIgnoreInstancesWithoutType || instance.hasTypeDeclaration())
					ir.handleInstance(instance, lLineCounter);
				super.endRDF();
			}

			/* (non-Javadoc)
			 * @see org.openrdf.rio.helpers.RDFHandlerBase#handleStatement(org.openrdf.model.Statement)
			 * handleStatement iterates over the file line-wise
			 * each call is ONE statement
			 */
			@Override
			public void handleStatement(Statement st) {
				if(ir instanceof ExtendedInstanceHandler){
					if(((ExtendedInstanceHandler) ir).skipStatement(st))
						return;
				}
				// Output notifier each 1 000 000 lines
				lLineCounter ++;
				if(lLineCounter%1000000==0){
					lTimeComplete = System.currentTimeMillis() - lTimeComplete;
					String sLinesTotal = String.format("%,d", lLineCounter);
					logger.info(sLinesTotal+": 1.000.000 lines in "+lTimeComplete+"ms (TOTAL), "+lTimeWork+"ms (WORK),"+(lTimeComplete - lTimeWork)+"ms (READ)");
					lTimeComplete = System.currentTimeMillis();
					lTimeWork = 0;
				}

				// Determine the URI of the instance
				String sURI = URIHelper.getURI(st);

				// If the URI does not match the old one, start a new instance
				// OR, beginning (instance is null)
				if(instance.hasStatement() && instance.getURI()!=null && !instance.getURI().equals(sURI)){

					// Save instance in store
					if(!bIgnoreInstancesWithoutType || instance.hasTypeDeclaration()){
						long l3 = System.currentTimeMillis();
						ir.handleInstance(instance, lLineCounter);
						lTimeWork += System.currentTimeMillis() - l3;
					}

					// Create new instance
					instance = new ExtendedInstance();
				}
				// Set the URI to the instance
				instance.setURI(st);

				// Okay, here we go. Add the statement to the instance
				instance.addStatement(st);
			}
		});
	}

}
