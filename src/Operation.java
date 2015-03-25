import java.util.*;

public class Operation {

	private ArrayList<DataItem> argList = new ArrayList<DataItem>();
	private String identifier;
	private String type;
	
	public void addArgument(DataItem dataItem){
		argList.add(dataItem);
	}
	
	public void myPrint(){
		String arguments = "";
		for (DataItem dataItem : argList) {
			arguments += (dataItem.getType() + " " + dataItem.getIdentifier() + ",");
		}
		System.out.println("--------" + type + " "
							+ identifier +"(" + arguments + ")" );
		
	}
	
	
	//Getters and setters
	public String getIdentifier() {
		return identifier;
	}

	public String getType() {
		return type;
	}
	//gets the number of arguments the operation takes
	public int numArgs(){
		return argList.size();
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<DataItem> getArgs(){
		return argList;
	}

}