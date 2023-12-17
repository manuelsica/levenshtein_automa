package benchmark;

// La classe rappresenta uno stato di un automa a stati finiti non deterministico (NFA).
public class NFAState implements Comparable<NFAState> {
    int offset;           // Offset dello stato.
    private final int distance;   // Distanza associata allo stato.
    private final boolean inTranspose;  // Flag che indica se lo stato è nella trasposizione.

    // Costruttore che inizializza uno stato con offset, distanza e flag di trasposizione.
    public NFAState(int offset, int distance, boolean inTranspose) {
        this.offset = offset;
        this.distance = distance;
        this.inTranspose = inTranspose;
    }

    // Verifica se lo stato corrente implica l'altro stato passato come argomento.
    public boolean imply(NFAState other) {
        // Calcola se la trasposizione implica l'altro stato o viceversa.
        boolean transposeImplies = this.inTranspose || !other.inTranspose;
        int deltaOffset = Math.abs(this.offset - other.offset);

        // Verifica se l'altro stato è implicato dallo stato corrente.
        if (transposeImplies) {
            return other.distance >= this.distance + deltaOffset;
        } else {
            return other.distance > this.distance + deltaOffset;
        }
    }

    // Implementazione del metodo compareTo per consentire il confronto tra stati per ordinamento.
    @Override
    public int compareTo(NFAState other) {
        if (this.offset == other.offset) {
            if (this.distance == other.distance) {
                return Boolean.compare(this.inTranspose, other.inTranspose);
            }
            return Integer.compare(this.distance, other.distance);
        }
        return Integer.compare(this.offset, other.offset);
    }

    // Metodo getter per ottenere l'offset dello stato.
    public int getOffset() {
        return offset;
    }

    // Metodo getter per ottenere la distanza dello stato.
    public int getDistance() {
        return distance;
    }

    // Metodo getter per verificare se lo stato è nella trasposizione.
    public boolean isInTranspose() {
        return inTranspose;
    }

    @Override
    public String toString() {
        return "benchmark.NFAState{" +
                "offset=" + offset +
                ", distance=" + distance +
                ", inTranspose=" + inTranspose +
                '}';
    }
}
