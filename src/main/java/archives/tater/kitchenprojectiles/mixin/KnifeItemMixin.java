package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.item.KnifeItem;

@Mixin(KnifeItem.class)
public abstract class KnifeItemMixin extends Item {

	public KnifeItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		return TypedActionResult.consume(user.getStackInHand(hand));
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
        if (getMaxUseTime(stack) - remainingUseTicks < 10) return;

		if (!world.isClient) {
			stack.damage(1, user, p -> p.sendToolBreakStatus(user.getActiveHand()));

			var knifeEntity = new KnifeEntity(world, playerEntity, stack);
			knifeEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 1.5f, 1.0F);
			if (playerEntity.getAbilities().creativeMode) {
				knifeEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
			}

			world.spawnEntity(knifeEntity);
			world.playSoundFromEntity(null, knifeEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}

		if (!playerEntity.getAbilities().creativeMode)
			playerEntity.getInventory().removeOne(stack);

		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
	}
}
