import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

public class TextParser {

	private ArrayList<Declaration> declarationList = new ArrayList<Declaration>();
	private String fileName = "";
	private String modelName = "";
	private ArrayList<String> words = new ArrayList<String>();
	
	public void parseFile() throws IOException {
		FileInputStream inputStream = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String lineReading = null;
		
		//Lire ligne par ligne
		while ((lineReading = br.readLine()) != null) {
			
			//Séparer chaque mots dans un tableau en igonrant les
			//parenthèse et les virgules
			lineReading = lineReading.replace(":", " : ");
			lineReading = lineReading.replace(";", " ; ");
			String[] wordList = lineReading.split("\\s+|\\(|\\)|\\,");
			
			//Ajouter chaque mots à wors
			for(int i=0; i<wordList.length; i++)
				if(wordList[i].length() != 0)
					words.add(wordList[i]);
		}
		br.close();
		
		System.out.println(words);

		//Parser les déclarations
		modelName = words.get(1);
		for(int i=2; i<words.size(); i++){
			if(words.get(i).contains("CLASS")) {
				i = parseClass(i);
			} else if(words.get(i).contains("RELATION")) {
				i = parseAssociation(i);
			} else if(words.get(i).contains("AGGREGATION")) {
				i = parseAggregation(i);
			} else if(words.get(i).contains("GENERALIZATION")) {
				i = parseGeneralization(i);
			}
		}
		
		System.out.println("Il y a "+declarationList.size() + " declarations");
		myPrint();
		//System.out.println(words);
	}
	
	
	private int parseClass(int i){
		i++;
		ClassDec classDec = getClassDecById(words.get(i));
		i+=2;
		
		//Parser les attributs
		while(! (words.get(i).contains("OPERATIONS"))){
			DataItem attribute = new DataItem();
			attribute.setIdentifier(words.get(i));
			attribute.setType(words.get(i+2));
			classDec.addAttribute(attribute);
			i+=3;
		}
		i++;
		
		//Parser les opérations
		while(! (words.get(i).contains(";"))){
			Operation operation = new Operation();
			operation.setIdentifier(words.get(i));
			while(! (words.get(i+1).contains(":")) ){
				DataItem argument = new DataItem();
				argument.setIdentifier(words.get(i+1));
				argument.setType(words.get(i+3));
				operation.addArgument(argument);
				i+=3;
			}
			operation.setType(words.get(i+2));
			classDec.addOperation(operation);
			i+=3;
		}
		
		return i;
	}
	
	private int parseAssociation(int i){
		i++;
		while(! (words.get(i).contains(";"))){
			i++;
		}
		return i;
	}
	
	private int parseGeneralization(int i){
		i++;
		
		//Setter la classe parente
		Generalization generalization = new Generalization();
		ClassDec parentClassDec = getClassDecById(words.get(i));
		generalization.setParentClass(parentClassDec);
		//Ajouter les enfants
		i+=2;
		while(!words.get(i).contains(";")) {
			generalization.addSubClass(getClassDecById(words.get(i)));
			i+=1;
		}
		
		declarationList.add(generalization);
		
		return i;
	}
	
	private int parseAggregation(int i){
		i++;
		while(! (words.get(i).contains(";"))){
			i++;
		}
		return i;
	}

	
	public void myPrint(){
		System.out.println("Model: "+ modelName);
		for(int i=0; i<declarationList.size(); i++)
			declarationList.get(i).myPrint();
	}
	
	//Retourne la classe si elle existe déjà, sinon en retourne
	//une nouvelle avec le bon identifier
	private ClassDec getClassDecById(String identifier){
		for (Declaration declaration : declarationList) {
			if (declaration instanceof ClassDec)
				if (((ClassDec) declaration).getIdentifier().equals(identifier))
					return (ClassDec) declaration;					
		}
		ClassDec classDec = new ClassDec();
		classDec.setIdentifier(identifier);
		declarationList.add(classDec);
		return classDec;
	}
	
	//Getters ans Setters
	
	public ArrayList<Declaration> getDeclarationList() {
		return declarationList;
	}
	
	public String getFileNameString() {
		return fileName;
	}
	
	public void setDeclarationList(ArrayList<Declaration> declarationList) {
		this.declarationList = declarationList;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	


}