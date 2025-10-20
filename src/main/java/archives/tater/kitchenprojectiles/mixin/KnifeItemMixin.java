package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KitchenProjectilesSounds;
import archives.tater.kitchenprojectiles.KnifeEntity;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Unit;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import org.joml.Quaternionf;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@Mixin(KnifeItem.class)
public abstract class KnifeItemMixin extends Item {

	public KnifeItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		var stack = user.getStackInHand(hand);
		if (stack.getMaxDamage() - stack.getDamage() <= 1 || world.getBlockState(raycast(world, user, RaycastContext.FluidHandling.NONE).getBlockPos()).isOf(ModBlocks.CUTTING_BOARD.get()))
			return TypedActionResult.pass(stack);
		user.setCurrentHand(hand);
		return TypedActionResult.consume(stack);
	}

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity playerEntity)) return;
        if (user.getItemUseTime() < 6) return;

        if (!(world instanceof ServerWorld serverWorld)) return;

        stack.damage(1, user, LivingEntity.getSlotForHand(user.getActiveHand()));

        var multishot = EnchantmentHelper.getProjectileCount(serverWorld, stack, user, 1);
        var spread = EnchantmentHelper.getProjectileSpread(serverWorld, stack, user, 0f);

        for (var i = 0; i < multishot; i++) {
            var projectileStack = i == 0 ? stack : stack.copy();
            if (i != 0)
                projectileStack.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

            var knifeEntity = new KnifeEntity(world, playerEntity, projectileStack);

            var spreadIndex = (2 * (i % 2) - 1) * (i + 1) / 2; // 0, 1, -1, 2, -2, etc.

            var yaw = spread * spreadIndex;

            var opposite = user.getOppositeRotationVector(1f);
            var quaternion = new Quaternionf().setAngleAxis(yaw * MathHelper.RADIANS_PER_DEGREE, opposite.x, opposite.y, opposite.z);
            var rotation = user.getRotationVec(1f);
            var velocity = rotation.toVector3f().rotate(quaternion);

            knifeEntity.setVelocity(velocity.x, velocity.y, velocity.z, 1.5f, 1f);

            if (playerEntity.getAbilities().creativeMode || i != 0) {
                knifeEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            world.spawnEntity(knifeEntity);
            if (i == 0)
                world.playSoundFromEntity(null, knifeEntity, KitchenProjectilesSounds.throwing(stack), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        if (!playerEntity.getAbilities().creativeMode)
            stack.decrement(1);

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }
}
