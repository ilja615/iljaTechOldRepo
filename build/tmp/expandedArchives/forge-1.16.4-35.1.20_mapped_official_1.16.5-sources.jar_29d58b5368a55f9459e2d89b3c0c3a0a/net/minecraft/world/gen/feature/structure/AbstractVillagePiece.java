package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractVillagePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final JigsawPiece element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final TemplateManager structureManager;

   public AbstractVillagePiece(TemplateManager p_i242036_1_, JigsawPiece p_i242036_2_, BlockPos p_i242036_3_, int p_i242036_4_, Rotation p_i242036_5_, MutableBoundingBox p_i242036_6_) {
      super(IStructurePieceType.JIGSAW, 0);
      this.structureManager = p_i242036_1_;
      this.element = p_i242036_2_;
      this.position = p_i242036_3_;
      this.groundLevelDelta = p_i242036_4_;
      this.rotation = p_i242036_5_;
      this.boundingBox = p_i242036_6_;
   }

   public AbstractVillagePiece(TemplateManager p_i242037_1_, CompoundNBT p_i242037_2_) {
      super(IStructurePieceType.JIGSAW, p_i242037_2_);
      this.structureManager = p_i242037_1_;
      this.position = new BlockPos(p_i242037_2_.getInt("PosX"), p_i242037_2_.getInt("PosY"), p_i242037_2_.getInt("PosZ"));
      this.groundLevelDelta = p_i242037_2_.getInt("ground_level_delta");
      this.element = JigsawPiece.CODEC.parse(NBTDynamicOps.INSTANCE, p_i242037_2_.getCompound("pool_element")).resultOrPartial(LOGGER::error).orElse(EmptyJigsawPiece.INSTANCE);
      this.rotation = Rotation.valueOf(p_i242037_2_.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(p_i242037_1_, this.position, this.rotation);
      ListNBT listnbt = p_i242037_2_.getList("junctions", 10);
      this.junctions.clear();
      listnbt.forEach((p_214827_1_) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, p_214827_1_)));
      });
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      p_143011_1_.putInt("PosX", this.position.getX());
      p_143011_1_.putInt("PosY", this.position.getY());
      p_143011_1_.putInt("PosZ", this.position.getZ());
      p_143011_1_.putInt("ground_level_delta", this.groundLevelDelta);
      JigsawPiece.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.element).resultOrPartial(LOGGER::error).ifPresent((p_237002_1_) -> {
         p_143011_1_.put("pool_element", p_237002_1_);
      });
      p_143011_1_.putString("rotation", this.rotation.name());
      ListNBT listnbt = new ListNBT();

      for(JigsawJunction jigsawjunction : this.junctions) {
         listnbt.add(jigsawjunction.serialize(NBTDynamicOps.INSTANCE).getValue());
      }

      p_143011_1_.put("junctions", listnbt);
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      return this.place(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_7_, false);
   }

   public boolean place(ISeedReader p_237001_1_, StructureManager p_237001_2_, ChunkGenerator p_237001_3_, Random p_237001_4_, MutableBoundingBox p_237001_5_, BlockPos p_237001_6_, boolean p_237001_7_) {
      return this.element.place(this.structureManager, p_237001_1_, p_237001_2_, p_237001_3_, this.position, p_237001_6_, this.rotation, p_237001_5_, p_237001_4_, p_237001_7_);
   }

   public void move(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      super.move(p_181138_1_, p_181138_2_, p_181138_3_);
      this.position = this.position.offset(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.position, this.rotation, this.element);
   }

   public JigsawPiece getElement() {
      return this.element;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction p_214831_1_) {
      this.junctions.add(p_214831_1_);
   }

   public List<JigsawJunction> getJunctions() {
      return this.junctions;
   }
}
