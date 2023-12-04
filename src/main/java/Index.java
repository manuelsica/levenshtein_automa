import java.util.HashMap;
import java.util.Map;

// La classe gestisce un'associazione tra oggetti generici e identificatori interi.
public class Index<I> {
    private final Map<I, Integer> index;    // Mappa che associa oggetti a identificatori interi.
    private final java.util.List<I> items;  // Lista che contiene gli oggetti nell'ordine in cui sono stati inseriti.

    // Costruttore che inizializza la mappa e la lista.
    public Index() {
        this.index = new HashMap<>();
        this.items = new java.util.ArrayList<>();
    }

    /**
     * Restituisce l'identificatore associato all'oggetto specificato.
     * Se l'oggetto non esiste nell'indice, viene allocato un nuovo identificatore.
     *
     * @param item L'oggetto per cui ottenere o allocare un identificatore.
     * @return L'identificatore associato all'oggetto.
     */
    public int getOrAllocate(I item) {
        // computeIfAbsent restituisce l'identificatore se l'oggetto esiste già nell'indice,
        // altrimenti assegna un nuovo identificatore incrementale e restituisce quello.
        int itemIndex = index.computeIfAbsent(item, key -> {
            int newIndex = items.size();
            items.add(key);
            return newIndex;
        });
        return itemIndex;
    }

    /**
     * Restituisce il numero di elementi nell'indice.
     *
     * @return Il numero di elementi nell'indice.
     */
    public int size() {
        return items.size();
    }

    /**
     * Restituisce l'oggetto associato a un dato identificatore.
     *
     * @param id L'identificatore dell'oggetto.
     * @return L'oggetto associato all'identificatore specificato.
     */
    public I getFromId(int id) {
        return items.get(id);
    }
}
