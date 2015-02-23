import java.util.*;

public class Generalization extends Declaration {
	
	private ClassDec parentClass;
	private ArrayList<ClassDec> subClasses = new ArrayList<ClassDec>();
	
	
	
	public void myPrint() {
		System.out.println("\nGeneralization Parent Class: " + parentClass.getIdentifier());
		for (ClassDec classDec : subClasses) {
			System.out.println("----"+classDec.getIdentifier());
		}
	}
	
	public void addSubClass(ClassDec classDec){
		subClasses.add(classDec);
	}
	
	//Getters and setters
	public ArrayList<ClassDec> getSubClasses() {
		return subClasses;
	}
	
	public ClassDec getParentClass() {
	return parentClass;
	}

	public void setParentClass(ClassDec parentClass) {
		this.parentClass = parentClass;
	}

	

	

}