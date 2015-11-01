package com.maximilian_boehm.lod.tools.reader.counter;

import java.util.HashMap;

import com.maximilian_boehm.lod.tools.reader.instance.ExtendedInstance;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceHandler;
import com.maximilian_boehm.lod.tools.reader.instance.InstanceReader;

public class TypeCounter extends CounterBase{

	public TypeCounter(String sFile) {
		super(sFile);
	}

	/**
	 * Detects which types are used in a file
	 * and how often they are used
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Long> countTypes() throws Exception {

		HashMap<String, Long> mapTypes = new HashMap<String, Long>();

		new InstanceReader(getFile()).readInstances(true, new InstanceHandler() {

			@Override
			public void handleInstance(ExtendedInstance instance, long lLineCounter) {
				for(String sType:instance.getTypes()){
					if(!mapTypes.containsKey(sType))
						mapTypes.put(sType, 1L);
					else
						mapTypes.put(sType, mapTypes.get(sType)+1);

				}
				;
			}
		});

		return mapTypes;
	}

}
