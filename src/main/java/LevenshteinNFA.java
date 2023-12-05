import java.util.Arrays;

public class LevenshteinNFA {
    private final int maxDistance;   // Massima distanza di Levenshtein consentita
    private final boolean damerau;   // Indica se il calcolo della distanza deve includere le trasposizioni di caratteri

    // Costruttore che inizializza l'NFA di Levenshtein con la massima distanza e l'opzione Damerau
    public LevenshteinNFA(int maxDistance, boolean damerau) {
        this.maxDistance = maxDistance;
        this.damerau = damerau;
    }

    // Calcola la distanza minima tra uno stato multistato e una lunghezza di query specificati
    public Distance multistateDistance(MultiState multistate, int queryLen) {
        return Arrays.stream(multistate.getStates())
                .map(state -> Distance.exact(state.getDistance() + Math.abs(queryLen - state.getOffset())))
                .filter(d -> d.getDistance() <= maxDistance)
                .min(Distance::compareTo)
                .orElse(Distance.atLeast(maxDistance + 1));
    }

    // Restituisce la massima distanza di Levenshtein consentita
    public int maxDistance() {
        return maxDistance;
    }

    // Restituisce il diametro dello stato multistato dell'NFA di Levenshtein
    public int multistateDiameter() {
        return 2 * maxDistance + 1;
    }

    // Restituisce uno stato multistato iniziale per l'NFA di Levenshtein
    public MultiState initialStates() {
        MultiState multistate = MultiState.empty();
        multistate.addState(new NFAState(0, 0, false));
        return multistate;
    }

    // Calcola la distanza di Levenshtein tra due stringhe
    public Distance computeDistance(String query, String other) {
        char[] queryChars = query.toCharArray();
        MultiState currentState = initialStates();
        MultiState nextState = MultiState.empty();

        for (char chr : other.toCharArray()) {
            nextState.clear();
            long chi = CharacteristicVector.computeCharacteristicVector(queryChars, chr);
            transition(currentState, nextState, chi);
            MultiState tempState = currentState;
            currentState = nextState;
            nextState = tempState;
        }

        return multistateDistance(currentState, queryChars.length);
    }

    // Esegue una transizione semplice per uno stato e un simbolo specificati
    private void simpleTransition(NFAState state, long symbol, MultiState multistate) {
        if (state.getDistance() < maxDistance) {
            // Inserimento
            multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, false));
            // Eliminazione
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance() + 1, false));

            for (int d = 1; d <= maxDistance - state.getDistance(); d++) {
                // Sostituzione
                if (extractBit(symbol, d)) {
                    // Trasposizione (Damerau)
                    multistate.addState(new NFAState(state.getOffset() + 1 + d, state.getDistance() + d, false));
                }
            }
            // Copia senza modifiche
            if (damerau && extractBit(symbol, 1)) {
                multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, true));
            }
        }
        // Copia con trasposizione
        if (extractBit(symbol, 0)) {
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance(), false));
        }

        if (state.isInTranspose() && extractBit(symbol, 0)) {
            multistate.addState(new NFAState(state.getOffset() + 2, state.getDistance(), false));
        }

        //Cancellazione non si aggiunge nulla
    }

    // Esegue una transizione per uno stato corrente e un vettore chi specificati
    void transition(MultiState currentState, MultiState destState, long shiftedChiVector) {
        destState.clear();
        long mask = (1L << multistateDiameter()) - 1L;

        for (NFAState state : currentState.getStates()) {
            long shiftedChiVectorMasked = (shiftedChiVector >> state.getOffset()) & mask;
            simpleTransition(state, shiftedChiVectorMasked, destState);
        }

        Arrays.sort(destState.getStates());
    }

    // Estrae un bit specifico da un bitset dato e restituisce true se il bit è impostato a 1
    private boolean extractBit(long bitset, int pos) {
        int shift = (int) (bitset >> pos);
        return (shift & 1) == 1;
    }

    @Override
    public String toString() {
        return "LevenshteinNFA{" +
                "maxDistance=" + maxDistance +
                ", damerau=" + damerau +
                '}';
    }

    // Metodo main di esempio
    public static void main(String[] args) {
        // Esempi di utilizzo
        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(4, true);
        Distance distance = levenshteinNFA.computeDistance("kitten", "sitting");
        System.out.println(distance);
    }
}
