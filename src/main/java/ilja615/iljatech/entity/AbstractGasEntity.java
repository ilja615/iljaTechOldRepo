package ilja615.iljatech.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractGasEntity extends Entity
{
    public AbstractGasEntity(EntityType<? extends AbstractGasEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    abstract BasicParticleType getParticle();
    abstract int maxLifeTime();

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
                this.remove();
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