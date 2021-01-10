package net.minecraft.tags;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagManager implements IFutureReloadListener {
   protected TagCollectionReader<Block> blocks = new TagCollectionReader<>(Registry.BLOCK::getOptional, "tags/blocks", "block");
   protected TagCollectionReader<Item> items = new TagCollectionReader<>(Registry.ITEM::getOptional, "tags/items", "item");
   protected TagCollectionReader<Fluid> fluids = new TagCollectionReader<>(Registry.FLUID::getOptional, "tags/fluids", "fluid");
   protected TagCollectionReader<EntityType<?>> entityTypes = new TagCollectionReader<>(Registry.ENTITY_TYPE::getOptional, "tags/entity_types", "entity_type");
   protected Map<ResourceLocation, TagCollectionReader<?>> customTagTypeReaders = net.minecraftforge.common.ForgeTagHandler.createCustomTagTypeReaders();
   private ITagCollectionSupplier tagCollectionSupplier = ITagCollectionSupplier.TAG_COLLECTION_SUPPLIER;

   public ITagCollectionSupplier getTagCollectionSupplier() {
      return this.tagCollectionSupplier;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture = this.blocks.readTagsFromManager(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture1 = this.items.readTagsFromManager(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture2 = this.fluids.readTagsFromManager(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture3 = this.entityTypes.readTagsFromManager(resourceManager, backgroundExecutor);
      CompletableFuture<java.util.List<net.minecraftforge.common.ForgeTagHandler.TagCollectionReaderInfo>> customTagTypeResults = net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeReloadResults(resourceManager, backgroundExecutor, customTagTypeReaders);
      return CompletableFuture.allOf(completablefuture, completablefuture1, completablefuture2, completablefuture3, customTagTypeResults).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((voidIn) -> {
         ITagCollection<Block> itagcollection = this.blocks.buildTagCollectionFromMap(completablefuture.join());
         ITagCollection<Item> itagcollection1 = this.items.buildTagCollectionFromMap(completablefuture1.join());
         ITagCollection<Fluid> itagcollection2 = this.fluids.buildTagCollectionFromMap(completablefuture2.join());
         ITagCollection<EntityType<?>> itagcollection3 = this.entityTypes.buildTagCollectionFromMap(completablefuture3.join());
         net.minecraftforge.common.ForgeTagHandler.updateCustomTagTypes(customTagTypeResults.join());
         ITagCollectionSupplier itagcollectionsupplier = ITagCollectionSupplier.getTagCollectionSupplier(itagcollection, itagcollection1, itagcollection2, itagcollection3);
         Multimap<ResourceLocation, ResourceLocation> multimap = TagRegistryManager.validateTags(itagcollectionsupplier);
         if (!multimap.isEmpty()) {
            throw new IllegalStateException("Missing required tags: " + (String)multimap.entries().stream().map((tags) -> {
               return tags.getKey() + ":" + tags.getValue();
            }).sorted().collect(Collectors.joining(",")));
         } else {
            itagcollectionsupplier = ITagCollectionSupplier.reinjectOptionalTags(itagcollectionsupplier);
            net.minecraftforge.common.ForgeTagHandler.reinjectOptionalTagsCustomTypes();
            TagCollectionManager.setManager(itagcollectionsupplier);
            this.tagCollectionSupplier = itagcollectionsupplier;
         }
      }, gameExecutor);
   }
}
