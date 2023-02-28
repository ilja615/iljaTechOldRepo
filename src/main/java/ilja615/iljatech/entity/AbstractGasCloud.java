package ilja615.iljatech.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public abstract class AbstractGasCloud extends Entity
{
    public AbstractGasCloud(EntityType<? extends AbstractGasCloud> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    abstract SimpleParticleType getParticle();
    abstract int maxLifeTime();

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void tick()
    {
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.level.isClientSide)
        {
            if (random.nextFloat() < 0.25 - (((float)this.tickCount) / 2000.0f)) this.level.addParticle(this.getParticle(), this.getX() + r(), this.getY() + r(), this.getZ() + r(), 0.0d, 0.0d, 0.0d);
        } else {
            if (this.tickCount >= this.maxLifeTime()) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
        }
    }

    private float r()
    {
        return random.nextFloat()*0.6f - 0.3f;
    }
}