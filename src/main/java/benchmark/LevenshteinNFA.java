package benchmark;

import java.util.Arrays;

public class LevenshteinNFA {
    private final int maxDistance;   // Massima distanza di Levenshtein consentita
    private final boolean damerau;   // Indica se il calcolo della distanza deve includere le trasposizioni di caratteri

    private final boolean touzet;    // Indica se la condizione locale di Touzet è attiva
    private final boolean similHamming; // Indica se sono permesse solo sostituzioni
    // Costruttore che inizializza l'NFA di Levenshtein con la massima distanza, l'opzione Damerau e l'opzione Touzet
    public LevenshteinNFA(int maxDistance, boolean damerau, boolean touzet, boolean similHamming) {
        this.maxDistance = maxDistance;
        this.damerau = damerau;
        this.touzet = touzet;
        this.similHamming = similHamming;
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

            if (similHamming) {
                if (query.length() != other.length()) {
                    // Se le lunghezze delle stringhe sono diverse, la distanza di Hamming non è definita
                    return Distance.atLeast(maxDistance + 1);
                } else {
                    // Calcola la distanza di Hamming
                    int hammingDistance = 0;
                    for (int i = 0; i < queryChars.length; i++) {
                        if (queryChars[i] != other.charAt(i)) {
                            hammingDistance++;
                        }
                    }
                    return Distance.exact(hammingDistance);
                }
            }

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

    // Definisce un metodo per eseguire una transizione semplice in un automa a stati finiti non deterministico (NFA)
    // per uno stato e un simbolo specificati.
    /*
    Facciamo l'usuale ipotesi che una cancellazione non sia seguita da un inserimento.
    Per ridurre il non determinismo, aggiungiamo un'altra condizione locale:
    una cancellazione non è seguita da una sostituzione. Infatti,
    è sempre possibile invertire le due operazioni e applicare la sostituzione prima della cancellazione.
    */
    // Modifica del metodo simpleTransition per rispettare la condizione di Touzet
    private void simpleTransition(NFAState state, long symbol, MultiState multistate) {
        if (state.getDistance() < maxDistance) {
            // Cancellazione (solo se non siamo in modalità Hamming)
            if (!similHamming && extractBit(symbol, 0)) {
                multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance(), false));
            } else {
                // Se non c'è stata una cancellazione o se Touzet è disattivato, procediamo con le altre operazioni

                // Inserimento
                if (!similHamming) {
                    multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, false));
                }

                // Sostituzione
                if (!touzet || !extractBit(symbol, 0)) { // Aggiunta della condizione Touzet
                    multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance() + 1, false));
                }

                // Sostituzione multipla (solo se non siamo in modalità Hamming)
                if (!similHamming) {
                    for (int d = 1; d <= maxDistance - state.getDistance(); d++) {
                        if (extractBit(symbol, d)) {
                            multistate.addState(new NFAState(state.getOffset() + 1 + d, state.getDistance() + d, false));
                        }
                    }
                }

                // Trasposizione (solo se Damerau è abilitato e non siamo in modalità Hamming)
                if (damerau && !similHamming && extractBit(symbol, 1)) {
                    multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, true));
                }
            }
        }

        // Gestione della transposizione in caso di cancellazione precedente (solo se Damerau è abilitato e non siamo in modalità Hamming)
        if (damerau && !similHamming && state.isInTranspose() && extractBit(symbol, 0)) {
            multistate.addState(new NFAState(state.getOffset() + 2, state.getDistance(), false));
        }
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
        return " LevenshteinNFA{" +
                "maxDistance=" + maxDistance +
                ", damerau=" + damerau +
                '}';
    }

    // Metodo main di esempio
    public static void main(String[] args) {

       /* // Esempio in cui coincide

        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(10, false, true, false);
        Distance distance = levenshteinNFA.computeDistance("casa", "cena");
        System.out.println(distance);


        LevenshteinNFA hammingNFA = new LevenshteinNFA(10, false, false, true);
        Distance hammingDistance = hammingNFA.computeDistance("casa", "cena");
        System.out.println(hammingDistance);

        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(10, false, true, false);
        Distance distance = levenshteinNFA.computeDistance("dire", "fare");
        System.out.println(distance);


        LevenshteinNFA hammingNFA = new LevenshteinNFA(5, false, false, true);
        Distance hammingDistance = hammingNFA.computeDistance("dire", "fare");
        System.out.println(hammingDistance);

        */

        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(10, false, true, false);
        Distance distance = levenshteinNFA.computeDistance("albero", "labbra");
        System.out.println(distance);

        // Esempio con distanza di Hamming
        LevenshteinNFA hammingNFA = new LevenshteinNFA(10, false, false, true);
        Distance hammingDistance = hammingNFA.computeDistance("dire", "fare");
        System.out.println(hammingDistance);

    }
}
