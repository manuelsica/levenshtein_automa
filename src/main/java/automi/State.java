package automi;

import java.util.ArrayList;
import java.util.Objects;

public class State {
	private int level;
	private ArrayList<String> stringhe;
	private boolean self_transition;
	private boolean accept;
	
	public State(int level, ArrayList<String> stringhe) {
		this.level = level;
		this.stringhe = stringhe;
		self_transition= false;
		accept= false;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public ArrayList<String> getStringhe() {
		return stringhe;
	}
	public void setStringhe(ArrayList<String> stringhe) {
		this.stringhe = stringhe;
	}

	public boolean isAccept() {
		return accept;
	}
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	public boolean hasSelf_transition() {
		return self_transition;
	}
	
	public void setSelf_transition(boolean self_transition) {
		this.self_transition = self_transition;
	}	
	@Override
	public String toString() {
		return "State [livello=" + level + ", stringhe=" + stringhe + ", self-transition:" + self_transition  + " ,accettante=" + accept + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(accept, level, self_transition, stringhe);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		return accept == other.accept && level == other.level && self_transition == other.self_transition
				&& Objects.equals(stringhe, other.stringhe);
	}
	
	
}
