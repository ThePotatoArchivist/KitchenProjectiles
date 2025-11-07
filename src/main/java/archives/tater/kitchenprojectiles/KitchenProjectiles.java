package archives.tater.kitchenprojectiles;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;

import org.slf4j.Logger;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mod(KitchenProjectiles.MOD_ID)
public class KitchenProjectiles {
    public static final String MOD_ID = "kitchenprojectiles";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<KnifeEntity>> KNIFE_ENTITY = ENTITIES.register("knife", () ->
            Builder.<KnifeEntity>of(KnifeEntity::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("knife")
    );

    public static final ResourceKey<DamageType> KNIFE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, id("knife"));

    public static final TagKey<Item> LIGHT_KNIVES = TagKey.create(Registries.ITEM, id("light_knives"));

    public KitchenProjectiles(IEventBus modEventBus, ModContainer modContainer) {
        ENTITIES.register(modEventBus);

        KitchenProjectilesSounds.init(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (KitchenProjectiles) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private static void commonSetup(FMLCommonSetupEvent event) {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) ->
                target.is(ModTags.KNIFE_ENCHANTABLE) &&
                        enchantment.unwrapKey().map(key -> key == Enchantments.LOYALTY || key == Enchantments.MULTISHOT).orElse(false)
                        ? TriState.TRUE
                        : TriState.DEFAULT
        );
    }
}
