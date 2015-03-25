public class DataItem {

	private String identifier;
	private String type;
	
	
	
	public void myPrint(){
		System.out.println("--------" + type + " " + identifier);
	}

	
	//@Override
	public boolean equals(DataItem di){
		if (identifier.trim().equals(di.getIdentifier().trim())
				&& type.trim().equals(di.getType().trim())) return true;
		return false;
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