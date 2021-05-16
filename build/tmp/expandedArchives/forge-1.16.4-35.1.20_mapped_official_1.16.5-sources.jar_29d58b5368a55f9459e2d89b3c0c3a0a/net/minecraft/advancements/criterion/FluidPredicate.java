package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class FluidPredicate {
   public static final FluidPredicate ANY = new FluidPredicate((ITag<Fluid>)null, (Fluid)null, StatePropertiesPredicate.ANY);
   @Nullable
   private final ITag<Fluid> tag;
   @Nullable
   private final Fluid fluid;
   private final StatePropertiesPredicate properties;

   public FluidPredicate(@Nullable ITag<Fluid> p_i225738_1_, @Nullable Fluid p_i225738_2_, StatePropertiesPredicate p_i225738_3_) {
      this.tag = p_i225738_1_;
      this.fluid = p_i225738_2_;
      this.properties = p_i225738_3_;
   }

   public boolean matches(ServerWorld p_226649_1_, BlockPos p_226649_2_) {
      if (this == ANY) {
         return true;
      } else if (!p_226649_1_.isLoaded(p_226649_2_)) {
         return false;
      } else {
         FluidState fluidstate = p_226649_1_.getFluidState(p_226649_2_);
         Fluid fluid = fluidstate.getType();
         if (this.tag != null && !this.tag.contains(fluid)) {
            return false;
         } else if (this.fluid != null && fluid != this.fluid) {
            return false;
         } else {
            return this.properties.matches(fluidstate);
         }
      }
   }

   public static FluidPredicate fromJson(@Nullable JsonElement p_226648_0_) {
      if (p_226648_0_ != null && !p_226648_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_226648_0_, "fluid");
         Fluid fluid = null;
         if (jsonobject.has("fluid")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "fluid"));
            fluid = Registry.FLUID.get(resourcelocation);
         }

         ITag<Fluid> itag = null;
         if (jsonobject.has("tag")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(jsonobject, "tag"));
            itag = TagCollectionManager.getInstance().getFluids().getTag(resourcelocation1);
            if (itag == null) {
               throw new JsonSyntaxException("Unknown fluid tag '" + resourcelocation1 + "'");
            }
         }

         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(jsonobject.get("state"));
         return new FluidPredicate(itag, fluid, statepropertiespredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.fluid != null) {
            jsonobject.addProperty("fluid", Registry.FLUID.getKey(this.fluid).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", TagCollectionManager.getInstance().getFluids().getIdOrThrow(this.tag).toString());
         }

         jsonobject.add("state", this.properties.serializeToJson());
         return jsonobject;
      }
   }
}
