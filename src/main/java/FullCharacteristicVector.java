import java.util.ArrayList;
import java.util.List;

// La classe rappresenta un vettore di caratteristiche completo.
public class FullCharacteristicVector {
    private final List<Long> vector;  // Lista di Long che rappresenta il vettore di caratteristiche.

    // Costruttore che inizializza la classe con una lista di Long.
    public FullCharacteristicVector(List<Long> vector) {
        this.vector = new ArrayList<>(vector);
    }

    /**
     * Esegue uno shift e una maschera sul vettore di caratteristiche.
     *
     * @param offset L'offset di shift.
     * @param mask   La maschera da applicare.
     * @return Il risultato dell'operazione di shift e maschera.
     */
    public long shiftAndMask(int offset, long mask) {
        int bucketId = offset / 32;  // Identifica il "bucket" (gruppo di 32 bit) a cui appartiene l'offset.
        int align = offset - bucketId * 32;  // Calcola l'allineamento all'interno del "bucket".

        // Se l'allineamento è 0, non è necessario shiftare a sinistra.
        if (align == 0) {
            return this.vector.get(bucketId) & mask;
        } else {
            // Shifta a destra nel "bucket" corrente e a sinistra nel "bucket" successivo.
            long left = this.vector.get(bucketId) >> align;
            long right = this.vector.get(bucketId + 1) << (32 - align);

            // Applica la maschera al risultato dell'operazione di shift e restituisce il valore.
            return (left | right) & mask;
        }
    }
}
