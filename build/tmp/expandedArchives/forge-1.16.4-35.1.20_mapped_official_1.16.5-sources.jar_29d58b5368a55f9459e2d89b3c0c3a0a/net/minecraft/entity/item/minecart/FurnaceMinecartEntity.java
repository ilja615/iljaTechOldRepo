package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
   private static final DataParameter<Boolean> DATA_ID_FUEL = EntityDataManager.defineId(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);
   private int fuel;
   public double xPush;
   public double zPush;
   private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

   public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> p_i50119_1_, World p_i50119_2_) {
      super(p_i50119_1_, p_i50119_2_);
   }

   public FurnaceMinecartEntity(World p_i1719_1_, double p_i1719_2_, double p_i1719_4_, double p_i1719_6_) {
      super(EntityType.FURNACE_MINECART, p_i1719_1_, p_i1719_2_, p_i1719_4_, p_i1719_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.FURNACE;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FUEL, false);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.xPush = 0.0D;
            this.zPush = 0.0D;
         }

         this.setHasFuel(this.fuel > 0);
      }

      if (this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaxSpeed() {
      return 0.2D;
   }

   public void destroy(DamageSource p_94095_1_) {
      super.destroy(p_94095_1_);
      if (!p_94095_1_.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, BlockState p_180460_2_) {
      double d0 = 1.0E-4D;
      double d1 = 0.001D;
      super.moveAlongTrack(p_180460_1_, p_180460_2_);
      Vector3d vector3d = this.getDeltaMovement();
      double d2 = getHorizontalDistanceSqr(vector3d);
      double d3 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (d3 > 1.0E-4D && d2 > 0.001D) {
         double d4 = (double)MathHelper.sqrt(d2);
         double d5 = (double)MathHelper.sqrt(d3);
         this.xPush = vector3d.x / d4 * d5;
         this.zPush = vector3d.z / d4 * d5;
      }

   }

   protected void applyNaturalSlowdown() {
      double d0 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (d0 > 1.0E-7D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.xPush /= d0;
         this.zPush /= d0;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush));
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
      }

      super.applyNaturalSlowdown();
   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ActionResultType ret = super.interact(p_184230_1_, p_184230_2_);
      if (ret.consumesAction()) return ret;
      ItemStack itemstack = p_184230_1_.getItemInHand(p_184230_2_);
      if (INGREDIENT.test(itemstack) && this.fuel + 3600 <= 32000) {
         if (!p_184230_1_.abilities.instabuild) {
            itemstack.shrink(1);
         }

         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.xPush = this.getX() - p_184230_1_.getX();
         this.zPush = this.getZ() - p_184230_1_.getZ();
      }

      return ActionResultType.sidedSuccess(this.level.isClientSide);
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putDouble("PushX", this.xPush);
      p_213281_1_.putDouble("PushZ", this.zPush);
      p_213281_1_.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.xPush = p_70037_1_.getDouble("PushX");
      this.zPush = p_70037_1_.getDouble("PushZ");
      this.fuel = p_70037_1_.getShort("Fuel");
   }

   protected boolean hasFuel() {
      return this.entityData.get(DATA_ID_FUEL);
   }

   protected void setHasFuel(boolean p_94107_1_) {
      this.entityData.set(DATA_ID_FUEL, p_94107_1_);
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH).setValue(FurnaceBlock.LIT, Boolean.valueOf(this.hasFuel()));
   }
}
