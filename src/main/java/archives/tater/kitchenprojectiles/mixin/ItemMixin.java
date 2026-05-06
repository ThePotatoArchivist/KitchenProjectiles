package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import archives.tater.kitchenprojectiles.KitchenProjectilesSounds;
import archives.tater.kitchenprojectiles.KnifeEntity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import static net.minecraft.util.Mth.DEG_TO_RAD;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow
    protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
        return BlockHitResult.miss(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO);
    }

    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
	public void throwUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		var stack = player.getItemInHand(hand);
		if (!stack.is(KitchenProjectiles.THROWABLE_KNIVES) || (stack.isDamageableItem() && stack.getMaxDamage() - stack.getDamageValue() <= 1) || level.getBlockState(getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getBlockPos()).is(ModBlocks.CUTTING_BOARD.get()))
			return;
		player.startUsingItem(hand);
		cir.setReturnValue(InteractionResult.CONSUME);
	}

    @ModifyReturnValue(
            method = "getUseDuration",
            at = @At("RETURN")
    )
    private int throwUseDuration(int original, ItemStack stack) {
        return stack.is(KitchenProjectiles.THROWABLE_KNIVES) ? 72000 : original;
    }

    @ModifyReturnValue(
            method = "getUseAnimation",
            at = @At("RETURN")
    )
    private ItemUseAnimation throwUseAnimation(ItemUseAnimation original, ItemStack stack) {
        return stack.is(KitchenProjectiles.THROWABLE_KNIVES) ? ItemUseAnimation.TRIDENT : original;
    }

    @Inject(
            method = "releaseUsing",
            at = @At("HEAD")
    )
    private void throwRelease(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime, CallbackInfoReturnable<Boolean> cir) {
        if (!itemStack.is(KitchenProjectiles.THROWABLE_KNIVES)) return;
        if (!(entity instanceof Player playerEntity)) return;
        if (entity.getTicksUsingItem() < 6) return;

        if (!(level instanceof ServerLevel serverLevel)) return;

        itemStack.hurtAndBreak(1, entity, entity.getUsedItemHand());

        var multishot = EnchantmentHelper.processProjectileCount(serverLevel, itemStack, entity, 1);
        var spread = EnchantmentHelper.processProjectileSpread(serverLevel, itemStack, entity, 0f);

        for (var i = 0; i < multishot; i++) {
            var projectileStack = itemStack.copyWithCount(1);
            if (i != 0)
                projectileStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

            var knifeEntity = new KnifeEntity(level, playerEntity, projectileStack);

            var spreadIndex = (2 * (i % 2) - 1) * (i + 1) / 2; // 0, 1, -1, 2, -2, etc.

            var yaw = spread * spreadIndex;

            var opposite = entity.getUpVector(1f);
            var quaternion = new Quaternionf().setAngleAxis(yaw * DEG_TO_RAD, opposite.x, opposite.y, opposite.z);
            var rotation = entity.getViewVector(1f);
            var velocity = rotation.toVector3f().rotate(quaternion);

            knifeEntity.shoot(velocity.x, velocity.y, velocity.z, 1.5f, 1f);

            if (playerEntity.getAbilities().instabuild || i != 0) {
                knifeEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            level.addFreshEntity(knifeEntity);
            if (i == 0)
                level.playSound(null, knifeEntity, KitchenProjectilesSounds.throwing(itemStack), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (!playerEntity.getAbilities().instabuild)
            itemStack.shrink(1);

        playerEntity.awardStat(Stats.ITEM_USED.get((Item) (Object) this));
    }
}
