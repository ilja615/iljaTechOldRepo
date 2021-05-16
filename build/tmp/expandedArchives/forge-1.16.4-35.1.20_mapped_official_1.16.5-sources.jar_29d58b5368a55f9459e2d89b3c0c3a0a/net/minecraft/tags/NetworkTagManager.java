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
   private ITagCollectionSupplier tags = ITagCollectionSupplier.EMPTY;

   public ITagCollectionSupplier getTags() {
      return this.tags;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture = this.blocks.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture1 = this.items.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture2 = this.fluids.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture3 = this.entityTypes.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<java.util.List<net.minecraftforge.common.ForgeTagHandler.TagCollectionReaderInfo>> customTagTypeResults = net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeReloadResults(p_215226_2_, p_215226_5_, customTagTypeReaders);
      return CompletableFuture.allOf(completablefuture, completablefuture1, completablefuture2, completablefuture3, customTagTypeResults).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_232979_5_) -> {
         ITagCollection<Block> itagcollection = this.blocks.load(completablefuture.join());
         ITagCollection<Item> itagcollection1 = this.items.load(completablefuture1.join());
         ITagCollection<Fluid> itagcollection2 = this.fluids.load(completablefuture2.join());
         ITagCollection<EntityType<?>> itagcollection3 = this.entityTypes.load(completablefuture3.join());
         net.minecraftforge.common.ForgeTagHandler.updateCustomTagTypes(customTagTypeResults.join());
         ITagCollectionSupplier itagcollectionsupplier = ITagCollectionSupplier.of(itagcollection, itagcollection1, itagcollection2, itagcollection3);
         Multimap<ResourceLocation, ResourceLocation> multimap = TagRegistryManager.getAllMissingTags(itagcollectionsupplier);
         if (!multimap.isEmpty()) {
            throw new IllegalStateException("Missing required tags: " + (String)multimap.entries().stream().map((p_232978_0_) -> {
               return p_232978_0_.getKey() + ":" + p_232978_0_.getValue();
            }).sorted().collect(Collectors.joining(",")));
         } else {
            itagcollectionsupplier = ITagCollectionSupplier.reinjectOptionalTags(itagcollectionsupplier);
            net.minecraftforge.common.ForgeTagHandler.reinjectOptionalTagsCustomTypes();
            TagCollectionManager.bind(itagcollectionsupplier);
            this.tags = itagcollectionsupplier;
         }
      }, p_215226_6_);
   }
}
