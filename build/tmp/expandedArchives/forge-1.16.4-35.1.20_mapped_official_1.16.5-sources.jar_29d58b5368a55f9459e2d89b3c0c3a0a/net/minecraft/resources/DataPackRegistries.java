package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.command.Commands;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTableManager;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.Unit;

public class DataPackRegistries implements AutoCloseable {
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private final IReloadableResourceManager resources = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
   private final Commands commands;
   private final RecipeManager recipes = new RecipeManager();
   private final NetworkTagManager tagManager = new NetworkTagManager();
   private final LootPredicateManager predicateManager = new LootPredicateManager();
   private final LootTableManager lootTables = new LootTableManager(this.predicateManager);
   private final AdvancementManager advancements = new AdvancementManager(this.predicateManager);
   private final FunctionReloader functionLibrary;

   public DataPackRegistries(Commands.EnvironmentType p_i232598_1_, int p_i232598_2_) {
      this.commands = new Commands(p_i232598_1_);
      this.functionLibrary = new FunctionReloader(p_i232598_2_, this.commands.getDispatcher());
      this.resources.registerReloadListener(this.tagManager);
      this.resources.registerReloadListener(this.predicateManager);
      this.resources.registerReloadListener(this.recipes);
      this.resources.registerReloadListener(this.lootTables);
      this.resources.registerReloadListener(this.functionLibrary);
      this.resources.registerReloadListener(this.advancements);
      net.minecraftforge.event.ForgeEventFactory.onResourceReload(this).forEach(resources::registerReloadListener);
   }

   public FunctionReloader getFunctionLibrary() {
      return this.functionLibrary;
   }

   public LootPredicateManager getPredicateManager() {
      return this.predicateManager;
   }

   public LootTableManager getLootTables() {
      return this.lootTables;
   }

   public ITagCollectionSupplier getTags() {
      return this.tagManager.getTags();
   }

   public RecipeManager getRecipeManager() {
      return this.recipes;
   }

   public Commands getCommands() {
      return this.commands;
   }

   public AdvancementManager getAdvancements() {
      return this.advancements;
   }

   public IResourceManager getResourceManager() {
      return this.resources;
   }

   public static CompletableFuture<DataPackRegistries> loadResources(List<IResourcePack> p_240961_0_, Commands.EnvironmentType p_240961_1_, int p_240961_2_, Executor p_240961_3_, Executor p_240961_4_) {
      DataPackRegistries datapackregistries = new DataPackRegistries(p_240961_1_, p_240961_2_);
      CompletableFuture<Unit> completablefuture = datapackregistries.resources.reload(p_240961_3_, p_240961_4_, p_240961_0_, DATA_RELOAD_INITIAL_TASK);
      return completablefuture.whenComplete((p_240963_1_, p_240963_2_) -> {
         if (p_240963_2_ != null) {
            datapackregistries.close();
         }

      }).thenApply((p_240962_1_) -> {
         return datapackregistries;
      });
   }

   public void updateGlobals() {
      this.tagManager.getTags().bindToGlobal();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent.CustomTagTypes(tagManager.getTags()));
   }

   public void close() {
      this.resources.close();
   }
}
