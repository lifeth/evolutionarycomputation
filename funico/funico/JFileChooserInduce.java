package funico;

import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import funico.interpreter.Program;
import unalcol.search.population.Population;

public class JFileChooserInduce extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1195783156850020688L;
	private JTextArea jareaExamples;
	private DefaultTableModel dtm;
	private JTable table;
	private JFileChooser filechooser = new JFileChooser();
	private int maxNumEq = 0, numTerm = 0;
	private InduceProgram induce;
	private ArrayList<Population<Program>> list;
	// private String newline = System.getProperty("line.separator");

	public JFileChooserInduce() {
		super("Inducing FUNICO Programs");

		JButton openButton = new JButton("Load Examples");
		openButton.addActionListener(this);

		JButton induceButton = new JButton("Induce Programs");
		induceButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(openButton);
		buttonPanel.add(induceButton);

		this.jareaExamples = new JTextArea(10, 50);
		this.jareaExamples.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane examplesScrollPane = new JScrollPane(jareaExamples);

		this.dtm = new DefaultTableModel(new String[] { "#", "Program", "Covering" }, 0);
		this.table = new JTable(this.dtm);
		this.table.setPreferredScrollableViewportSize(new Dimension(610, 400));
		JScrollPane programsScrollPane = new JScrollPane(this.table);

		JPanel jareasPanel = new JPanel();
		jareasPanel.add(examplesScrollPane, BorderLayout.NORTH);
		jareasPanel.add(programsScrollPane, BorderLayout.SOUTH);

		Container contentPane = getContentPane();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		contentPane.add(jareasPanel, BorderLayout.CENTER);

		this.induce = new InduceProgram();
		this.list = new ArrayList<Population<Program>>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals("Load Examples")) {
				int returnVal = filechooser.showOpenDialog(JFileChooserInduce.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					this.list.clear();
					this.dtm.setRowCount(0);
					File file = filechooser.getSelectedFile();
					BufferedReader in = new BufferedReader(new FileReader(file));
					String line;
					this.maxNumEq = (line = in.readLine()) != null ? Integer.parseInt(line) : 0;
					this.numTerm = (line = in.readLine()) != null ? Integer.parseInt(line) : 0;
					this.jareaExamples.read(in, null);
					in.close();

				} else {
					this.jareaExamples.setText("No examples were loaded.");
				}
			} else if (e.getActionCommand().equals("Induce Programs")) {

				this.dtm.setRowCount(0);
				this.list.clear();

				if (this.maxNumEq != 0 && this.numTerm != 0) {

					StringBuilder examples = new StringBuilder(this.jareaExamples.getText());

					this.induce.init(examples.toString(), this.maxNumEq, this.numTerm);
					boolean isCovered = false;

					while (!isCovered) {

						isCovered = this.induce.evolve();
						System.out.println(isCovered);

						this.list.add(0, InduceProgram.inducedPrograms);

						if (isCovered) {

							for (Population<Program> population : list) {
								for (int j = 0; j < population.size(); j++) {
									this.dtm.addRow(new Object[] { this.table.getRowCount() + 1,
											population.get(j).object().toString(),
											population.get(j).info(InduceProgram.gName) });
								}
							}

							InduceProgram.inducedPrograms = null;
						}
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String s[]) {
		JFrame frame = new JFileChooserInduce();

		WindowListener l = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);

		frame.pack();
		frame.setVisible(true);
		frame.setSize(700, 700);
	}
}