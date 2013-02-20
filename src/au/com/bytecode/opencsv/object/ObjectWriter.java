package au.com.bytecode.opencsv.object;

import java.io.Closeable;
import java.io.IOException;


/**
 * @author filippor
 * 
 * @param <T>
 */
public class ObjectWriter<T, W extends Closeable> implements Closeable{
	protected W csv;
	private ProcessorStrategy<T, W, ?> mapper;

	public ObjectWriter(W csv, ProcessorStrategy<T, W, ?> mapper) {
		this.csv = csv;
		this.mapper = mapper;
	}

	public boolean writeHeader() {
		try {
			mapper.processHeader(csv);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Error writing CSV !", e);
		}
	}

	public boolean writeObject(T obj) {
		try {
			mapper.processObject(obj, csv);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Error writing CSV !", e);
		}
	}

	public boolean write(Iterable<T> objects) {
		if (objects == null || !objects.iterator().hasNext())
			return false;

		try {
			writeHeader();
			for (T obj : objects) {
				writeObject(obj);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Error writing CSV !", e);
		}
	}

	public void close() throws IOException {
		csv.close();
	}

}
