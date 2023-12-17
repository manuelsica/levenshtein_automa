package benchmark;

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

    // Definisce un metodo per eseguire una transizione semplice in un automa a stati finiti non deterministico (NFA)
    // per uno stato e un simbolo specificati.
    private void simpleTransition(NFAState state, long symbol, MultiState multistate) {
        // Controlla se la distanza dello stato corrente è minore della distanza massima consentita.
        if (state.getDistance() < maxDistance) {

            // Aggiunge uno stato al benchmark.MultiState con la stessa posizione dell'offset dello stato corrente
            // ma con la distanza incrementata di 1 e non in transposizione.
            /*
            L'inserimento è rappresentato da uno stato che ha lo stesso offset dello stato corrente ma con una distanza incrementata.
            Questo rappresenta l'aggiunta di un nuovo carattere nella stringa di input.
             */
            multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, false)); //Inserimento

            // Aggiunge uno stato al benchmark.MultiState con l'offset incrementato di 1 e la distanza incrementata di 1,
            // e non in transposizione.
             /*
            La sostituzione è implicita quando si crea un nuovo stato con l'offset incrementato e la distanza incrementata.
            Questo rappresenta la sostituzione di un carattere nella stringa di input con un altro carattere.
             */
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance() + 1, false));//Sostituzione

            // Itera per ogni possibile distanza a partire da 1 fino alla distanza massima meno la distanza corrente.
            /*
            Questo ciclo permette di gestire le sostituzioni multiple che possono avvenire quando si considerano errori di battitura più complessi o
            stringhe con più errori.
             */
            for (int d = 1; d <= maxDistance - state.getDistance(); d++) {

                // Controlla se il bit nella posizione 'd' del simbolo è 1 (true).
                if (extractBit(symbol, d)) {

                    // Se il bit è 1, aggiunge uno stato al benchmark.MultiState con l'offset incrementato di 1 + d
                    // e la distanza incrementata di d, e non in transposizione.

                    multistate.addState(new NFAState(state.getOffset() + 1 + d, state.getDistance() + d, false));//Sostituzione multipla
                }
            }

            // Se l'opzione Damerau è abilitata e il bit nella posizione 1 del simbolo è 1,
            // aggiunge uno stato al benchmark.MultiState con la stessa posizione dell'offset dello stato corrente
            // ma con la distanza incrementata di 1 e in transposizione.
            /*
            La transposizione, se abilitata (indicata dalla variabile damerau), è rappresentata da uno stato che ha lo stesso offset e una distanza incrementata,
            ma con il flag di transposizione impostato su true. Questo rappresenta lo scambio di due caratteri adiacenti nella stringa di input.
            */
            if (damerau && extractBit(symbol, 1)) {
                multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, true));//Trasposizione
            }
        }

        // Controlla se il bit nella posizione 0 del simbolo è 1.
        /*
                    La cancellazione è rappresentata da uno stato che ha l'offset incrementato ma la stessa distanza dello stato corrente.
                    Questo rappresenta la rimozione di un carattere dalla stringa di input.
        */
        if (extractBit(symbol, 0)) {
            // Se il bit è 1, aggiunge uno stato al benchmark.MultiState con l'offset incrementato di 1
            // e la stessa distanza dello stato corrente, e non in transposizione.
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance(), false));//Cancellazione
        }

        // Se lo stato corrente è in transposizione e il bit nella posizione 0 del simbolo è 1,
        // aggiunge uno stato al benchmark.MultiState con l'offset incrementato di 2, la stessa distanza dello stato corrente,
        // e non in transposizione.
        if (state.isInTranspose() && extractBit(symbol, 0)) {
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
        return "benchmark.LevenshteinNFA{" +
                "maxDistance=" + maxDistance +
                ", damerau=" + damerau +
                '}';
    }

    // Metodo main di esempio
    public static void main(String[] args) {
        // Esempi di utilizzo
        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(4, true);
        Distance distance = levenshteinNFA.computeDistance("bar", "biro");
        System.out.println(distance);
    }
}
