package automiconsensus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutomaConsensus {
	private ArrayList<StateConsensus> states;
	 private Map<StateConsensus, Map<String, StateConsensus>> transitions;

    public AutomaConsensus() {
        this.states = new ArrayList<>();
       this.transitions = new HashMap<>();
    }

    public void addState(StateConsensus state) {
        states.add(state);
    }

    public StateConsensus getStateByLevelAndError(int level, int errorCount) {
        for (StateConsensus state : states) {
            if (state.getLevel() == level && state.getError() == errorCount) {
                return state;
            }
        }
        return null;
    }

    public ArrayList<StateConsensus> getStates() {
        return states;
    }
   
    public void addTransition(StateConsensus st, String string, StateConsensus next) {
    	st.addTransition(string, next);
    }

    public Map<StateConsensus, Map<String, StateConsensus>> getTransitionMap(){
    	return transitions;
    }
    
    public void addTransitionLabel(StateConsensus state, String label) {
        transitionsLabels.put(state, label);
    }

    public String getTransitionLabel(StateConsensus state) {
        return transitionsLabels.get(state);
    }

    private Map<StateConsensus, String> transitionsLabels = new HashMap<>();

}
