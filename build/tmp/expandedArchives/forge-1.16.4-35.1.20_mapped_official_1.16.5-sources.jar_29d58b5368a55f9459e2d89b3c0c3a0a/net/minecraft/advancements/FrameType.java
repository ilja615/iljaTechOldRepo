package net.minecraft.advancements;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum FrameType {
   TASK("task", 0, TextFormatting.GREEN),
   CHALLENGE("challenge", 26, TextFormatting.DARK_PURPLE),
   GOAL("goal", 52, TextFormatting.GREEN);

   private final String name;
   private final int texture;
   private final TextFormatting chatColor;
   private final ITextComponent displayName;

   private FrameType(String p_i47585_3_, int p_i47585_4_, TextFormatting p_i47585_5_) {
      this.name = p_i47585_3_;
      this.texture = p_i47585_4_;
      this.chatColor = p_i47585_5_;
      this.displayName = new TranslationTextComponent("advancements.toast." + p_i47585_3_);
   }

   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTexture() {
      return this.texture;
   }

   public static FrameType byName(String p_192308_0_) {
      for(FrameType frametype : values()) {
         if (frametype.name.equals(p_192308_0_)) {
            return frametype;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + p_192308_0_ + "'");
   }

   public TextFormatting getChatColor() {
      return this.chatColor;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }
}
