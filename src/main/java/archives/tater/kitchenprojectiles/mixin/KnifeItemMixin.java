package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KitchenProjectilesSounds;
import archives.tater.kitchenprojectiles.KnifeEntity;

import org.spongepowered.asm.mixin.Mixin;
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
import org.joml.Quaternionf;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@Mixin(KnifeItem.class)
public abstract class KnifeItemMixin extends Item {

	public KnifeItemMixin(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		var stack = user.getItemInHand(hand);
		if (stack.getMaxDamage() - stack.getDamageValue() <= 1 || world.getBlockState(getPlayerPOVHitResult(world, user, ClipContext.Fluid.NONE).getBlockPos()).is(ModBlocks.CUTTING_BOARD.get()))
			return InteractionResultHolder.pass(stack);
		user.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.SPEAR;
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof Player playerEntity)) return;
        if (user.getTicksUsingItem() < 6) return;

        if (!(world instanceof ServerLevel serverWorld)) return;

        stack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(user.getUsedItemHand()));

        var multishot = EnchantmentHelper.processProjectileCount(serverWorld, stack, user, 1);
        var spread = EnchantmentHelper.processProjectileSpread(serverWorld, stack, user, 0f);

        for (var i = 0; i < multishot; i++) {
            var projectileStack = i == 0 ? stack : stack.copy();
            if (i != 0)
                projectileStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

            var knifeEntity = new KnifeEntity(world, playerEntity, projectileStack);

            var spreadIndex = (2 * (i % 2) - 1) * (i + 1) / 2; // 0, 1, -1, 2, -2, etc.

            var yaw = spread * spreadIndex;

            var opposite = user.getUpVector(1f);
            var quaternion = new Quaternionf().setAngleAxis(yaw * Mth.DEG_TO_RAD, opposite.x, opposite.y, opposite.z);
            var rotation = user.getViewVector(1f);
            var velocity = rotation.toVector3f().rotate(quaternion);

            knifeEntity.shoot(velocity.x, velocity.y, velocity.z, 1.5f, 1f);

            if (playerEntity.getAbilities().instabuild || i != 0) {
                knifeEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            world.addFreshEntity(knifeEntity);
            if (i == 0)
                world.playSound(null, knifeEntity, KitchenProjectilesSounds.throwing(stack), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (!playerEntity.getAbilities().instabuild)
            stack.shrink(1);

        playerEntity.awardStat(Stats.ITEM_USED.get(this));
    }
}
