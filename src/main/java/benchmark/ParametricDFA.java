package benchmark;

import java.util.ArrayList;
import java.util.List;

// Definizione della classe ParametricDFA, che rappresenta un automa a stati finiti deterministico parametrico
public class ParametricDFA {
    // Lista delle distanze associate agli stati del DFA
    private final List<Distance> distance;
    // Lista delle transizioni tra gli stati del DFA
    private final List<Transition> transitions;
    // Massima distanza consentita per le operazioni di editing
    private final byte maxDistance;
    // Dimensione delle transizioni (numero di possibili transizioni per stato)
    private final int transitionStride;
    // Diametro del DFA, che rappresenta il numero massimo di operazioni di editing consentite
    private final int diameter;

    // Costruttore che inizializza un ParametricDFA con le informazioni necessarie
    public ParametricDFA(List<Distance> distance, List<Transition> transitions, byte maxDistance, int transitionStride, int diameter) {
        this.distance = distance;
        this.transitions = transitions;
        this.maxDistance = maxDistance;
        this.transitionStride = transitionStride;
        this.diameter = diameter;
    }

    // Metodo statico che restituisce uno stato iniziale per il DFA
    public static ParametricState initialState() {
        return new ParametricState(1, 0);
    }

    // Verifica se uno stato è un "sink" rispetto al prefisso della query, ovvero se non ci sono più transizioni possibili
    public boolean isPrefixSink(ParametricState state, int queryLen) {
        // Se lo stato è un vicolo cieco, ritorna true
        if (state.isDeadEnd()) {
            return true;
        }
        // Calcola l'offset rimanente rispetto alla lunghezza della query
        int remainingOffset = queryLen - state.getOffset();
        // Se l'offset rimanente è minore del diametro, verifica le condizioni per essere un sink
        if (remainingOffset < diameter) {
            int stateDistanceIndex = diameter * state.getShapeId() + remainingOffset;
            Distance prefixDistance = distance.get(stateDistanceIndex);
            // Se la distanza è maggiore della distanza massima, non è un sink
            if (prefixDistance.getDistance() > maxDistance) {
                return false;
            }
            // Controlla se ci sono distanze minori rispetto a quella del prefisso
            for (int i = 0; i < remainingOffset; i++) {
                if (distance.get(diameter * state.getShapeId() + i).compareTo(prefixDistance) < 0) {
                    return false;
                }
            }
            // Se nessuna delle condizioni precedenti è vera, allora è un sink
            return true;
        } else {
            // Se l'offset rimanente è maggiore o uguale al diametro, non è un sink
            return false;
        }
    }

    // Costruisce un DFA standard o personalizzato a seconda del parametro "prefix"
    public DFA buildDFA(String query, boolean prefix) {
        return buildCustomDFA(query, prefix, false);
    }

    // Costruisce un DFA personalizzato a seconda dei parametri specificati
    public DFA buildCustomDFA(String query, boolean prefix, boolean useAppliedDistance) {
        // Converte la query in un array di caratteri
        char[] queryChars = query.toCharArray();
        // Crea un alfabeto basato sui caratteri della query
        Alphabet alphabet = Alphabet.forQueryChars(queryChars);
        // Ottiene la lunghezza della query
        int queryLen = queryChars.length;

        // Crea un indice per gli stati parametrici
        ParametricStateIndex parametricStateIndex = new ParametricStateIndex(queryLen, numStates());
        // Calcola il numero massimo di stati
        int maxNumStates = parametricStateIndex.maxNumStates();

        // Alloca lo stato di vicolo cieco e verifica che il suo ID sia 0
        int deadEndStateId = parametricStateIndex.getOrAllocate(ParametricState.empty());
        assert deadEndStateId == 0;
        // Alloca lo stato iniziale
        int initialStateId = parametricStateIndex.getOrAllocate(initialState());

        // Crea un costruttore per il DFA UTF-8 con il numero massimo di stati
        Utf8DFABuilder dfaBuilder = Utf8DFABuilder.withMaxNumStates(maxNumStates);
        // Calcola la maschera per il diametro
        int mask = (1 << diameter) - 1;

        // Itera su tutti gli stati dell'indice parametrico
        for (int stateId = 0; stateId < parametricStateIndex.numStates(); stateId++) {
            ParametricState state = parametricStateIndex.get(stateId);
            // Calcola la distanza per lo stato corrente
            Distance distance = useAppliedDistance ? appliedDistance(state) : distance(state, queryLen);

            // Se il parametro "prefix" è true e lo stato è un sink, aggiunge lo stato al DFA
            if (prefix && isPrefixSink(state, queryLen)) {
                dfaBuilder.addState(stateId, distance, stateId);
            } else {
                // Altrimenti, calcola la transizione di default e il successore di default
                Transition defaultTransition = transition(state, 0);
                ParametricState defaultSuccessor = defaultTransition.apply(state);
                int defaultSuccessorId = parametricStateIndex.getOrAllocate(defaultSuccessor);
                // Aggiunge lo stato al DFA con la transizione di default
                Utf8DFAStateBuilder stateBuilder = dfaBuilder.addState(stateId, distance, defaultSuccessorId);
                // Itera su tutti i caratteri dell'alfabeto e aggiunge le transizioni corrispondenti
                for (Pair<Character, FullCharacteristicVector> pair : alphabet.getCharset()) {
                    char chr = pair.getFirst();
                    FullCharacteristicVector characteristicVec = pair.getSecond();
                    int chi = (int) characteristicVec.shiftAndMask(state.getOffset(), mask);
                    Transition destTransition = transition(state, chi);
                    ParametricState destState = destTransition.apply(state);
                    int destStateId = parametricStateIndex.getOrAllocate(destState);
                    stateBuilder.addTransition(chr, destStateId);
                }
            }
        }

        // Imposta lo stato iniziale del DFA e costruisce il DFA
        dfaBuilder.setInitialState(initialStateId);
        return dfaBuilder.build();
    }

