package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Team {
   public boolean isAlliedTo(@Nullable Team p_142054_1_) {
      if (p_142054_1_ == null) {
         return false;
      } else {
         return this == p_142054_1_;
      }
   }

   public abstract String getName();

   public abstract IFormattableTextComponent getFormattedName(ITextComponent p_230427_1_);

   @OnlyIn(Dist.CLIENT)
   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   @OnlyIn(Dist.CLIENT)
   public abstract Team.Visible getNameTagVisibility();

   @OnlyIn(Dist.CLIENT)
   public abstract TextFormatting getColor();

   public abstract Collection<String> getPlayers();

   public abstract Team.Visible getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((p_199871_0_) -> {
         return p_199871_0_.name;
      }, (p_199870_0_) -> {
         return p_199870_0_;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.CollisionRule byName(String p_186686_0_) {
         return BY_NAME.get(p_186686_0_);
      }

      private CollisionRule(String p_i47053_3_, int p_i47053_4_) {
         this.name = p_i47053_3_;
         this.id = p_i47053_4_;
      }

      public ITextComponent getDisplayName() {
         return new TranslationTextComponent("team.collision." + this.name);
      }
   }

   public static enum Visible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.Visible> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((p_199873_0_) -> {
         return p_199873_0_.name;
      }, (p_199872_0_) -> {
         return p_199872_0_;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.Visible byName(String p_178824_0_) {
         return BY_NAME.get(p_178824_0_);
      }

      private Visible(String p_i45550_3_, int p_i45550_4_) {
         this.name = p_i45550_3_;
         this.id = p_i45550_4_;
      }

      public ITextComponent getDisplayName() {
         return new TranslationTextComponent("team.visibility." + this.name);
      }
   }
}
