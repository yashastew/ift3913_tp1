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
	//@Override
	//on verifies qu'ils ont les memes type et identifiant
	//et que les arg list sont "pareils" qu'ils contennent les
	//memes elements - sans sucier de l'ordre. il faut faire
	// le test deux fois pour s'assurer que un n'est pas un
	//sous ensemble de l'autre.
	public boolean equals(Operation op){
		if (identifier.trim().equals(op.getIdentifier().trim())
				&& type.trim().equals(op.getType().trim())
				&& argList.containsAll(op.getArgs())
				&&op.getArgs().containsAll(argList)) return true;
		return false;
		
	}

}
