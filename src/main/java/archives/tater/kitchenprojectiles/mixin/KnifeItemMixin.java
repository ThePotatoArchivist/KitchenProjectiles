package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KitchenProjectilesSounds;
import archives.tater.kitchenprojectiles.KnifeEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
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
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity playerEntity)) return;
        if (getMaxUseTime(stack) - remainingUseTicks < 6) return;

		if (!world.isClient) {
			stack.damage(1, user, p -> p.sendToolBreakStatus(user.getActiveHand()));

			var multishot = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack) > 0;

			for (var i = multishot ? -1 : 0; i <= (multishot ? 1 : 0); i++) {
				var knifeEntity = new KnifeEntity(world, playerEntity, stack.copy(), i != 0);
				knifeEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw() + i * 15, 0.0F, 1.5f, 1.0F);
				if (playerEntity.getAbilities().creativeMode || i != 0) {
					knifeEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
				}

				world.spawnEntity(knifeEntity);
				if (i == 0)
					world.playSoundFromEntity(null, knifeEntity, KitchenProjectilesSounds.throwing(stack), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}

            if (!playerEntity.getAbilities().creativeMode)
                stack.decrement(1);
		}

		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
	}
}
