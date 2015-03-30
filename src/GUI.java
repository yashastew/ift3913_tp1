import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI extends JFrame {
	private static final int FRAME_HEIGHT = 430;
	private static final int FRAME_WIDTH = 650;
	

	private int selectedClass = 0;
	private int selectedAsso = 0;

	private TextParser tp;

	private JButton chargerButton;
	private JButton metricsButton;
	private JTextField fileToLoadField;
	private JList<String> classesList;
	private JList<String> methodesList;
	private JList<String> attributesList;
	private JList<String> sousClassesList;
	private JList<String> associationsList;
	private JList<String> detailsList;
	private JList<String> metricsList;

	private DefaultListModel<String> classes = new DefaultListModel<String>();
	private DefaultListModel<String> methodes = new DefaultListModel<String>();
	private DefaultListModel<String> attributes = new DefaultListModel<String>();
	private DefaultListModel<String> sousClasses = new DefaultListModel<String>();
	private DefaultListModel<String> associations = new DefaultListModel<String>();
	private DefaultListModel<String> details = new DefaultListModel<String>();
	private DefaultListModel<String> metrics= new DefaultListModel<String>();

	public GUI(TextParser tp) {
		this.tp = tp;
		setClassNames();
		createComponents();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	private String makeMetricsFile(){
		String bigString="CLASS, ANA, NOM, NOA, ITC, ETC, CAC, NOC, NOD, DIT, CLD \n";
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof ClassDec) {
				bigString += tp.makeMetricLine(((ClassDec)d).getIdentifier())+"\n";
			}
		}
		return bigString;
	}
	
	// initialise les noms des classes
	private void setClassNames() {
		classes.clear();
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof ClassDec) {
				classes.addElement(((ClassDec) d).getIdentifier());
			}
		}
	}
	private void updateMetrics(){
		metrics.clear();
		String[] tempMetrics= tp.getMetricsArray(classes.getElementAt(selectedClass));
		for(int i=0; i<tempMetrics.length; i++){
			metrics.addElement(tempMetrics[i]);
		}
		
	}
	private void clearDetails() {
		details.clear();
	}

	// Met a jour les details
	private void updateDetails() {
		details.clear();
		String temp = associations.getElementAt(selectedAsso);
		temp = temp.substring(1, 2);// should be A or B
		String rest = associations.getElementAt(selectedAsso).substring(4);
		if (temp.equals("R"))
			updateAsDetails(rest);
		else if (temp.equals("A"))
			updateAgDetails(rest.substring(1));
	}

	private void updateAgDetails(String ag) {
		String[] temp = ag.split("_");
		for (Iterator<Declaration> m = tp.getDeclarationList().iterator(); m
				.hasNext();) {
			Declaration d = m.next();
			if (d instanceof Aggregation) {
				Aggregation a = (Aggregation) d;

				if (a.getContainer().getClassRole().getIdentifier()
						.equals(classes.getElementAt(selectedClass))) {
					boolean containsAll = true;

					for (Iterator<Role> rl = a.getParts().iterator(); rl
							.hasNext();) {
						String st = rl.next().getClassRole().getIdentifier();
						boolean contains = false;
						for (int z = 0; z < temp.length; z++) {
							if (st.equals(temp[z])) {
								contains = true;
							}
						}
						if (!contains) {
							containsAll = false;
						}
					}

					if (containsAll) {
						details.addElement("AGGREGATION");
						details.addElement("    CONTAINER");
						details.addElement("        CLASS "
								+ a.getContainer().getClassRole()
										.getIdentifier()
								+ a.getContainer().getMultiplicity().toString());
						details.addElement("    PARTS");
						for (Role r : ((Aggregation) d).getParts()) {
							details.addElement("        CLASS"
									+ (r.getClassRole().getIdentifier()));
						}

					}
				}
			}
		}
	}

	private void updateAsDetails(String as) {
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof Association) {
				if (((Association) d).getIdentifier().equals(as)) {
					details.addElement("RELATION " + as);
					details.addElement("    ROLES");
					for (Role r : ((Association) d).getRoles()) {
						details.addElement("        CLASS "
								+ r.getClassRole().getIdentifier() + " "
								+ r.getMultiplicity().toString());
					}
				}
			}
		}

	}

	// met a jour les associations
	private void updateAsso() {
		associations.clear();
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof Association) {
				for (Role r : ((Association) d).getRoles()) {
					if ((r.equals(classes.get(selectedClass)))) {
						associations.addElement("(R) "
								+ ((Association) d).getIdentifier());
					}
				}
			}
		}
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof Aggregation) {
				if ((((Aggregation) d).getContainer().equals(classes
						.get(selectedClass)))) {
					String temp = "(A) P";
					for (Role r : ((Aggregation) d).getParts()) {
						temp += "_" + r.getClassRole().getIdentifier();
					}
					associations.addElement(temp);
				}
			}
		}
	}

	// Met a jour les sous classes
	private void updateSubClasses() {
		sousClasses.clear();
		for (Declaration g : tp.getDeclarationList()) {
			if (g instanceof Generalization) {
				if (((Generalization) g).getParentClass().getIdentifier()
						.equals(classes.get(selectedClass))) {
					for (ClassDec cd : ((Generalization) g).getSubClasses()) {
						sousClasses.addElement(cd.getIdentifier());
					}
				}
			}
		}
	}

	// met a jour les attributs
	private void updateAttributes() {
		attributes.clear();
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof ClassDec) {
				if (((ClassDec) d).getIdentifier().equals(
						classes.get(selectedClass))) {
					for (DataItem o : ((ClassDec) d).getAttributes()) {
						attributes.addElement(o.getType() + " "
								+ o.getIdentifier());
					}
				}
			}
		}
	}

	// met a jour les methodes
	private void updateMethodes() {
		methodes.clear();

		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof ClassDec) {
				if (((ClassDec) d).getIdentifier().equals(
						classes.get(selectedClass))) {
					for (Operation o : ((ClassDec) d).getOperations()) {
						String elem = "";
						elem = o.getType() + " " + o.getIdentifier() + "(";
						for (DataItem di : o.getArgs()) {
							elem += di.getType() + ", ";
						}
						if (!o.getArgs().isEmpty())
							elem = elem.substring(0, elem.length() - 2);
						elem += ")";
						methodes.addElement(elem);
					}

				}
			}
		}

	}
	//cree et met les valeurs initiales 
	private void createComponents() {
		chargerButton = new JButton("Charger");
		metricsButton= new JButton("Calculer métriques" );
		fileToLoadField = new JTextField(10);
		fileToLoadField.setText("Ligue.ucd");
		final JFileChooser fc = new JFileChooser();

		chargerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if(actionEvent.getSource()== chargerButton){
				int returnVal=fc.showOpenDialog(GUI.this);
				if(returnVal ==JFileChooser.APPROVE_OPTION){
				tp = new TextParser();
				tp.setFileName(fc.getSelectedFile().toString());
				fileToLoadField.setText(fc.getSelectedFile().toString());
				try {
					tp.parseFile();
					setClassNames();
				} catch (IOException e) {
					System.out.println("Could not read file");
					e.printStackTrace();
				}
				}
				}

			}
		});
		
		metricsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent){

				if(actionEvent.getSource() ==metricsButton){
					int returnVal= fc.showSaveDialog(GUI.this);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//tp = new TextParser();
				//tp.setFileName(fc.getSelectedFile().toString());
					if(returnVal ==JFileChooser.APPROVE_OPTION){
				Path file= Paths.get(fc.getSelectedFile().toString());
				//Path file = "\metrics.txt";
				Charset charset = Charset.forName("UTF-8");
				String s = makeMetricsFile();
				try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
				    writer.write(s, 0, s.length());
				} catch (IOException x) {
				    System.err.format("IOException: %s%n", x);
				}
					}
			}
			}
		});
		//initilise les JLists
		classesList = new JList<String>(classes);
		methodesList = new JList<String>(methodes);
		attributesList = new JList<String>(attributes);
		sousClassesList = new JList<String>(sousClasses);
		associationsList = new JList<String>(associations);
		detailsList = new JList<String>(details);
		metricsList= new JList<String>(metrics);

		setVisiblePanelSize();
		createFChooserPanel();



		//set Selection modes
		classesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		classesList.getSelectionModel().addListSelectionListener(
				new classListDataListener());

		associationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		associationsList.getSelectionModel().addListSelectionListener(
				new assoListDataListener());

		// Empeche lùtilisateur de selectionner des elements
		//malheuresement ils sont girs pale mais je crois c'est
		//un solution acceptable 
		detailsList.setEnabled(false);
		methodesList.setEnabled(false);
		sousClassesList.setEnabled(false);
		attributesList.setEnabled(false);
		metricsList.setEnabled(false);

		JPanel metricPanel= new JPanel();
		JScrollPane jspmt= new JScrollPane(metricsList);
		metricPanel.add(jspmt);
		metricPanel.setBorder(new TitledBorder("Métriques"));
		add(metricPanel, BorderLayout.EAST);
		
		
		JPanel classePanel = new JPanel();
		JScrollPane jspc = new JScrollPane(classesList);
		classePanel.add(jspc);
		classePanel.setBorder(new TitledBorder("Classes"));
		add(classePanel, BorderLayout.WEST);

		JPanel square = new JPanel();
		square.setLayout(new GridLayout(2, 2));
		JPanel rectangle = new JPanel();
		rectangle.setLayout(new BorderLayout());

		JPanel attributesPanel = new JPanel();
		JScrollPane jspat = new JScrollPane(attributesList);
		attributesPanel.add(jspat);
		attributesPanel.setBorder(new TitledBorder("Attributs"));
		square.add(attributesPanel);

		JPanel methodesPanel = new JPanel();
		JScrollPane jspm = new JScrollPane(methodesList);
		methodesPanel.add(jspm);
		methodesPanel.setBorder(new TitledBorder("Méthodes"));
		square.add(methodesPanel);

		JPanel sousClassesPanel = new JPanel();
		JScrollPane jspsp = new JScrollPane(sousClassesList);
		sousClassesPanel.add(jspsp);
		sousClassesPanel.setBorder(new TitledBorder("Sous-Classes"));
		square.add(sousClassesPanel);

		JPanel associationsPanel = new JPanel();
		JScrollPane jspas = new JScrollPane(associationsList);
		associationsPanel.add(jspas);
		associationsPanel.setBorder(new TitledBorder("Associations/agrégations"));
		square.add(associationsPanel);

		JPanel detailsPanel = new JPanel();
		JScrollPane jsp = new JScrollPane(detailsList);
		detailsPanel.add(jsp);
		detailsPanel.setBorder(new TitledBorder("Détails"));
		rectangle.add(detailsPanel, BorderLayout.SOUTH);
		rectangle.add(square, BorderLayout.CENTER);
		add(rectangle, BorderLayout.CENTER);

	}
	private void createFChooserPanel() {
		JPanel chooseFPanel = new JPanel();
		chooseFPanel.add(chargerButton);
		chooseFPanel.add(fileToLoadField);
		chooseFPanel.add(metricsButton);
		add(chooseFPanel, BorderLayout.NORTH);
		
	}

	//ajuste la grandeur des JLists
	private void setVisiblePanelSize() {
				metricsList.setVisibleRowCount(17);
				metricsList.setFixedCellWidth(100);
				metricsList.setFixedCellHeight(18);
				classesList.setVisibleRowCount(17);
				classesList.setFixedCellWidth(100);
				classesList.setFixedCellHeight(18);
				methodesList.setVisibleRowCount(4);
				methodesList.setFixedCellWidth(160);
				methodesList.setFixedCellHeight(18);
				attributesList.setVisibleRowCount(4);
				attributesList.setFixedCellWidth(160);
				attributesList.setFixedCellHeight(18);
				sousClassesList.setVisibleRowCount(4);
				sousClassesList.setFixedCellWidth(160);
				sousClassesList.setFixedCellHeight(18);
				associationsList.setVisibleRowCount(4);
				associationsList.setFixedCellWidth(160);
				associationsList.setFixedCellHeight(18);
				detailsList.setVisibleRowCount(5);
				detailsList.setFixedCellWidth(320);
				detailsList.setFixedCellHeight(18);
		
	}

	class classListDataListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			}
			else if (!e.getValueIsAdjusting()) {
				selectedClass = lsm.getMinSelectionIndex();
				updateMethodes();
				updateAttributes();
				updateSubClasses();
				updateAsso();
				updateMetrics();
				clearDetails();
			}
		}

	}

	class assoListDataListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			} else if (!e.getValueIsAdjusting()) {
				selectedAsso = lsm.getMinSelectionIndex();
				updateDetails();
			}

		}

	}

}
