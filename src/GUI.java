import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI extends JFrame {
	private static final int FRAME_HEIGHT = 400;
	private static final int FRAME_WIDTH = 400;

	private int selectedClass = 0;
	private int selectedAsso = 0;

	private TextParser tp;

	private JButton chargerButton;
	private JTextField fileToLoadField;
	private JList<String> classesList;
	private JList<String> methodesList;
	private JList<String> attributesList;
	private JList<String> sousClassesList;
	private JList<String> associationsList;
	private JList<String> detailsList;

	private DefaultListModel<String> classes = new DefaultListModel<String>();
	private DefaultListModel<String> methodes = new DefaultListModel<String>();
	private DefaultListModel<String> attributes = new DefaultListModel<String>();
	private DefaultListModel<String> sousClasses = new DefaultListModel<String>();
	private DefaultListModel<String> associations = new DefaultListModel<String>();
	private DefaultListModel<String> details = new DefaultListModel<String>();

	public GUI(TextParser tp) {
		this.tp = tp;
		// initLists(tp);
		getClassNames();
		createComponents();
		setSize(FRAME_HEIGHT, FRAME_WIDTH);
	}

	// should really have been called SET clasNames
	private void getClassNames() {
		classes.clear();
		for (Declaration d : tp.getDeclarationList()) {
			if (d instanceof ClassDec) {
				classes.addElement(((ClassDec) d).getIdentifier());
			}
		}
	}

	private void clearDetails() {
		details.clear();
	}

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
						details.addElement("CONTAINER");
						details.addElement("CLASS "
								+ a.getContainer().getClassRole()
										.getIdentifier()
								+ a.getContainer().getMultiplicity().toString());
						details.addElement("PARTS");
						for (Role r : ((Aggregation) d).getParts()) {
							details.addElement("CLASS"
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
					details.addElement("ROLES");
					for (Role r : ((Association) d).getRoles()) {
						details.addElement("CLASS "
								+ r.getClassRole().getIdentifier() + " "
								+ r.getMultiplicity().toString());
					}
				}
			}
		}

	}

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
							elem += di.getIdentifier() + ", ";
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

	private void createComponents() {
		chargerButton = new JButton("charger");
		fileToLoadField = new JTextField("Ligue.ucd");
		final JFileChooser fc = new JFileChooser();

		chargerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				fc.showOpenDialog(GUI.this);
				tp = new TextParser();// is this OK...i assume its Java so no
										// worrries about the old tp
				tp.setFileName(fc.getSelectedFile().toString());
				fileToLoadField.setText(fc.getSelectedFile().toString());
				try {
					tp.parseFile();
					getClassNames();
				} catch (IOException e) {
					System.out.println("Could not read file");
					e.printStackTrace();
				}

			}
		});

		classesList = new JList<String>(classes);
		methodesList = new JList<String>(methodes);
		attributesList = new JList<String>(attributes);
		sousClassesList = new JList<String>(sousClasses);
		associationsList = new JList<String>(associations);
		detailsList = new JList<String>(details);

		classesList.setVisibleRowCount(15);
		methodesList.setVisibleRowCount(7);
		attributesList.setVisibleRowCount(7);
		sousClassesList.setVisibleRowCount(7);
		associationsList.setVisibleRowCount(7);
		detailsList.setVisibleRowCount(5);

		JPanel chooseFPanel = new JPanel();
		chooseFPanel.add(chargerButton);
		chooseFPanel.add(fileToLoadField);
		add(chooseFPanel, BorderLayout.NORTH);

		JPanel classePanel = new JPanel();
		classePanel.add(classesList);

		classesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		classesList.getSelectionModel().addListSelectionListener(
				new classListDataListener());

		associationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		associationsList.getSelectionModel().addListSelectionListener(
				new assoListDataListener());

		// Make these guys non-selectable
		// not doing anything
		detailsList.getSelectionModel().setSelectionInterval(-1, -1);
		methodesList.getSelectionModel().setSelectionInterval(-1, -1);
		sousClassesList.getSelectionModel().setSelectionInterval(-1, -1);
		attributesList.getSelectionModel().setSelectionInterval(-1, -1);

		classePanel.setBorder(new TitledBorder("Classes"));
		add(classePanel, BorderLayout.WEST);

		JPanel square = new JPanel();
		square.setLayout(new GridLayout(2, 2));
		JPanel rectangle = new JPanel();
		rectangle.setLayout(new BorderLayout());

		JPanel attributesPanel = new JPanel();
		attributesPanel.add(attributesList);
		attributesPanel.setBorder(new TitledBorder("Attributs"));
		square.add(attributesPanel);

		JPanel methodesPanel = new JPanel();
		methodesPanel.add(methodesList);
		methodesPanel.setBorder(new TitledBorder("Methodes"));
		square.add(methodesPanel);

		JPanel sousClassesPanel = new JPanel();
		sousClassesPanel.add(sousClassesList);
		sousClassesPanel.setBorder(new TitledBorder("Sous-Classes"));
		square.add(sousClassesPanel);

		JPanel associationsPanel = new JPanel();
		associationsPanel.add(associationsList);
		associationsPanel.setBorder(new TitledBorder("Associations"));
		square.add(associationsPanel);

		JPanel detailsPanel = new JPanel();
		JScrollPane jsp = new JScrollPane(detailsList);
		detailsPanel.add(jsp);
		detailsPanel.setBorder(new TitledBorder("Details"));
		rectangle.add(detailsPanel, BorderLayout.SOUTH);
		rectangle.add(square, BorderLayout.CENTER);
		add(rectangle, BorderLayout.CENTER);

	}

	class classListDataListener implements ListSelectionListener {

		@Override
		// so right not the clear selection is a hacky work around but it works
		// its calling everything twice but at least the final one ( that you
		// see in the
		// GUI is correct
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			}// do nothing or there may be a meltdown
			else if (!e.getValueIsAdjusting()) {
				// selectedClass=e.getFirstIndex();
				selectedClass = lsm.getMinSelectionIndex();
				updateMethodes();
				updateAttributes();
				updateSubClasses();
				updateAsso();
				clearDetails();
				// classesList.clearSelection();
			}
			// updateAtributs();

		}

	}

	class assoListDataListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			} else if (!e.getValueIsAdjusting()) {
				// selectedAsso=e.getFirstIndex();
				selectedAsso = lsm.getMinSelectionIndex();
				updateDetails();
				// associationsList.clearSelection();
			}
			// updateAtributs();

		}
		/*
		 * public void valueChanged(ListSelectionEvent e) { ListSelectionModel
		 * lsm = (ListSelectionModel)e.getSource();
		 * 
		 * int firstIndex = e.getFirstIndex(); int lastIndex = e.getLastIndex();
		 * boolean isAdjusting = e.getValueIsAdjusting();
		 * output.append("Event for indexes " + firstIndex + " - " + lastIndex +
		 * "; isAdjusting is " + isAdjusting + "; selected indexes:");
		 * 
		 * if (lsm.isSelectionEmpty()) { output.append(" <none>"); } else { //
		 * Find out which indexes are selected. int minIndex =
		 * lsm.getMinSelectionIndex(); int maxIndex =
		 * lsm.getMaxSelectionIndex(); for (int i = minIndex; i <= maxIndex;
		 * i++) { if (lsm.isSelectedIndex(i)) { output.append(" " + i); } } }
		 * output.append(newline); }
		 */

	}

}
