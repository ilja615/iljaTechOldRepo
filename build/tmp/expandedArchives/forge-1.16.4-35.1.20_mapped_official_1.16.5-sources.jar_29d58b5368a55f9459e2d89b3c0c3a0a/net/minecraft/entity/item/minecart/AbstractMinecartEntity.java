package net.minecraft.entity.item.minecart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractMinecartEntity extends Entity implements net.minecraftforge.common.extensions.IForgeEntityMinecart {
   private static final DataParameter<Integer> DATA_ID_HURT = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> DATA_ID_HURTDIR = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.INT);
   private static final DataParameter<Float> DATA_ID_DAMAGE = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DATA_ID_DISPLAY_BLOCK = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> DATA_ID_DISPLAY_OFFSET = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> DATA_ID_CUSTOM_DISPLAY = EntityDataManager.defineId(AbstractMinecartEntity.class, DataSerializers.BOOLEAN);
   private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));
   private boolean flipped;
   private static final Map<RailShape, Pair<Vector3i, Vector3i>> EXITS = Util.make(Maps.newEnumMap(RailShape.class), (p_226574_0_) -> {
      Vector3i vector3i = Direction.WEST.getNormal();
      Vector3i vector3i1 = Direction.EAST.getNormal();
      Vector3i vector3i2 = Direction.NORTH.getNormal();
      Vector3i vector3i3 = Direction.SOUTH.getNormal();
      Vector3i vector3i4 = vector3i.below();
      Vector3i vector3i5 = vector3i1.below();
      Vector3i vector3i6 = vector3i2.below();
      Vector3i vector3i7 = vector3i3.below();
      p_226574_0_.put(RailShape.NORTH_SOUTH, Pair.of(vector3i2, vector3i3));
      p_226574_0_.put(RailShape.EAST_WEST, Pair.of(vector3i, vector3i1));
      p_226574_0_.put(RailShape.ASCENDING_EAST, Pair.of(vector3i4, vector3i1));
      p_226574_0_.put(RailShape.ASCENDING_WEST, Pair.of(vector3i, vector3i5));
      p_226574_0_.put(RailShape.ASCENDING_NORTH, Pair.of(vector3i2, vector3i7));
      p_226574_0_.put(RailShape.ASCENDING_SOUTH, Pair.of(vector3i6, vector3i3));
      p_226574_0_.put(RailShape.SOUTH_EAST, Pair.of(vector3i3, vector3i1));
      p_226574_0_.put(RailShape.SOUTH_WEST, Pair.of(vector3i3, vector3i));
      p_226574_0_.put(RailShape.NORTH_WEST, Pair.of(vector3i2, vector3i));
      p_226574_0_.put(RailShape.NORTH_EAST, Pair.of(vector3i2, vector3i1));
   });
   private int lSteps;
   private double lx;
   private double ly;
   private double lz;
   private double lyr;
   private double lxr;
   @OnlyIn(Dist.CLIENT)
   private double lxd;
   @OnlyIn(Dist.CLIENT)
   private double lyd;
   @OnlyIn(Dist.CLIENT)
   private double lzd;
   private boolean canBePushed = true;

   protected AbstractMinecartEntity(EntityType<?> p_i48538_1_, World p_i48538_2_) {
      super(p_i48538_1_, p_i48538_2_);
      this.blocksBuilding = true;
   }

   protected AbstractMinecartEntity(EntityType<?> p_i48539_1_, World p_i48539_2_, double p_i48539_3_, double p_i48539_5_, double p_i48539_7_) {
      this(p_i48539_1_, p_i48539_2_);
      this.setPos(p_i48539_3_, p_i48539_5_, p_i48539_7_);
      this.setDeltaMovement(Vector3d.ZERO);
      this.xo = p_i48539_3_;
      this.yo = p_i48539_5_;
      this.zo = p_i48539_7_;
   }

   public static AbstractMinecartEntity createMinecart(World p_184263_0_, double p_184263_1_, double p_184263_3_, double p_184263_5_, AbstractMinecartEntity.Type p_184263_7_) {
      if (p_184263_7_ == AbstractMinecartEntity.Type.CHEST) {
         return new ChestMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.FURNACE) {
         return new FurnaceMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.TNT) {
         return new TNTMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.SPAWNER) {
         return new SpawnerMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.HOPPER) {
         return new HopperMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else {
         return (AbstractMinecartEntity)(p_184263_7_ == AbstractMinecartEntity.Type.COMMAND_BLOCK ? new CommandBlockMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_) : new MinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_));
      }
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_HURT, 0);
      this.entityData.define(DATA_ID_HURTDIR, 1);
      this.entityData.define(DATA_ID_DAMAGE, 0.0F);
      this.entityData.define(DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
      this.entityData.define(DATA_ID_DISPLAY_OFFSET, 6);
      this.entityData.define(DATA_ID_CUSTOM_DISPLAY, false);
   }

   public boolean canCollideWith(Entity p_241849_1_) {
      return BoatEntity.canVehicleCollide(this, p_241849_1_);
   }

   public boolean isPushable() {
      return canBePushed;
   }

   protected Vector3d getRelativePortalPosition(Direction.Axis p_241839_1_, TeleportationRepositioner.Result p_241839_2_) {
      return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(p_241839_1_, p_241839_2_));
   }

   public double getPassengersRidingOffset() {
      return 0.0D;
   }

   public Vector3d getDismountLocationForPassenger(LivingEntity p_230268_1_) {
      Direction direction = this.getMotionDirection();
      if (direction.getAxis() == Direction.Axis.Y) {
         return super.getDismountLocationForPassenger(p_230268_1_);
      } else {
         int[][] aint = TransportationHelper.offsetsForDirection(direction);
         BlockPos blockpos = this.blockPosition();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         ImmutableList<Pose> immutablelist = p_230268_1_.getDismountPoses();

         for(Pose pose : immutablelist) {
            EntitySize entitysize = p_230268_1_.getDimensions(pose);
            float f = Math.min(entitysize.width, 1.0F) / 2.0F;

            for(int i : POSE_DISMOUNT_HEIGHTS.get(pose)) {
               for(int[] aint1 : aint) {
                  blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY() + i, blockpos.getZ() + aint1[1]);
                  double d0 = this.level.getBlockFloorHeight(TransportationHelper.nonClimbableShape(this.level, blockpos$mutable), () -> {
                     return TransportationHelper.nonClimbableShape(this.level, blockpos$mutable.below());
                  });
                  if (TransportationHelper.isBlockFloorValid(d0)) {
                     AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)(-f), 0.0D, (double)(-f), (double)f, (double)entitysize.height, (double)f);
                     Vector3d vector3d = Vector3d.upFromBottomCenterOf(blockpos$mutable, d0);
                     if (TransportationHelper.canDismountTo(this.level, p_230268_1_, axisalignedbb.move(vector3d))) {
                        p_230268_1_.setPose(pose);
                        return vector3d;
                     }
                  }
               }
            }
         }

         double d1 = this.getBoundingBox().maxY;
         blockpos$mutable.set((double)blockpos.getX(), d1, (double)blockpos.getZ());

         for(Pose pose1 : immutablelist) {
            double d2 = (double)p_230268_1_.getDimensions(pose1).height;
            int j = MathHelper.ceil(d1 - (double)blockpos$mutable.getY() + d2);
            double d3 = TransportationHelper.findCeilingFrom(blockpos$mutable, j, (p_242377_1_) -> {
               return this.level.getBlockState(p_242377_1_).getCollisionShape(this.level, p_242377_1_);
            });
            if (d1 + d2 <= d3) {
               p_230268_1_.setPose(pose1);
               break;
            }
         }

         return super.getDismountLocationForPassenger(p_230268_1_);
      }
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.level.isClientSide && !this.removed) {
         if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
         } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + p_70097_2_ * 10.0F);
            boolean flag = p_70097_1_.getEntity() instanceof PlayerEntity && ((PlayerEntity)p_70097_1_.getEntity()).abilities.instabuild;
            if (flag || this.getDamage() > 40.0F) {
               this.ejectPassengers();
               if (flag && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.destroy(p_70097_1_);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   protected float getBlockSpeedFactor() {
      BlockState blockstate = this.level.getBlockState(this.blockPosition());
      return blockstate.is(BlockTags.RAILS) ? 1.0F : super.getBlockSpeedFactor();
   }

   public void destroy(DamageSource p_94095_1_) {
      this.remove();
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         ItemStack itemstack = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            itemstack.setHoverName(this.getCustomName());
         }

         this.spawnAtLocation(itemstack);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateHurt() {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   private static Pair<Vector3i, Vector3i> exits(RailShape p_226573_0_) {
      return EXITS.get(p_226573_0_);
   }

   public Direction getMotionDirection() {
      return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
   }

   public void tick() {
      if (this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.getY() < -64.0D) {
         this.outOfWorld();
      }

      this.handleNetherPortal();
      if (this.level.isClientSide) {
         if (this.lSteps > 0) {
            double d4 = this.getX() + (this.lx - this.getX()) / (double)this.lSteps;
            double d5 = this.getY() + (this.ly - this.getY()) / (double)this.lSteps;
            double d6 = this.getZ() + (this.lz - this.getZ()) / (double)this.lSteps;
            double d1 = MathHelper.wrapDegrees(this.lyr - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + d1 / (double)this.lSteps);
            this.xRot = (float)((double)this.xRot + (this.lxr - (double)this.xRot) / (double)this.lSteps);
            --this.lSteps;
            this.setPos(d4, d5, d6);
            this.setRot(this.yRot, this.xRot);
         } else {
            this.reapplyPosition();
            this.setRot(this.yRot, this.xRot);
         }

      } else {
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         int i = MathHelper.floor(this.getX());
         int j = MathHelper.floor(this.getY());
         int k = MathHelper.floor(this.getZ());
         if (this.level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            --j;
         }

         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (canUseRail() && AbstractRailBlock.isRail(blockstate)) {
            this.moveAlongTrack(blockpos, blockstate);
            if (blockstate.getBlock() instanceof PoweredRailBlock && ((PoweredRailBlock) blockstate.getBlock()).isActivatorRail()) {
               this.activateMinecart(i, j, k, blockstate.getValue(PoweredRailBlock.POWERED));
            }
         } else {
            this.comeOffTrack();
         }

         this.checkInsideBlocks();
         this.xRot = 0.0F;
         double d0 = this.xo - this.getX();
         double d2 = this.zo - this.getZ();
         if (d0 * d0 + d2 * d2 > 0.001D) {
            this.yRot = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
            if (this.flipped) {
               this.yRot += 180.0F;
            }
         }

         double d3 = (double)MathHelper.wrapDegrees(this.yRot - this.yRotO);
         if (d3 < -170.0D || d3 >= 170.0D) {
            this.yRot += 180.0F;
            this.flipped = !this.flipped;
         }

         this.setRot(this.yRot, this.xRot);
         AxisAlignedBB box;
         if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
         else                               box = this.getBoundingBox().inflate(0.2F, 0.0D, 0.2F);
         if (canBeRidden() && getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.01D) {
            List<Entity> list = this.level.getEntities(this, box, EntityPredicates.pushableBy(this));
            if (!list.isEmpty()) {
               for(int l = 0; l < list.size(); ++l) {
                  Entity entity1 = list.get(l);
                  if (!(entity1 instanceof PlayerEntity) && !(entity1 instanceof IronGolemEntity) && !(entity1 instanceof AbstractMinecartEntity) && !this.isVehicle() && !entity1.isPassenger()) {
                     entity1.startRiding(this);
                  } else {
                     entity1.push(this);
                  }
               }
            }
         } else {
            for(Entity entity : this.level.getEntities(this, box)) {
               if (!this.hasPassenger(entity) && entity.isPushable() && entity instanceof AbstractMinecartEntity) {
                  entity.push(this);
               }
            }
         }

         this.updateInWaterStateAndDoFluidPushing();
         if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5F;
         }

         this.firstTick = false;
      }
   }

   protected double getMaxSpeed() {
      return 0.4D;
   }

   public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
   }

   protected void comeOffTrack() {
      double d0 = onGround ? this.getMaxSpeed() : getMaxSpeedAirLateral();
      Vector3d vector3d = this.getDeltaMovement();
      this.setDeltaMovement(MathHelper.clamp(vector3d.x, -d0, d0), vector3d.y, MathHelper.clamp(vector3d.z, -d0, d0));
      if (this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      }

      if (getMaxSpeedAirVertical() > 0 && getDeltaMovement().y > getMaxSpeedAirVertical()) {
          if(Math.abs(getDeltaMovement().x) < 0.3f && Math.abs(getDeltaMovement().z) < 0.3f)
              setDeltaMovement(new Vector3d(getDeltaMovement().x, 0.15f, getDeltaMovement().z));
          else
              setDeltaMovement(new Vector3d(getDeltaMovement().x, getMaxSpeedAirVertical(), getDeltaMovement().z));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      if (!this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(getDragAir()));
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, BlockState p_180460_2_) {
      this.fallDistance = 0.0F;
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      Vector3d vector3d = this.getPos(d0, d1, d2);
      d1 = (double)p_180460_1_.getY();
      boolean flag = false;
      boolean flag1 = false;
      AbstractRailBlock abstractrailblock = (AbstractRailBlock)p_180460_2_.getBlock();
      if (abstractrailblock instanceof PoweredRailBlock && !((PoweredRailBlock) abstractrailblock).isActivatorRail()) {
         flag = p_180460_2_.getValue(PoweredRailBlock.POWERED);
         flag1 = !flag;
      }

      double d3 = 0.0078125D;
      Vector3d vector3d1 = this.getDeltaMovement();
      RailShape railshape = ((AbstractRailBlock)p_180460_2_.getBlock()).getRailDirection(p_180460_2_, this.level, p_180460_1_, this);
      switch(railshape) {
      case ASCENDING_EAST:
         this.setDeltaMovement(vector3d1.add(-1 * getSlopeAdjustment(), 0.0D, 0.0D));
         ++d1;
         break;
      case ASCENDING_WEST:
         this.setDeltaMovement(vector3d1.add(getSlopeAdjustment(), 0.0D, 0.0D));
         ++d1;
         break;
      case ASCENDING_NORTH:
         this.setDeltaMovement(vector3d1.add(0.0D, 0.0D, getSlopeAdjustment()));
         ++d1;
         break;
      case ASCENDING_SOUTH:
         this.setDeltaMovement(vector3d1.add(0.0D, 0.0D, -1 * getSlopeAdjustment()));
         ++d1;
      }

      vector3d1 = this.getDeltaMovement();
      Pair<Vector3i, Vector3i> pair = exits(railshape);
      Vector3i vector3i = pair.getFirst();
      Vector3i vector3i1 = pair.getSecond();
      double d4 = (double)(vector3i1.getX() - vector3i.getX());
      double d5 = (double)(vector3i1.getZ() - vector3i.getZ());
      double d6 = Math.sqrt(d4 * d4 + d5 * d5);
      double d7 = vector3d1.x * d4 + vector3d1.z * d5;
      if (d7 < 0.0D) {
         d4 = -d4;
         d5 = -d5;
      }

      double d8 = Math.min(2.0D, Math.sqrt(getHorizontalDistanceSqr(vector3d1)));
      vector3d1 = new Vector3d(d8 * d4 / d6, vector3d1.y, d8 * d5 / d6);
      this.setDeltaMovement(vector3d1);
      Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
      if (entity instanceof PlayerEntity) {
         Vector3d vector3d2 = entity.getDeltaMovement();
         double d9 = getHorizontalDistanceSqr(vector3d2);
         double d11 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if (d9 > 1.0E-4D && d11 < 0.01D) {
            this.setDeltaMovement(this.getDeltaMovement().add(vector3d2.x * 0.1D, 0.0D, vector3d2.z * 0.1D));
            flag1 = false;
         }
      }

      if (flag1 && shouldDoRailFunctions()) {
         double d22 = Math.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement()));
         if (d22 < 0.03D) {
            this.setDeltaMovement(Vector3d.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
         }
      }

      double d23 = (double)p_180460_1_.getX() + 0.5D + (double)vector3i.getX() * 0.5D;
      double d10 = (double)p_180460_1_.getZ() + 0.5D + (double)vector3i.getZ() * 0.5D;
      double d12 = (double)p_180460_1_.getX() + 0.5D + (double)vector3i1.getX() * 0.5D;
      double d13 = (double)p_180460_1_.getZ() + 0.5D + (double)vector3i1.getZ() * 0.5D;
      d4 = d12 - d23;
      d5 = d13 - d10;
      double d14;
      if (d4 == 0.0D) {
         d14 = d2 - (double)p_180460_1_.getZ();
      } else if (d5 == 0.0D) {
         d14 = d0 - (double)p_180460_1_.getX();
      } else {
         double d15 = d0 - d23;
         double d16 = d2 - d10;
         d14 = (d15 * d4 + d16 * d5) * 2.0D;
      }

      d0 = d23 + d4 * d14;
      d2 = d10 + d5 * d14;
      this.setPos(d0, d1, d2);
      this.moveMinecartOnRail(p_180460_1_);
      if (vector3i.getY() != 0 && MathHelper.floor(this.getX()) - p_180460_1_.getX() == vector3i.getX() && MathHelper.floor(this.getZ()) - p_180460_1_.getZ() == vector3i.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)vector3i.getY(), this.getZ());
      } else if (vector3i1.getY() != 0 && MathHelper.floor(this.getX()) - p_180460_1_.getX() == vector3i1.getX() && MathHelper.floor(this.getZ()) - p_180460_1_.getZ() == vector3i1.getZ()) {
         this.setPos(this.getX(), this.getY() + (double)vector3i1.getY(), this.getZ());
      }

      this.applyNaturalSlowdown();
      Vector3d vector3d3 = this.getPos(this.getX(), this.getY(), this.getZ());
      if (vector3d3 != null && vector3d != null) {
         double d17 = (vector3d.y - vector3d3.y) * 0.05D;
         Vector3d vector3d4 = this.getDeltaMovement();
         double d18 = Math.sqrt(getHorizontalDistanceSqr(vector3d4));
         if (d18 > 0.0D) {
            this.setDeltaMovement(vector3d4.multiply((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
         }

         this.setPos(this.getX(), vector3d3.y, this.getZ());
      }

      int j = MathHelper.floor(this.getX());
      int i = MathHelper.floor(this.getZ());
      if (j != p_180460_1_.getX() || i != p_180460_1_.getZ()) {
         Vector3d vector3d5 = this.getDeltaMovement();
         double d26 = Math.sqrt(getHorizontalDistanceSqr(vector3d5));
         this.setDeltaMovement(d26 * (double)(j - p_180460_1_.getX()), vector3d5.y, d26 * (double)(i - p_180460_1_.getZ()));
      }

      if (shouldDoRailFunctions())
          ((AbstractRailBlock)p_180460_2_.getBlock()).onMinecartPass(p_180460_2_, level, p_180460_1_, this);

      if (flag && shouldDoRailFunctions()) {
         Vector3d vector3d6 = this.getDeltaMovement();
         double d27 = Math.sqrt(getHorizontalDistanceSqr(vector3d6));
         if (d27 > 0.01D) {
            double d19 = 0.06D;
            this.setDeltaMovement(vector3d6.add(vector3d6.x / d27 * 0.06D, 0.0D, vector3d6.z / d27 * 0.06D));
         } else {
            Vector3d vector3d7 = this.getDeltaMovement();
            double d20 = vector3d7.x;
            double d21 = vector3d7.z;
            if (railshape == RailShape.EAST_WEST) {
               if (this.isRedstoneConductor(p_180460_1_.west())) {
                  d20 = 0.02D;
               } else if (this.isRedstoneConductor(p_180460_1_.east())) {
                  d20 = -0.02D;
               }
            } else {
               if (railshape != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.isRedstoneConductor(p_180460_1_.north())) {
                  d21 = 0.02D;
               } else if (this.isRedstoneConductor(p_180460_1_.south())) {
                  d21 = -0.02D;
               }
            }

            this.setDeltaMovement(d20, vector3d7.y, d21);
         }
      }

   }

   private boolean isRedstoneConductor(BlockPos p_213900_1_) {
      return this.level.getBlockState(p_213900_1_).isRedstoneConductor(this.level, p_213900_1_);
   }

   protected void applyNaturalSlowdown() {
      double d0 = this.isVehicle() ? 0.997D : 0.96D;
      this.setDeltaMovement(this.getDeltaMovement().multiply(d0, 0.0D, d0));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vector3d getPosOffs(double p_70495_1_, double p_70495_3_, double p_70495_5_, double p_70495_7_) {
      int i = MathHelper.floor(p_70495_1_);
      int j = MathHelper.floor(p_70495_3_);
      int k = MathHelper.floor(p_70495_5_);
      if (this.level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.level.getBlockState(new BlockPos(i, j, k));
      if (AbstractRailBlock.isRail(blockstate)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.level, new BlockPos(i, j, k), this);
         p_70495_3_ = (double)j;
         if (railshape.isAscending()) {
            p_70495_3_ = (double)(j + 1);
         }

         Pair<Vector3i, Vector3i> pair = exits(railshape);
         Vector3i vector3i = pair.getFirst();
         Vector3i vector3i1 = pair.getSecond();
         double d0 = (double)(vector3i1.getX() - vector3i.getX());
         double d1 = (double)(vector3i1.getZ() - vector3i.getZ());
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         d0 = d0 / d2;
         d1 = d1 / d2;
         p_70495_1_ = p_70495_1_ + d0 * p_70495_7_;
         p_70495_5_ = p_70495_5_ + d1 * p_70495_7_;
         if (vector3i.getY() != 0 && MathHelper.floor(p_70495_1_) - i == vector3i.getX() && MathHelper.floor(p_70495_5_) - k == vector3i.getZ()) {
            p_70495_3_ += (double)vector3i.getY();
         } else if (vector3i1.getY() != 0 && MathHelper.floor(p_70495_1_) - i == vector3i1.getX() && MathHelper.floor(p_70495_5_) - k == vector3i1.getZ()) {
            p_70495_3_ += (double)vector3i1.getY();
         }

         return this.getPos(p_70495_1_, p_70495_3_, p_70495_5_);
      } else {
         return null;
      }
   }

   @Nullable
   public Vector3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
      int i = MathHelper.floor(p_70489_1_);
      int j = MathHelper.floor(p_70489_3_);
      int k = MathHelper.floor(p_70489_5_);
      if (this.level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.level.getBlockState(new BlockPos(i, j, k));
      if (AbstractRailBlock.isRail(blockstate)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.level, new BlockPos(i, j, k), this);
         Pair<Vector3i, Vector3i> pair = exits(railshape);
         Vector3i vector3i = pair.getFirst();
         Vector3i vector3i1 = pair.getSecond();
         double d0 = (double)i + 0.5D + (double)vector3i.getX() * 0.5D;
         double d1 = (double)j + 0.0625D + (double)vector3i.getY() * 0.5D;
         double d2 = (double)k + 0.5D + (double)vector3i.getZ() * 0.5D;
         double d3 = (double)i + 0.5D + (double)vector3i1.getX() * 0.5D;
         double d4 = (double)j + 0.0625D + (double)vector3i1.getY() * 0.5D;
         double d5 = (double)k + 0.5D + (double)vector3i1.getZ() * 0.5D;
         double d6 = d3 - d0;
         double d7 = (d4 - d1) * 2.0D;
         double d8 = d5 - d2;
         double d9;
         if (d6 == 0.0D) {
            d9 = p_70489_5_ - (double)k;
         } else if (d8 == 0.0D) {
            d9 = p_70489_1_ - (double)i;
         } else {
            double d10 = p_70489_1_ - d0;
            double d11 = p_70489_5_ - d2;
            d9 = (d10 * d6 + d11 * d8) * 2.0D;
         }

         p_70489_1_ = d0 + d6 * d9;
         p_70489_3_ = d1 + d7 * d9;
         p_70489_5_ = d2 + d8 * d9;
         if (d7 < 0.0D) {
            ++p_70489_3_;
         } else if (d7 > 0.0D) {
            p_70489_3_ += 0.5D;
         }

         return new Vector3d(p_70489_1_, p_70489_3_, p_70489_5_);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getBoundingBoxForCulling() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      return this.hasCustomDisplay() ? axisalignedbb.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D) : axisalignedbb;
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      if (p_70037_1_.getBoolean("CustomDisplayTile")) {
         this.setDisplayBlockState(NBTUtil.readBlockState(p_70037_1_.getCompound("DisplayState")));
         this.setDisplayOffset(p_70037_1_.getInt("DisplayOffset"));
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      if (this.hasCustomDisplay()) {
         p_213281_1_.putBoolean("CustomDisplayTile", true);
         p_213281_1_.put("DisplayState", NBTUtil.writeBlockState(this.getDisplayBlockState()));
         p_213281_1_.putInt("DisplayOffset", this.getDisplayOffset());
      }

   }

   public void push(Entity p_70108_1_) {
      //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartCollisionEvent(this, entityIn));
      if (getCollisionHandler() != null) {
         getCollisionHandler().onEntityCollision(this, p_70108_1_);
         return;
      }
      if (!this.level.isClientSide) {
         if (!p_70108_1_.noPhysics && !this.noPhysics) {
            if (!this.hasPassenger(p_70108_1_)) {
               double d0 = p_70108_1_.getX() - this.getX();
               double d1 = p_70108_1_.getZ() - this.getZ();
               double d2 = d0 * d0 + d1 * d1;
               if (d2 >= (double)1.0E-4F) {
                  d2 = (double)MathHelper.sqrt(d2);
                  d0 = d0 / d2;
                  d1 = d1 / d2;
                  double d3 = 1.0D / d2;
                  if (d3 > 1.0D) {
                     d3 = 1.0D;
                  }

                  d0 = d0 * d3;
                  d1 = d1 * d3;
                  d0 = d0 * (double)0.1F;
                  d1 = d1 * (double)0.1F;
                  d0 = d0 * (double)(1.0F - this.pushthrough);
                  d1 = d1 * (double)(1.0F - this.pushthrough);
                  d0 = d0 * 0.5D;
                  d1 = d1 * 0.5D;
                  if (p_70108_1_ instanceof AbstractMinecartEntity) {
                     double d4 = p_70108_1_.getX() - this.getX();
                     double d5 = p_70108_1_.getZ() - this.getZ();
                     Vector3d vector3d = (new Vector3d(d4, 0.0D, d5)).normalize();
                     Vector3d vector3d1 = (new Vector3d((double)MathHelper.cos(this.yRot * ((float)Math.PI / 180F)), 0.0D, (double)MathHelper.sin(this.yRot * ((float)Math.PI / 180F)))).normalize();
                     double d6 = Math.abs(vector3d.dot(vector3d1));
                     if (d6 < (double)0.8F) {
                        return;
                     }

                     Vector3d vector3d2 = this.getDeltaMovement();
                     Vector3d vector3d3 = p_70108_1_.getDeltaMovement();
                     if (((AbstractMinecartEntity)p_70108_1_).isPoweredCart() && !this.isPoweredCart()) {
                        this.setDeltaMovement(vector3d2.multiply(0.2D, 1.0D, 0.2D));
                        this.push(vector3d3.x - d0, 0.0D, vector3d3.z - d1);
                        p_70108_1_.setDeltaMovement(vector3d3.multiply(0.95D, 1.0D, 0.95D));
                     } else if (!((AbstractMinecartEntity)p_70108_1_).isPoweredCart() && this.isPoweredCart()) {
                        p_70108_1_.setDeltaMovement(vector3d3.multiply(0.2D, 1.0D, 0.2D));
                        p_70108_1_.push(vector3d2.x + d0, 0.0D, vector3d2.z + d1);
                        this.setDeltaMovement(vector3d2.multiply(0.95D, 1.0D, 0.95D));
                     } else {
                        double d7 = (vector3d3.x + vector3d2.x) / 2.0D;
                        double d8 = (vector3d3.z + vector3d2.z) / 2.0D;
                        this.setDeltaMovement(vector3d2.multiply(0.2D, 1.0D, 0.2D));
                        this.push(d7 - d0, 0.0D, d8 - d1);
                        p_70108_1_.setDeltaMovement(vector3d3.multiply(0.2D, 1.0D, 0.2D));
                        p_70108_1_.push(d7 + d0, 0.0D, d8 + d1);
                     }
                  } else {
                     this.push(-d0, 0.0D, -d1);
                     p_70108_1_.push(d0 / 4.0D, 0.0D, d1 / 4.0D);
                  }
               }

            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.lx = p_180426_1_;
      this.ly = p_180426_3_;
      this.lz = p_180426_5_;
      this.lyr = (double)p_180426_7_;
      this.lxr = (double)p_180426_8_;
      this.lSteps = p_180426_9_ + 2;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.lxd = p_70016_1_;
      this.lyd = p_70016_3_;
      this.lzd = p_70016_5_;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public void setDamage(float p_70492_1_) {
      this.entityData.set(DATA_ID_DAMAGE, p_70492_1_);
   }

   public float getDamage() {
      return this.entityData.get(DATA_ID_DAMAGE);
   }

   public void setHurtTime(int p_70497_1_) {
      this.entityData.set(DATA_ID_HURT, p_70497_1_);
   }

   public int getHurtTime() {
      return this.entityData.get(DATA_ID_HURT);
   }

   public void setHurtDir(int p_70494_1_) {
      this.entityData.set(DATA_ID_HURTDIR, p_70494_1_);
   }

   public int getHurtDir() {
      return this.entityData.get(DATA_ID_HURTDIR);
   }

   public abstract AbstractMinecartEntity.Type getMinecartType();

   public BlockState getDisplayBlockState() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayBlockState() : Block.stateById(this.getEntityData().get(DATA_ID_DISPLAY_BLOCK));
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.AIR.defaultBlockState();
   }

   public int getDisplayOffset() {
      return !this.hasCustomDisplay() ? this.getDefaultDisplayOffset() : this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
   }

   public int getDefaultDisplayOffset() {
      return 6;
   }

   public void setDisplayBlockState(BlockState p_174899_1_) {
      this.getEntityData().set(DATA_ID_DISPLAY_BLOCK, Block.getId(p_174899_1_));
      this.setCustomDisplay(true);
   }

   public void setDisplayOffset(int p_94086_1_) {
      this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, p_94086_1_);
      this.setCustomDisplay(true);
   }

   public boolean hasCustomDisplay() {
      return this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY);
   }

   public void setCustomDisplay(boolean p_94096_1_) {
      this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, p_94096_1_);
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }

   private boolean canUseRail = true;
   @Override public boolean canUseRail() { return canUseRail; }
   @Override public void setCanUseRail(boolean value) { this.canUseRail = value; }
   private float currentSpeedCapOnRail = getMaxCartSpeedOnRail();
   @Override public float getCurrentCartSpeedCapOnRail() { return currentSpeedCapOnRail; }
   @Override public void setCurrentCartSpeedCapOnRail(float value) { currentSpeedCapOnRail = Math.min(value, getMaxCartSpeedOnRail()); }
   private float maxSpeedAirLateral = DEFAULT_MAX_SPEED_AIR_LATERAL;
   @Override public float getMaxSpeedAirLateral() { return maxSpeedAirLateral; }
   @Override public void setMaxSpeedAirLateral(float value) { maxSpeedAirLateral = value; }
   private float maxSpeedAirVertical = DEFAULT_MAX_SPEED_AIR_VERTICAL;
   @Override public float getMaxSpeedAirVertical() { return maxSpeedAirVertical; }
   @Override public void setMaxSpeedAirVertical(float value) { maxSpeedAirVertical = value; }
   private double dragAir = DEFAULT_AIR_DRAG;
   @Override public double getDragAir() { return dragAir; }
   @Override public void setDragAir(double value) { dragAir = value; }
   @Override
   public double getMaxSpeedWithRail() { //Non-default because getMaximumSpeed is protected
      if (!canUseRail()) return getMaxSpeed();
      BlockPos pos = this.getCurrentRailPosition();
      BlockState state = getMinecart().level.getBlockState(pos);
      if (!state.is(BlockTags.RAILS)) return getMaxSpeed();

      float railMaxSpeed = ((AbstractRailBlock)state.getBlock()).getRailMaxSpeed(state, getMinecart().level, pos, getMinecart());
      return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
   }
   @Override
   public void moveMinecartOnRail(BlockPos pos) { //Non-default because getMaximumSpeed is protected
      AbstractMinecartEntity mc = getMinecart();
      double d24 = mc.isVehicle() ? 0.75D : 1.0D;
      double d25 = mc.getMaxSpeedWithRail();
      Vector3d vec3d1 = mc.getDeltaMovement();
      mc.move(MoverType.SELF, new Vector3d(MathHelper.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, MathHelper.clamp(d24 * vec3d1.z, -d25, d25)));
   }

   public static enum Type {
      RIDEABLE,
      CHEST,
      FURNACE,
      TNT,
      SPAWNER,
      HOPPER,
      COMMAND_BLOCK;
   }
}
