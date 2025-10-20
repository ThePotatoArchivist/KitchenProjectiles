package archives.tater.kitchenprojectiles;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

// Lazy both in the technical and colloquial sense (I didn't want to learn neoforge)
public class LazyRegistryRef<T> implements Supplier<T> {
    private final Registry<? super T> registry;
    private final Identifier id;
    private T value;

    public LazyRegistryRef(Registry<? super T> registry, Identifier id) {
        this.registry = registry;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        if (value == null)
            value = (T) registry.get(id);
        return value;
    }
}
