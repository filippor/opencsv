package au.com.bytecode.opencsv.object;



public class MockBeanChild extends MockBean {
	public MockBeanChild() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MockBeanChild(String name, String id, String orderNumber, Integer num,String tmp) {
		super(name, id, orderNumber, num);
		this.tmp = tmp;
	}

	private String tmp;


	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}
	

}
