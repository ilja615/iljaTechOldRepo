package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum NarratorStatus {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final NarratorStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray((p_216826_0_) -> {
      return new NarratorStatus[p_216826_0_];
   });
   private final int id;
   private final ITextComponent name;

   private NarratorStatus(int p_i51160_3_, String p_i51160_4_) {
      this.id = p_i51160_3_;
      this.name = new TranslationTextComponent(p_i51160_4_);
   }

   public int getId() {
      return this.id;
   }

   public ITextComponent getName() {
      return this.name;
   }

   public static NarratorStatus byId(int p_216825_0_) {
      return BY_ID[MathHelper.positiveModulo(p_216825_0_, BY_ID.length)];
   }
}
