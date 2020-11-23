package net.minecraft.tags;

import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

public final class FluidTags {
   protected static final TagRegistry<Fluid> collection = TagRegistryManager.create(new ResourceLocation("fluid"), ITagCollectionSupplier::getFluidTags);
   public static final ITag.INamedTag<Fluid> WATER = makeWrapperTag("water");
   public static final ITag.INamedTag<Fluid> LAVA = makeWrapperTag("lava");

   public static ITag.INamedTag<Fluid> makeWrapperTag(String id) {
      return collection.createTag(id);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Fluid> createOptional(ResourceLocation name) {
       return collection.createOptional(name, () -> null);
   }

   public static List<? extends ITag.INamedTag<Fluid>> getAllTags() {
      return collection.getTags();
   }

   //Forge: Readd this stripped getter like the other tag classes
   public static ITagCollection<Fluid> getCollection() {
       return collection.getCollection();
   }
}