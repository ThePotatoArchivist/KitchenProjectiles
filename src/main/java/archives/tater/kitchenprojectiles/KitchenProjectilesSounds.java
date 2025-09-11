package archives.tater.kitchenprojectiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
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

    public static boolean isKnifeHeavy(ItemStack knifeStack) {
        return knifeStack.getItem() instanceof ToolItem toolItem && toolItem.getMaterial().getAttackDamage() >= 3; // yeah a bit hacky
    }

    public static SoundEvent throwing(ItemStack knifeStack) {
        return isKnifeHeavy(knifeStack) ? KNIFE_THROW_HEAVY : KNIFE_THROW_LIGHT;
    }

    public static SoundEvent hit(ItemStack knifeStack) {
        return isKnifeHeavy(knifeStack) ? KNIFE_HIT_HEAVY : KNIFE_HIT_LIGHT;
    }

    public static SoundEvent hitGround(ItemStack knifeStack) {
        return isKnifeHeavy(knifeStack) ? KNIFE_GROUND_HEAVY : KNIFE_GROUND_LIGHT;
    }

    public static SoundEvent returning(ItemStack knifeStack) {
        return isKnifeHeavy(knifeStack) ? KNIFE_RETURN_HEAVY : KNIFE_RETURN_LIGHT;
    }

    public static void init() {}
}
