package au.com.bytecode.opencsv.object;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author filippor
 * 
 * @param <T>
 */
public class CsvBeanProcessorStrategy<T> implements
		ProcessorStrategy<T, CSVWriter, CSVReader> {
	private String[] colums;
	private Class<T> type;
	private boolean skipClassProperty = true;
	private boolean withHeader;
	private String[] nullStrings;
	private boolean applyQuotesToAll = true;
	private boolean expandChildBean = true;
	private String childBeanEclusionPattern = "java.*";
	
	
	
	private Map<String,List<PropertyDescriptor>> descriptorMap = null;
	private Map<Class<?>, PropertyEditor> editorMap = null;
	private Map<String, PropertyEditor> editorColumnMap = new HashMap<String, PropertyEditor>();

	public CsvBeanProcessorStrategy(Class<T> cls) {
		this(cls, null, true,true ,true);
	}

	public CsvBeanProcessorStrategy(Class<T> cls, boolean withHeader) {
		this(cls, null, withHeader,true, true);
	}

	public boolean isSkipClassProperty() {
		return skipClassProperty;
	}

	public void setSkipClassProperty(boolean skipClassProperty) {
		this.skipClassProperty = skipClassProperty;
	}

	public String[] getNullStrings() {
		return nullStrings;
	}

	public void setNullStrings(String[] nullStrings) {
		this.nullStrings = nullStrings;
	}

	public boolean isApplyQuotesToAll() {
		return applyQuotesToAll;
	}

	public void setApplyQuotesToAll(boolean applyQuotesToAll) {
		this.applyQuotesToAll = applyQuotesToAll;
	}

	public CsvBeanProcessorStrategy(Class<T> cls, String[] nullString,
			boolean withHeader, boolean applyQuotesToAll, boolean skipClassProperty) {
		this.type = cls;
		this.setWithHeader(withHeader);
		this.nullStrings = nullString;
		this.skipClassProperty = skipClassProperty;
		try {
			init();
		} catch (IntrospectionException e) {
			throw new RuntimeException("Error introspecting bean", e);
		}
	}

	public boolean captureHeader(CSVReader reader) throws IOException {
		if (withHeader) {
			String[] readColums = reader.readNext();
			if (readColums == null) {
				throw new RuntimeException("CSV file is empty");
			}
			for (int i = 0; i < readColums.length; i++) {
				this.colums[i] = cleanColumnName(readColums[i]);
			}
			return true;
		}
		return false;

	}

	public boolean processHeader(CSVWriter writer) {
		if (withHeader) {
			writer.writeNext(colums);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public T processLine(CSVReader reader) throws Exception {
		String[] line = reader.readNext();
		if (line == null)
			return null;
		T bean = null;
		
		if(skipClassProperty){
			bean = type.newInstance();
		}else{
			for (int i = 0; i < colums.length; i++) {
				if("CLASS".equalsIgnoreCase(colums[i])){
					Class<? extends T> cls = (Class<? extends T>) Class.forName(line[i]);
					bean = cls.newInstance();
				}
			}
		}
		if(bean == null)throw new RuntimeException("connot find valid class column");
		for (int col = 0; col < line.length; col++) {
			if (col < colums.length) {
				String value = line[col];
				List<PropertyDescriptor> prop = descriptorMap
						.get(cleanColumnName(colums[col]));
				if ("class".equals(prop.get(prop.size()-1).getName())
						&& Class.class.equals(prop.get(prop.size()-1).getPropertyType())) {
						continue;
				}else{
					Object obj = convertStringToValue(value, cleanColumnName(colums[col]));
					Object bean2 = bean; 
					for (PropertyDescriptor propertyDescriptor : prop) {
						if(prop.indexOf(propertyDescriptor)==(prop.size()-1))
							break;
						Object parent = bean2;
						bean2 = propertyDescriptor.getReadMethod().invoke(bean2);
						if(bean2 == null){
							bean2 = propertyDescriptor.getPropertyType().newInstance();
							propertyDescriptor.getWriteMethod().invoke(parent, bean2);
						}
					}
					prop.get(prop.size()-1).getWriteMethod().invoke(bean2, obj);
				}
			}
		}
		
		return bean;
	}

	public boolean processObject(T bean, CSVWriter writer) throws Exception {
		String[] values = new String[colums.length];
		// retrieve bean values
		for (int i = 0; i < values.length; i++) {

			String columnName = cleanColumnName(colums[i]);
			List<PropertyDescriptor> descriptor = descriptorMap
					.get(columnName);
			Object value = bean;
			for (PropertyDescriptor propertyDescriptor : descriptor) {
				value = propertyDescriptor.getReadMethod().invoke(value);
			}
			
			if(!skipClassProperty&& columnName.equals("CLASS")&&value instanceof Class){
				values[i] = ((Class<?>)value).getName();
			}else{
				values[i] = convertValueToString(value, columnName);
			}
		}
		writer.writeNext(values, applyQuotesToAll);
		return true;
	}

	private void init() throws IntrospectionException {
		descriptorMap = new HashMap<String, List<PropertyDescriptor>>();
		
		List<String> columsTmp = loadProperty(type,new ArrayList<PropertyDescriptor>(),"");
		
		colums = columsTmp.toArray(new String[columsTmp.size()]);
	}

	private List<String> loadProperty(Class<?> type,List<PropertyDescriptor> parent,String parentString) throws IntrospectionException {
		PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type)
				.getPropertyDescriptors();
		
		int lenght = descriptors.length;
		
		if (skipClassProperty)
			lenght--;
		List<String> columsTmp =  new ArrayList<String>(lenght);
		for (PropertyDescriptor descriptor : descriptors) {
			if (skipClassProperty) {
				if ("class".equals(descriptor.getName())
						&& Class.class.equals(descriptor.getPropertyType())) {
					continue;
				}
			}
			List<PropertyDescriptor> current = new ArrayList<PropertyDescriptor>(parent);
			current.add(descriptor);
			if(expandChildBean && !	descriptor.getPropertyType().getName().matches(childBeanEclusionPattern)){
				
				columsTmp.addAll(loadProperty(descriptor.getPropertyType(), current,parentString + descriptor.getName()+"."));
			}else{
			descriptorMap
					.put(cleanColumnName(parentString + descriptor.getName()), current);
			columsTmp.add(cleanColumnName(parentString + descriptor.getName()));
			}
		}
		return columsTmp;
	}

	public void setColumnMapping(String... columns) {
		if (columns == null) {
			throw new IllegalArgumentException();
		}
		this.colums = columns;

	}

	private String cleanColumnName(String string) {
		return string.toUpperCase().trim();
	}

	/**
	 * register a editor to be used for cls property type
	 * 
	 * @param cls
	 * @param editor
	 */
	public void registerEditor(Class<?> cls, PropertyEditor editor) {
		if (editorMap == null) {
			editorMap = new HashMap<Class<?>, PropertyEditor>();
		}
		editorMap.put(cls, editor);
	}
	/**
	 * register a editor to be used for a column
	 * 
	 * @param cls
	 * @param editor
	 */
	public void registerEditor(String column, PropertyEditor editor) {
		if (editorColumnMap == null) {
			editorColumnMap = new HashMap<String, PropertyEditor>();
		}
		editorColumnMap.put(column, editor);
	}

	private String checkForTrim(String s, String  column) {
		PropertyDescriptor prop = descriptorMap.get(column).get(descriptorMap.get(column).size()-1);
		return trimmableProperty(prop) ? s.trim() : s;
	}

	private boolean trimmableProperty(PropertyDescriptor prop) {
		return !prop.getPropertyType().equals(String.class);
	}

	private PropertyEditor getPropertyEditor(String column) throws InstantiationException, IllegalAccessException {
		PropertyEditor propertyEditor = editorColumnMap.get(column);
		if(propertyEditor != null)
			return propertyEditor;
		
		PropertyDescriptor propertyDescriptor = descriptorMap.get(column).get(descriptorMap.get(column).size()-1);
		if(propertyDescriptor.getPropertyEditorClass()!=null){
			propertyEditor = (PropertyEditor) propertyDescriptor.getPropertyEditorClass().newInstance();
			editorColumnMap.put(column, propertyEditor);
			
		}
		Class<?> cls = propertyDescriptor.getPropertyType(); 
		if (editorMap == null) {
			editorMap = new HashMap<Class<?>, PropertyEditor>();
		}

		propertyEditor = editorMap.get(cls);

		if (propertyEditor == null) {
			propertyEditor = PropertyEditorManager.findEditor(cls);
		}

		return propertyEditor;
	}

	

	/*private PropertyEditor getPropertyEditor(PropertyDescriptor desc)
			throws InstantiationException, IllegalAccessException {
		Class<?> cls = desc.getPropertyEditorClass();
		if (null != cls)
			return (PropertyEditor) cls.newInstance();
		return getPropertyEditorValue(desc.getPropertyType());
	}*/

	private Object convertStringToValue(String string, String column)
			throws InstantiationException, IllegalAccessException {
		PropertyEditor editor = getPropertyEditor(column);
		if (isNull(string))
			return null;
		Object obj = string;
		if (null != editor) {
			editor.setAsText(checkForTrim(string, column));
			obj = editor.getValue();
		}

		return obj;
	}

	private boolean isNull(String string) {
		if(string == null) return true;
		if (nullStrings != null) {
			for (String nullString : nullStrings) {
				if (nullString.equals(string))
					return true;
			}
		}
		return false;
	}

	private String convertValueToString(Object value, String column)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		
		if (value == null)
			return nullStrings == null?null:nullStrings[0];;
		PropertyEditor editor = getPropertyEditor(column);
		String obj = value.toString();
		if (null != editor) {
			editor.setValue(value);
			obj = editor.getAsText();
		}
		return obj;
	}

	

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.bytecode.opencsv.object.ProcessorStrategy#isWithHeader()
	 */
	public boolean isWithHeader() {
		return withHeader;
	}

	public void setWithHeader(boolean withHeader) {
		this.withHeader = withHeader;
	}

}
