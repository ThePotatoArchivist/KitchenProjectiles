package archives.tater.kitchenprojectiles;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KitchenProjectilesSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, KitchenProjectiles.MOD_ID);

    private static DeferredHolder<SoundEvent, SoundEvent> of(String path) {
        return SOUNDS.register(path, SoundEvent::createVariableRangeEvent);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_THROW_LIGHT = of("entity.kitchenprojectiles.knife.light.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_HIT_LIGHT = of("entity.kitchenprojectiles.knife.light.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_GROUND_LIGHT = of("entity.kitchenprojectiles.knife.light.hit_ground");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_RETURN_LIGHT = of("entity.kitchenprojectiles.knife.light.return");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_THROW_HEAVY = of("entity.kitchenprojectiles.knife.heavy.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_HIT_HEAVY = of("entity.kitchenprojectiles.knife.heavy.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_GROUND_HEAVY = of("entity.kitchenprojectiles.knife.heavy.hit_ground");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_RETURN_HEAVY = of("entity.kitchenprojectiles.knife.heavy.return");

    public static boolean isKnifeLight(ItemStack knifeStack) {
        return knifeStack.is(KitchenProjectiles.LIGHT_KNIVES);
    }

    public static SoundEvent throwing(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_THROW_LIGHT.get() : KNIFE_THROW_HEAVY.get();
    }

    public static SoundEvent hit(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_HIT_LIGHT.get() : KNIFE_HIT_HEAVY.get();
    }

    public static SoundEvent hitGround(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_GROUND_LIGHT.get() : KNIFE_GROUND_HEAVY.get();
    }

    public static SoundEvent returning(ItemStack knifeStack) {
        return isKnifeLight(knifeStack) ? KNIFE_RETURN_LIGHT.get() : KNIFE_RETURN_HEAVY.get();
    }

    public static void init(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
