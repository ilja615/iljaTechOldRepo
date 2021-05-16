package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((p_199928_0_) -> {
      return new Difficulty[p_199928_0_];
   });
   private final int id;
   private final String key;

   private Difficulty(int p_i45312_3_, String p_i45312_4_) {
      this.id = p_i45312_3_;
      this.key = p_i45312_4_;
   }

   public int getId() {
      return this.id;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("options.difficulty." + this.key);
   }

   public static Difficulty byId(int p_151523_0_) {
      return BY_ID[p_151523_0_ % BY_ID.length];
   }

   @Nullable
   public static Difficulty byName(String p_219963_0_) {
      for(Difficulty difficulty : values()) {
         if (difficulty.key.equals(p_219963_0_)) {
            return difficulty;
         }
      }

      return null;
   }

   public String getKey() {
      return this.key;
   }

   @OnlyIn(Dist.CLIENT)
   public Difficulty nextById() {
      return BY_ID[(this.id + 1) % BY_ID.length];
   }
}
