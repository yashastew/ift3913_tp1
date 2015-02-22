import java.util.*;

public class ClassDec extends Declaration {

	private ArrayList<DataItem> attributeList = new ArrayList<DataItem>();
	private ArrayList<Operation> operationList = new ArrayList<Operation>();
	private String identifier;
	
	
	public void myPrint(){
		System.out.println("");
		System.out.println("Class Identifier: " + identifier);
		System.out.println("----Attributes ");
		for(int i=0; i<attributeList.size(); i++)
			attributeList.get(i).myPrint();
		System.out.println("----Operations");
		for(int i=0; i<operationList.size(); i++)
			operationList.get(i).myPrint();
		
	}
	
	public void addAttribute(DataItem attribute){
		attributeList.add(attribute);
	}
	
	public void addOperation(Operation operation){
		operationList.add(operation);
	}
	
	
	//Getters and setters

	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}