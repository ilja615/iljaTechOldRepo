package net.minecraft.tags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;

public interface ITagCollectionSupplier extends net.minecraftforge.common.extensions.IForgeTagCollectionSupplier {
   ITagCollectionSupplier EMPTY = of(ITagCollection.empty(), ITagCollection.empty(), ITagCollection.empty(), ITagCollection.empty());

   ITagCollection<Block> getBlocks();

   ITagCollection<Item> getItems();

   ITagCollection<Fluid> getFluids();

   ITagCollection<EntityType<?>> getEntityTypes();

   default void bindToGlobal() {
      TagRegistryManager.resetAll(this);
      Blocks.rebuildCache();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent.VanillaTagTypes(this));
   }

   default void serializeToNetwork(PacketBuffer p_242210_1_) {
      this.getBlocks().serializeToNetwork(p_242210_1_, Registry.BLOCK);
      this.getItems().serializeToNetwork(p_242210_1_, Registry.ITEM);
      this.getFluids().serializeToNetwork(p_242210_1_, Registry.FLUID);
      this.getEntityTypes().serializeToNetwork(p_242210_1_, Registry.ENTITY_TYPE);
   }

   static ITagCollectionSupplier deserializeFromNetwork(PacketBuffer p_242211_0_) {
      ITagCollection<Block> itagcollection = ITagCollection.loadFromNetwork(p_242211_0_, Registry.BLOCK);
      ITagCollection<Item> itagcollection1 = ITagCollection.loadFromNetwork(p_242211_0_, Registry.ITEM);
      ITagCollection<Fluid> itagcollection2 = ITagCollection.loadFromNetwork(p_242211_0_, Registry.FLUID);
      ITagCollection<EntityType<?>> itagcollection3 = ITagCollection.loadFromNetwork(p_242211_0_, Registry.ENTITY_TYPE);
      return of(itagcollection, itagcollection1, itagcollection2, itagcollection3);
   }

   static ITagCollectionSupplier of(final ITagCollection<Block> p_242209_0_, final ITagCollection<Item> p_242209_1_, final ITagCollection<Fluid> p_242209_2_, final ITagCollection<EntityType<?>> p_242209_3_) {
      return new ITagCollectionSupplier() {
         public ITagCollection<Block> getBlocks() {
            return p_242209_0_;
         }

         public ITagCollection<Item> getItems() {
            return p_242209_1_;
         }

         public ITagCollection<Fluid> getFluids() {
            return p_242209_2_;
         }

         public ITagCollection<EntityType<?>> getEntityTypes() {
            return p_242209_3_;
         }
      };
   }

   static ITagCollectionSupplier reinjectOptionalTags(ITagCollectionSupplier tagCollectionSupplier) {
      ITagCollection<Block> blockTagCollection = BlockTags.HELPER.reinjectOptionalTags(tagCollectionSupplier.getBlocks());
      ITagCollection<Item> itemTagCollection = ItemTags.HELPER.reinjectOptionalTags(tagCollectionSupplier.getItems());
      ITagCollection<Fluid> fluidTagCollection = FluidTags.HELPER.reinjectOptionalTags(tagCollectionSupplier.getFluids());
      ITagCollection<EntityType<?>> entityTypeTagCollection = EntityTypeTags.HELPER.reinjectOptionalTags(tagCollectionSupplier.getEntityTypes());
      return new ITagCollectionSupplier() {
         @Override
         public ITagCollection<Block> getBlocks() {
            return blockTagCollection;
         }

         @Override
         public ITagCollection<Item> getItems() {
            return itemTagCollection;
         }

         @Override
         public ITagCollection<Fluid> getFluids() {
            return fluidTagCollection;
         }

         @Override
         public ITagCollection<EntityType<?>> getEntityTypes() {
            return entityTypeTagCollection;
         }
      };
   }
}
