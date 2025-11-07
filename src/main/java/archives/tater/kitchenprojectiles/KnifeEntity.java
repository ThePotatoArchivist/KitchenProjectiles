package archives.tater.kitchenprojectiles;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.item.enchantment.BackstabbingEnchantment;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModItems;

public class KnifeEntity extends AbstractArrow {
    private static final EntityDataAccessor<Byte> LOYALTY = SynchedEntityData.defineId(KnifeEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<ItemStack> TRACKED_STACK = SynchedEntityData.defineId(KnifeEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DEALT_DAMAGE = SynchedEntityData.defineId(KnifeEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean hasHit;
    private int slot = -1;
    public int returnTimer;

    protected KnifeEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public KnifeEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(KitchenProjectiles.KNIFE_ENTITY, owner, level, stack, null);
        entityData.set(TRACKED_STACK, getPickupItemStackOrigin()); // minecraft:intangible_projectile is removed from instance but not copy
        updateLoyalty();
        if (owner instanceof Player playerEntity) {
            var inventory = playerEntity.getInventory();
            var size = inventory.getContainerSize();
            for (int iSlot = 0; iSlot < size; iSlot++)
                if (inventory.getItem(iSlot) == stack) {
                    slot = iSlot;
                    break;
                }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LOYALTY, (byte)0);
        builder.define(TRACKED_STACK, getDefaultPickupItem());
        builder.define(DEALT_DAMAGE, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (data == TRACKED_STACK)
            setSoundEvent(getDefaultHitGroundSoundEvent());
    }

    public boolean hasDealtDamage() {
        return entityData.get(DEALT_DAMAGE);
    }

    private void setDealtDamage(boolean dealtDamage) {
        entityData.set(DEALT_DAMAGE, dealtDamage);
    }

    private void updateLoyalty() {
        var owner = getOwner();
        entityData.set(LOYALTY, owner != null && level() instanceof ServerLevel serverLevel ? (byte) EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, getPickupItemStackOrigin(), owner) : 0);
    }

    private int getLoyalty() {
        return entityData.get(LOYALTY);
    }

    public boolean isIntangible() {
        // Called on both sides
        return getStackClient().has(DataComponents.INTANGIBLE_PROJECTILE);
    }

    @Override
    public void tick() {
        if (inGroundTime > 4) {
            hasHit = true;
            if (!EnchantmentHelper.has(getStackClient(), ModDataComponents.BACKSTABBING.get()))
                setDealtDamage(true);
        }

        Entity entity = getOwner();
        var loyaltyLevel = getLoyalty();
        if (loyaltyLevel > 0 && !isIntangible() && (hasHit || isNoPhysics()) && entity != null) {
            if (!isOwnerAlive()) {
                if (!level().isClientSide && pickup == AbstractArrow.Pickup.ALLOWED) {
                    spawnAtLocation(getPickupItem(), 0.1F);
                }

                discard();
            } else {
                setNoPhysics(true);
                Vec3 vec3d = entity.getEyePosition().subtract(position());
                setPosRaw(getX(), getY() + vec3d.y * 0.015 * (double) loyaltyLevel, getZ());
                if (level().isClientSide) {
                    yOld = getY();
                }

                double d = 0.05 * (double) loyaltyLevel;
                setDeltaMovement(getDeltaMovement().scale(0.95).add(vec3d.normalize().scale(d)));
                if (returnTimer == 0) {
                    playSound(KitchenProjectilesSounds.returning(getPickupItemStackOrigin()), 10.0F, 1.0F);
                    setDeltaMovement(0, 0, 0);
                }

                returnTimer++;
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity owner = getOwner();
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayer) || !owner.isSpectator());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ModItems.IRON_KNIFE.get().getDefaultInstance();
    }

    public ItemStack getStackClient() {
        return entityData.get(TRACKED_STACK);
    }

    @Override
    protected void setPickupItemStack(ItemStack stack) {
        super.setPickupItemStack(stack);
        entityData.set(TRACKED_STACK, stack);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (entity != getOwner() || !noPhysics);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 currentPosition, Vec3 nextPosition) {
        return hasDealtDamage() ? null : super.findHitEntity(currentPosition, nextPosition);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        var entity = entityHitResult.getEntity();
        var owner = getOwner();
        var stack = getPickupItemStackOrigin();
        var damageSource = damageSources().source(KitchenProjectiles.KNIFE_DAMAGE, this, owner == null ? this : owner);

        var damage = KitchenProjectilesUtil.getDamage(stack, damageSource, level(), entity);

        if (entity instanceof LivingEntity livingEntity && BackstabbingEnchantment.isLookingBehindTarget(livingEntity, position()) && level() instanceof ServerLevel serverLevel) {
            var dmg = new MutableFloat(damage);
            EnchantmentHelper.runIterationOnItem(getPickupItemStackOrigin(), (enchantment, powerLevel) ->
                    enchantment.value().modifyDamageFilteredValue(ModDataComponents.BACKSTABBING.get(), serverLevel, powerLevel, stack, this, damageSource, dmg)
            );

            if (damage != dmg.getValue()) {
                damage = dmg.getValue();
                serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        hasHit = true;
        setDealtDamage(true);
        if (entity.hurt(damageSource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity2) {
                if (owner instanceof LivingEntity && level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, entity, damageSource, stack);
                }

                doPostHurtEffects(livingEntity2);
            }
        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));

        playSound(KitchenProjectilesSounds.hit(getPickupItemStackOrigin()), 1.0F, 1.0F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult blockHitResult, ItemStack weaponStack) {
        EnchantmentHelper.onHitBlock(level,
                weaponStack,
                this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null,
                this,
                null,
                blockHitResult.getBlockPos().clampLocationWithin(blockHitResult.getLocation()),
                level.getBlockState(blockHitResult.getBlockPos()),
                item -> kill());
    }

    @Override
    protected boolean tryPickup(Player player) {
        var inventory = player.getInventory();
        var stack = getPickupItem();
        return switch (this.pickup) {
            case ALLOWED -> insertStack(inventory, slot, stack);
            case CREATIVE_ONLY -> player.getAbilities().instabuild;
            default -> false;
        } || isNoPhysics() && ownedBy(player) && insertStack(inventory, slot, stack);
    }

    private static boolean insertStack(Inventory playerInventory, int slot, ItemStack stack) {
        if (slot >= 0 && playerInventory.getItem(slot).isEmpty()) {
            playerInventory.setItem(slot, stack);
            return true;
        }
        return playerInventory.add(stack);
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return KitchenProjectilesSounds.hitGround(getStackClient());
    }

    @Override
    public void playerTouch(Player player) {
        if (ownedBy(player) || getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        hasHit = nbt.getBoolean("HasHit");
        setDealtDamage(nbt.getBoolean("DealtDamage"));
        updateLoyalty();
        slot = nbt.getInt("Slot");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("HasHit", hasHit);
        nbt.putBoolean("DealtDamage", hasDealtDamage());
        nbt.putInt("Slot", slot);
    }

    @Override
    public void tickDespawn() {
        if (pickup != AbstractArrow.Pickup.ALLOWED) {
            super.tickDespawn();
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public @NotNull Component getName() {
        return getStackClient().getHoverName();
    }

    @Override
    protected void onBelowWorld() {
        if (getLoyalty() <= 0) super.onBelowWorld();
        if (!hasHit) {
            hasHit = true;
            setDeltaMovement(0, 0, 0);
        }
    }
}
