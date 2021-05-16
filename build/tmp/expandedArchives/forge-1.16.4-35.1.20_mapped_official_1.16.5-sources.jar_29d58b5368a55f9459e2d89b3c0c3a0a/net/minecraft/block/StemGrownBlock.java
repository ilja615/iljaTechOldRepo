package net.minecraft.block;

public abstract class StemGrownBlock extends Block {
   public StemGrownBlock(AbstractBlock.Properties p_i48317_1_) {
      super(p_i48317_1_);
   }

   public abstract StemBlock getStem();

   public abstract AttachedStemBlock getAttachedStem();
}
