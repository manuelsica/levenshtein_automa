import java.util.Objects;

public class Distance implements Comparable<Distance> {
    private final int distance;
    private final boolean isExact;

    private Distance(int distance, boolean isExact) {
        this.distance = distance;
        this.isExact = isExact;
    }

    public static Distance exact(int distance) {
        return new Distance(distance, true);
    }

    public static Distance atLeast(int distance) {
        return new Distance(distance, false);
    }

    public int getDistance() {
        return distance;
    }

    public boolean isExact() {
        return isExact;
    }

    @Override
    public String toString() {
        return isExact ? "Ci vogliono esattamente " + distance + " operazioni" : "Ci voglio almeno " + distance + " operazioni, aumenta la maxdistance";
    }

    @Override
    public int compareTo(Distance other) {
        if (this.distance == other.distance) {
            return Boolean.compare(this.isExact, other.isExact);
        }
        return Integer.compare(this.distance, other.distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Distance other = (Distance) obj;
        return distance == other.distance && isExact == other.isExact;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, isExact);
    }
}
