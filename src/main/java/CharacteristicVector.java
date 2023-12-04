public class CharacteristicVector {
    // Metodo statico per calcolare un vettore caratteristico per un dato carattere in base a un array di caratteri.
    public static long computeCharacteristicVector(char[] query, char c) {
        // Inizializza il vettore caratteristico a 0.
        long chi = 0L;

        // Itera attraverso l'array di caratteri "query".
        for (int i = 0; i < query.length; i++) {
            // Se il carattere corrente nella "query" corrisponde al carattere specificato, imposta il bit corrispondente in "chi".
            if (query[i] == c) {
                chi |= 1L << i;
            }
        }

        // Restituisce il vettore caratteristico risultante.
        return chi;
    }
}
