package net.minecraft.world.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VersionData {
   private final int levelDataVersion;
   private final long lastPlayed;
   private final String minecraftVersionName;
   private final int minecraftVersion;
   private final boolean snapshot;

   public VersionData(int p_i232156_1_, long p_i232156_2_, String p_i232156_4_, int p_i232156_5_, boolean p_i232156_6_) {
      this.levelDataVersion = p_i232156_1_;
      this.lastPlayed = p_i232156_2_;
      this.minecraftVersionName = p_i232156_4_;
      this.minecraftVersion = p_i232156_5_;
      this.snapshot = p_i232156_6_;
   }

   public static VersionData parse(Dynamic<?> p_237324_0_) {
      int i = p_237324_0_.get("version").asInt(0);
      long j = p_237324_0_.get("LastPlayed").asLong(0L);
      OptionalDynamic<?> optionaldynamic = p_237324_0_.get("Version");
      return optionaldynamic.result().isPresent() ? new VersionData(i, j, optionaldynamic.get("Name").asString(SharedConstants.getCurrentVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getCurrentVersion().getWorldVersion()), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable())) : new VersionData(i, j, "", 0, false);
   }

   public int levelDataVersion() {
      return this.levelDataVersion;
   }

   public long lastPlayed() {
      return this.lastPlayed;
   }

   @OnlyIn(Dist.CLIENT)
   public String minecraftVersionName() {
      return this.minecraftVersionName;
   }

   @OnlyIn(Dist.CLIENT)
   public int minecraftVersion() {
      return this.minecraftVersion;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean snapshot() {
      return this.snapshot;
   }
}
