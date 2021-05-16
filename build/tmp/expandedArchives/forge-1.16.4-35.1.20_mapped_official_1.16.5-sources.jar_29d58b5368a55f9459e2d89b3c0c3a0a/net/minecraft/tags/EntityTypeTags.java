package net.minecraft.tags;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public final class EntityTypeTags {
   protected static final TagRegistry<EntityType<?>> HELPER = TagRegistryManager.create(new ResourceLocation("entity_type"), ITagCollectionSupplier::getEntityTypes);
   public static final ITag.INamedTag<EntityType<?>> SKELETONS = bind("skeletons");
   public static final ITag.INamedTag<EntityType<?>> RAIDERS = bind("raiders");
   public static final ITag.INamedTag<EntityType<?>> BEEHIVE_INHABITORS = bind("beehive_inhabitors");
   public static final ITag.INamedTag<EntityType<?>> ARROWS = bind("arrows");
   public static final ITag.INamedTag<EntityType<?>> IMPACT_PROJECTILES = bind("impact_projectiles");

   public static ITag.INamedTag<EntityType<?>> bind(String p_232896_0_) {
      return HELPER.bind(p_232896_0_);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<EntityType<?>> createOptional(ResourceLocation name) {
       return createOptional(name, null);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<EntityType<?>> createOptional(ResourceLocation name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<EntityType<?>>> defaults) {
      return HELPER.createOptional(name, defaults);
   }

   public static ITagCollection<EntityType<?>> getAllTags() {
      return HELPER.getAllTags();
   }

   public static List<? extends ITag.INamedTag<EntityType<?>>> getWrappers() {
      return HELPER.getWrappers();
   }
}
