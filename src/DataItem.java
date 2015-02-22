public class DataItem {

	private String identifier;
	private String type;
	
	
	
	public void myPrint(){
		System.out.println("--------" + type + " " + identifier);
	}

	
	
	
	//Getters and setters
	public String getIdentifier() {
		return identifier;
	}
	public String getType() {
		return type;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public void setType(String type) {
		this.type = type;
	}

}