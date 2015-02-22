import java.io.IOException;



public class main {
	
	private static String fileName = "textfiles/Ligue.ucd";
	private static TextParser textParser;
	
	public static void main(String[] args) {
		
		
		
		textParser = new TextParser();
		textParser.setFileName(fileName);
		try {
			textParser.parseFile();
		} catch (IOException e) {
			System.out.println("Could not read file");
			e.printStackTrace();
		}

	}

}
