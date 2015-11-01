package com.maximilian_boehm.lod.tools.reader.counter;

import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class InstanceCounter extends CounterBase {

	public InstanceCounter(String sFile) {
		super(sFile);
	}

	long nCounter = 0;
	long nCounterWTD = 0;
	long nCounterWOTD = 0;

	public long countInstances() throws Exception{
		nCounter = 0;
		new InstanceReader(getFile()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				nCounter++;
			}
		});
		return nCounter;
	}

	public long countInstancesWithTypeDeclaration() throws Exception{
		nCounterWTD = 0;
		new InstanceReader(getFile()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {

				if(instance.hasTypeDeclaration())
					nCounterWTD++;
			}
		});
		return nCounterWTD;
	}

	public long countInstancesWithoutTypeDeclaration() throws Exception{
		nCounterWOTD = 0;
		new InstanceReader(getFile()).readInstances(false, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				if(!instance.hasTypeDeclaration())
					nCounterWOTD++;
			}
		});
		return nCounterWOTD;
	}

}
