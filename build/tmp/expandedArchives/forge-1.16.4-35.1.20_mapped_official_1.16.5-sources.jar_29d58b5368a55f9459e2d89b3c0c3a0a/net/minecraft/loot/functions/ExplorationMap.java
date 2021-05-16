package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Structure<?> DEFAULT_FEATURE = Structure.BURIED_TREASURE;
   public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   private final Structure<?> destination;
   private final MapDecoration.Type mapDecoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipKnownStructures;

   private ExplorationMap(ILootCondition[] p_i232169_1_, Structure<?> p_i232169_2_, MapDecoration.Type p_i232169_3_, byte p_i232169_4_, int p_i232169_5_, boolean p_i232169_6_) {
      super(p_i232169_1_);
      this.destination = p_i232169_2_;
      this.mapDecoration = p_i232169_3_;
      this.zoom = p_i232169_4_;
      this.searchRadius = p_i232169_5_;
      this.skipKnownStructures = p_i232169_6_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.EXPLORATION_MAP;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.ORIGIN);
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.getItem() != Items.MAP) {
         return p_215859_1_;
      } else {
         Vector3d vector3d = p_215859_2_.getParamOrNull(LootParameters.ORIGIN);
         if (vector3d != null) {
            ServerWorld serverworld = p_215859_2_.getLevel();
            BlockPos blockpos = serverworld.findNearestMapFeature(this.destination, new BlockPos(vector3d), this.searchRadius, this.skipKnownStructures);
            if (blockpos != null) {
               ItemStack itemstack = FilledMapItem.create(serverworld, blockpos.getX(), blockpos.getZ(), this.zoom, true, true);
               FilledMapItem.renderBiomePreviewMap(serverworld, itemstack);
               MapData.addTargetDecoration(itemstack, blockpos, "+", this.mapDecoration);
               itemstack.setHoverName(new TranslationTextComponent("filled_map." + this.destination.getFeatureName().toLowerCase(Locale.ROOT)));
               return itemstack;
            }
         }

         return p_215859_1_;
      }
   }

   public static ExplorationMap.Builder makeExplorationMap() {
      return new ExplorationMap.Builder();
   }

   public static class Builder extends LootFunction.Builder<ExplorationMap.Builder> {
      private Structure<?> destination = ExplorationMap.DEFAULT_FEATURE;
      private MapDecoration.Type mapDecoration = ExplorationMap.DEFAULT_DECORATION;
      private byte zoom = 2;
      private int searchRadius = 50;
      private boolean skipKnownStructures = true;

      protected ExplorationMap.Builder getThis() {
         return this;
      }

      public ExplorationMap.Builder setDestination(Structure<?> p_237427_1_) {
         this.destination = p_237427_1_;
         return this;
      }

      public ExplorationMap.Builder setMapDecoration(MapDecoration.Type p_216064_1_) {
         this.mapDecoration = p_216064_1_;
         return this;
      }

      public ExplorationMap.Builder setZoom(byte p_216062_1_) {
         this.zoom = p_216062_1_;
         return this;
      }

      public ExplorationMap.Builder setSkipKnownStructures(boolean p_216063_1_) {
         this.skipKnownStructures = p_216063_1_;
         return this;
      }

      public ILootFunction build() {
         return new ExplorationMap(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }
   }

   public static class Serializer extends LootFunction.Serializer<ExplorationMap> {
      public void serialize(JsonObject p_230424_1_, ExplorationMap p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         if (!p_230424_2_.destination.equals(ExplorationMap.DEFAULT_FEATURE)) {
            p_230424_1_.add("destination", p_230424_3_.serialize(p_230424_2_.destination.getFeatureName()));
         }

         if (p_230424_2_.mapDecoration != ExplorationMap.DEFAULT_DECORATION) {
            p_230424_1_.add("decoration", p_230424_3_.serialize(p_230424_2_.mapDecoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (p_230424_2_.zoom != 2) {
            p_230424_1_.addProperty("zoom", p_230424_2_.zoom);
         }

         if (p_230424_2_.searchRadius != 50) {
            p_230424_1_.addProperty("search_radius", p_230424_2_.searchRadius);
         }

         if (!p_230424_2_.skipKnownStructures) {
            p_230424_1_.addProperty("skip_existing_chunks", p_230424_2_.skipKnownStructures);
         }

      }

      public ExplorationMap deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         Structure<?> structure = readStructure(p_186530_1_);
         String s = p_186530_1_.has("decoration") ? JSONUtils.getAsString(p_186530_1_, "decoration") : "mansion";
         MapDecoration.Type mapdecoration$type = ExplorationMap.DEFAULT_DECORATION;

         try {
            mapdecoration$type = MapDecoration.Type.valueOf(s.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException illegalargumentexception) {
            ExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMap.DEFAULT_DECORATION, (Object)s);
         }

         byte b0 = JSONUtils.getAsByte(p_186530_1_, "zoom", (byte)2);
         int i = JSONUtils.getAsInt(p_186530_1_, "search_radius", 50);
         boolean flag = JSONUtils.getAsBoolean(p_186530_1_, "skip_existing_chunks", true);
         return new ExplorationMap(p_186530_3_, structure, mapdecoration$type, b0, i, flag);
      }

      private static Structure<?> readStructure(JsonObject p_237428_0_) {
         if (p_237428_0_.has("destination")) {
            String s = JSONUtils.getAsString(p_237428_0_, "destination");
            Structure<?> structure = Structure.STRUCTURES_REGISTRY.get(s.toLowerCase(Locale.ROOT));
            if (structure != null) {
               return structure;
            }
         }

         return ExplorationMap.DEFAULT_FEATURE;
      }
   }
}
