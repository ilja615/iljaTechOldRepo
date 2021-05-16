package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TagLootEntry extends StandaloneLootEntry {
   private final ITag<Item> tag;
   private final boolean expand;

   private TagLootEntry(ITag<Item> p_i51248_1_, boolean p_i51248_2_, int p_i51248_3_, int p_i51248_4_, ILootCondition[] p_i51248_5_, ILootFunction[] p_i51248_6_) {
      super(p_i51248_3_, p_i51248_4_, p_i51248_5_, p_i51248_6_);
      this.tag = p_i51248_1_;
      this.expand = p_i51248_2_;
   }

   public LootPoolEntryType getType() {
      return LootEntryManager.TAG;
   }

   public void createItemStack(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      this.tag.getValues().forEach((p_216174_1_) -> {
         p_216154_1_.accept(new ItemStack(p_216174_1_));
      });
   }

   private boolean expandTag(LootContext p_216179_1_, Consumer<ILootGenerator> p_216179_2_) {
      if (!this.canRun(p_216179_1_)) {
         return false;
      } else {
         for(final Item item : this.tag.getValues()) {
            p_216179_2_.accept(new StandaloneLootEntry.Generator() {
               public void createItemStack(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
                  p_216188_1_.accept(new ItemStack(item));
               }
            });
         }

         return true;
      }
   }

   public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      return this.expand ? this.expandTag(p_expand_1_, p_expand_2_) : super.expand(p_expand_1_, p_expand_2_);
   }

   public static StandaloneLootEntry.Builder<?> expandTag(ITag<Item> p_216176_0_) {
      return simpleBuilder((p_216177_1_, p_216177_2_, p_216177_3_, p_216177_4_) -> {
         return new TagLootEntry(p_216176_0_, true, p_216177_1_, p_216177_2_, p_216177_3_, p_216177_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<TagLootEntry> {
      public void serializeCustom(JsonObject p_230422_1_, TagLootEntry p_230422_2_, JsonSerializationContext p_230422_3_) {
         super.serializeCustom(p_230422_1_, p_230422_2_, p_230422_3_);
         p_230422_1_.addProperty("name", TagCollectionManager.getInstance().getItems().getIdOrThrow(p_230422_2_.tag).toString());
         p_230422_1_.addProperty("expand", p_230422_2_.expand);
      }

      protected TagLootEntry deserialize(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_212829_1_, "name"));
         ITag<Item> itag = TagCollectionManager.getInstance().getItems().getTag(resourcelocation);
         if (itag == null) {
            throw new JsonParseException("Can't find tag: " + resourcelocation);
         } else {
            boolean flag = JSONUtils.getAsBoolean(p_212829_1_, "expand");
            return new TagLootEntry(itag, flag, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
         }
      }
   }
}
