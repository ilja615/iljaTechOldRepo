package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<TileEntity> implements net.minecraftforge.common.extensions.IForgeTileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityType<?> type;
   @Nullable
   protected World level;
   protected BlockPos worldPosition = BlockPos.ZERO;
   protected boolean remove;
   @Nullable
   private BlockState blockState;
   private boolean hasLoggedInvalidStateBefore;
   private CompoundNBT customTileData;

   public TileEntity(TileEntityType<?> p_i48289_1_) {
      super(TileEntity.class);
      this.type = p_i48289_1_;
      this.gatherCapabilities();
   }

   @Nullable
   public World getLevel() {
      return this.level;
   }

   public void setLevelAndPosition(World p_226984_1_, BlockPos p_226984_2_) {
      this.level = p_226984_1_;
      this.worldPosition = p_226984_2_.immutable();
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      this.worldPosition = new BlockPos(p_230337_2_.getInt("x"), p_230337_2_.getInt("y"), p_230337_2_.getInt("z"));
      if (p_230337_2_.contains("ForgeData")) this.customTileData = p_230337_2_.getCompound("ForgeData");
      if (getCapabilities() != null && p_230337_2_.contains("ForgeCaps")) deserializeCaps(p_230337_2_.getCompound("ForgeCaps"));
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      return this.saveMetadata(p_189515_1_);
   }

   private CompoundNBT saveMetadata(CompoundNBT p_189516_1_) {
      ResourceLocation resourcelocation = TileEntityType.getKey(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         p_189516_1_.putString("id", resourcelocation.toString());
         p_189516_1_.putInt("x", this.worldPosition.getX());
         p_189516_1_.putInt("y", this.worldPosition.getY());
         p_189516_1_.putInt("z", this.worldPosition.getZ());
         if (this.customTileData != null) p_189516_1_.put("ForgeData", this.customTileData);
         if (getCapabilities() != null) p_189516_1_.put("ForgeCaps", serializeCaps());
         return p_189516_1_;
      }
   }

   @Nullable
   public static TileEntity loadStatic(BlockState p_235657_0_, CompoundNBT p_235657_1_) {
      String s = p_235657_1_.getString("id");
      return Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(s)).map((p_213134_1_) -> {
         try {
            return p_213134_1_.create();
         } catch (Throwable throwable) {
            LOGGER.error("Failed to create block entity {}", s, throwable);
            return null;
         }
      }).map((p_235656_3_) -> {
         try {
            p_235656_3_.load(p_235657_0_, p_235657_1_);
            return p_235656_3_;
         } catch (Throwable throwable) {
            LOGGER.error("Failed to load data for block entity {}", s, throwable);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
         return null;
      });
   }

   public void setChanged() {
      if (this.level != null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
         this.level.blockEntityChanged(this.worldPosition, this);
         if (!this.blockState.isAir(this.level, this.worldPosition)) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.blockState.getBlock());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getViewDistance() {
      return 64.0D;
   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      if (this.blockState == null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
      }

      return this.blockState;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return null;
   }

   public CompoundNBT getUpdateTag() {
      return this.saveMetadata(new CompoundNBT());
   }

   public boolean isRemoved() {
      return this.remove;
   }

   public void setRemoved() {
      this.remove = true;
      this.invalidateCaps();
      requestModelDataUpdate();
   }

   @Override
   public void onChunkUnloaded() {
      this.invalidateCaps();
   }

   public void clearRemoved() {
      this.remove = false;
   }

   public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
      return false;
   }

   public void clearCache() {
      this.blockState = null;
   }

   public void fillCrashReportCategory(CrashReportCategory p_145828_1_) {
      p_145828_1_.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(p_145828_1_, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(p_145828_1_, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public void setPosition(BlockPos p_174878_1_) {
      this.worldPosition = p_174878_1_.immutable();
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public void rotate(Rotation p_189667_1_) {
   }

   public void mirror(Mirror p_189668_1_) {
   }

   public TileEntityType<?> getType() {
      return this.type;
   }

   @Override
   public CompoundNBT getTileData() {
      if (this.customTileData == null)
         this.customTileData = new CompoundNBT();
      return this.customTileData;
   }

   public void logInvalidState() {
      if (!this.hasLoggedInvalidStateBefore) {
         this.hasLoggedInvalidStateBefore = true;
         LOGGER.warn("Block entity invalid: {} @ {}", () -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
         }, this::getBlockPos);
      }
   }
}
