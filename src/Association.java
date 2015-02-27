import java.util.*;

public class Association extends Declaration {

	private ArrayList<Role> roles = new ArrayList<Role>();
	private String identifier = "";

	public void myPrint() {
		System.out.println("\nAssociation: " + identifier);
		System.out.print("----");
		roles.get(0).myPrint();
		System.out.print("----");
		roles.get(1).myPrint();
		
	}
	
	public void addRole(ClassDec classDec, Multiplicity multiplicity){
		Role role = new Role();
		role.setClassRole(classDec);
		role.setMultiplicity(multiplicity);
		roles.add(role);
	}
	
	
	//Getters and setters

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public ArrayList<Role> getRoles(){
		return this.roles;
	}
	
	

}