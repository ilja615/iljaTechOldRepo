package ilja615.iljatech.entity;

import ilja615.iljatech.init.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class ElectricFishEntity extends AbstractSchoolingFish
{
    public ElectricFishEntity(EntityType<? extends ElectricFishEntity> p_28276_, Level p_28277_) {
        super(p_28276_, p_28277_);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(ModItems.ELECTRIC_FISH_BUCKET.get());
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.COD_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.COD_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_)
    {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.COD_FLOP;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
