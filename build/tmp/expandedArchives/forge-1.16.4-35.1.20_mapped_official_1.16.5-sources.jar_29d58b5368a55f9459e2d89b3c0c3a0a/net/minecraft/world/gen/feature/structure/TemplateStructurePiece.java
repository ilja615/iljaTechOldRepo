package net.minecraft.world.gen.feature.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected Template template;
   protected PlacementSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(IStructurePieceType p_i51338_1_, int p_i51338_2_) {
      super(p_i51338_1_, p_i51338_2_);
   }

   public TemplateStructurePiece(IStructurePieceType p_i51339_1_, CompoundNBT p_i51339_2_) {
      super(p_i51339_1_, p_i51339_2_);
      this.templatePosition = new BlockPos(p_i51339_2_.getInt("TPX"), p_i51339_2_.getInt("TPY"), p_i51339_2_.getInt("TPZ"));
   }

   protected void setup(Template p_186173_1_, BlockPos p_186173_2_, PlacementSettings p_186173_3_) {
      this.template = p_186173_1_;
      this.setOrientation(Direction.NORTH);
      this.templatePosition = p_186173_2_;
      this.placeSettings = p_186173_3_;
      this.boundingBox = p_186173_1_.getBoundingBox(p_186173_3_, p_186173_2_);
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      p_143011_1_.putInt("TPX", this.templatePosition.getX());
      p_143011_1_.putInt("TPY", this.templatePosition.getY());
      p_143011_1_.putInt("TPZ", this.templatePosition.getZ());
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      this.placeSettings.setBoundingBox(p_230383_5_);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (this.template.placeInWorld(p_230383_1_, this.templatePosition, p_230383_7_, this.placeSettings, p_230383_4_, 2)) {
         for(Template.BlockInfo template$blockinfo : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
            if (template$blockinfo.nbt != null) {
               StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  this.handleDataMarker(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, p_230383_1_, p_230383_4_, p_230383_5_);
               }
            }
         }

         for(Template.BlockInfo template$blockinfo1 : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW)) {
            if (template$blockinfo1.nbt != null) {
               String s = template$blockinfo1.nbt.getString("final_state");
               BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);
               BlockState blockstate = Blocks.AIR.defaultBlockState();

               try {
                  blockstateparser.parse(true);
                  BlockState blockstate1 = blockstateparser.getState();
                  if (blockstate1 != null) {
                     blockstate = blockstate1;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
                  }
               } catch (CommandSyntaxException commandsyntaxexception) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
               }

               p_230383_1_.setBlock(template$blockinfo1.pos, blockstate, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IServerWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_);

   public void move(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      super.move(p_181138_1_, p_181138_2_, p_181138_3_);
      this.templatePosition = this.templatePosition.offset(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}
