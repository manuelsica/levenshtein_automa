package benchmark;

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

    // Costruttore privato, utilizzato da 'withMaxNumStates' per impostare la dimensione massima degli stati
    Utf8DFABuilder(int maxNumStates) {
        this.index = new HashMap<>();
        // Inizializza gli array con la dimensione corretta basata su maxNumStates
        this.distances = new Distance[maxNumStates * 4 + 3]; // Aggiustato per gestire gli stati intermedi
        this.transitions = new int[maxNumStates * 4 + 3][256]; // Aggiustato per gestire gli stati intermedi
        this.initialState = 0;
        this.numStates = 0;
        this.maxNumStates = maxNumStates;
    }

    // Metodo di fabbrica per creare un'istanza di Utf8DFABuilder con la dimensione massima degli stati specificata
    public static Utf8DFABuilder withMaxNumStates(int maxNumStates) {
        return new Utf8DFABuilder(maxNumStates);
    }

    // Metodo per allocare un nuovo stato e gestire il numero massimo di stati
    private int allocate() {
        if (numStates >= maxNumStates * 4 + 3) {
            throw new IllegalStateException("Numero massimo di stati superato");
        }
        int newState = numStates++;
        Arrays.fill(transitions[newState], DFA.SINK_STATE);
        distances[newState] = Distance.atLeast(255);
        return newState;
    }

    // Metodo per ottenere l'ID di uno stato esistente o allocarne uno nuovo
    private int getOrAllocate(int state) {
        return index.computeIfAbsent(state, k -> allocate());
    }

    // Imposta lo stato iniziale dell'automa
    public void setInitialState(int initialState) {
        this.initialState = getOrAllocate(initialState);
    }

    // Aggiunge uno stato all'automa con una determinata distanza e successore predefinito
    public Utf8DFAStateBuilder addState(int state, Distance distance, int defaultSuccessorOrig) {
        int stateId = getOrAllocate(state);
        distances[stateId] = distance;

        int defaultSuccessorId = getOrAllocate(defaultSuccessorOrig);
        int[] predecessorStates = new int[4];
        Arrays.fill(predecessorStates, defaultSuccessorId);

        // Crea gli stati intermedi per gestire la lunghezza variabile degli UTF-8
        for (int numBytes = 1; numBytes < 4; numBytes++) {
            int predecessorState = state * 4 + numBytes;
            int predecessorStateId = getOrAllocate(predecessorState);
            predecessorStates[numBytes] = predecessorStateId;
            Arrays.fill(transitions[predecessorStateId], predecessorStates[numBytes - 1]);
        }

        // Collega gli stati intermedi con i byte UTF-8
        for (int i = 0; i < 256; i++) {
            transitions[stateId][i] = predecessorStates[0]; // Semplificato a scopo esemplificativo
        }

        return new Utf8DFAStateBuilder(this, stateId);
    }

    // Aggiunge una transizione da uno stato a un altro per un determinato code point
    public void addTransition(int fromStateId, int codePoint, int toStateId) {
        // Semplificato a scopo esemplificativo
        transitions[fromStateId][codePoint] = toStateId;
    }

    // Costruisce l'automa a stati finiti deterministico (DFA)
    public DFA build() {
        return new DFA(transitions, distances, initialState);
    }

    @Override
    public String toString() {
        return "Utf8DFABuilder{" +
                "index=" + index +
                ", distances=" + Arrays.toString(distances) +
                ", transitions=" + Arrays.toString(transitions) +
                ", initialState=" + initialState +
                ", numStates=" + numStates +
                ", maxNumStates=" + maxNumStates +
                '}';
    }
}
