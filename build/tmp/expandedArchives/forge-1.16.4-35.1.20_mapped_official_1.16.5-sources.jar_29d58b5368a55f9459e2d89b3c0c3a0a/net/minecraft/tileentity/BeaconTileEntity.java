package net.minecraft.tileentity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeaconTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
   public static final Effect[][] BEACON_EFFECTS = new Effect[][]{{Effects.MOVEMENT_SPEED, Effects.DIG_SPEED}, {Effects.DAMAGE_RESISTANCE, Effects.JUMP}, {Effects.DAMAGE_BOOST}, {Effects.REGENERATION}};
   private static final Set<Effect> VALID_EFFECTS = Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
   private List<BeaconTileEntity.BeamSegment> beamSections = Lists.newArrayList();
   private List<BeaconTileEntity.BeamSegment> checkingBeamSections = Lists.newArrayList();
   private int levels;
   private int lastCheckY = -1;
   @Nullable
   private Effect primaryPower;
   @Nullable
   private Effect secondaryPower;
   @Nullable
   private ITextComponent name;
   private LockCode lockKey = LockCode.NO_LOCK;
   private final IIntArray dataAccess = new IIntArray() {
      public int get(int p_221476_1_) {
         switch(p_221476_1_) {
         case 0:
            return BeaconTileEntity.this.levels;
         case 1:
            return Effect.getId(BeaconTileEntity.this.primaryPower);
         case 2:
            return Effect.getId(BeaconTileEntity.this.secondaryPower);
         default:
            return 0;
         }
      }

      public void set(int p_221477_1_, int p_221477_2_) {
         switch(p_221477_1_) {
         case 0:
            BeaconTileEntity.this.levels = p_221477_2_;
            break;
         case 1:
            if (!BeaconTileEntity.this.level.isClientSide && !BeaconTileEntity.this.beamSections.isEmpty()) {
               BeaconTileEntity.this.playSound(SoundEvents.BEACON_POWER_SELECT);
            }

            BeaconTileEntity.this.primaryPower = BeaconTileEntity.getValidEffectById(p_221477_2_);
            break;
         case 2:
            BeaconTileEntity.this.secondaryPower = BeaconTileEntity.getValidEffectById(p_221477_2_);
         }

      }

      public int getCount() {
         return 3;
      }
   };

   public BeaconTileEntity() {
      super(TileEntityType.BEACON);
   }

   public void tick() {
      int i = this.worldPosition.getX();
      int j = this.worldPosition.getY();
      int k = this.worldPosition.getZ();
      BlockPos blockpos;
      if (this.lastCheckY < j) {
         blockpos = this.worldPosition;
         this.checkingBeamSections = Lists.newArrayList();
         this.lastCheckY = blockpos.getY() - 1;
      } else {
         blockpos = new BlockPos(i, this.lastCheckY + 1, k);
      }

      BeaconTileEntity.BeamSegment beacontileentity$beamsegment = this.checkingBeamSections.isEmpty() ? null : this.checkingBeamSections.get(this.checkingBeamSections.size() - 1);
      int l = this.level.getHeight(Heightmap.Type.WORLD_SURFACE, i, k);

      for(int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
         BlockState blockstate = this.level.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         float[] afloat = blockstate.getBeaconColorMultiplier(this.level, blockpos, getBlockPos());
         if (afloat != null) {
            if (this.checkingBeamSections.size() <= 1) {
               beacontileentity$beamsegment = new BeaconTileEntity.BeamSegment(afloat);
               this.checkingBeamSections.add(beacontileentity$beamsegment);
            } else if (beacontileentity$beamsegment != null) {
               if (Arrays.equals(afloat, beacontileentity$beamsegment.color)) {
                  beacontileentity$beamsegment.increaseHeight();
               } else {
                  beacontileentity$beamsegment = new BeaconTileEntity.BeamSegment(new float[]{(beacontileentity$beamsegment.color[0] + afloat[0]) / 2.0F, (beacontileentity$beamsegment.color[1] + afloat[1]) / 2.0F, (beacontileentity$beamsegment.color[2] + afloat[2]) / 2.0F});
                  this.checkingBeamSections.add(beacontileentity$beamsegment);
               }
            }
         } else {
            if (beacontileentity$beamsegment == null || blockstate.getLightBlock(this.level, blockpos) >= 15 && block != Blocks.BEDROCK) {
               this.checkingBeamSections.clear();
               this.lastCheckY = l;
               break;
            }

            beacontileentity$beamsegment.increaseHeight();
         }

         blockpos = blockpos.above();
         ++this.lastCheckY;
      }

      int j1 = this.levels;
      if (this.level.getGameTime() % 80L == 0L) {
         if (!this.beamSections.isEmpty()) {
            this.updateBase(i, j, k);
         }

         if (this.levels > 0 && !this.beamSections.isEmpty()) {
            this.applyEffects();
            this.playSound(SoundEvents.BEACON_AMBIENT);
         }
      }

      if (this.lastCheckY >= l) {
         this.lastCheckY = -1;
         boolean flag = j1 > 0;
         this.beamSections = this.checkingBeamSections;
         if (!this.level.isClientSide) {
            boolean flag1 = this.levels > 0;
            if (!flag && flag1) {
               this.playSound(SoundEvents.BEACON_ACTIVATE);

               for(ServerPlayerEntity serverplayerentity : this.level.getEntitiesOfClass(ServerPlayerEntity.class, (new AxisAlignedBB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).inflate(10.0D, 5.0D, 10.0D))) {
                  CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayerentity, this);
               }
            } else if (flag && !flag1) {
               this.playSound(SoundEvents.BEACON_DEACTIVATE);
            }
         }
      }

   }

   private void updateBase(int p_213927_1_, int p_213927_2_, int p_213927_3_) {
      this.levels = 0;

      for(int i = 1; i <= 4; this.levels = i++) {
         int j = p_213927_2_ - i;
         if (j < 0) {
            break;
         }

         boolean flag = true;

         for(int k = p_213927_1_ - i; k <= p_213927_1_ + i && flag; ++k) {
            for(int l = p_213927_3_ - i; l <= p_213927_3_ + i; ++l) {
               if (!this.level.getBlockState(new BlockPos(k, j, l)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                  flag = false;
                  break;
               }
            }
         }

         if (!flag) {
            break;
         }
      }

   }

   public void setRemoved() {
      this.playSound(SoundEvents.BEACON_DEACTIVATE);
      super.setRemoved();
   }

   private void applyEffects() {
      if (!this.level.isClientSide && this.primaryPower != null) {
         double d0 = (double)(this.levels * 10 + 10);
         int i = 0;
         if (this.levels >= 4 && this.primaryPower == this.secondaryPower) {
            i = 1;
         }

         int j = (9 + this.levels * 2) * 20;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);
         List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);

         for(PlayerEntity playerentity : list) {
            playerentity.addEffect(new EffectInstance(this.primaryPower, j, i, true, true));
         }

         if (this.levels >= 4 && this.primaryPower != this.secondaryPower && this.secondaryPower != null) {
            for(PlayerEntity playerentity1 : list) {
               playerentity1.addEffect(new EffectInstance(this.secondaryPower, j, 0, true, true));
            }
         }

      }
   }

   public void playSound(SoundEvent p_205736_1_) {
      this.level.playSound((PlayerEntity)null, this.worldPosition, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public List<BeaconTileEntity.BeamSegment> getBeamSections() {
      return (List<BeaconTileEntity.BeamSegment>)(this.levels == 0 ? ImmutableList.of() : this.beamSections);
   }

   public int getLevels() {
      return this.levels;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   @OnlyIn(Dist.CLIENT)
   public double getViewDistance() {
      return 256.0D;
   }

   @Nullable
   private static Effect getValidEffectById(int p_184279_0_) {
      Effect effect = Effect.byId(p_184279_0_);
      return VALID_EFFECTS.contains(effect) ? effect : null;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.primaryPower = getValidEffectById(p_230337_2_.getInt("Primary"));
      this.secondaryPower = getValidEffectById(p_230337_2_.getInt("Secondary"));
      if (p_230337_2_.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(p_230337_2_.getString("CustomName"));
      }

      this.lockKey = LockCode.fromTag(p_230337_2_);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putInt("Primary", Effect.getId(this.primaryPower));
      p_189515_1_.putInt("Secondary", Effect.getId(this.secondaryPower));
      p_189515_1_.putInt("Levels", this.levels);
      if (this.name != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      this.lockKey.addToTag(p_189515_1_);
      return p_189515_1_;
   }

   public void setCustomName(@Nullable ITextComponent p_200227_1_) {
      this.name = p_200227_1_;
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return LockableTileEntity.canUnlock(p_createMenu_3_, this.lockKey, this.getDisplayName()) ? new BeaconContainer(p_createMenu_1_, p_createMenu_2_, this.dataAccess, IWorldPosCallable.create(this.level, this.getBlockPos())) : null;
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.name != null ? this.name : new TranslationTextComponent("container.beacon"));
   }

   public static class BeamSegment {
      private final float[] color;
      private int height;

      public BeamSegment(float[] p_i45669_1_) {
         this.color = p_i45669_1_;
         this.height = 1;
      }

      protected void increaseHeight() {
         ++this.height;
      }

      @OnlyIn(Dist.CLIENT)
      public float[] getColor() {
         return this.color;
      }

      @OnlyIn(Dist.CLIENT)
      public int getHeight() {
         return this.height;
      }
   }
}
