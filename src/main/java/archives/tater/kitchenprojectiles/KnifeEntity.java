package archives.tater.kitchenprojectiles;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.item.enchantment.BackstabbingEnchantment;

public class KnifeEntity extends PersistentProjectileEntity {
    private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> KNIFE_STACK = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Boolean> SIMULATED = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private boolean dealtDamage;
    private int slot = -1;
    public int returnTimer;

    protected KnifeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public KnifeEntity(World world, LivingEntity owner, ItemStack stack, boolean simulated) {
        super(KitchenProjectiles.KNIFE_ENTITY, owner, world);
        dataTracker.set(KNIFE_STACK, stack);
        dataTracker.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
        dataTracker.set(ENCHANTED, stack.hasGlint());
        dataTracker.set(SIMULATED, simulated);
        if (owner instanceof PlayerEntity playerEntity) {
            var inventory = playerEntity.getInventory();
            var size = inventory.size();
            for (int iSlot = 0; iSlot < size; iSlot++)
                if (inventory.getStack(iSlot) == stack) {
                    slot = iSlot;
                    break;
                }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(LOYALTY, (byte)0);
        dataTracker.startTracking(ENCHANTED, false);
        dataTracker.startTracking(KNIFE_STACK, KitchenProjectiles.IRON_KNIFE.get().getDefaultStack());
        dataTracker.startTracking(SIMULATED, false);
    }

    @Override
    public void tick() {
        if (inGroundTime > 4) {
            dealtDamage = true;
        }

        Entity entity = getOwner();
        var loyaltyLevel = dataTracker.get(LOYALTY);
        if (loyaltyLevel > 0 && !dataTracker.get(SIMULATED) && (dealtDamage || isNoClip()) && entity != null) {
            if (!isOwnerAlive()) {
                if (!getWorld().isClient && pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    dropStack(asItemStack(), 0.1F);
                }

                discard();
            } else {
                setNoClip(true);
                Vec3d vec3d = entity.getEyePos().subtract(getPos());
                setPos(getX(), getY() + vec3d.y * 0.015 * (double) loyaltyLevel, getZ());
                if (getWorld().isClient) {
                    lastRenderY = getY();
                }

                double d = 0.05 * (double) loyaltyLevel;
                setVelocity(getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (returnTimer == 0) {
                    playSound(KitchenProjectilesSounds.returning(getKnifeStack()), 10.0F, 1.0F);
                    setVelocity(0, 0, 0);
                }

                returnTimer++;
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity owner = getOwner();
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayerEntity) || !owner.isSpectator());
    }

    public ItemStack getKnifeStack() {
        return dataTracker.get(KNIFE_STACK);
    }

    protected void setKnifeStack(ItemStack stack) {
        dataTracker.set(KNIFE_STACK, stack);
    }

    public boolean isSimulated() {
        return dataTracker.get(SIMULATED);
    }

    @Override
    protected ItemStack asItemStack() {
        return getKnifeStack().copy();
    }

    @Nullable
    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        var entity = entityHitResult.getEntity();
        float damage = (1
                + (getKnifeStack().getItem() instanceof KnifeItem knifeItem ? knifeItem.getAttackDamage() : 0)
                + (entity instanceof LivingEntity livingEntity ? EnchantmentHelper.getAttackDamage(getKnifeStack(), livingEntity.getGroup()) : 0));

        var backstabbingLevel = EnchantmentHelper.getLevel(KitchenProjectiles.BACKSTABBING.get(), getKnifeStack());
        if (backstabbingLevel > 0 && entity instanceof LivingEntity livingEntity && BackstabbingEnchantment.isLookingBehindTarget(livingEntity, getPos())) {
            damage = BackstabbingEnchantment.getBackstabbingDamagePerLevel(damage, backstabbingLevel);
        }

        var owner = getOwner();
        var damageSource = getDamageSources().create(KitchenProjectiles.KNIFE_DAMAGE, this, owner == null ? this : owner);
        dealtDamage = true;
        if (entity.damage(damageSource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity2) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity2, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity) owner, livingEntity2);
                }

                onHit(livingEntity2);
            }
        }

        setVelocity(getVelocity().multiply(-0.01, -0.1, -0.01));

        playSound(KitchenProjectilesSounds.hit(getKnifeStack()), 1.0F, 1.0F);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        var inventory = player.getInventory();
        var stack = asItemStack();
        return switch (this.pickupType) {
            case ALLOWED -> insertStack(inventory, slot, stack);
            case CREATIVE_ONLY -> player.getAbilities().creativeMode;
            default -> false;
        } || isNoClip() && isOwner(player) && insertStack(inventory, slot, stack);
    }

    private static boolean insertStack(PlayerInventory playerInventory, int slot, ItemStack stack) {
        if (playerInventory.getStack(slot).isEmpty()) {
            playerInventory.setStack(slot, stack);
            return true;
        }
        return playerInventory.insertStack(stack);
    }

    @Override
    protected SoundEvent getHitSound() {
        return KitchenProjectilesSounds.hitGround(getKnifeStack());
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (isOwner(player) || getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Knife", NbtElement.COMPOUND_TYPE)) {
            setKnifeStack(ItemStack.fromNbt(nbt.getCompound("Knife")));
        }

        dealtDamage = nbt.getBoolean("DealtDamage");
        dataTracker.set(SIMULATED, nbt.getBoolean("Simulated"));
        dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(getKnifeStack()));
        slot = nbt.getInt("Slot");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Knife", getKnifeStack().writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", dealtDamage);
        nbt.putBoolean("Simulated", dataTracker.get(SIMULATED));
        nbt.putInt("Slot", slot);
    }

    @Override
    public void age() {
        if (pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED) {
            super.age();
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Text getName() {
        return getKnifeStack().getName();
    }

    @Override
    protected void tickInVoid() {
        if (dataTracker.get(LOYALTY) <= 0) super.tickInVoid();
        if (!dealtDamage) {
            dealtDamage = true;
            setVelocity(0, 0, 0);
        }
    }
}
