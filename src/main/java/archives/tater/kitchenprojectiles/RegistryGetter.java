package archives.tater.kitchenprojectiles;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegistryGetter<T> {
    private @Nullable T value;
    private final Registry<T> registry;
    private final Identifier id;

    public RegistryGetter(Registry<T> registry, Identifier id) {
        this.registry = registry;
        this.id = id;
    }

    public @NotNull T get() {
        if (value == null)
            value = registry.getOrEmpty(id).orElseThrow();
        return value;
    }
}
