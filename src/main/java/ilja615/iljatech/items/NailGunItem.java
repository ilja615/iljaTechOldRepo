package ilja615.iljatech.items;

import ilja615.iljatech.entity.NailProjectile;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class NailGunItem extends ProjectileWeaponItem
{
    public static final Predicate<ItemStack> NAILS = (p_43017_) -> {
        return p_43017_.getItem() == ModBlocks.IRON_NAILS.get().asItem();
    };
    public static final Predicate<ItemStack> GUNPOWDER = (p_43017_) -> {
        return p_43017_.getItem() == Items.GUNPOWDER;
    };

    public NailGunItem(Properties properties)
    {
        super(properties);
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int p_40670_) {
        if (livingEntity instanceof Player player) {
            ItemStack itemstack = player.getProjectile(itemStack);
            ItemStack gunPowder = findGunPowder(player);

            int i = this.getUseDuration(itemStack) - p_40670_;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, level, player, i, !itemstack.isEmpty());
            if (i < 0) return;

            if (!itemstack.isEmpty() && !gunPowder.isEmpty()) {
                float f = getPowerForTime(i);
                if (!((double)f < 0.1D)) {
                    if (!level.isClientSide) {
                        NailProjectile nailProjectile = ModEntities.IRON_NAILS_PROJECTILE.get().create(level);
                        nailProjectile.setPos(player.position().x, player.position().y + 1, player.position().z);
                        Vec3 vec3 = player.getLookAngle();
                        nailProjectile.move(MoverType.SELF, vec3);
                        nailProjectile.setDeltaMovement(vec3.x * f * 3.0f, vec3.y * f * 3.0f, vec3.z * f * 3.0f);

                        itemStack.hurtAndBreak(1, player, (p_40665_) -> {
                            p_40665_.broadcastBreakEvent(player.getUsedItemHand());
                        });

                        level.addFreshEntity(nailProjectile);
                    }

                    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.getInventory().removeItem(itemstack);
                    }

                    gunPowder.shrink(1);
                    if (gunPowder.isEmpty()) {
                        player.getInventory().removeItem(gunPowder);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public static float getPowerForTime(int p_40662_) {
        float f = (float)p_40662_ / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getUseDuration(ItemStack p_40680_) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        boolean flag = !p_40673_.getProjectile(itemstack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, p_40672_, p_40673_, p_40674_, flag);
        if (ret != null) return ret;

        if (!p_40673_.getAbilities().instabuild && !flag) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            p_40673_.startUsingItem(p_40674_);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return NAILS;
    }

    public int getDefaultProjectileRange() {
        return 15;
    }

    public ItemStack findGunPowder(Player player)
    {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack itemstack = player.getInventory().getItem(i);
            if (GUNPOWDER.test(itemstack)) {
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }
}