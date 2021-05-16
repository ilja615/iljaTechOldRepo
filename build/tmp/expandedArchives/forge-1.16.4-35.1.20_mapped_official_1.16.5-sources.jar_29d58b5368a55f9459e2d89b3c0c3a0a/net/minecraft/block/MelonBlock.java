package net.minecraft.block;

public class MelonBlock extends StemGrownBlock {
   public MelonBlock(AbstractBlock.Properties p_i48365_1_) {
      super(p_i48365_1_);
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.MELON_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_MELON_STEM;
   }
}
