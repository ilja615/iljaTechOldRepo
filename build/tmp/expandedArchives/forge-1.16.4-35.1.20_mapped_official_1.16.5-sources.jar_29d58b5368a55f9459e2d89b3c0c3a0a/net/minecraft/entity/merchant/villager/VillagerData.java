package net.minecraft.entity.merchant.villager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VillagerData {
   private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
   public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create((p_234556_0_) -> {
      return p_234556_0_.group(Registry.VILLAGER_TYPE.fieldOf("type").orElseGet(() -> {
         return VillagerType.PLAINS;
      }).forGetter((p_234558_0_) -> {
         return p_234558_0_.type;
      }), Registry.VILLAGER_PROFESSION.fieldOf("profession").orElseGet(() -> {
         return VillagerProfession.NONE;
      }).forGetter((p_234557_0_) -> {
         return p_234557_0_.profession;
      }), Codec.INT.fieldOf("level").orElse(1).forGetter((p_234555_0_) -> {
         return p_234555_0_.level;
      })).apply(p_234556_0_, VillagerData::new);
   });
   private final VillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(VillagerType p_i50180_1_, VillagerProfession p_i50180_2_, int p_i50180_3_) {
      this.type = p_i50180_1_;
      this.profession = p_i50180_2_;
      this.level = Math.max(1, p_i50180_3_);
   }

   public VillagerType getType() {
      return this.type;
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public int getLevel() {
      return this.level;
   }

   public VillagerData setType(VillagerType p_221134_1_) {
      return new VillagerData(p_221134_1_, this.profession, this.level);
   }

   public VillagerData setProfession(VillagerProfession p_221126_1_) {
      return new VillagerData(this.type, p_221126_1_, this.level);
   }

   public VillagerData setLevel(int p_221135_1_) {
      return new VillagerData(this.type, this.profession, p_221135_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static int getMinXpPerLevel(int p_221133_0_) {
      return canLevelUp(p_221133_0_) ? NEXT_LEVEL_XP_THRESHOLDS[p_221133_0_ - 1] : 0;
   }

   public static int getMaxXpPerLevel(int p_221127_0_) {
      return canLevelUp(p_221127_0_) ? NEXT_LEVEL_XP_THRESHOLDS[p_221127_0_] : 0;
   }

   public static boolean canLevelUp(int p_221128_0_) {
      return p_221128_0_ >= 1 && p_221128_0_ < 5;
   }
}
