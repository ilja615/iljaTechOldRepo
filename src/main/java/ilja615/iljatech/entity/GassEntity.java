package ilja615.iljatech.entity;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class GassEntity extends Entity {
    public GassEntity(EntityType<? extends GassEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.world.isRemote)
        {
            if (rand.nextFloat() < 0.25 - (((float)this.ticksExisted) / 2000.0f)) this.world.addParticle(ModParticles.STEAM_PARTICLE.get(), this.getPosX() + r(), this.getPosY() + r(), this.getPosZ() + r(), 0.0d, 0.0d, 0.0d);
        } else {
            if (this.ticksExisted >= 500) {
                this.remove();
                return;
            }
            this.prevPosY = this.getPosY();
            this.move(MoverType.SELF, new Vector3d(0.0f, 0.05f,0.0f));
        }
    }

    private float r()
    {
        return rand.nextFloat()*0.6f - 0.3f;
    }
}