import java.util.ArrayList;
import java.util.List;

// La classe rappresenta un insieme di stati di un automa a stati finiti non deterministico (NFA).
public class MultiState {
    private List<NFAState> states;  // Lista degli stati nell'insieme.

    // Costruttore che inizializza la lista degli stati.
    public MultiState() {
        this.states = new ArrayList<>();
    }

    // Restituisce un array contenente gli stati nell'insieme.
    public NFAState[] getStates() {
        return states.toArray(new NFAState[0]);
    }

    // Cancella tutti gli stati nell'insieme.
    public void clear() {
        states.clear();
    }

    // Aggiunge uno stato all'insieme, assicurandosi che non ci siano stati duplicati o implicati.
    public void addState(NFAState newState) {
        if (states.stream().anyMatch(state -> state.imply(newState))) {
            return;
        }

        states.removeIf(state -> newState.imply(state));

        states.add(newState);
    }

    // Restituisce un nuovo oggetto MultiState vuoto.
    public static MultiState empty() {
        return new MultiState();
    }

    // Normalizza gli stati nell'insieme, aggiustando gli offset e ordinandoli.
    public int normalize() {
        // Trova il valore minimo dell'offset tra gli stati.
        int minOffset = states.stream()
                .mapToInt(state -> state.getOffset())
                .min()
                .orElse(0);

        // Sottrae il valore minimo a tutti gli offset degli stati.
        states.forEach(state -> state.offset -= minOffset);

        // Ordina gli stati nell'insieme.
        states.sort(NFAState::compareTo);

        return minOffset;
    }
}
