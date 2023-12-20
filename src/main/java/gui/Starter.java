package gui;

import javax.swing.JFrame;

public class Starter {

	public static void main(String[] args) {
		JFrame frame = new HammingMenu();
		frame.setLocation(550,300);
		frame.setSize(400,400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		frame.setVisible(true);
		

	}

}
