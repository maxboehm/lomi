package com.maximilian_boehm.lod.tools;

import java.util.HashMap;
import java.util.Map;

import java.io.File;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.maximilian_boehm.lod.tools.reader.statement.StatementReader;

public class FileSplitter {
	
	int nCounter = 0;
	long l0 = 0;

	@Test
	public void test() throws Exception{
		Settings.initSettings();
		String sInput = "schemaorgPostalAddress.nq_Unzipped.nq";
		File fInput = Settings.getTmpFile(sInput);

		Map<String, NTripleWriter> mapWriter = new HashMap<String, NTripleWriter>();

		for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
			String letter = (""+alphabet).toLowerCase();
			NTripleWriter writer = new NTripleWriter(Settings.getOutputFile(sInput+"_"+letter+".nq"));
			mapWriter.put(letter, writer);
		}
		NTripleWriter writer0 = new NTripleWriter(Settings.getOutputFile(sInput+"_0.nq"));
		mapWriter.put("0", writer0);
		
		l0 = System.currentTimeMillis();

		new StatementReader().openFile(fInput.getAbsolutePath(), new RDFHandlerBase() {

			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				NTripleWriter writer = null;
				
				
				if(st.getContext()!=null && st.getContext().stringValue()!=null){
					String sDomain = st.getContext().stringValue();
					
					sDomain = sDomain.replace("http://www.", "");
					sDomain = sDomain.replace("http://", "");
					
					sDomain = sDomain.substring(0,1).toLowerCase();
					
					writer = mapWriter.get(sDomain);
				}
				
				if(writer==null)
					writer = writer0;

				try {
					writer.writeStatement(st);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				nCounter++;
				
				if(nCounter%1000000==0){
					
					System.out.println("1000000 lines in "+(System.currentTimeMillis()-l0)+"ms, "+nCounter+" of 0 lines");
					
					l0 = System.currentTimeMillis();
				}

			}

		});

		for(NTripleWriter writer:mapWriter.values()){
			writer.close();
			if(writer.getFile().length()==0)
				writer.getFile().delete();
		}

	}

}
