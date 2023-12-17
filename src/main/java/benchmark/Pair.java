package benchmark;

// Classe generica che rappresenta una coppia di oggetti
public class Pair<K, V> {
    private K first;  // Il primo elemento della coppia
    private V second; // Il secondo elemento della coppia

    // Costruttore che inizializza la coppia con due oggetti
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    // Restituisce il primo elemento della coppia
    public K getFirst() {
        return first;
    }

    // Restituisce il secondo elemento della coppia
    public V getSecond() {
        return second;
    }

    // Override del metodo toString per ottenere una rappresentazione testuale della coppia
    @Override
    public String toString() {
        return " Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
