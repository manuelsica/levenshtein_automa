// DFA.java

import java.nio.charset.StandardCharsets;

/**
 * Implementazione di un Automi Finiti Deterministici per
 * un Automi di Levenshtein che mira a stringhe codificate in UTF-8.
 *
 * L'automa non valida `utf-8`.
 * Non restituirà errori quando alimentato con `utf-8` non valido.
 *
 * L'unico stato `sink` è garantito essere `SINK`.
 *
 * Questo significa che se si raggiunge lo stato sink si è
 * garantiti che, indipendentemente dalla sequenza di byte
 * che si potrebbe consumare in futuro, si rimarrà sempre
 * nello stesso stato.
 *
 * Questa proprietà può essere sfruttata per interrompere ulteriori
 * valutazioni.
 */
public class DFA {
    private final int[][] transitions;
    private final Distance[] distances;
    private final int initial_state;

    // Stato sink costante. Vedi [DFA](./index.html)
    public static final int SINK_STATE = 0;

    // Costruttore che inizializza l'automa DFA con le transizioni, le distanze e lo stato iniziale.
    public DFA(int[][] transitions, Distance[] distances, int initial_state) {
        this.transitions = transitions;
        this.distances = distances;
        this.initial_state = initial_state;
    }

    /**
     * Restituisce lo stato iniziale
     */
    public int getInitialState() {
        return initial_state;
    }

    /**
     * Funzione di aiuto che consuma tutti i byte
     * di una sequenza di byte e restituisce la distanza risultante.
     */
    public Distance eval(byte[] text) {
        int state = getInitialState();
        for (byte b : text) {
            state = transition(state, b);
        }
        return distance(state);
    }

    /**
     * Restituisce la distanza di Levenshtein associata allo
     * stato corrente.
     */
    public Distance distance(int state_id) {
        return distances[state_id];
    }

    /**
     * Restituisce il numero di stati nell'`DFA`.
     */
    public int numStates() {
        return transitions.length;
    }

    /**
     * Restituisce lo stato di destinazione raggiunto dopo aver consumato un dato byte.
     */
    public int transition(int from_state_id, byte b) {
        return transitions[from_state_id][b & 0xFF];
    }

    // Metodo main per eseguire test sull'automa DFA.
    public static void main(String[] args) {
        // Creazione di un costruttore DFA con un numero massimo di stati
        Utf8DFABuilder builder = new Utf8DFABuilder(2);

        // Aggiunta degli stati all'automa
        builder.addState(0, Distance.exact(1), 1);
        builder.addState(1, Distance.exact(0), 0);

        // Impostazione dello stato iniziale
        builder.setInitialState(1);

        // Costruzione dell'automa DFA
        DFA dfa = builder.build();

        // Test dell'automa con diverse stringhe
        testDFA(dfa, "abcdef"); // Dovrebbe essere 0
        testDFA(dfa, "a");      // Dovrebbe essere 1
        testDFA(dfa, "aあ");    // Dovrebbe essere 0
        testDFA(dfa, "❤");      // Dovrebbe essere 1
        testDFA(dfa, "❤❤");    // Dovrebbe essere 0
        testDFA(dfa, "❤a");     // Dovrebbe essere 0
        testDFA(dfa, "あ");      // Dovrebbe essere 1
        testDFA(dfa, "ああ");    // Dovrebbe essere 0
    }

    private static void testDFA(DFA dfa, String text) {
        // Valutazione della distanza per la stringa data
        Distance distance = dfa.eval(text.getBytes(StandardCharsets.UTF_8));
        System.out.println("La distanza per la stringa \"" + text + "\" è: " + distance);
    }
}
