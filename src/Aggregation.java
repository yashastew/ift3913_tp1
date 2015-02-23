import java.util.*;


public class Aggregation extends Declaration {

	private Role container;
	private ArrayList<Role> parts = new ArrayList<Role>();

	public void myPrint() {
		System.out.print("\nAggregation: ");
		container.myPrint();
		for (Role role : parts) {
			System.out.print("----");
			role.myPrint();			
		}
		
	}
	
	
	
	
	public void addPart(ClassDec classDec, Multiplicity multiplicity){
		Role role = new Role();
		role.setClassRole(classDec);
		role.setMultiplicity(multiplicity);
		parts.add(role);
	}
	
	//Getters and setters

	public Role getContainer() {
		return container;
	}

	public void setContainer(ClassDec classDec, Multiplicity multiplicity) {
		Role role = new Role();
		role.setClassRole(classDec);
		role.setMultiplicity(multiplicity);
		container = role;
	}

}
