package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JigsawTileEntity extends TileEntity {
   private ResourceLocation name = new ResourceLocation("empty");
   private ResourceLocation target = new ResourceLocation("empty");
   private ResourceLocation pool = new ResourceLocation("empty");
   private JigsawTileEntity.OrientationType joint = JigsawTileEntity.OrientationType.ROLLABLE;
   private String finalState = "minecraft:air";

   public JigsawTileEntity(TileEntityType<?> p_i49960_1_) {
      super(p_i49960_1_);
   }

   public JigsawTileEntity() {
      this(TileEntityType.JIGSAW);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTarget() {
      return this.target;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getPool() {
      return this.pool;
   }

   @OnlyIn(Dist.CLIENT)
   public String getFinalState() {
      return this.finalState;
   }

   @OnlyIn(Dist.CLIENT)
   public JigsawTileEntity.OrientationType getJoint() {
      return this.joint;
   }

   public void setName(ResourceLocation p_235664_1_) {
      this.name = p_235664_1_;
   }

   public void setTarget(ResourceLocation p_235666_1_) {
      this.target = p_235666_1_;
   }

   public void setPool(ResourceLocation p_235667_1_) {
      this.pool = p_235667_1_;
   }

   public void setFinalState(String p_214055_1_) {
      this.finalState = p_214055_1_;
   }

   public void setJoint(JigsawTileEntity.OrientationType p_235662_1_) {
      this.joint = p_235662_1_;
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putString("name", this.name.toString());
      p_189515_1_.putString("target", this.target.toString());
      p_189515_1_.putString("pool", this.pool.toString());
      p_189515_1_.putString("final_state", this.finalState);
      p_189515_1_.putString("joint", this.joint.getSerializedName());
      return p_189515_1_;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.name = new ResourceLocation(p_230337_2_.getString("name"));
      this.target = new ResourceLocation(p_230337_2_.getString("target"));
      this.pool = new ResourceLocation(p_230337_2_.getString("pool"));
      this.finalState = p_230337_2_.getString("final_state");
      this.joint = JigsawTileEntity.OrientationType.byName(p_230337_2_.getString("joint")).orElseGet(() -> {
         return JigsawBlock.getFrontFacing(p_230337_1_).getAxis().isHorizontal() ? JigsawTileEntity.OrientationType.ALIGNED : JigsawTileEntity.OrientationType.ROLLABLE;
      });
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 12, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public void generate(ServerWorld p_235665_1_, int p_235665_2_, boolean p_235665_3_) {
      ChunkGenerator chunkgenerator = p_235665_1_.getChunkSource().getGenerator();
      TemplateManager templatemanager = p_235665_1_.getStructureManager();
      StructureManager structuremanager = p_235665_1_.structureFeatureManager();
      Random random = p_235665_1_.getRandom();
      BlockPos blockpos = this.getBlockPos();
      List<AbstractVillagePiece> list = Lists.newArrayList();
      Template template = new Template();
      template.fillFromWorld(p_235665_1_, blockpos, new BlockPos(1, 1, 1), false, (Block)null);
      JigsawPiece jigsawpiece = new SingleJigsawPiece(template);
      AbstractVillagePiece abstractvillagepiece = new AbstractVillagePiece(templatemanager, jigsawpiece, blockpos, 1, Rotation.NONE, new MutableBoundingBox(blockpos, blockpos));
      JigsawManager.addPieces(p_235665_1_.registryAccess(), abstractvillagepiece, p_235665_2_, AbstractVillagePiece::new, chunkgenerator, templatemanager, list, random);

      for(AbstractVillagePiece abstractvillagepiece1 : list) {
         abstractvillagepiece1.place(p_235665_1_, structuremanager, chunkgenerator, random, MutableBoundingBox.infinite(), blockpos, p_235665_3_);
      }

   }

   public static enum OrientationType implements IStringSerializable {
      ROLLABLE("rollable"),
      ALIGNED("aligned");

      private final String name;

      private OrientationType(String p_i231862_3_) {
         this.name = p_i231862_3_;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Optional<JigsawTileEntity.OrientationType> byName(String p_235673_0_) {
         return Arrays.stream(values()).filter((p_235674_1_) -> {
            return p_235674_1_.getSerializedName().equals(p_235673_0_);
         }).findFirst();
      }
   }
}