    // Restituisce il numero totale di stati nel DFA
    public int numStates() {
        return transitions.size() / transitionStride;
    }

    // Calcola la distanza per uno stato e una lunghezza di query specificati
    public Distance distance(ParametricState state, int queryLen) {
        int remainingOffset = queryLen - state.getOffset();
        // Se lo stato è un vicolo cieco o l'offset rimanente è maggiore o uguale al diametro, restituisce una distanza almeno maggiore della massima
        if (state.isDeadEnd() || remainingOffset >= diameter) {
            return Distance.atLeast(maxDistance + 1);
        } else {
            // Altrimenti, calcola l'indice e restituisce la distanza corrispondente
            int index = diameter * state.getShapeId() + remainingOffset;
            Distance d = distance.get(index);
            return d.getDistance() > maxDistance ? Distance.atLeast(d.getDistance()) : Distance.exact(d.getDistance());
        }
    }

    // Calcola la distanza applicata per uno stato specificato
    public Distance appliedDistance(ParametricState state) {
        int index = diameter * state.getShapeId();
        Distance d = distance.get(index);
        return d.getDistance() > maxDistance ? Distance.atLeast(d.getDistance()) : Distance.exact(d.getDistance());
    }

    // Restituisce la transizione per uno stato e un valore chi specificati
    public Transition transition(ParametricState state, int chi) {
        assert chi < transitionStride;
        return transitions.get(transitionStride * state.getShapeId() + chi);
    }

    // Costruisce un ParametricDFA da un NFA (Automaton di Levenshtein non deterministico)
    public static ParametricDFA fromNFA(LevenshteinNFA nfa) {
        // Crea un indice per gli stati multipli
        Index<MultiState> index = new Index<>();
        index.getOrAllocate(MultiState.empty());
        MultiState initialState = nfa.initialStates();
        index.getOrAllocate(initialState);

        // Ottiene la massima distanza dal NFA
        byte maxDistance = (byte) nfa.maxDistance();
        // Ottiene il diametro degli stati multipli dal NFA
        int multistateDiameter = nfa.multistateDiameter();
        // Crea una lista per le transizioni
        List<Transition> transitions = new ArrayList<>();

        // Calcola il numero di possibili valori chi
        int numChi = 1 << multistateDiameter;
        List<Integer> chiValues = new ArrayList<>();
        for (int i = 0; i < numChi; i++) {
            chiValues.add(i);
        }

        // Crea uno stato multiplo vuoto
        MultiState destMultistate = MultiState.empty();

        // Itera su tutti gli stati e calcola le transizioni
        for (int stateId = 0; stateId < index.size(); stateId++) {
            for (int chi : chiValues) {
                MultiState multistate = index.getFromId(stateId);
                nfa.transition(multistate, destMultistate, chi);
                int translation = destMultistate.normalize();
                int destId = index.getOrAllocate(destMultistate);
                transitions.add(new Transition(destId, translation));
            }
        }

        // Calcola il numero di stati totali basandosi sulla dimensione dell'indice
        int numStates = index.size();
        // Crea una lista per memorizzare le distanze
        List<Distance> distance = new ArrayList<>();
        // Calcola le distanze per ogni stato e offset e le aggiunge alla lista
        for (int stateId = 0; stateId < numStates; stateId++) {
            MultiState multistate = index.getFromId(stateId);
            for (int offset = 0; offset < multistateDiameter; offset++) {
                Distance dist = nfa.multistateDistance(multistate, offset);
                distance.add(dist);
            }
        }

        // Crea e restituisce un nuovo ParametricDFA con le distanze e le transizioni calcolate,
        // la massima distanza, il numero di possibili valori chi e il diametro degli stati multipli
        return new ParametricDFA(distance, transitions, maxDistance, numChi, multistateDiameter);
    }

    // Override del metodo toString per fornire una rappresentazione in stringa dell'oggetto ParametricDFA
    @Override
    public String toString() {
        return " ParametricDFA{" +
                "distance=" + distance +
                ", transitions=" + transitions +
                ", maxDistance=" + maxDistance +
                ", transitionStride=" + transitionStride +
                ", diameter=" + diameter +
                '}';
    }

    // Metodo main per testare la classe ParametricDFA
    public static void main(String [] args){
        // Crea un ParametricDFA da un NFA con distanza massima 7 e transposizione abilitata
        ParametricDFA parametricDFA = ParametricDFA.fromNFA((new LevenshteinNFA(7, false, true, false)));

        // Crea uno stato parametrico iniziale
        ParametricState state = new ParametricState(1, 0);
        // Ottiene la lunghezza della query di test
        int queryLen = "kitten".length();

        // Calcola la distanza per lo stato e la lunghezza della query
        Distance distance = parametricDFA.distance(state ,queryLen);
        // Stampa la distanza calcolata
        System.out.println(distance);
    }
}

