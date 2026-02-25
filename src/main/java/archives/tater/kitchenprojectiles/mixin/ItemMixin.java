package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import archives.tater.kitchenprojectiles.KitchenProjectilesSounds;
import archives.tater.kitchenprojectiles.KnifeEntity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import vectorwing.farmersdelight.common.registry.ModBlocks;

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
	public void throwUse(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		var stack = player.getItemInHand(usedHand);
		if (!stack.is(KitchenProjectiles.THROWABLE_KNIVES) || (stack.isDamageableItem() && stack.getMaxDamage() - stack.getDamageValue() <= 1) || level.getBlockState(getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getBlockPos()).is(ModBlocks.CUTTING_BOARD.get()))
			return;
		player.startUsingItem(usedHand);
		cir.setReturnValue(InteractionResultHolder.consume(stack));
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
    private UseAnim throwUseAnimation(UseAnim original, ItemStack stack) {
        return stack.is(KitchenProjectiles.THROWABLE_KNIVES) ? UseAnim.SPEAR : original;
    }

    @Inject(
            method = "releaseUsing",
            at = @At("HEAD")
    )
    private void throwRelease(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo ci) {
        if (!stack.is(KitchenProjectiles.THROWABLE_KNIVES)) return;
        if (!(livingEntity instanceof Player playerEntity)) return;
        if (livingEntity.getTicksUsingItem() < 6) return;

        if (!(level instanceof ServerLevel serverLevel)) return;

        stack.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));

        var multishot = EnchantmentHelper.processProjectileCount(serverLevel, stack, livingEntity, 1);
        var spread = EnchantmentHelper.processProjectileSpread(serverLevel, stack, livingEntity, 0f);

        for (var i = 0; i < multishot; i++) {
            var projectileStack = stack.copyWithCount(1);
            if (i != 0)
                projectileStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

            var knifeEntity = new KnifeEntity(level, playerEntity, projectileStack);

            var spreadIndex = (2 * (i % 2) - 1) * (i + 1) / 2; // 0, 1, -1, 2, -2, etc.

            var yaw = spread * spreadIndex;

            var opposite = livingEntity.getUpVector(1f);
            var quaternion = new Quaternionf().setAngleAxis(yaw * Mth.DEG_TO_RAD, opposite.x, opposite.y, opposite.z);
            var rotation = livingEntity.getViewVector(1f);
            var velocity = rotation.toVector3f().rotate(quaternion);

            knifeEntity.shoot(velocity.x, velocity.y, velocity.z, 1.5f, 1f);

            if (playerEntity.getAbilities().instabuild || i != 0) {
                knifeEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            level.addFreshEntity(knifeEntity);
            if (i == 0)
                level.playSound(null, knifeEntity, KitchenProjectilesSounds.throwing(stack), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (!playerEntity.getAbilities().instabuild)
            stack.shrink(1);

        playerEntity.awardStat(Stats.ITEM_USED.get((Item) (Object) this));
    }
}
