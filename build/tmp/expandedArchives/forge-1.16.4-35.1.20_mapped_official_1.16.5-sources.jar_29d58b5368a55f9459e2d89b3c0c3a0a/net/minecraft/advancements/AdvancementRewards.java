package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.NONE);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final FunctionObject.CacheableFunction function;

   public AdvancementRewards(int p_i47587_1_, ResourceLocation[] p_i47587_2_, ResourceLocation[] p_i47587_3_, FunctionObject.CacheableFunction p_i47587_4_) {
      this.experience = p_i47587_1_;
      this.loot = p_i47587_2_;
      this.recipes = p_i47587_3_;
      this.function = p_i47587_4_;
   }

   public void grant(ServerPlayerEntity p_192113_1_) {
      p_192113_1_.giveExperiencePoints(this.experience);
      LootContext lootcontext = (new LootContext.Builder(p_192113_1_.getLevel())).withParameter(LootParameters.THIS_ENTITY, p_192113_1_).withParameter(LootParameters.ORIGIN, p_192113_1_.position()).withRandom(p_192113_1_.getRandom()).withLuck(p_192113_1_.getLuck()).create(LootParameterSets.ADVANCEMENT_REWARD); // FORGE: luck to LootContext
      boolean flag = false;

      for(ResourceLocation resourcelocation : this.loot) {
         for(ItemStack itemstack : p_192113_1_.server.getLootTables().get(resourcelocation).getRandomItems(lootcontext)) {
            if (p_192113_1_.addItem(itemstack)) {
               p_192113_1_.level.playSound((PlayerEntity)null, p_192113_1_.getX(), p_192113_1_.getY(), p_192113_1_.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((p_192113_1_.getRandom().nextFloat() - p_192113_1_.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               ItemEntity itementity = p_192113_1_.drop(itemstack, false);
               if (itementity != null) {
                  itementity.setNoPickUpDelay();
                  itementity.setOwner(p_192113_1_.getUUID());
               }
            }
         }
      }

      if (flag) {
         p_192113_1_.inventoryMenu.broadcastChanges();
      }

      if (this.recipes.length > 0) {
         p_192113_1_.awardRecipesByKey(this.recipes);
      }

      MinecraftServer minecraftserver = p_192113_1_.server;
      this.function.get(minecraftserver.getFunctions()).ifPresent((p_215098_2_) -> {
         minecraftserver.getFunctions().execute(p_215098_2_, p_192113_1_.createCommandSourceStack().withSuppressedOutput().withPermission(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serializeToJson() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         if (this.loot.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ResourceLocation resourcelocation : this.loot) {
               jsonarray.add(resourcelocation.toString());
            }

            jsonobject.add("loot", jsonarray);
         }

         if (this.recipes.length > 0) {
            JsonArray jsonarray1 = new JsonArray();

            for(ResourceLocation resourcelocation1 : this.recipes) {
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   public static AdvancementRewards deserialize(JsonObject p_241096_0_) throws JsonParseException {
      int i = JSONUtils.getAsInt(p_241096_0_, "experience", 0);
      JsonArray jsonarray = JSONUtils.getAsJsonArray(p_241096_0_, "loot", new JsonArray());
      ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

      for(int j = 0; j < aresourcelocation.length; ++j) {
         aresourcelocation[j] = new ResourceLocation(JSONUtils.convertToString(jsonarray.get(j), "loot[" + j + "]"));
      }

      JsonArray jsonarray1 = JSONUtils.getAsJsonArray(p_241096_0_, "recipes", new JsonArray());
      ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

      for(int k = 0; k < aresourcelocation1.length; ++k) {
         aresourcelocation1[k] = new ResourceLocation(JSONUtils.convertToString(jsonarray1.get(k), "recipes[" + k + "]"));
      }

      FunctionObject.CacheableFunction functionobject$cacheablefunction;
      if (p_241096_0_.has("function")) {
         functionobject$cacheablefunction = new FunctionObject.CacheableFunction(new ResourceLocation(JSONUtils.getAsString(p_241096_0_, "function")));
      } else {
         functionobject$cacheablefunction = FunctionObject.CacheableFunction.NONE;
      }

      return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, functionobject$cacheablefunction);
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int p_203907_0_) {
         return (new AdvancementRewards.Builder()).addExperience(p_203907_0_);
      }

      public AdvancementRewards.Builder addExperience(int p_203906_1_) {
         this.experience += p_203906_1_;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation p_200280_0_) {
         return (new AdvancementRewards.Builder()).addRecipe(p_200280_0_);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation p_200279_1_) {
         this.recipes.add(p_200279_1_);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.toArray(new ResourceLocation[0]), this.recipes.toArray(new ResourceLocation[0]), this.function == null ? FunctionObject.CacheableFunction.NONE : new FunctionObject.CacheableFunction(this.function));
      }
   }
}
