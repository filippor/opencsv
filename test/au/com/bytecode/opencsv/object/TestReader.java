package au.com.bytecode.opencsv.object;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVParserBuilder;
import au.com.bytecode.opencsv.CSVReader;

public class TestReader {
@Test
public void test() throws IOException {
	String s = ",";
	CSVReader reader = new CSVReader(new StringReader(s ),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
	String[] value = reader.readNext();
	reader.close();
	Assert.assertArrayEquals(new String[]{null,null}, value);
}
@Test
public void test1() throws IOException {
	String s = ",1";
	CSVReader reader = new CSVReader(new StringReader(s ),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
	String[] value = reader.readNext();
	reader.close();
	Assert.assertArrayEquals(new String[]{null,"1"}, value);
}
@Test
public void test2() throws IOException {
	String s = ",\"\"";
	CSVReader reader = new CSVReader(new StringReader(s ),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
	String[] value = reader.readNext();
	reader.close();
	Assert.assertArrayEquals(new String[]{null,""}, value);
}
@Test
public void test3() throws IOException {
	String s = "\"\n\",";
	CSVReader reader = new CSVReader(new StringReader(s ),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
	String[] value = reader.readNext();
	reader.close();
	Assert.assertArrayEquals(new String[]{"\n",null}, value);
}
}
