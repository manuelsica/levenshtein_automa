package benchmark;

import java.util.Objects;

// La classe rappresenta una distanza associata a uno stato in un'automa.
public class Distance implements Comparable<Distance> {
    private final int distance;   // La distanza associata.
    private final boolean isExact; // Indica se la distanza è esatta o almeno.

    // Costruttore privato che può essere chiamato solo dai metodi statici di fabbrica.
    private Distance(int distance, boolean isExact) {
        this.distance = distance;
        this.isExact = isExact;
    }

    // Metodo statico di fabbrica per creare un'istanza con distanza esatta.
    public static Distance exact(int distance) {
        return new Distance(distance, true);
    }

    // Metodo statico di fabbrica per creare un'istanza con distanza almeno.
    public static Distance atLeast(int distance) {
        return new Distance(distance, false);
    }

    // Metodo getter per ottenere la distanza.
    public int getDistance() {
        return distance;
    }

    // Metodo getter per verificare se la distanza è esatta.
    public boolean isExact() {
        return isExact;
    }

    // Override del metodo toString per ottenere una rappresentazione testuale della distanza.
    @Override
    public String toString() {
        return isExact ? "Ci vogliono esattamente " + distance + " operazioni" : "Ci voglio almeno " + distance + " operazioni, aumenta la maxdistance";
    }

    // Implementazione del metodo compareTo per consentire il confronto tra distanze.
    @Override
    public int compareTo(Distance other) {
        if (this.distance == other.distance) {
            return Boolean.compare(this.isExact, other.isExact);
        }
        return Integer.compare(this.distance, other.distance);
    }

    // Override del metodo equals per confrontare due oggetti benchmark.Distance.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Distance other = (Distance) obj;
        return distance == other.distance && isExact == other.isExact;
    }

    // Override del metodo hashCode per fornire un valore hash basato su distance e isExact.
    @Override
    public int hashCode() {
        return Objects.hash(distance, isExact);
    }
}
