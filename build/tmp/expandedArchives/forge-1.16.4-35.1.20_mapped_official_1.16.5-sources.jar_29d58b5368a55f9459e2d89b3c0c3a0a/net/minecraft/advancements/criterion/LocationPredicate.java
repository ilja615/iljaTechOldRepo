package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationPredicate {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (RegistryKey<Biome>)null, (Structure<?>)null, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   @Nullable
   private final RegistryKey<Biome> biome;
   @Nullable
   private final Structure<?> feature;
   @Nullable
   private final RegistryKey<World> dimension;
   @Nullable
   private final Boolean smokey;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(MinMaxBounds.FloatBound p_i241961_1_, MinMaxBounds.FloatBound p_i241961_2_, MinMaxBounds.FloatBound p_i241961_3_, @Nullable RegistryKey<Biome> p_i241961_4_, @Nullable Structure<?> p_i241961_5_, @Nullable RegistryKey<World> p_i241961_6_, @Nullable Boolean p_i241961_7_, LightPredicate p_i241961_8_, BlockPredicate p_i241961_9_, FluidPredicate p_i241961_10_) {
      this.x = p_i241961_1_;
      this.y = p_i241961_2_;
      this.z = p_i241961_3_;
      this.biome = p_i241961_4_;
      this.feature = p_i241961_5_;
      this.dimension = p_i241961_6_;
      this.smokey = p_i241961_7_;
      this.light = p_i241961_8_;
      this.block = p_i241961_9_;
      this.fluid = p_i241961_10_;
   }

   public static LocationPredicate inBiome(RegistryKey<Biome> p_242665_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, p_242665_0_, (Structure<?>)null, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inDimension(RegistryKey<World> p_235308_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (RegistryKey<Biome>)null, (Structure<?>)null, p_235308_0_, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(Structure<?> p_218020_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (RegistryKey<Biome>)null, p_218020_0_, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public boolean matches(ServerWorld p_193452_1_, double p_193452_2_, double p_193452_4_, double p_193452_6_) {
      return this.matches(p_193452_1_, (float)p_193452_2_, (float)p_193452_4_, (float)p_193452_6_);
   }

   public boolean matches(ServerWorld p_193453_1_, float p_193453_2_, float p_193453_3_, float p_193453_4_) {
      if (!this.x.matches(p_193453_2_)) {
         return false;
      } else if (!this.y.matches(p_193453_3_)) {
         return false;
      } else if (!this.z.matches(p_193453_4_)) {
         return false;
      } else if (this.dimension != null && this.dimension != p_193453_1_.dimension()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos((double)p_193453_2_, (double)p_193453_3_, (double)p_193453_4_);
         boolean flag = p_193453_1_.isLoaded(blockpos);
         Optional<RegistryKey<Biome>> optional = p_193453_1_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(p_193453_1_.getBiome(blockpos));
         if (!optional.isPresent()) {
            return false;
         } else if (this.biome == null || flag && this.biome == optional.get()) {
            if (this.feature == null || flag && p_193453_1_.structureFeatureManager().getStructureAt(blockpos, true, this.feature).isValid()) {
               if (this.smokey == null || flag && this.smokey == CampfireBlock.isSmokeyPos(p_193453_1_, blockpos)) {
                  if (!this.light.matches(p_193453_1_, blockpos)) {
                     return false;
                  } else if (!this.block.matches(p_193453_1_, blockpos)) {
                     return false;
                  } else {
                     return this.fluid.matches(p_193453_1_, blockpos);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("x", this.x.serializeToJson());
            jsonobject1.add("y", this.y.serializeToJson());
            jsonobject1.add("z", this.z.serializeToJson());
            jsonobject.add("position", jsonobject1);
         }

         if (this.dimension != null) {
            World.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension).resultOrPartial(LOGGER::error).ifPresent((p_235307_1_) -> {
               jsonobject.add("dimension", p_235307_1_);
            });
         }

         if (this.feature != null) {
            jsonobject.addProperty("feature", this.feature.getFeatureName());
         }

         if (this.biome != null) {
            jsonobject.addProperty("biome", this.biome.location().toString());
         }

         if (this.smokey != null) {
            jsonobject.addProperty("smokey", this.smokey);
         }

         jsonobject.add("light", this.light.serializeToJson());
         jsonobject.add("block", this.block.serializeToJson());
         jsonobject.add("fluid", this.fluid.serializeToJson());
         return jsonobject;
      }
   }

   public static LocationPredicate fromJson(@Nullable JsonElement p_193454_0_) {
      if (p_193454_0_ != null && !p_193454_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_193454_0_, "location");
         JsonObject jsonobject1 = JSONUtils.getAsJsonObject(jsonobject, "position", new JsonObject());
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("z"));
         RegistryKey<World> registrykey = jsonobject.has("dimension") ? ResourceLocation.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("dimension")).resultOrPartial(LOGGER::error).map((p_235310_0_) -> {
            return RegistryKey.create(Registry.DIMENSION_REGISTRY, p_235310_0_);
         }).orElse((RegistryKey<World>)null) : null;
         Structure<?> structure = jsonobject.has("feature") ? Structure.STRUCTURES_REGISTRY.get(JSONUtils.getAsString(jsonobject, "feature")) : null;
         RegistryKey<Biome> registrykey1 = null;
         if (jsonobject.has("biome")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "biome"));
            registrykey1 = RegistryKey.create(Registry.BIOME_REGISTRY, resourcelocation);
         }

         Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
         LightPredicate lightpredicate = LightPredicate.fromJson(jsonobject.get("light"));
         BlockPredicate blockpredicate = BlockPredicate.fromJson(jsonobject.get("block"));
         FluidPredicate fluidpredicate = FluidPredicate.fromJson(jsonobject.get("fluid"));
         return new LocationPredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, registrykey1, structure, registrykey, obool, lightpredicate, blockpredicate, fluidpredicate);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.FloatBound x = MinMaxBounds.FloatBound.ANY;
      private MinMaxBounds.FloatBound y = MinMaxBounds.FloatBound.ANY;
      private MinMaxBounds.FloatBound z = MinMaxBounds.FloatBound.ANY;
      @Nullable
      private RegistryKey<Biome> biome;
      @Nullable
      private Structure<?> feature;
      @Nullable
      private RegistryKey<World> dimension;
      @Nullable
      private Boolean smokey;
      private LightPredicate light = LightPredicate.ANY;
      private BlockPredicate block = BlockPredicate.ANY;
      private FluidPredicate fluid = FluidPredicate.ANY;

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder setBiome(@Nullable RegistryKey<Biome> p_242666_1_) {
         this.biome = p_242666_1_;
         return this;
      }

      public LocationPredicate.Builder setBlock(BlockPredicate p_235312_1_) {
         this.block = p_235312_1_;
         return this;
      }

      public LocationPredicate.Builder setSmokey(Boolean p_235313_1_) {
         this.smokey = p_235313_1_;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}
