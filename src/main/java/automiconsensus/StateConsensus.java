package automiconsensus;

import java.util.HashMap;
import java.util.Map;

public class StateConsensus {
	private int level;
    private int error;
    
    private Map<String, StateConsensus> transitions;
    public boolean selftransition;

    public StateConsensus() {
    	 this.transitions = new HashMap<>();
    	 selftransition=false;
    }
    
    public StateConsensus(int level, int error) {
        this.level = level;
        this.error = error;
        this.transitions = new HashMap<>();
        selftransition=false;
    }

    public void setLevel(int l) {
    	level=l;
    }
    
    public void setErrorCount(int e) {
    	error=e;
    }
    
    public int getLevel() {
        return level;
    }

    public int getError() {
        return error;
    }

    public void addTransition(String string, StateConsensus next) {
        transitions.put(string, next);
    }

    public StateConsensus getNextState(String input) {
        return transitions.get(input);
    }
    
    public Map<String, StateConsensus> getMapTransition(){
    	return transitions;
    }

	public boolean isSelftransition() {
		return selftransition;
	}

	public void setSelftransition(boolean selftransition) {
		this.selftransition = selftransition;
	}
    
    
}
