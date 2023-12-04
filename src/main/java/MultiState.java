import java.util.ArrayList;
import java.util.List;

public class MultiState {
    private List<NFAState> states;

    public MultiState() {
        this.states = new ArrayList<>();
    }

    public NFAState[] getStates() {
        return states.toArray(new NFAState[0]);
    }

    public void clear() {
        states.clear();
    }

    public void addState(NFAState newState) {
        if (states.stream().anyMatch(state -> state.imply(newState))) {
            return;
        }

        states.removeIf(state -> newState.imply(state));

        states.add(newState);
    }

    public static MultiState empty() {
        return new MultiState();
    }

    public int normalize() {
        int minOffset = states.stream()
                .mapToInt(state -> state.getOffset())
                .min()
                .orElse(0);

        states.forEach(state -> state.offset -= minOffset);
        states.sort(NFAState::compareTo);

        return minOffset;
    }
}
