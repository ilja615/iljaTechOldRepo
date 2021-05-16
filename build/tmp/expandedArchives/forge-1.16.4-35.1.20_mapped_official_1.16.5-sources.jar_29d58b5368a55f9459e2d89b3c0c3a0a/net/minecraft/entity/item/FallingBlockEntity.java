package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlockEntity extends Entity {
   private BlockState blockState = Blocks.SAND.defaultBlockState();
   public int time;
   public boolean dropItem = true;
   private boolean cancelDrop;
   private boolean hurtEntities;
   private int fallDamageMax = 40;
   private float fallDamageAmount = 2.0F;
   public CompoundNBT blockData;
   protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(FallingBlockEntity.class, DataSerializers.BLOCK_POS);

   public FallingBlockEntity(EntityType<? extends FallingBlockEntity> p_i50218_1_, World p_i50218_2_) {
      super(p_i50218_1_, p_i50218_2_);
   }

   public FallingBlockEntity(World p_i45848_1_, double p_i45848_2_, double p_i45848_4_, double p_i45848_6_, BlockState p_i45848_8_) {
      this(EntityType.FALLING_BLOCK, p_i45848_1_);
      this.blockState = p_i45848_8_;
      this.blocksBuilding = true;
      this.setPos(p_i45848_2_, p_i45848_4_ + (double)((1.0F - this.getBbHeight()) / 2.0F), p_i45848_6_);
      this.setDeltaMovement(Vector3d.ZERO);
      this.xo = p_i45848_2_;
      this.yo = p_i45848_4_;
      this.zo = p_i45848_6_;
      this.setStartPos(this.blockPosition());
   }

   public boolean isAttackable() {
      return false;
   }

   public void setStartPos(BlockPos p_184530_1_) {
      this.entityData.set(DATA_START_POS, p_184530_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getStartPos() {
      return this.entityData.get(DATA_START_POS);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_START_POS, BlockPos.ZERO);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public void tick() {
      if (this.blockState.isAir()) {
         this.remove();
      } else {
         Block block = this.blockState.getBlock();
         if (this.time++ == 0) {
            BlockPos blockpos = this.blockPosition();
            if (this.level.getBlockState(blockpos).is(block)) {
               this.level.removeBlock(blockpos, false);
            } else if (!this.level.isClientSide) {
               this.remove();
               return;
            }
         }

         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         if (!this.level.isClientSide) {
            BlockPos blockpos1 = this.blockPosition();
            boolean flag = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean flag1 = flag && this.level.getFluidState(blockpos1).is(FluidTags.WATER);
            double d0 = this.getDeltaMovement().lengthSqr();
            if (flag && d0 > 1.0D) {
               BlockRayTraceResult blockraytraceresult = this.level.clip(new RayTraceContext(new Vector3d(this.xo, this.yo, this.zo), this.position(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
               if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.level.getFluidState(blockraytraceresult.getBlockPos()).is(FluidTags.WATER)) {
                  blockpos1 = blockraytraceresult.getBlockPos();
                  flag1 = true;
               }
            }

            if (!this.onGround && !flag1) {
               if (!this.level.isClientSide && (this.time > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.time > 600)) {
                  if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     this.spawnAtLocation(block);
                  }

                  this.remove();
               }
            } else {
               BlockState blockstate = this.level.getBlockState(blockpos1);
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
               if (!blockstate.is(Blocks.MOVING_PISTON)) {
                  this.remove();
                  if (!this.cancelDrop) {
                     boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                     boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos1.below())) && (!flag || !flag1);
                     boolean flag4 = this.blockState.canSurvive(this.level, blockpos1) && !flag3;
                     if (flag2 && flag4) {
                        if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(blockpos1).getType() == Fluids.WATER) {
                           this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                        }

                        if (this.level.setBlock(blockpos1, this.blockState, 3)) {
                           if (block instanceof FallingBlock) {
                              ((FallingBlock)block).onLand(this.level, blockpos1, this.blockState, blockstate, this);
                           }

                           if (this.blockData != null && this.blockState.hasTileEntity()) {
                              TileEntity tileentity = this.level.getBlockEntity(blockpos1);
                              if (tileentity != null) {
                                 CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());

                                 for(String s : this.blockData.getAllKeys()) {
                                    INBT inbt = this.blockData.get(s);
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                       compoundnbt.put(s, inbt.copy());
                                    }
                                 }

                                 tileentity.load(this.blockState, compoundnbt);
                                 tileentity.setChanged();
                              }
                           }
                        } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.spawnAtLocation(block);
                        }
                     } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(block);
                     }
                  } else if (block instanceof FallingBlock) {
                     ((FallingBlock)block).onBroken(this.level, blockpos1, this);
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
      }
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      if (this.hurtEntities) {
         int i = MathHelper.ceil(p_225503_1_ - 1.0F);
         if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
            boolean flag = this.blockState.is(BlockTags.ANVIL);
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for(Entity entity : list) {
               entity.hurt(damagesource, (float)Math.min(MathHelper.floor((float)i * this.fallDamageAmount), this.fallDamageMax));
            }

            if (flag && (double)this.random.nextFloat() < (double)0.05F + (double)i * 0.05D) {
               BlockState blockstate = AnvilBlock.damage(this.blockState);
               if (blockstate == null) {
                  this.cancelDrop = true;
               } else {
                  this.blockState = blockstate;
               }
            }
         }
      }

      return false;
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.put("BlockState", NBTUtil.writeBlockState(this.blockState));
      p_213281_1_.putInt("Time", this.time);
      p_213281_1_.putBoolean("DropItem", this.dropItem);
      p_213281_1_.putBoolean("HurtEntities", this.hurtEntities);
      p_213281_1_.putFloat("FallHurtAmount", this.fallDamageAmount);
      p_213281_1_.putInt("FallHurtMax", this.fallDamageMax);
      if (this.blockData != null) {
         p_213281_1_.put("TileEntityData", this.blockData);
      }

   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.blockState = NBTUtil.readBlockState(p_70037_1_.getCompound("BlockState"));
      this.time = p_70037_1_.getInt("Time");
      if (p_70037_1_.contains("HurtEntities", 99)) {
         this.hurtEntities = p_70037_1_.getBoolean("HurtEntities");
         this.fallDamageAmount = p_70037_1_.getFloat("FallHurtAmount");
         this.fallDamageMax = p_70037_1_.getInt("FallHurtMax");
      } else if (this.blockState.is(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if (p_70037_1_.contains("DropItem", 99)) {
         this.dropItem = p_70037_1_.getBoolean("DropItem");
      }

      if (p_70037_1_.contains("TileEntityData", 10)) {
         this.blockData = p_70037_1_.getCompound("TileEntityData");
      }

      if (this.blockState.isAir()) {
         this.blockState = Blocks.SAND.defaultBlockState();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public World getLevel() {
      return this.level;
   }

   public void setHurtsEntities(boolean p_145806_1_) {
      this.hurtEntities = p_145806_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean displayFireAnimation() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory p_85029_1_) {
      super.fillCrashReportCategory(p_85029_1_);
      p_85029_1_.setDetail("Immitating BlockState", this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this, Block.getId(this.getBlockState()));
   }
}
