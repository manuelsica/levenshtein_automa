package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import automiconsensus.AutomaConsensus;
import automiconsensus.StateConsensus;


public class HammingViewer extends JFrame {
	private AutomaConsensusPanel panel;
	private JPanel info;
	
	public HammingViewer(AutomaConsensus a, String str, ArrayList<Character> alf, int d) {
	
	panel = new AutomaConsensusPanel(a);
    info = new JPanel();
    JLabel info_stringhe = new JLabel("Stringhe dell'automa: " + str);
    JLabel info_alf = new JLabel("Alfabeto: " + alf);
    JLabel info_d = new JLabel("Distanza massima: " + d);
    info.setLayout(new GridLayout(3,1));
    info.add(info_stringhe);
    info.add(info_alf);
    info.add(info_d);
    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    add(info, BorderLayout.NORTH);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
	}
	
	private class AutomaConsensusPanel extends JPanel {
		
		private AutomaConsensus automa;
		private Map<StateConsensus, Point> statePositions;
        private Map<StateConsensus, Set<String>> transitionLabels = new HashMap<>();
		
		public AutomaConsensusPanel(AutomaConsensus automa) {
		    this.automa = automa;
		    statePositions = new HashMap<>();
		    calculatePositions();
		}
		
		private void calculatePositions() {
		    int xSpacing = 150;
		    int ySpacing = 150;

		    int x = xSpacing;
		    int y = ySpacing;

		   
		    int maxStatesPerLevel = 0;
		    int maxLevel = 0;

		    for (StateConsensus state : automa.getStates()) {
		        maxStatesPerLevel = Math.max(maxStatesPerLevel, state.getLevel());
		        maxLevel = Math.max(maxLevel, state.getError());
		    }

		   
		    for (int level = maxLevel; level >= 0; level--) { 
		        ArrayList<StateConsensus> statesAtLevel = new ArrayList<>();
		        for (StateConsensus s : automa.getStates()) {
		            if (s.getError() == level) {
		                statesAtLevel.add(s);
		            }
		        }

		      
		        int xLevel = xSpacing;

		        for (StateConsensus state : statesAtLevel) {
		            statePositions.put(state, new Point(xLevel, y));
		            y += ySpacing;
		        }

		        y = ySpacing; 
		        xSpacing += xSpacing; 
		    }
		}

		protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int maxStatesPerLevel = 0;
		    int maxLevel = 0;
            
            for (StateConsensus state : automa.getStates()) {
		        maxStatesPerLevel = Math.max(maxStatesPerLevel, state.getLevel());
		        maxLevel = Math.max(maxLevel, state.getError());
		    }
            
            for (StateConsensus currentState : automa.getStates()) {
                Point currentStatePosition = statePositions.get(currentState);
                for (Entry<String, StateConsensus> transition : currentState.getMapTransition().entrySet()) {
                	StateConsensus nextState = transition.getValue();
                    Point nextStatePosition = statePositions.get(nextState);

                   
                    int startX = currentStatePosition.x + 25;
                    int startY = currentStatePosition.y + 25;
                    int targetX = nextStatePosition.x + 25;
                    int targetY = nextStatePosition.y + 25;

                    
                    g.setColor(Color.BLACK);
                    g.drawLine(startX, startY, targetX, targetY);

                   
                    String transitionLabel = transition.getKey();
                    int labelX = (startX + targetX) / 2  +13;
                    int labelY = (startY + targetY) / 2 + 30;

                    g.setColor(Color.BLACK);
                    g.drawString(transitionLabel, labelX+2, labelY+2);
                }

               
                if(currentState.getLevel()==maxLevel+1) {
                	g.setColor(Color.GREEN);
                }else { 
                	g.setColor(Color.RED);
                }
                g.fillOval(currentStatePosition.x, currentStatePosition.y, 50, 50);
                g.setColor(Color.BLACK);
                g.drawOval(currentStatePosition.x, currentStatePosition.y, 50, 50);
            }
        }


	}
}