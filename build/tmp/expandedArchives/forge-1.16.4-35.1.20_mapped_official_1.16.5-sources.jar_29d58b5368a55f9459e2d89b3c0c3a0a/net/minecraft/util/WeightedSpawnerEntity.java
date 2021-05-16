package net.minecraft.util;

import net.minecraft.nbt.CompoundNBT;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final CompoundNBT tag;

   public WeightedSpawnerEntity() {
      super(1);
      this.tag = new CompoundNBT();
      this.tag.putString("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(CompoundNBT p_i46715_1_) {
      this(p_i46715_1_.contains("Weight", 99) ? p_i46715_1_.getInt("Weight") : 1, p_i46715_1_.getCompound("Entity"));
   }

   public WeightedSpawnerEntity(int p_i46716_1_, CompoundNBT p_i46716_2_) {
      super(p_i46716_1_);
      this.tag = p_i46716_2_;
      ResourceLocation resourcelocation = ResourceLocation.tryParse(p_i46716_2_.getString("id"));
      if (resourcelocation != null) {
         p_i46716_2_.putString("id", resourcelocation.toString());
      } else {
         p_i46716_2_.putString("id", "minecraft:pig");
      }

   }

   public CompoundNBT save() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("Entity", this.tag);
      compoundnbt.putInt("Weight", this.weight);
      return compoundnbt;
   }

   public CompoundNBT getTag() {
      return this.tag;
   }
}
