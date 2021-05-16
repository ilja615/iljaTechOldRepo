package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class RecipeBookStatus {
   private static final Map<RecipeBookCategory, Pair<String, String>> TAG_FIELDS = ImmutableMap.of(RecipeBookCategory.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookCategory.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookCategory.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookCategory.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
   private final Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> states;

   private RecipeBookStatus(Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> p_i241892_1_) {
      this.states = p_i241892_1_;
   }

   public RecipeBookStatus() {
      this(Util.make(Maps.newEnumMap(RecipeBookCategory.class), (p_242153_0_) -> {
         for(RecipeBookCategory recipebookcategory : RecipeBookCategory.values()) {
            p_242153_0_.put(recipebookcategory, new RecipeBookStatus.CategoryStatus(false, false));
         }

      }));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOpen(RecipeBookCategory p_242151_1_) {
      return (this.states.get(p_242151_1_)).open;
   }

   public void setOpen(RecipeBookCategory p_242152_1_, boolean p_242152_2_) {
      (this.states.get(p_242152_1_)).open = p_242152_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFiltering(RecipeBookCategory p_242158_1_) {
      return (this.states.get(p_242158_1_)).filtering;
   }

   public void setFiltering(RecipeBookCategory p_242159_1_, boolean p_242159_2_) {
      (this.states.get(p_242159_1_)).filtering = p_242159_2_;
   }

   public static RecipeBookStatus read(PacketBuffer p_242157_0_) {
      Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);

      for(RecipeBookCategory recipebookcategory : RecipeBookCategory.values()) {
         boolean flag = p_242157_0_.readBoolean();
         boolean flag1 = p_242157_0_.readBoolean();
         map.put(recipebookcategory, new RecipeBookStatus.CategoryStatus(flag, flag1));
      }

      return new RecipeBookStatus(map);
   }

   public void write(PacketBuffer p_242161_1_) {
      for(RecipeBookCategory recipebookcategory : RecipeBookCategory.values()) {
         RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.states.get(recipebookcategory);
         if (recipebookstatus$categorystatus == null) {
            p_242161_1_.writeBoolean(false);
            p_242161_1_.writeBoolean(false);
         } else {
            p_242161_1_.writeBoolean(recipebookstatus$categorystatus.open);
            p_242161_1_.writeBoolean(recipebookstatus$categorystatus.filtering);
         }
      }

   }

   public static RecipeBookStatus read(CompoundNBT p_242154_0_) {
      Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);
      TAG_FIELDS.forEach((p_242156_2_, p_242156_3_) -> {
         boolean flag = p_242154_0_.getBoolean(p_242156_3_.getFirst());
         boolean flag1 = p_242154_0_.getBoolean(p_242156_3_.getSecond());
         map.put(p_242156_2_, new RecipeBookStatus.CategoryStatus(flag, flag1));
      });
      return new RecipeBookStatus(map);
   }

   public void write(CompoundNBT p_242160_1_) {
      TAG_FIELDS.forEach((p_242155_2_, p_242155_3_) -> {
         RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.states.get(p_242155_2_);
         p_242160_1_.putBoolean(p_242155_3_.getFirst(), recipebookstatus$categorystatus.open);
         p_242160_1_.putBoolean(p_242155_3_.getSecond(), recipebookstatus$categorystatus.filtering);
      });
   }

   public RecipeBookStatus copy() {
      Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);

      for(RecipeBookCategory recipebookcategory : RecipeBookCategory.values()) {
         RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.states.get(recipebookcategory);
         map.put(recipebookcategory, recipebookstatus$categorystatus.copy());
      }

      return new RecipeBookStatus(map);
   }

   public void replaceFrom(RecipeBookStatus p_242150_1_) {
      this.states.clear();

      for(RecipeBookCategory recipebookcategory : RecipeBookCategory.values()) {
         RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = p_242150_1_.states.get(recipebookcategory);
         this.states.put(recipebookcategory, recipebookstatus$categorystatus.copy());
      }

   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_ || p_equals_1_ instanceof RecipeBookStatus && this.states.equals(((RecipeBookStatus)p_equals_1_).states);
   }

   public int hashCode() {
      return this.states.hashCode();
   }

   static final class CategoryStatus {
      private boolean open;
      private boolean filtering;

      public CategoryStatus(boolean p_i241893_1_, boolean p_i241893_2_) {
         this.open = p_i241893_1_;
         this.filtering = p_i241893_2_;
      }

      public RecipeBookStatus.CategoryStatus copy() {
         return new RecipeBookStatus.CategoryStatus(this.open, this.filtering);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof RecipeBookStatus.CategoryStatus)) {
            return false;
         } else {
            RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = (RecipeBookStatus.CategoryStatus)p_equals_1_;
            return this.open == recipebookstatus$categorystatus.open && this.filtering == recipebookstatus$categorystatus.filtering;
         }
      }

      public int hashCode() {
         int i = this.open ? 1 : 0;
         return 31 * i + (this.filtering ? 1 : 0);
      }

      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + ']';
      }
   }
}
