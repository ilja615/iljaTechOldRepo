package net.minecraft.tags;

import java.util.stream.Collectors;

public class TagCollectionManager {
   private static volatile ITagCollectionSupplier manager = net.minecraftforge.common.ForgeTagHandler.populateTagCollectionManager(ITagCollection.getTagCollectionFromMap(BlockTags.getAllTags().stream().distinct().collect(Collectors.toMap(ITag.INamedTag::getName, (blockTag) -> {
      return blockTag;
   }))), ITagCollection.getTagCollectionFromMap(ItemTags.getAllTags().stream().distinct().collect(Collectors.toMap(ITag.INamedTag::getName, (itemTag) -> {
      return itemTag;
   }))), ITagCollection.getTagCollectionFromMap(FluidTags.getAllTags().stream().distinct().collect(Collectors.toMap(ITag.INamedTag::getName, (fluidTag) -> {
      return fluidTag;
   }))), ITagCollection.getTagCollectionFromMap(EntityTypeTags.getAllTags().stream().distinct().collect(Collectors.toMap(ITag.INamedTag::getName, (entityTypeTag) -> {
      return entityTypeTag;
   }))));

   public static ITagCollectionSupplier getManager() {
      return manager;
   }

   public static void setManager(ITagCollectionSupplier managerIn) {
      manager = managerIn;
   }
}
