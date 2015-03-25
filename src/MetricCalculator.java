import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.AncestorEvent;


public class MetricCalculator {
	
	private ArrayList<Declaration> declarationList;
	
	public MetricCalculator(ArrayList<Declaration> declarationList){
		this.declarationList = declarationList;
	}
	
	public String[] getMetricArray(String identifier){
		String CAC, NOC, NOD, ANA, NOM, NOA, ITC, ETC;
		ClassDec classDec = getClassDecById(identifier);
		ANA= "ANA="+calculateANA(classDec);
		NOM= "NOM="+calculateNOM(classDec);
		NOA= "NOA="+calculateNOA(classDec);
		ITC= "ITC="+calculateITC(classDec);
		ETC= "ETC="+calculateETC(classDec);
		CAC = "CAC="+calculateCAC(classDec);
		NOC = "NOC="+calculateNOC(classDec);
		NOD = "NOD="+calculateNOD(classDec);
		String[] metricsArray= {ANA, NOM, NOA, ITC, ETC, CAC, NOC, NOD};
		return metricsArray;
	}
	
	public void calculateMetric(String identifier){
		
		int CAC, NOC, NOD, NOM, NOA, ITC, ETC;
		float ANA;
		ClassDec classDec = getClassDecById(identifier);
		println("\n########################\nMétriques\n########################");
		println("\nClasse: " + identifier);
		
		ANA = calculateANA(classDec);
		NOM= calculateNOM(classDec);
		NOA = calculateNOA(classDec);
		ITC = calculateITC(classDec);
		ETC = calculateETC(classDec);
		CAC = calculateCAC(classDec);
		NOC = calculateNOC(classDec);
		NOD = calculateNOD(classDec);
		
		println("ANA: " + ANA);
		println("NOM: " + NOM);
		println("NOA: " + NOA);
		println("ITC: " + ITC);
		println("ETC: " + ETC);
		println("CAC: " + CAC);
		println("NOC: " + NOC);
		println("NOD: " + NOD);
		
	}
	//ETC(ci) : Nombre de fois où ci apparaît comme type des arguments
	//dans les méthodes des autres classes du diagramme
	private int calculateETC(ClassDec classDec){
		//System.out.println("CHECKING ETC for:"+ classDec.getIdentifier());
		int ETC=0;
		for(Declaration d: declarationList){
			if(d instanceof ClassDec){
				//System.out.println("Class: "+ ((ClassDec)d).getIdentifier());
				for(Operation op: ((ClassDec)d).getOperations()){
					for(DataItem di: op.getArgs()){
						if(di.getType().equals(classDec.getIdentifier())){
							ETC++;
							//System.out.println("usage found");
						}
					}
				}
			}
		}
		return ETC;
	}
	
	//ITC(ci) : Nombre de fois où d’autres classes du diagramme
	//apparaissent comme types des arguments des méthodes de ci.
	private int calculateITC(ClassDec classDec){
		ClassDec currentClass=classDec;
		ArrayList<String> stringDecs= new ArrayList<String>();
		for(Declaration d: declarationList){
			stringDecs.add(d.getIdentifier());
		}
		int ITC=0;
		Set<Operation> uniqueMethodes = new HashSet<Operation>();
		while(currentClass!= null){
		for(Operation op: currentClass.getOperations()){
			uniqueMethodes.add(op);
		}
		currentClass=getAncestor(currentClass);
		}
		for(Operation op: uniqueMethodes){
			for( DataItem di :op.getArgs()){
				if(stringDecs.contains(di.getType())) ITC++;
			}
		}
		return ITC;
	}
	
	
	//number of attributes
	private int calculateNOA(ClassDec classDec){
		ClassDec currentClass=classDec;
		int NOA=0;
		while(currentClass != null){
			NOA += currentClass.getAttributes().length;
			//println("current class: "+ currentClass.getIdentifier());
			for(int i=0;i<currentClass.getAttributes().length; i++){
			//println("attribute name= "+ currentClass.getAttributes()[i].getIdentifier());	
			}
			currentClass=getAncestor(currentClass);
		}
		
		return NOA;
	}
	//number of methodes including inherited UNLESS redefined
	//unique methodes should contain ALL the unique methodes and eleminate repetition 
	//via redefinition
	private int calculateNOM(ClassDec classDec){
		ClassDec currentClass=classDec;
		int NOM=0;
		Set<Operation> uniqueMethodes = new HashSet<Operation>();
		while(currentClass!= null){
		for(Operation op: currentClass.getOperations()){
			uniqueMethodes.add(op);
		}
		currentClass=getAncestor(currentClass);
		}
		NOM= uniqueMethodes.size();
		return NOM;
	}
	
