import java.util.ArrayList;

import javax.swing.event.AncestorEvent;


public class MetricCalculator {
	
	private ArrayList<Declaration> declarationList;
	
	public MetricCalculator(ArrayList<Declaration> declarationList){
		this.declarationList = declarationList;
	}
	
	public String[] getMetricArray(String identifier){
		String CAC, NOC, NOD;
		ClassDec classDec = getClassDecById(identifier);

		CAC = "CAC="+calculateCAC(classDec);
		NOC = "NOC="+calculateNOC(classDec);
		NOD = "NOD="+calculateNOD(classDec);
		String[] metricsArray= {CAC, NOC, NOD};
		return metricsArray;
	}
	
	public void calculateMetric(String identifier){
		
		int CAC, NOC, NOD;
		ClassDec classDec = getClassDecById(identifier);
		println("\n########################\nMétriques\n########################");
		println("\nClasse: " + identifier);
		
		CAC = calculateCAC(classDec);
		NOC = calculateNOC(classDec);
		NOD = calculateNOD(classDec);
		
		println("CAC: " + CAC);
		println("NOC: " + NOC);
		println("NOD: " + NOD);
		
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
