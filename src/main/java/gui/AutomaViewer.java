package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import automi.Automa;
import automi.State;

public class AutomaViewer extends JFrame {
    private AutomatonPanel panel;
    private JPanel info;
    

    public AutomaViewer(Automa automa, ArrayList<String> alf, ArrayList<String> str) {
        panel = new AutomatonPanel(automa);
        info = new JPanel();
        setResizable(true);
        JLabel info_stringhe = new JLabel("Stringhe dell'automa: " + str);
        JLabel info_alf = new JLabel("Alfabeto: " + alf);
        info.setLayout(new GridLayout(2,1));
        info.add(info_stringhe);
        info.add(info_alf);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(info, BorderLayout.NORTH);
        pack(); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private class AutomatonPanel extends JPanel {
        private Automa automa;
        private Map<State, Point> statePositions;
        private ArrayList<Point> punti = new ArrayList<Point>();

        public AutomatonPanel(Automa a) {
            automa = a;
            statePositions = new HashMap<>();
            calculatePositions();
        }

        private void calculatePositions() {
            int xSpacing = 150;
            int ySpacing = 150;
            int yOffset = 50;

            int x = xSpacing;
            int y = ySpacing;

            int maxLevel = 0;
            for (State state : automa.getStates()) {
                maxLevel = Math.max(maxLevel, state.getLevel());
            }


            int maxStatesPerLevel = 0;
            ArrayList<Integer> levelCounts = new ArrayList<Integer>();
            for (int level = 0; level <= maxLevel; level++) {
                int count = 0;
                for (State state : automa.getStates()) {
                    if (state.getLevel() == level) {
                        count++;
                    }
                }
                levelCounts.add(count);
                maxStatesPerLevel = Math.max(maxStatesPerLevel, count);
            }

            for (int level = 0; level <= maxLevel; level++) {
                ArrayList<State> statesAtLevel = new ArrayList<State>();
                for (State s : automa.getStates()) {
                    if (s.getLevel() == level) statesAtLevel.add(s);
                }

                for (State state : statesAtLevel) {
                    statePositions.put(state, new Point(x, y));
                    x += xSpacing;
                }

                x = xSpacing; 
                y += ySpacing + yOffset; 
            }
        }

     
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (State currentState : automa.getStates()) {
                Point currentStatePosition = statePositions.get(currentState);
                int count=0;
                for (Entry<String, State> transition : automa.getTransitionMap().get(currentState).entrySet()) {
                    State nextState = transition.getValue();
                    Point nextStatePosition = statePositions.get(nextState);


                    int startX = currentStatePosition.x + 25;
                    int startY = currentStatePosition.y + 25;
                    int targetX = nextStatePosition.x + 25;
                    int targetY = nextStatePosition.y + 25;


                    g.setColor(Color.BLACK);
                    g.drawLine(startX, startY, targetX, targetY);
                    

                    String transitionLabel = transition.getKey();
                      
                    int labelX = (startX + targetX)/2;
                    int labelY = (startY + targetY)/2+30;
                    g.setColor(Color.BLACK);
                    g.drawString(transitionLabel, labelX+count, labelY);
                    count+=7;
                }


                g.setColor(currentState.isAccept() ? Color.GREEN : Color.RED);
                g.fillOval(currentStatePosition.x, currentStatePosition.y, 50, 50);
                g.setColor(Color.BLACK);
                g.drawOval(currentStatePosition.x, currentStatePosition.y, 50, 50);
                g.drawString(currentState.getStringhe() + "", currentStatePosition.x + 20, currentStatePosition.y + 30);
            }
        }
    }

}
