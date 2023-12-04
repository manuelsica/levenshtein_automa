import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utf8DFABuilder {
    private Map<Integer, Integer> index;
    private Distance[] distances;
    private int[][] transitions;
    private int initialState;
    private int numStates;
    private int maxNumStates;

    Utf8DFABuilder(int maxNumStates) {
        this.index = new HashMap<>();
        // Inizializza gli array con la dimensione corretta basata su maxNumStates
        this.distances = new Distance[maxNumStates * 4 + 3]; // Aggiustato per gestire gli stati intermedi
        this.transitions = new int[maxNumStates * 4 + 3][256]; // Aggiustato per gestire gli stati intermedi
        this.initialState = 0;
        this.numStates = 0;
        this.maxNumStates = maxNumStates;
    }

    public static Utf8DFABuilder withMaxNumStates(int maxNumStates) {
        return new Utf8DFABuilder(maxNumStates);
    }

    private int allocate() {
        if (numStates >= maxNumStates * 4 + 3) {
            throw new IllegalStateException("Numero massimo di stati superato");
        }
        int newState = numStates++;
        Arrays.fill(transitions[newState], DFA.SINK_STATE);
        distances[newState] = Distance.atLeast(255);
        return newState;
    }

    private int getOrAllocate(int state) {
        return index.computeIfAbsent(state, k -> allocate());
    }

    public void setInitialState(int initialState) {
        this.initialState = getOrAllocate(initialState);
    }

    public Utf8DFAStateBuilder addState(int state, Distance distance, int defaultSuccessorOrig) {
        int stateId = getOrAllocate(state);
        distances[stateId] = distance;

        int defaultSuccessorId = getOrAllocate(defaultSuccessorOrig);
        int[] predecessorStates = new int[4];
        Arrays.fill(predecessorStates, defaultSuccessorId);

        for (int numBytes = 1; numBytes < 4; numBytes++) {
            int predecessorState = state * 4 + numBytes;
            int predecessorStateId = getOrAllocate(predecessorState);
            predecessorStates[numBytes] = predecessorStateId;
            Arrays.fill(transitions[predecessorStateId], predecessorStates[numBytes - 1]);
        }

        for (int i = 0; i < 256; i++) {
            transitions[stateId][i] = predecessorStates[0]; // Simplified for example purposes
        }

        return new Utf8DFAStateBuilder(this, stateId, predecessorStates);
    }

    public void addTransition(int fromStateId, int codePoint, int toStateId) {
        // Simplified for example purposes
        transitions[fromStateId][codePoint] = toStateId;
    }

    public DFA build() {
        return new DFA(transitions, distances, initialState);
    }
}
