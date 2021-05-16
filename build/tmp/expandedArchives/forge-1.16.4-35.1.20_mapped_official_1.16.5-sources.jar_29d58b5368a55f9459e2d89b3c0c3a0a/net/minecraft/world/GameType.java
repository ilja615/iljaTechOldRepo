package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int id;
   private final String name;

   private GameType(int p_i48711_3_, String p_i48711_4_) {
      this.id = p_i48711_3_;
      this.name = p_i48711_4_;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("gameMode." + this.name);
   }

   public void updatePlayerAbilities(PlayerAbilities p_77147_1_) {
      if (this == CREATIVE) {
         p_77147_1_.mayfly = true;
         p_77147_1_.instabuild = true;
         p_77147_1_.invulnerable = true;
      } else if (this == SPECTATOR) {
         p_77147_1_.mayfly = true;
         p_77147_1_.instabuild = false;
         p_77147_1_.invulnerable = true;
         p_77147_1_.flying = true;
      } else {
         p_77147_1_.mayfly = false;
         p_77147_1_.instabuild = false;
         p_77147_1_.invulnerable = false;
         p_77147_1_.flying = false;
      }

      p_77147_1_.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int p_77146_0_) {
      return byId(p_77146_0_, SURVIVAL);
   }

   public static GameType byId(int p_185329_0_, GameType p_185329_1_) {
      for(GameType gametype : values()) {
         if (gametype.id == p_185329_0_) {
            return gametype;
         }
      }

      return p_185329_1_;
   }

   public static GameType byName(String p_77142_0_) {
      return byName(p_77142_0_, SURVIVAL);
   }

   public static GameType byName(String p_185328_0_, GameType p_185328_1_) {
      for(GameType gametype : values()) {
         if (gametype.name.equals(p_185328_0_)) {
            return gametype;
         }
      }

      return p_185328_1_;
   }
}
