package au.com.bytecode.opencsv.object;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVParserBuilder;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvBeanProcessorStrategyTest {
	@Test
	public void testWriteRead() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class);
		
		File file = testStrategy(strategy);
		BufferedReader r = new BufferedReader(new FileReader(file));
		Assert.assertEquals("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"",
				r.readLine());
		Assert.assertEquals(",,,", r.readLine());
		Assert.assertEquals("\" \",\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals(",\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals("\"as\",\"null\",,", r.readLine());
		Assert.assertEquals("\"a\",\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
	}

	@Test
	public void testWriteReadWithClass() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class, null, true, true, false);

		File file = testStrategy(strategy);
		BufferedReader r = new BufferedReader(new FileReader(file));

		Assert.assertEquals(
				"\"CLASS\",\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"",
				r.readLine());
		Assert.assertEquals("\"au.com.bytecode.opencsv.object.MockBean\",,,,",
				r.readLine());
		Assert.assertEquals(
				"\"au.com.bytecode.opencsv.object.MockBean\",\" \",\"\",\"1\",\"\"",
				r.readLine());
		Assert.assertEquals(
				"\"au.com.bytecode.opencsv.object.MockBean\",,\"prov",
				r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals(
				"\"au.com.bytecode.opencsv.object.MockBean\",\"as\",\"null\",,",
				r.readLine());
		Assert.assertEquals(
				"\"au.com.bytecode.opencsv.object.MockBeanChild\",\"a\",\"ciao\",\"0\",\"me\"",
				r.readLine());

		r.close();
		CSVReader reader = new CSVReader(new FileReader(file),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		Assert.assertEquals(MockBeanChild.class, objectReader.parse().get(4)
				.getClass());
		reader.close();
	}

	@Test
	public void testWriteReadNoHeader() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class, false);

		File file = testStrategy(strategy);
		BufferedReader r = new BufferedReader(new FileReader(file));
		Assert.assertEquals(",,,", r.readLine());
		Assert.assertEquals("\" \",\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals(",\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals("\"as\",\"null\",,", r.readLine());
		Assert.assertEquals("\"a\",\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
	}

	@Test
	public void testWriteReadColums() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class);

		strategy.setColumnMapping("nAmE", "num", "OrDerNumber");

		File f = File.createTempFile("test", ".csv");
		CSVWriter writer = new CSVWriter(new FileWriter(f));
		ObjectWriter<MockBean, CSVWriter> objectWriter = new ObjectWriter<MockBean, CSVWriter>(
				writer, strategy);

		ArrayList<MockBean> list = createObjects();

		objectWriter.write(list);
		writer.close();
		print(f);

		BufferedReader r = new BufferedReader(new FileReader(f));
		Assert.assertEquals("\"nAmE\",\"num\",\"OrDerNumber\"", r.readLine());
		Assert.assertEquals(",,", r.readLine());
		Assert.assertEquals("\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals("\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals("\"null\",,", r.readLine());
		Assert.assertEquals("\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
		CSVReader reader = new CSVReader(new FileReader(f),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		List<MockBean> parse = objectReader.parse();
		for (MockBean mockBean : list) {
			mockBean.setId(null);
		}
		Assert.assertArrayEquals(list.toArray(), parse.toArray());
	}

	@Test
	public void testWriteReadColumsNoHeader() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class);

		strategy.setColumnMapping("nAmE", "num", "OrDerNumber");
		strategy.setWithHeader(false);
		File f = File.createTempFile("test", ".csv");
		CSVWriter writer = new CSVWriter(new FileWriter(f));
		ObjectWriter<MockBean, CSVWriter> objectWriter = new ObjectWriter<MockBean, CSVWriter>(
				writer, strategy);

		ArrayList<MockBean> list = createObjects();

		objectWriter.write(list);
		writer.close();
		print(f);

		BufferedReader r = new BufferedReader(new FileReader(f));
		Assert.assertEquals(",,", r.readLine());
		Assert.assertEquals("\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals("\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals("\"null\",,", r.readLine());
		Assert.assertEquals("\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
		CSVReader reader = new CSVReader(new FileReader(f),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		List<MockBean> parse = objectReader.parse();
		for (MockBean mockBean : list) {
			mockBean.setId(null);
		}
		Assert.assertArrayEquals(list.toArray(), parse.toArray());
	}

	@Test
	public void testReadCustomNull() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class, new String[] { "this is null", "alsoNull" },
				true, false, true);

		File file = File.createTempFile("test", ".csv");

		ArrayList<MockBean> list = createObjects();

		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.write("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"");
		w.newLine();
		w.write("\"alsoNull\",\"this is null\",,\"alsoNull\"");
		w.newLine();
		w.write("\" \",\"\",\"1\",\"\"");
		w.newLine();
		w.write("\"this is null\",\"prov");
		w.newLine();
		w.write("a\",\"123\",\"test\"");
		w.newLine();
		w.write("\"as\",\"null\",this is null,");
		w.newLine();
		w.write("\"a\",\"ciao\",\"0\",\"me\"");
		w.newLine();

		w.close();

		CSVReader reader = new CSVReader(new FileReader(file),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		List<MockBean> parse = objectReader.parse();

		Assert.assertArrayEquals(list.toArray(), parse.toArray());
	}

	@Test
	public void testWriteReadCustomNull() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class, new String[] { "this is null" }, true, false,
				true);

		File file = testStrategy(strategy);
		BufferedReader r = new BufferedReader(new FileReader(file));
		Assert.assertEquals("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"",
				r.readLine());
		Assert.assertEquals(
				"\"this is null\",\"this is null\",\"this is null\",\"this is null\"",
				r.readLine());
		Assert.assertEquals("\" \",\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals("\"this is null\",\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals(
				"\"as\",\"null\",\"this is null\",\"this is null\"",
				r.readLine());
		Assert.assertEquals("\"a\",\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
	}

	@Test
	public void testReadEmptyString() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class, new String[] { "this is null", "alsoNull" },
				true, false, true);

		File file = File.createTempFile("test", ".csv");

		ArrayList<MockBean> list = createObjects();

		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.write("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"");
		w.newLine();
		w.write("\"alsoNull\",\"this is null\",alsoNull,\"alsoNull\"");
		w.newLine();
		w.write("\" \",,\"1\",\"\"");
		w.newLine();
		w.write("\"this is null\",\"prov");
		w.newLine();
		w.write("a\",\"123\",\"test\"");
		w.newLine();
		w.write("\"as\",\"null\",alsoNull,alsoNull");
		w.newLine();
		w.write("\"a\",\"ciao\",\"0\",\"me\"");
		w.newLine();

		w.close();

		CSVReader reader = new CSVReader(new FileReader(file));
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		List<MockBean> parse = objectReader.parse();

		Assert.assertArrayEquals(list.toArray(), parse.toArray());
	}

	@Test
	public void testWriteReadCustomEditor() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class);

		strategy.registerEditor(Integer.class, new PropertyEditor() {

			Integer value;

			public boolean supportsCustomEditor() {
				// TODO Auto-generated method stub
				return false;
			}

			public void setValue(Object value) {
				this.value = (Integer) value;

			}

			public void setAsText(String text) throws IllegalArgumentException {
				Integer i = Integer.valueOf(text);
				value = i / 100;
			}

			public void removePropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}

			public void paintValue(Graphics gfx, Rectangle box) {
				// TODO Auto-generated method stub

			}

			public boolean isPaintable() {
				// TODO Auto-generated method stub
				return false;
			}

			public Object getValue() {
				return value;
			}

			public String[] getTags() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getJavaInitializationString() {
				// TODO Auto-generated method stub
				return null;
			}

			public Component getCustomEditor() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getAsText() {
				// TODO Auto-generated method stub
				return "" + value * 100;
			}

			public void addPropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}
		});

		File file = testStrategy(strategy);
		BufferedReader r = new BufferedReader(new FileReader(file));
		Assert.assertEquals("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"",
				r.readLine());
		Assert.assertEquals(",,,", r.readLine());
		Assert.assertEquals("\" \",\"\",\"100\",\"\"", r.readLine());
		Assert.assertEquals(",\"prov", r.readLine());
		Assert.assertEquals("a\",\"12300\",\"test\"", r.readLine());
		Assert.assertEquals("\"as\",\"null\",,", r.readLine());
		Assert.assertEquals("\"a\",\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
	}

	@Test
	public void testWriteReadIterator() throws IOException {
		CsvBeanProcessorStrategy<MockBean> strategy = new CsvBeanProcessorStrategy<MockBean>(
				MockBean.class);

		File f = File.createTempFile("test", ".csv");
		CSVWriter writer = new CSVWriter(new FileWriter(f));
		ObjectWriter<MockBean, CSVWriter> objectWriter = new ObjectWriter<MockBean, CSVWriter>(
				writer, strategy);

		ArrayList<MockBean> list = createObjects();

		objectWriter.write(list);
		writer.close();
		print(f);

		BufferedReader r = new BufferedReader(new FileReader(f));
		Assert.assertEquals("\"ID\",\"NAME\",\"NUM\",\"ORDERNUMBER\"",
				r.readLine());
		Assert.assertEquals(",,,", r.readLine());
		Assert.assertEquals("\" \",\"\",\"1\",\"\"", r.readLine());
		Assert.assertEquals(",\"prov", r.readLine());
		Assert.assertEquals("a\",\"123\",\"test\"", r.readLine());
		Assert.assertEquals("\"as\",\"null\",,", r.readLine());
		Assert.assertEquals("\"a\",\"ciao\",\"0\",\"me\"", r.readLine());

		r.close();
		CSVReader reader = new CSVReader(new FileReader(f),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);
		int i = 0;
		for (MockBean bean : objectReader) {
			Assert.assertEquals("i", list.get(i++), bean);
		}
	}

	private File testStrategy(CsvBeanProcessorStrategy<MockBean> strategy)
			throws IOException, FileNotFoundException {
		File f = File.createTempFile("test", ".csv");
		CSVWriter writer = new CSVWriter(new FileWriter(f));
		ObjectWriter<MockBean, CSVWriter> objectWriter = new ObjectWriter<MockBean, CSVWriter>(
				writer, strategy);
		
		CSVReader reader = new CSVReader(new FileReader(f),0,new CSVParserBuilder().withEmptyUnquotedIsNull(true).build());
		ObjectReader<MockBean, CSVReader> objectReader = new ObjectReader<MockBean, CSVReader>(
				reader, strategy);

		ArrayList<MockBean> list = createObjects();

		objectWriter.write(list);
		writer.close();

		print(f);

		List<MockBean> parse = objectReader.parse();
		reader.close();

		Assert.assertArrayEquals(list.toArray(), parse.toArray());
		return f;
	}

	private ArrayList<MockBean> createObjects() {
		ArrayList<MockBean> list = new ArrayList<MockBean>();
		MockBean bean = new MockBean();
		list.add(bean);
		bean = new MockBean("", " ", "", 1);
		list.add(bean);
		bean = new MockBean("prov\na", null, "test", 123);
		list.add(bean);
		bean = new MockBean("null", "as", null, null);
		list.add(bean);
		bean = new MockBeanChild("ciao", "a", "me", 0, "aaaa");
		list.add(bean);
		return list;
	}

	private void print(File f) throws FileNotFoundException, IOException {
		if (true) {
			System.out
					.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line;
			while ((line = r.readLine()) != null) {
				PropertyEditor se = PropertyEditorManager
						.findEditor(String.class);
				se.setValue(line);
				System.out
						.println("Assert.assertEquals("
								+ se.getJavaInitializationString()
								+ ", r.readLine());");
			}
			r.close();
		}
	}
	
	@Test
	public void testChildBeanExpansion() throws IOException, ParseException{
		CsvBeanProcessorStrategy<MockBean2> strat = new CsvBeanProcessorStrategy<MockBean2>(MockBean2.class);
		strat.registerEditor("DATE", new PropertyEditor() {
			
			private Object value;
			SimpleDateFormat sdf = new SimpleDateFormat();
			
			public boolean supportsCustomEditor() {
				return false;
			}
			
			public void setValue(Object value) {
				this.value = value;
				
			}
			
			public void setAsText(String text) throws IllegalArgumentException {
				try {
					value = sdf.parse(text);
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
				
			}
			
			public void removePropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			public void paintValue(Graphics gfx, Rectangle box) {
				// TODO Auto-generated method stub
				
			}
			
			public boolean isPaintable() {
				// TODO Auto-generated method stub
				return false;
			}
			
			public Object getValue() {
				return value;
			}
			
			public String[] getTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getJavaInitializationString() {
				
				return "new SimpleDateFormat().parse("+getAsText()+")";
			}
			
			public Component getCustomEditor() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getAsText() {
				
				return sdf.format(value);
			}
			
			public void addPropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}
		});
		
		File f = File.createTempFile("tmp", ".csv"); 
		ObjectWriter<MockBean2, CSVWriter> objectWriter = new ObjectWriter<MockBean2, CSVWriter>(new CSVWriter(new FileWriter(f)), strat);
		
		
		List<MockBean2> list = new ArrayList<MockBean2>();
		list.add(new MockBean2("aa", new SimpleDateFormat().parse("2/1/12 12:00 AM"), new MockBean("name", "id", "ord", 12)));
		
		objectWriter.write(list);
		objectWriter.close();
		print(f);
		
		ObjectReader<MockBean2, CSVReader> objectReader = new ObjectReader<MockBean2, CSVReader>(new CSVReader(new FileReader(f)), strat);
		List<MockBean2> list2 = objectReader.parse();
		objectReader.close();
		
		BufferedReader r = new BufferedReader(new FileReader(f));
		Assert.assertEquals("\"CHILD.ID\",\"CHILD.NAME\",\"CHILD.NUM\",\"CHILD.ORDERNUMBER\",\"DATE\",\"NAME\"", r.readLine());
		Assert.assertEquals("\"id\",\"name\",\"12\",\"ord\",\"2/1/12 12:00 AM\",\"aa\"", r.readLine());
		r.close();
		Assert.assertArrayEquals(list.toArray(), list2.toArray());
		
		
		
	}
}
