package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AttackIndicatorStatus {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final AttackIndicatorStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(AttackIndicatorStatus::getId)).toArray((p_216750_0_) -> {
      return new AttackIndicatorStatus[p_216750_0_];
   });
   private final int id;
   private final String key;

   private AttackIndicatorStatus(int p_i51168_3_, String p_i51168_4_) {
      this.id = p_i51168_3_;
      this.key = p_i51168_4_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AttackIndicatorStatus byId(int p_216749_0_) {
      return BY_ID[MathHelper.positiveModulo(p_216749_0_, BY_ID.length)];
   }
}
