package net.minecraft.world.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

public class WorldSummary implements Comparable<WorldSummary> {
   private final WorldSettings settings;
   private final VersionData levelVersion;
   private final String levelId;
   private final boolean requiresConversion;
   private final boolean locked;
   private final File icon;
   @Nullable
   @OnlyIn(Dist.CLIENT)
   private ITextComponent info;

   public WorldSummary(WorldSettings p_i232155_1_, VersionData p_i232155_2_, String p_i232155_3_, boolean p_i232155_4_, boolean p_i232155_5_, File p_i232155_6_) {
      this.settings = p_i232155_1_;
      this.levelVersion = p_i232155_2_;
      this.levelId = p_i232155_3_;
      this.locked = p_i232155_5_;
      this.icon = p_i232155_6_;
      this.requiresConversion = p_i232155_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getLevelId() {
      return this.levelId;
   }

   @OnlyIn(Dist.CLIENT)
   public String getLevelName() {
      return StringUtils.isEmpty(this.settings.levelName()) ? this.levelId : this.settings.levelName();
   }

   @OnlyIn(Dist.CLIENT)
   public File getIcon() {
      return this.icon;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isRequiresConversion() {
      return this.requiresConversion;
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastPlayed() {
      return this.levelVersion.lastPlayed();
   }

   public int compareTo(WorldSummary p_compareTo_1_) {
      if (this.levelVersion.lastPlayed() < p_compareTo_1_.levelVersion.lastPlayed()) {
         return 1;
      } else {
         return this.levelVersion.lastPlayed() > p_compareTo_1_.levelVersion.lastPlayed() ? -1 : this.levelId.compareTo(p_compareTo_1_.levelId);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameMode() {
      return this.settings.gameType();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCheats() {
      return this.settings.allowCommands();
   }

   @OnlyIn(Dist.CLIENT)
   public IFormattableTextComponent getWorldVersionName() {
      return (IFormattableTextComponent)(net.minecraft.util.StringUtils.isNullOrEmpty(this.levelVersion.minecraftVersionName()) ? new TranslationTextComponent("selectWorld.versionUnknown") : new StringTextComponent(this.levelVersion.minecraftVersionName()));
   }

   public VersionData levelVersion() {
      return this.levelVersion;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean markVersionInList() {
      return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.shouldBackup();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean askToOpenWorld() {
      return this.levelVersion.minecraftVersion() > SharedConstants.getCurrentVersion().getWorldVersion();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldBackup() {
      return this.levelVersion.minecraftVersion() < SharedConstants.getCurrentVersion().getWorldVersion();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLocked() {
      return this.locked;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getInfo() {
      if (this.info == null) {
         this.info = this.createInfo();
      }

      return this.info;
   }

   @OnlyIn(Dist.CLIENT)
   private ITextComponent createInfo() {
      if (this.isLocked()) {
         return (new TranslationTextComponent("selectWorld.locked")).withStyle(TextFormatting.RED);
      } else if (this.isRequiresConversion()) {
         return new TranslationTextComponent("selectWorld.conversion");
      } else {
         IFormattableTextComponent iformattabletextcomponent = (IFormattableTextComponent)(this.isHardcore() ? (new StringTextComponent("")).append((new TranslationTextComponent("gameMode.hardcore")).withStyle(TextFormatting.DARK_RED)) : new TranslationTextComponent("gameMode." + this.getGameMode().getName()));
         if (this.hasCheats()) {
            iformattabletextcomponent.append(", ").append(new TranslationTextComponent("selectWorld.cheats"));
         }

         IFormattableTextComponent iformattabletextcomponent1 = this.getWorldVersionName();
         IFormattableTextComponent iformattabletextcomponent2 = (new StringTextComponent(", ")).append(new TranslationTextComponent("selectWorld.version")).append(" ");
         if (this.markVersionInList()) {
            iformattabletextcomponent2.append(iformattabletextcomponent1.withStyle(this.askToOpenWorld() ? TextFormatting.RED : TextFormatting.ITALIC));
         } else {
            iformattabletextcomponent2.append(iformattabletextcomponent1);
         }

         iformattabletextcomponent.append(iformattabletextcomponent2);
         return iformattabletextcomponent;
      }
   }
}
