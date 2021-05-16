package net.minecraft.resources;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final ITextComponent description;
   private final ITextComponent confirmation;

   private PackCompatibility(String p_i47910_3_) {
      this.description = (new TranslationTextComponent("pack.incompatible." + p_i47910_3_)).withStyle(TextFormatting.GRAY);
      this.confirmation = new TranslationTextComponent("pack.incompatible.confirm." + p_i47910_3_);
   }

   public boolean isCompatible() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility forFormat(int p_198969_0_) {
      if (p_198969_0_ < SharedConstants.getCurrentVersion().getPackVersion()) {
         return TOO_OLD;
      } else {
         return p_198969_0_ > SharedConstants.getCurrentVersion().getPackVersion() ? TOO_NEW : COMPATIBLE;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDescription() {
      return this.description;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getConfirmation() {
      return this.confirmation;
   }
}
