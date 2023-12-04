import java.util.HashMap;
import java.util.Map;

public class Index<I> {
    private final Map<I, Integer> index;
    private final java.util.List<I> items;

    public Index() {
        this.index = new HashMap<>();
        this.items = new java.util.ArrayList<>();
    }

    public int getOrAllocate(I item) {
        int itemIndex = index.computeIfAbsent(item, key -> {
            int newIndex = items.size();
            items.add(key);
            return newIndex;
        });
        return itemIndex;
    }

    public int size() {
        return items.size();
    }

    public I getFromId(int id) {
        return items.get(id);
    }
}