	//average number of arguments
	private float calculateANA(ClassDec classDec){
		float ANA=0;
		int totalArgs=classDec.totalMethodeArguments();
		int totalMeth=classDec.totalMethodes();
		if(totalMeth != 0) ANA= ((float)totalArgs)/((float)totalMeth);
		return ANA;
	}

	


	
	private int calculateNOD(ClassDec classDec){
		int NOD = 0;
		ArrayList<ClassDec> subClasses = new ArrayList<ClassDec>();
		
		//Trouver le nombre d'enfant
		for (Declaration declaration : declarationList) {
			if (declaration instanceof Generalization){
				Generalization generalization = (Generalization)declaration;
				if(generalization.getParentClass() == classDec){
					subClasses = generalization.getSubClasses();
					NOD += subClasses.size();
					//Appels récursifs
					for (ClassDec subClass : subClasses) {
						NOD += calculateNOD(subClass);
					}
				}
			}			
		}
		return NOD;
	}
	
	private int calculateNOC(ClassDec classDec){
		int NOC = 0;
		
		for (Declaration declaration : declarationList) {
			if (declaration instanceof Generalization){
				Generalization generalization = (Generalization)declaration;
				if(generalization.getParentClass() == classDec)
					NOC += generalization.getSubClasses().size();
			}			
		}
		
		
		return NOC;
	}
	
	private int calculateCAC(ClassDec classDec){
		
		int CAC = 0;
		ClassDec ancestor = getAncestor(classDec);
		
		do {
			//Compter les associations
			for (Declaration declaration : declarationList) {
				if(declaration instanceof Association){
					Association association = (Association)declaration;
					if(association.getRoleOne() == classDec ||
					   (association.getRoleTwo() == classDec)){
						//println("Found: " + declaration.getIdentifier());
						CAC ++;
					}
				}
			}
			
			//Compter les aggrégations
			for (Declaration declaration : declarationList) {
				if(declaration instanceof Aggregation){
					Aggregation aggregation = (Aggregation)declaration;
					if(aggregation.getContainer().getClassRole() == classDec){
						CAC++;
						//println("Found: " + classDec.getIdentifier() + " aggr.");
					}
					ArrayList<Role> parts = aggregation.getParts();
					for (Role role : parts) {
						if(role.getClassRole() == classDec){
							CAC++;
							//println("Found: " + classDec.getIdentifier() + " aggr.");
						}
					}
				}
			}
		
			classDec = ancestor;
			ancestor = getAncestor(classDec);

			
		} while (null != classDec); //Répéter pour les ancêtres
		
		
		return CAC;
		
	}
	
	/* Inutile pour l'instant
	private boolean hasAncestor(ClassDec classDec){
		String identifier = classDec.getIdentifier();
		
		for (Declaration declaration : declarationList) {
			
			if (declaration instanceof Generalization){
				ArrayList<ClassDec> subClasses = ((Generalization) declaration).getSubClasses();
				
				for (ClassDec subClass : subClasses) {
					if (subClass.getIdentifier().compareTo(identifier) == 0)
						return true;
				}
			}
		}
		return false;
	}
	*/
	
	//Retourne null s'il n'y a pas d'ancêtre
	private ClassDec getAncestor(ClassDec classDec){
		if(classDec == null)
			return null;
		String identifier = classDec.getIdentifier();
		ClassDec ancestor = null;
		
		for (Declaration declaration : declarationList) {
			
			if (declaration instanceof Generalization){
				ArrayList<ClassDec> subClasses = ((Generalization) declaration).getSubClasses();
				
				for (ClassDec subClass : subClasses) {
					if (subClass.getIdentifier().compareTo(identifier) == 0)
						ancestor = ((Generalization) declaration).getParentClass();
						//println("Classe parente: " + ((Generalization) declaration).getParentClass().getIdentifier());
				}
			}			
		}
		return ancestor;	
	}
	
	
	
	private ClassDec getClassDecById(String identifier){
		for (Declaration declaration : declarationList) {
			if (declaration instanceof ClassDec)
				if (declaration.getIdentifier().equals(identifier))
					return (ClassDec) declaration;					
		}
		ClassDec classDec = new ClassDec();
		classDec.setIdentifier(identifier);
		declarationList.add(classDec);
		return classDec;
	}
	
	//pour le debuggage seulement
	void println(Object line) {
	    System.out.println(line);
	}
	
	
}
