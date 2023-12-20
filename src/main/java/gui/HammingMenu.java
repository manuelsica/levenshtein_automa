package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import automiconsensus.AutomaConsensus;
import operazioni.SimpleNFAHamming;

public class HammingMenu extends JFrame{

	private JPanel panel;
	private JTextField text;
	private JButton generate;
	private JFrame viewer;
	private JLabel label;
	private String alph;
	private ArrayList<Character> alfabeto;
	private String str;
	private ArrayList<String> stringhe;
	private JLabel label1;
	private JTextField alf; 
	private JLabel label2;
	private JTextField dmax; 
	private ActionListener genlist;
	private AutomaConsensus a;
	private int d;
	
	public HammingMenu() {
		label1= new JLabel("Inserire alfabeto seguendo il pattern abc: ");
		alf= new JTextField(1);
		text= new JTextField();
		label= new JLabel("Inserire stringa: ");
		label2= new JLabel("Inserire distanza di hamming massima: ");
		dmax= new JTextField(1);
		
		class GenButton implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				str = text.getText();

				alph = alf.getText();
				alfabeto = new ArrayList<Character>();
				for(int i=0; i<alph.length(); i++) {
					alfabeto.add(alph.charAt(i));
				}
				
				d = Integer.parseInt(dmax.getText());
				SimpleNFAHamming prova = new SimpleNFAHamming();
				a= prova.compute(str, d, alfabeto);
				createViewer(a, str, alfabeto, d);
				
			}
			
		}
		genlist = new GenButton();
		generate= new JButton("GENERA");
		generate.addActionListener(genlist);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(5,2));
		panel.add(label1);
		panel.add(alf);
		panel.add(label);
		panel.add(text);
		panel.add(label2);
		panel.add(dmax);
		
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(1,3));
		p1.add(generate);
		
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(p1, BorderLayout.SOUTH);

	}
	
	public void createViewer(AutomaConsensus a, String str, ArrayList<Character> alf, int d) {
		viewer= new HammingViewer(a, str, alf, d);
		viewer.setSize(600,500);
		viewer.setLocation(600,200);
		viewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		viewer.setVisible(true);
	}
}
