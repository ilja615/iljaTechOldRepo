package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespacedPacks = Maps.newHashMap();
   private final List<IFutureReloadListener> listeners = Lists.newArrayList();
   private final List<IFutureReloadListener> recentlyRegistered = Lists.newArrayList();
   private final Set<String> namespaces = Sets.newLinkedHashSet();
   private final List<IResourcePack> packs = Lists.newArrayList();
   private final ResourcePackType type;

   public SimpleReloadableResourceManager(ResourcePackType p_i47905_1_) {
      this.type = p_i47905_1_;
   }

   public void add(IResourcePack p_199021_1_) {
      this.packs.add(p_199021_1_);

      for(String s : p_199021_1_.getNamespaces(this.type)) {
         this.namespaces.add(s);
         FallbackResourceManager fallbackresourcemanager = this.namespacedPacks.get(s);
         if (fallbackresourcemanager == null) {
            fallbackresourcemanager = new FallbackResourceManager(this.type, s);
            this.namespacedPacks.put(s, fallbackresourcemanager);
         }

         fallbackresourcemanager.add(p_199021_1_);
      }

   }

   public Set<String> getNamespaces() {
      return this.namespaces;
   }

   public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
      IResourceManager iresourcemanager = this.namespacedPacks.get(p_199002_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResource(p_199002_1_);
      } else {
         throw new FileNotFoundException(p_199002_1_.toString());
      }
   }

   public boolean hasResource(ResourceLocation p_219533_1_) {
      IResourceManager iresourcemanager = this.namespacedPacks.get(p_219533_1_.getNamespace());
      return iresourcemanager != null ? iresourcemanager.hasResource(p_219533_1_) : false;
   }

   public List<IResource> getResources(ResourceLocation p_199004_1_) throws IOException {
      IResourceManager iresourcemanager = this.namespacedPacks.get(p_199004_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResources(p_199004_1_);
      } else {
         throw new FileNotFoundException(p_199004_1_.toString());
      }
   }

   public Collection<ResourceLocation> listResources(String p_199003_1_, Predicate<String> p_199003_2_) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(FallbackResourceManager fallbackresourcemanager : this.namespacedPacks.values()) {
         set.addAll(fallbackresourcemanager.listResources(p_199003_1_, p_199003_2_));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   private void clear() {
      this.namespacedPacks.clear();
      this.namespaces.clear();
      this.packs.forEach(IResourcePack::close);
      this.packs.clear();
   }

   public void close() {
      this.clear();
   }

   public void registerReloadListener(IFutureReloadListener p_219534_1_) {
      this.listeners.add(p_219534_1_);
      this.recentlyRegistered.add(p_219534_1_);
   }

   protected IAsyncReloader createReload(Executor p_219538_1_, Executor p_219538_2_, List<IFutureReloadListener> p_219538_3_, CompletableFuture<Unit> p_219538_4_) {
      IAsyncReloader iasyncreloader;
      if (LOGGER.isDebugEnabled()) {
         iasyncreloader = new DebugAsyncReloader(this, Lists.newArrayList(p_219538_3_), p_219538_1_, p_219538_2_, p_219538_4_);
      } else {
         iasyncreloader = AsyncReloader.of(this, Lists.newArrayList(p_219538_3_), p_219538_1_, p_219538_2_, p_219538_4_);
      }

      this.recentlyRegistered.clear();
      return iasyncreloader;
   }

   public IAsyncReloader createFullReload(Executor p_219537_1_, Executor p_219537_2_, CompletableFuture<Unit> p_219537_3_, List<IResourcePack> p_219537_4_) {
      this.clear();
      LOGGER.info("Reloading ResourceManager: {}", () -> {
         return p_219537_4_.stream().map(IResourcePack::getName).collect(Collectors.joining(", "));
      });

      for(IResourcePack iresourcepack : p_219537_4_) {
         try {
            this.add(iresourcepack);
         } catch (Exception exception) {
            LOGGER.error("Failed to add resource pack {}", iresourcepack.getName(), exception);
            return new SimpleReloadableResourceManager.FailedPackReloader(new SimpleReloadableResourceManager.FailedPackException(iresourcepack, exception));
         }
      }

      return this.createReload(p_219537_1_, p_219537_2_, this.listeners, p_219537_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public Stream<IResourcePack> listPacks() {
      return this.packs.stream();
   }

   public static class FailedPackException extends RuntimeException {
      private final IResourcePack pack;

      public FailedPackException(IResourcePack p_i229962_1_, Throwable p_i229962_2_) {
         super(p_i229962_1_.getName(), p_i229962_2_);
         this.pack = p_i229962_1_;
      }

      @OnlyIn(Dist.CLIENT)
      public IResourcePack getPack() {
         return this.pack;
      }
   }

   static class FailedPackReloader implements IAsyncReloader {
      private final SimpleReloadableResourceManager.FailedPackException exception;
      private final CompletableFuture<Unit> failedFuture;

      public FailedPackReloader(SimpleReloadableResourceManager.FailedPackException p_i229961_1_) {
         this.exception = p_i229961_1_;
         this.failedFuture = new CompletableFuture<>();
         this.failedFuture.completeExceptionally(p_i229961_1_);
      }

      public CompletableFuture<Unit> done() {
         return this.failedFuture;
      }

      @OnlyIn(Dist.CLIENT)
      public float getActualProgress() {
         return 0.0F;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isApplying() {
         return false;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isDone() {
         return true;
      }

      @OnlyIn(Dist.CLIENT)
      public void checkExceptions() {
         throw this.exception;
      }
   }
}
