// Classe generica che rappresenta una coppia di oggetti
public class Pair<K, V> {
    private K first;
    private V second;

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

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
