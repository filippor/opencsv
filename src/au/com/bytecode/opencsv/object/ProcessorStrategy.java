package au.com.bytecode.opencsv.object;

import java.io.IOException;

public interface ProcessorStrategy<T,W,R> {

	
	/**
	 * read and consume header information from CSVReader
	 * @param reader
	 * @throws IOException
	 */
	public  boolean captureHeader(R reader) throws Exception;

	
	public boolean processHeader(W writer) throws Exception;


	public boolean processObject(T bean,W writer)
			throws Exception;

	public  T processLine(R reader)
			throws Exception;


}