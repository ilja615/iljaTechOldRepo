package net.minecraft.state.properties;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum NoteBlockInstrument implements IStringSerializable {
   HARP("harp", SoundEvents.NOTE_BLOCK_HARP),
   BASEDRUM("basedrum", SoundEvents.NOTE_BLOCK_BASEDRUM),
   SNARE("snare", SoundEvents.NOTE_BLOCK_SNARE),
   HAT("hat", SoundEvents.NOTE_BLOCK_HAT),
   BASS("bass", SoundEvents.NOTE_BLOCK_BASS),
   FLUTE("flute", SoundEvents.NOTE_BLOCK_FLUTE),
   BELL("bell", SoundEvents.NOTE_BLOCK_BELL),
   GUITAR("guitar", SoundEvents.NOTE_BLOCK_GUITAR),
   CHIME("chime", SoundEvents.NOTE_BLOCK_CHIME),
   XYLOPHONE("xylophone", SoundEvents.NOTE_BLOCK_XYLOPHONE),
   IRON_XYLOPHONE("iron_xylophone", SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE),
   COW_BELL("cow_bell", SoundEvents.NOTE_BLOCK_COW_BELL),
   DIDGERIDOO("didgeridoo", SoundEvents.NOTE_BLOCK_DIDGERIDOO),
   BIT("bit", SoundEvents.NOTE_BLOCK_BIT),
   BANJO("banjo", SoundEvents.NOTE_BLOCK_BANJO),
   PLING("pling", SoundEvents.NOTE_BLOCK_PLING);

   private final String name;
   private final SoundEvent soundEvent;

   private NoteBlockInstrument(String p_i49336_3_, SoundEvent p_i49336_4_) {
      this.name = p_i49336_3_;
      this.soundEvent = p_i49336_4_;
   }

   public String getSerializedName() {
      return this.name;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public static NoteBlockInstrument byState(BlockState p_208087_0_) {
      if (p_208087_0_.is(Blocks.CLAY)) {
         return FLUTE;
      } else if (p_208087_0_.is(Blocks.GOLD_BLOCK)) {
         return BELL;
      } else if (p_208087_0_.is(BlockTags.WOOL)) {
         return GUITAR;
      } else if (p_208087_0_.is(Blocks.PACKED_ICE)) {
         return CHIME;
      } else if (p_208087_0_.is(Blocks.BONE_BLOCK)) {
         return XYLOPHONE;
      } else if (p_208087_0_.is(Blocks.IRON_BLOCK)) {
         return IRON_XYLOPHONE;
      } else if (p_208087_0_.is(Blocks.SOUL_SAND)) {
         return COW_BELL;
      } else if (p_208087_0_.is(Blocks.PUMPKIN)) {
         return DIDGERIDOO;
      } else if (p_208087_0_.is(Blocks.EMERALD_BLOCK)) {
         return BIT;
      } else if (p_208087_0_.is(Blocks.HAY_BLOCK)) {
         return BANJO;
      } else if (p_208087_0_.is(Blocks.GLOWSTONE)) {
         return PLING;
      } else {
         Material material = p_208087_0_.getMaterial();
         if (material == Material.STONE) {
            return BASEDRUM;
         } else if (material == Material.SAND) {
            return SNARE;
         } else if (material == Material.GLASS) {
            return HAT;
         } else {
            return material != Material.WOOD && material != Material.NETHER_WOOD ? HARP : BASS;
         }
      }
   }
}
