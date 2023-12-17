package benchmark;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// La classe rappresenta un alfabeto con vettori di caratteristiche associati per ogni carattere.
public class Alphabet {
    // Una lista di coppie, dove ogni coppia è composta da un carattere e il suo FullCharacteristicVector associato.
    private final List<Pair<Character, FullCharacteristicVector>> charset;

    // Costruttore che prende una lista di coppie carattere e FullCharacteristicVector.
    public Alphabet(List<Pair<Character, FullCharacteristicVector>> charset) {
        // Crea una nuova ArrayList per memorizzare l'insieme di caratteri e copia gli elementi dalla lista di input.
        this.charset = new ArrayList<>(charset);
    }

    // Metodo getter per recuperare l'insieme di caratteri.
    public List<Pair<Character, FullCharacteristicVector>> getCharset() {
        return charset;
    }

    // Un metodo statico per creare un Alfabeto basato su un array di caratteri.
    public static Alphabet forQueryChars(char[] queryChars) {
        // Crea una lista per memorizzare i caratteri univoci dall'array di input.
        List<Character> charList = new ArrayList<>();

        // Popola charList dall'array e rimuovi i duplicati.
        for (char c : queryChars) {
            charList.add(c);
        }
        charList.sort(null);
        charList = new ArrayList<>(new HashSet<>(charList));

        // Crea una lista per memorizzare le coppie di caratteri e i loro FullCharacteristicVector associati.
        List<Pair<Character, FullCharacteristicVector>> charset = new ArrayList<>();

        // Itera attraverso i caratteri univoci e crea FullCharacteristicVector per ciascuno.
        for (char c : charList) {
            List<Long> bits = new ArrayList<>();

            // Itera attraverso ciascun carattere nell'array di input e imposta i bit corrispondenti in FullCharacteristicVector.
            for (char chr : queryChars) {
                long chunkBits = 0L;
                long bit = 1L;

                // Se il carattere corrisponde al carattere univoco corrente, imposta il bit corrispondente.
                if (chr == c) {
                    chunkBits |= bit;
                }

                // Shifta il bit a sinistra per la prossima iterazione.
                bit <<= 1;
                bits.add(chunkBits);
            }

            // Aggiungi un valore Long zero per rappresentare la fine del FullCharacteristicVector.
            bits.add(0L);

            // Crea una coppia con il carattere univoco e il suo FullCharacteristicVector associato.
            charset.add(new Pair<>(c, new FullCharacteristicVector(bits)));
        }

        // Crea e restituisci un nuovo Alfabeto con l'insieme di caratteri generato.
        return new Alphabet(charset);
    }

    @Override
    public String toString() {
        return "benchmark.Alphabet{" +
                "charset=" + charset +
                '}';
    }
}
