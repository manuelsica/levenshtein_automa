package operazioni;


import java.util.ArrayList;

import automiconsensus.AutomaConsensus;
import automiconsensus.StateConsensus;

public class SimpleNFAHamming {
	
	public SimpleNFAHamming() {
		
	}
	
	public AutomaConsensus compute(String g, int dmax, ArrayList<Character> alphabet) {
	        AutomaConsensus automa = new AutomaConsensus();
	        int length = g.length();
	        //inizializza l'insieme degli stati (e,k)
	        for (int e = 0; e <= dmax; e++) {
	            for (int k = 0; k <= length-2; k++) {
	                StateConsensus state = new StateConsensus();
	                state.setLevel(k);
	                state.setErrorCount(e);
	                automa.addState(state);
	            }
	        }
	        
	        //per costruzione il penultimo livello ha sempre due soli stati
	        StateConsensus secondlast_one = new StateConsensus();
	        secondlast_one.setLevel(length-1);
            secondlast_one.setErrorCount(0);
            automa.addState(secondlast_one);
	        StateConsensus secondlast_two = new StateConsensus();
	        secondlast_two.setLevel(length-1);
            secondlast_two.setErrorCount(1);
            automa.addState(secondlast_two);
	        
	      //per costruzione l'ultimo livello ha solo uno stato ovvero quello accettante
	        StateConsensus last = new StateConsensus();
	        last.setLevel(length);
            last.setErrorCount(0);
            automa.addState(last);
	        
	        
	        //per ogni stato (e,k)
	        for (StateConsensus cs : automa.getStates()) {
	            int e = cs.getError();
	            int k = cs.getLevel();

	            for (char c : alphabet) {//per ogni simbolo dell'alfabeto
	                StateConsensus next = null;

	                if (k < length) {
	                    if (c == g.charAt(k)) {
	                        next = automa.getStateByLevelAndError(k + 1, e); //match
	                      //aggiungere la transizione se non andiamo fuori matrice
	                        if (next != null) {
	    	                    automa.addTransition(cs, String.valueOf(c), next);
	    	                }
	                    } else {
	                        next = automa.getStateByLevelAndError(k + 1, e - 1); //mismatch
	                      //aggiungere la transizione se non andiamo fuori matrice
	                        if (next != null) {
	    	                    automa.addTransition(cs, "x", next);
	    	                }
	                    }
	                }
	                
	            }
	        }
	        //cicli agli stati iniziali
	        for(StateConsensus s : automa.getStates()) {
	        	if(s.getLevel()==0) s.setSelftransition(false);
	        }

	        return automa;
	}
}
	
