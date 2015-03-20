import java.io.IOException;

import javax.swing.JFrame;



public class main {
	
	//test git
	
	private static String fileName = "textfiles/Ligue.ucd";
	private static TextParser textParser;
	
	public static void main(String [] args) {
		
		
		
		textParser = new TextParser();
		textParser.setFileName(fileName);
		try {
			textParser.parseFile();
		} catch (IOException e) {
			System.out.println("Could not read file");
			e.printStackTrace();
		}
		
		JFrame frame = new GUI(textParser);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("UML viewer");
		frame.setVisible(true);

	}

}
