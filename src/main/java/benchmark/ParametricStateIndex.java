package benchmark;// benchmark.ParametricStateIndex.java
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class ParametricStateIndex {
    private final List<OptionalInt> stateIndex;
    private final List<ParametricState> stateQueue;
    private final int numOffsets;

    // Costruttore che inizializza l'indice degli stati parametrici
    public ParametricStateIndex(int queryLen, int numParamStates) {
        this.numOffsets = queryLen + 1;
        int maxNumStates = numParamStates * numOffsets;

        // Inizializzazione della lista di OptionalInt per l'indice degli stati
        this.stateIndex = new ArrayList<>(maxNumStates);
        for (int i = 0; i < maxNumStates; i++) {
            this.stateIndex.add(OptionalInt.empty());
        }

        // Inizializzazione della lista di benchmark.ParametricState per la coda degli stati
        this.stateQueue = new ArrayList<>(100);
    }

    // Restituisce il numero di stati presenti nella coda
    public int numStates() {
        return stateQueue.size();
    }

    // Restituisce il massimo numero di stati che possono essere allocati
    public int maxNumStates() {
        return stateIndex.size();
    }

    // Alloca uno stato parametrico nell'indice (o restituisce l'ID dello stato se già allocato)
    public int getOrAllocate(ParametricState parametricState) {
        int bucket = parametricState.getShapeId() * numOffsets + parametricState.getOffset();
        OptionalInt stateIdOpt = stateIndex.get(bucket);

        // Se lo stato è già stato allocato, restituisci il suo ID
        if (stateIdOpt.isPresent()) {
            return stateIdOpt.getAsInt();
        }

        // Altrimenti, alloca uno stato nella coda e aggiorna l'indice
        int newState = stateQueue.size();
        stateQueue.add(parametricState);
        stateIndex.set(bucket, OptionalInt.of(newState));
        return newState;
    }

    // Restituisce lo stato parametrico corrispondente a un dato ID
    public ParametricState get(int stateId) {
        return stateQueue.get(stateId);
    }

    @Override
    public String toString() {
        return "benchmark.ParametricStateIndex{" +
                "stateIndex=" + stateIndex +
                ", stateQueue=" + stateQueue +
                ", numOffsets=" + numOffsets +
                '}';
    }
}
