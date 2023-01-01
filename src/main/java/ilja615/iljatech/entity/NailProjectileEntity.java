package ilja615.iljatech.entity;

import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class NailProjectileEntity extends ThrowableItemProjectile implements ItemSupplier
{
    public NailProjectileEntity(EntityType<NailProjectileEntity> p_i50159_1_, Level p_i50159_2_) { super(p_i50159_1_, p_i50159_2_); }

    @Override
    protected Item getDefaultItem()
    {
        return ModBlocks.IRON_NAILS.get().asItem();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_)
    {
        if (p_70103_1_ == 3)
        {
            for(int lvt_4_1_ = 0; lvt_4_1_ < 8; ++lvt_4_1_)
            {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 1.0f);
    }

    @Override
    protected void onHit(HitResult result)
    {
        super.onHit(result);
        if (!this.level.isClientSide)
        {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
