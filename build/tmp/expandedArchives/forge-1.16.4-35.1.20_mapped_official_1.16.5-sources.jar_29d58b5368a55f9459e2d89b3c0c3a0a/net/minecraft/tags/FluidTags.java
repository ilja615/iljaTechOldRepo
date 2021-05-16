package net.minecraft.tags;

import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

public final class FluidTags {
   protected static final TagRegistry<Fluid> HELPER = TagRegistryManager.create(new ResourceLocation("fluid"), ITagCollectionSupplier::getFluids);
   public static final ITag.INamedTag<Fluid> WATER = bind("water");
   public static final ITag.INamedTag<Fluid> LAVA = bind("lava");

   public static ITag.INamedTag<Fluid> bind(String p_206956_0_) {
      return HELPER.bind(p_206956_0_);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Fluid> createOptional(ResourceLocation name) {
       return createOptional(name, null);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Fluid> createOptional(ResourceLocation name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<Fluid>> defaults) {
      return HELPER.createOptional(name, defaults);
   }

   public static List<? extends ITag.INamedTag<Fluid>> getWrappers() {
      return HELPER.getWrappers();
   }

   //Forge: Readd this stripped getter like the other tag classes
   public static ITagCollection<Fluid> getAllTags() {
       return HELPER.getAllTags();
   }
}
