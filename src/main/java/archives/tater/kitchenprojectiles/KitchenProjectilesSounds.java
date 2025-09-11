package archives.tater.kitchenprojectiles;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class KitchenProjectilesSounds {
    private static SoundEvent of(Identifier id) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private static SoundEvent of(String path) {
        return of(KitchenProjectiles.id(path));
    }

    public static final SoundEvent KNIFE_THROW_LIGHT = of("entity.kitchenprojectiles.knife.light.throw");
    public static final SoundEvent KNIFE_HIT_LIGHT = of("entity.kitchenprojectiles.knife.light.hit");
    public static final SoundEvent KNIFE_GROUND_LIGHT = of("entity.kitchenprojectiles.knife.light.hit_ground");
    public static final SoundEvent KNIFE_RETURN_LIGHT = of("entity.kitchenprojectiles.knife.light.return");
    public static final SoundEvent KNIFE_THROW_HEAVY = of("entity.kitchenprojectiles.knife.heavy.throw");
    public static final SoundEvent KNIFE_HIT_HEAVY = of("entity.kitchenprojectiles.knife.heavy.hit");
    public static final SoundEvent KNIFE_GROUND_HEAVY = of("entity.kitchenprojectiles.knife.heavy.hit_ground");
    public static final SoundEvent KNIFE_RETURN_HEAVY = of("entity.kitchenprojectiles.knife.heavy.return");

    public static boolean isKnifeLight(ItemStack knifeStack) {
        return knifeStack.isIn(KitchenProjectiles.LIGHT_KNIVES);
    }

    public static SoundEvent throwing(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_THROW_LIGHT : KNIFE_THROW_HEAVY;
    }

    public static SoundEvent hit(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_HIT_LIGHT : KNIFE_HIT_HEAVY;
    }

    public static SoundEvent hitGround(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_GROUND_LIGHT : KNIFE_GROUND_HEAVY;
    }

    public static SoundEvent returning(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_RETURN_LIGHT : KNIFE_RETURN_HEAVY;
    }

    public static void init() {}
}
