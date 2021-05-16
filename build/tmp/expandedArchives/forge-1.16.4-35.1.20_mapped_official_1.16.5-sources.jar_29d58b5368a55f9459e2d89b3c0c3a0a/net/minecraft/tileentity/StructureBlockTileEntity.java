package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureBlockTileEntity extends TileEntity {
   private ResourceLocation structureName;
   private String author = "";
   private String metaData = "";
   private BlockPos structurePos = new BlockPos(0, 1, 0);
   private BlockPos structureSize = BlockPos.ZERO;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode = StructureMode.DATA;
   private boolean ignoreEntities = true;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox = true;
   private float integrity = 1.0F;
   private long seed;

   public StructureBlockTileEntity() {
      super(TileEntityType.STRUCTURE_BLOCK);
   }

   @OnlyIn(Dist.CLIENT)
   public double getViewDistance() {
      return 96.0D;
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putString("name", this.getStructureName());
      p_189515_1_.putString("author", this.author);
      p_189515_1_.putString("metadata", this.metaData);
      p_189515_1_.putInt("posX", this.structurePos.getX());
      p_189515_1_.putInt("posY", this.structurePos.getY());
      p_189515_1_.putInt("posZ", this.structurePos.getZ());
      p_189515_1_.putInt("sizeX", this.structureSize.getX());
      p_189515_1_.putInt("sizeY", this.structureSize.getY());
      p_189515_1_.putInt("sizeZ", this.structureSize.getZ());
      p_189515_1_.putString("rotation", this.rotation.toString());
      p_189515_1_.putString("mirror", this.mirror.toString());
      p_189515_1_.putString("mode", this.mode.toString());
      p_189515_1_.putBoolean("ignoreEntities", this.ignoreEntities);
      p_189515_1_.putBoolean("powered", this.powered);
      p_189515_1_.putBoolean("showair", this.showAir);
      p_189515_1_.putBoolean("showboundingbox", this.showBoundingBox);
      p_189515_1_.putFloat("integrity", this.integrity);
      p_189515_1_.putLong("seed", this.seed);
      return p_189515_1_;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.setStructureName(p_230337_2_.getString("name"));
      this.author = p_230337_2_.getString("author");
      this.metaData = p_230337_2_.getString("metadata");
      int i = MathHelper.clamp(p_230337_2_.getInt("posX"), -48, 48);
      int j = MathHelper.clamp(p_230337_2_.getInt("posY"), -48, 48);
      int k = MathHelper.clamp(p_230337_2_.getInt("posZ"), -48, 48);
      this.structurePos = new BlockPos(i, j, k);
      int l = MathHelper.clamp(p_230337_2_.getInt("sizeX"), 0, 48);
      int i1 = MathHelper.clamp(p_230337_2_.getInt("sizeY"), 0, 48);
      int j1 = MathHelper.clamp(p_230337_2_.getInt("sizeZ"), 0, 48);
      this.structureSize = new BlockPos(l, i1, j1);

      try {
         this.rotation = Rotation.valueOf(p_230337_2_.getString("rotation"));
      } catch (IllegalArgumentException illegalargumentexception2) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(p_230337_2_.getString("mirror"));
      } catch (IllegalArgumentException illegalargumentexception1) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(p_230337_2_.getString("mode"));
      } catch (IllegalArgumentException illegalargumentexception) {
         this.mode = StructureMode.DATA;
      }

      this.ignoreEntities = p_230337_2_.getBoolean("ignoreEntities");
      this.powered = p_230337_2_.getBoolean("powered");
      this.showAir = p_230337_2_.getBoolean("showair");
      this.showBoundingBox = p_230337_2_.getBoolean("showboundingbox");
      if (p_230337_2_.contains("integrity")) {
         this.integrity = p_230337_2_.getFloat("integrity");
      } else {
         this.integrity = 1.0F;
      }

      this.seed = p_230337_2_.getLong("seed");
      this.updateBlockState();
   }

   private void updateBlockState() {
      if (this.level != null) {
         BlockPos blockpos = this.getBlockPos();
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (blockstate.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(blockpos, blockstate.setValue(StructureBlock.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 7, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public boolean usedBy(PlayerEntity p_189701_1_) {
      if (!p_189701_1_.canUseGameMasterBlocks()) {
         return false;
      } else {
         if (p_189701_1_.getCommandSenderWorld().isClientSide) {
            p_189701_1_.openStructureBlock(this);
         }

         return true;
      }
   }

   public String getStructureName() {
      return this.structureName == null ? "" : this.structureName.toString();
   }

   public String getStructurePath() {
      return this.structureName == null ? "" : this.structureName.getPath();
   }

   public boolean hasStructureName() {
      return this.structureName != null;
   }

   public void setStructureName(@Nullable String p_184404_1_) {
      this.setStructureName(StringUtils.isNullOrEmpty(p_184404_1_) ? null : ResourceLocation.tryParse(p_184404_1_));
   }

   public void setStructureName(@Nullable ResourceLocation p_210163_1_) {
      this.structureName = p_210163_1_;
   }

   public void createdBy(LivingEntity p_189720_1_) {
      this.author = p_189720_1_.getName().getString();
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getStructurePos() {
      return this.structurePos;
   }

   public void setStructurePos(BlockPos p_184414_1_) {
      this.structurePos = p_184414_1_;
   }

   public BlockPos getStructureSize() {
      return this.structureSize;
   }

   public void setStructureSize(BlockPos p_184409_1_) {
      this.structureSize = p_184409_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public Mirror getMirror() {
      return this.mirror;
   }

   public void setMirror(Mirror p_184411_1_) {
      this.mirror = p_184411_1_;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public void setRotation(Rotation p_184408_1_) {
      this.rotation = p_184408_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getMetaData() {
      return this.metaData;
   }

   public void setMetaData(String p_184410_1_) {
      this.metaData = p_184410_1_;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public void setMode(StructureMode p_184405_1_) {
      this.mode = p_184405_1_;
      BlockState blockstate = this.level.getBlockState(this.getBlockPos());
      if (blockstate.is(Blocks.STRUCTURE_BLOCK)) {
         this.level.setBlock(this.getBlockPos(), blockstate.setValue(StructureBlock.MODE, p_184405_1_), 2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void nextMode() {
      switch(this.getMode()) {
      case SAVE:
         this.setMode(StructureMode.LOAD);
         break;
      case LOAD:
         this.setMode(StructureMode.CORNER);
         break;
      case CORNER:
         this.setMode(StructureMode.DATA);
         break;
      case DATA:
         this.setMode(StructureMode.SAVE);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public void setIgnoreEntities(boolean p_184406_1_) {
      this.ignoreEntities = p_184406_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getIntegrity() {
      return this.integrity;
   }

   public void setIntegrity(float p_189718_1_) {
      this.integrity = p_189718_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long p_189725_1_) {
      this.seed = p_189725_1_;
   }

   public boolean detectSize() {
      if (this.mode != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos blockpos = this.getBlockPos();
         int i = 80;
         BlockPos blockpos1 = new BlockPos(blockpos.getX() - 80, 0, blockpos.getZ() - 80);
         BlockPos blockpos2 = new BlockPos(blockpos.getX() + 80, 255, blockpos.getZ() + 80);
         List<StructureBlockTileEntity> list = this.getNearbyCornerBlocks(blockpos1, blockpos2);
         List<StructureBlockTileEntity> list1 = this.filterRelatedCornerBlocks(list);
         if (list1.size() < 1) {
            return false;
         } else {
            MutableBoundingBox mutableboundingbox = this.calculateEnclosingBoundingBox(blockpos, list1);
            if (mutableboundingbox.x1 - mutableboundingbox.x0 > 1 && mutableboundingbox.y1 - mutableboundingbox.y0 > 1 && mutableboundingbox.z1 - mutableboundingbox.z0 > 1) {
               this.structurePos = new BlockPos(mutableboundingbox.x0 - blockpos.getX() + 1, mutableboundingbox.y0 - blockpos.getY() + 1, mutableboundingbox.z0 - blockpos.getZ() + 1);
               this.structureSize = new BlockPos(mutableboundingbox.x1 - mutableboundingbox.x0 - 1, mutableboundingbox.y1 - mutableboundingbox.y0 - 1, mutableboundingbox.z1 - mutableboundingbox.z0 - 1);
               this.setChanged();
               BlockState blockstate = this.level.getBlockState(blockpos);
               this.level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<StructureBlockTileEntity> filterRelatedCornerBlocks(List<StructureBlockTileEntity> p_184415_1_) {
      Predicate<StructureBlockTileEntity> predicate = (p_200665_1_) -> {
         return p_200665_1_.mode == StructureMode.CORNER && Objects.equals(this.structureName, p_200665_1_.structureName);
      };
      return p_184415_1_.stream().filter(predicate).collect(Collectors.toList());
   }

   private List<StructureBlockTileEntity> getNearbyCornerBlocks(BlockPos p_184418_1_, BlockPos p_184418_2_) {
      List<StructureBlockTileEntity> list = Lists.newArrayList();

      for(BlockPos blockpos : BlockPos.betweenClosed(p_184418_1_, p_184418_2_)) {
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (blockstate.is(Blocks.STRUCTURE_BLOCK)) {
            TileEntity tileentity = this.level.getBlockEntity(blockpos);
            if (tileentity != null && tileentity instanceof StructureBlockTileEntity) {
               list.add((StructureBlockTileEntity)tileentity);
            }
         }
      }

      return list;
   }

   private MutableBoundingBox calculateEnclosingBoundingBox(BlockPos p_184416_1_, List<StructureBlockTileEntity> p_184416_2_) {
      MutableBoundingBox mutableboundingbox;
      if (p_184416_2_.size() > 1) {
         BlockPos blockpos = p_184416_2_.get(0).getBlockPos();
         mutableboundingbox = new MutableBoundingBox(blockpos, blockpos);
      } else {
         mutableboundingbox = new MutableBoundingBox(p_184416_1_, p_184416_1_);
      }

      for(StructureBlockTileEntity structureblocktileentity : p_184416_2_) {
         BlockPos blockpos1 = structureblocktileentity.getBlockPos();
         if (blockpos1.getX() < mutableboundingbox.x0) {
            mutableboundingbox.x0 = blockpos1.getX();
         } else if (blockpos1.getX() > mutableboundingbox.x1) {
            mutableboundingbox.x1 = blockpos1.getX();
         }

         if (blockpos1.getY() < mutableboundingbox.y0) {
            mutableboundingbox.y0 = blockpos1.getY();
         } else if (blockpos1.getY() > mutableboundingbox.y1) {
            mutableboundingbox.y1 = blockpos1.getY();
         }

         if (blockpos1.getZ() < mutableboundingbox.z0) {
            mutableboundingbox.z0 = blockpos1.getZ();
         } else if (blockpos1.getZ() > mutableboundingbox.z1) {
            mutableboundingbox.z1 = blockpos1.getZ();
         }
      }

      return mutableboundingbox;
   }

   public boolean saveStructure() {
      return this.saveStructure(true);
   }

   public boolean saveStructure(boolean p_189712_1_) {
      if (this.mode == StructureMode.SAVE && !this.level.isClientSide && this.structureName != null) {
         BlockPos blockpos = this.getBlockPos().offset(this.structurePos);
         ServerWorld serverworld = (ServerWorld)this.level;
         TemplateManager templatemanager = serverworld.getStructureManager();

         Template template;
         try {
            template = templatemanager.getOrCreate(this.structureName);
         } catch (ResourceLocationException resourcelocationexception1) {
            return false;
         }

         template.fillFromWorld(this.level, blockpos, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
         template.setAuthor(this.author);
         if (p_189712_1_) {
            try {
               return templatemanager.save(this.structureName);
            } catch (ResourceLocationException resourcelocationexception) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean loadStructure(ServerWorld p_242687_1_) {
      return this.loadStructure(p_242687_1_, true);
   }

   private static Random createRandom(long p_214074_0_) {
      return p_214074_0_ == 0L ? new Random(Util.getMillis()) : new Random(p_214074_0_);
   }

   public boolean loadStructure(ServerWorld p_242688_1_, boolean p_242688_2_) {
      if (this.mode == StructureMode.LOAD && this.structureName != null) {
         TemplateManager templatemanager = p_242688_1_.getStructureManager();

         Template template;
         try {
            template = templatemanager.get(this.structureName);
         } catch (ResourceLocationException resourcelocationexception) {
            return false;
         }

         return template == null ? false : this.loadStructure(p_242688_1_, p_242688_2_, template);
      } else {
         return false;
      }
   }

   public boolean loadStructure(ServerWorld p_242689_1_, boolean p_242689_2_, Template p_242689_3_) {
      BlockPos blockpos = this.getBlockPos();
      if (!StringUtils.isNullOrEmpty(p_242689_3_.getAuthor())) {
         this.author = p_242689_3_.getAuthor();
      }

      BlockPos blockpos1 = p_242689_3_.getSize();
      boolean flag = this.structureSize.equals(blockpos1);
      if (!flag) {
         this.structureSize = blockpos1;
         this.setChanged();
         BlockState blockstate = p_242689_1_.getBlockState(blockpos);
         p_242689_1_.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
      }

      if (p_242689_2_ && !flag) {
         return false;
      } else {
         PlacementSettings placementsettings = (new PlacementSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPos((ChunkPos)null);
         if (this.integrity < 1.0F) {
            placementsettings.clearProcessors().addProcessor(new IntegrityProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
         }

         BlockPos blockpos2 = blockpos.offset(this.structurePos);
         p_242689_3_.placeInWorldChunk(p_242689_1_, blockpos2, placementsettings, createRandom(this.seed));
         return true;
      }
   }

   public void unloadStructure() {
      if (this.structureName != null) {
         ServerWorld serverworld = (ServerWorld)this.level;
         TemplateManager templatemanager = serverworld.getStructureManager();
         templatemanager.remove(this.structureName);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
         ServerWorld serverworld = (ServerWorld)this.level;
         TemplateManager templatemanager = serverworld.getStructureManager();

         try {
            return templatemanager.get(this.structureName) != null;
         } catch (ResourceLocationException resourcelocationexception) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean p_189723_1_) {
      this.powered = p_189723_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getShowAir() {
      return this.showAir;
   }

   public void setShowAir(boolean p_189703_1_) {
      this.showAir = p_189703_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getShowBoundingBox() {
      return this.showBoundingBox;
   }

   public void setShowBoundingBox(boolean p_189710_1_) {
      this.showBoundingBox = p_189710_1_;
   }

   public static enum UpdateCommand {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;
   }
}
