public class Role {

	private ClassDec classRole;
	private Multiplicity multiplicity;
	
	public void myPrint() {
		System.out.println(classRole.getIdentifier() + " " + multiplicity);
	}
	
	
	//Getters and setters

	public ClassDec getClassRole() {
		return classRole;
	}

	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setClassRole(ClassDec classRole) {
		this.classRole = classRole;
	}

	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}
}