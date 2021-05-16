package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class BlockPredicate {
   public static final BlockPredicate ANY = new BlockPredicate((ITag<Block>)null, (Block)null, StatePropertiesPredicate.ANY, NBTPredicate.ANY);
   @Nullable
   private final ITag<Block> tag;
   @Nullable
   private final Block block;
   private final StatePropertiesPredicate properties;
   private final NBTPredicate nbt;

   public BlockPredicate(@Nullable ITag<Block> p_i225708_1_, @Nullable Block p_i225708_2_, StatePropertiesPredicate p_i225708_3_, NBTPredicate p_i225708_4_) {
      this.tag = p_i225708_1_;
      this.block = p_i225708_2_;
      this.properties = p_i225708_3_;
      this.nbt = p_i225708_4_;
   }

   public boolean matches(ServerWorld p_226238_1_, BlockPos p_226238_2_) {
      if (this == ANY) {
         return true;
      } else if (!p_226238_1_.isLoaded(p_226238_2_)) {
         return false;
      } else {
         BlockState blockstate = p_226238_1_.getBlockState(p_226238_2_);
         Block block = blockstate.getBlock();
         if (this.tag != null && !this.tag.contains(block)) {
            return false;
         } else if (this.block != null && block != this.block) {
            return false;
         } else if (!this.properties.matches(blockstate)) {
            return false;
         } else {
            if (this.nbt != NBTPredicate.ANY) {
               TileEntity tileentity = p_226238_1_.getBlockEntity(p_226238_2_);
               if (tileentity == null || !this.nbt.matches(tileentity.save(new CompoundNBT()))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static BlockPredicate fromJson(@Nullable JsonElement p_226237_0_) {
      if (p_226237_0_ != null && !p_226237_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_226237_0_, "block");
         NBTPredicate nbtpredicate = NBTPredicate.fromJson(jsonobject.get("nbt"));
         Block block = null;
         if (jsonobject.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "block"));
            block = Registry.BLOCK.get(resourcelocation);
         }

         ITag<Block> itag = null;
         if (jsonobject.has("tag")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(jsonobject, "tag"));
            itag = TagCollectionManager.getInstance().getBlocks().getTag(resourcelocation1);
            if (itag == null) {
               throw new JsonSyntaxException("Unknown block tag '" + resourcelocation1 + "'");
            }
         }

         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(jsonobject.get("state"));
         return new BlockPredicate(itag, block, statepropertiespredicate, nbtpredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", TagCollectionManager.getInstance().getBlocks().getIdOrThrow(this.tag).toString());
         }

         jsonobject.add("nbt", this.nbt.serializeToJson());
         jsonobject.add("state", this.properties.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      @Nullable
      private Block block;
      @Nullable
      private ITag<Block> blocks;
      private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;
      private NBTPredicate nbt = NBTPredicate.ANY;

      private Builder() {
      }

      public static BlockPredicate.Builder block() {
         return new BlockPredicate.Builder();
      }

      public BlockPredicate.Builder of(Block p_233458_1_) {
         this.block = p_233458_1_;
         return this;
      }

      public BlockPredicate.Builder of(ITag<Block> p_226244_1_) {
         this.blocks = p_226244_1_;
         return this;
      }

      public BlockPredicate.Builder setProperties(StatePropertiesPredicate p_233459_1_) {
         this.properties = p_233459_1_;
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.blocks, this.block, this.properties, this.nbt);
      }
   }
}
