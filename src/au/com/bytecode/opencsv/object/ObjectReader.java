package au.com.bytecode.opencsv.object;

/**
 Copyright 2007 Kyle Miller.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectReader<T,R> implements Iterable<T> {

	R csv;
	private ProcessorStrategy<T,?,R> mapper;

	public ObjectReader(R csv,ProcessorStrategy<T,?,R> mapper) {
		this.mapper = mapper;
		this.csv = csv;
	}

	
	public  List<T> parse() {
		try {
			
			mapper.captureHeader(csv);
			List<T> list = new ArrayList<T>();
			T obj;
			while ((obj = mapper.processLine(csv))!=null) {
				list.add(obj); // TODO: (Kyle) null check object
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing CSV!", e);
		}
	}

	public Iterator<T> iterator() {

		try {
			mapper.captureHeader(csv);
			
			return new Iterator<T>() {
				T next = mapper.processLine(csv);
					
				public boolean hasNext() {
					return next!=null;
				}

				public T next() {
					try {
						T current = next;
						next = mapper.processLine(csv);
						return current;
					} catch (Exception e) {
						throw new RuntimeException("Error parsing CSV!", e);
					}
				}

				public void remove() {
					throw new UnsupportedOperationException("This is a read only iterator.");
				}

			};
		} catch (Exception e) {
			throw new RuntimeException("Error parsing CSV!", e);
		}
	}

	

}
