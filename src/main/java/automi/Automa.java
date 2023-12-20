package automi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Automa {
	 private Map<State, Map<String, State>> transitionMap;
	 private ArrayList<State> q;
	
	public Automa() {
		transitionMap= new HashMap<State, Map<String, State>>();
		q = new ArrayList<>();
	}
	public Map<State, Map<String, State>> getTransitionMap() {
		return transitionMap;
	}
	public void setTransitionMap(Map<State, Map<String, State>> transitionMap) {
		this.transitionMap = transitionMap;
	}
	public ArrayList<State> getStates() {
		return q;
	}
	public void setStates(ArrayList<State> q) {
		this.q = q;
	}
	 
	 
}

