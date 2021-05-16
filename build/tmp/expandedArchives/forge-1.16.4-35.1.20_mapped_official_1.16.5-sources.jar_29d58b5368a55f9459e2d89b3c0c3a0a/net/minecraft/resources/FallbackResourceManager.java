package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List<IResourcePack> fallbacks = Lists.newArrayList();
   private final ResourcePackType type;
   private final String namespace;

   public FallbackResourceManager(ResourcePackType p_i226096_1_, String p_i226096_2_) {
      this.type = p_i226096_1_;
      this.namespace = p_i226096_2_;
   }

   public void add(IResourcePack p_199021_1_) {
      this.fallbacks.add(p_199021_1_);
   }

   public Set<String> getNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
      this.validateLocation(p_199002_1_);
      IResourcePack iresourcepack = null;
      ResourceLocation resourcelocation = getMetadataLocation(p_199002_1_);

      for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
         IResourcePack iresourcepack1 = this.fallbacks.get(i);
         if (iresourcepack == null && iresourcepack1.hasResource(this.type, resourcelocation)) {
            iresourcepack = iresourcepack1;
         }

         if (iresourcepack1.hasResource(this.type, p_199002_1_)) {
            InputStream inputstream = null;
            if (iresourcepack != null) {
               inputstream = this.getWrappedResource(resourcelocation, iresourcepack);
            }

            return new SimpleResource(iresourcepack1.getName(), p_199002_1_, this.getWrappedResource(p_199002_1_, iresourcepack1), inputstream);
         }
      }

      throw new FileNotFoundException(p_199002_1_.toString());
   }

   public boolean hasResource(ResourceLocation p_219533_1_) {
      if (!this.isValidLocation(p_219533_1_)) {
         return false;
      } else {
         for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
            IResourcePack iresourcepack = this.fallbacks.get(i);
            if (iresourcepack.hasResource(this.type, p_219533_1_)) {
               return true;
            }
         }

         return false;
      }
   }

   protected InputStream getWrappedResource(ResourceLocation p_199019_1_, IResourcePack p_199019_2_) throws IOException {
      InputStream inputstream = p_199019_2_.getResource(this.type, p_199019_1_);
      return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakComplainerInputStream(inputstream, p_199019_1_, p_199019_2_.getName()) : inputstream);
   }

   private void validateLocation(ResourceLocation p_199022_1_) throws IOException {
      if (!this.isValidLocation(p_199022_1_)) {
         throw new IOException("Invalid relative path to resource: " + p_199022_1_);
      }
   }

   private boolean isValidLocation(ResourceLocation p_219541_1_) {
      return !p_219541_1_.getPath().contains("..");
   }

   public List<IResource> getResources(ResourceLocation p_199004_1_) throws IOException {
      this.validateLocation(p_199004_1_);
      List<IResource> list = Lists.newArrayList();
      ResourceLocation resourcelocation = getMetadataLocation(p_199004_1_);

      for(IResourcePack iresourcepack : this.fallbacks) {
         if (iresourcepack.hasResource(this.type, p_199004_1_)) {
            InputStream inputstream = iresourcepack.hasResource(this.type, resourcelocation) ? this.getWrappedResource(resourcelocation, iresourcepack) : null;
            list.add(new SimpleResource(iresourcepack.getName(), p_199004_1_, this.getWrappedResource(p_199004_1_, iresourcepack), inputstream));
         }
      }

      if (list.isEmpty()) {
         throw new FileNotFoundException(p_199004_1_.toString());
      } else {
         return list;
      }
   }

   public Collection<ResourceLocation> listResources(String p_199003_1_, Predicate<String> p_199003_2_) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(IResourcePack iresourcepack : this.fallbacks) {
         list.addAll(iresourcepack.getResources(this.type, this.namespace, p_199003_1_, Integer.MAX_VALUE, p_199003_2_));
      }

      Collections.sort(list);
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public Stream<IResourcePack> listPacks() {
      return this.fallbacks.stream();
   }

   static ResourceLocation getMetadataLocation(ResourceLocation p_199020_0_) {
      return new ResourceLocation(p_199020_0_.getNamespace(), p_199020_0_.getPath() + ".mcmeta");
   }

   static class LeakComplainerInputStream extends FilterInputStream {
      private final String message;
      private boolean closed;

      public LeakComplainerInputStream(InputStream p_i47727_1_, ResourceLocation p_i47727_2_, String p_i47727_3_) {
         super(p_i47727_1_);
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
         this.message = "Leaked resource: '" + p_i47727_2_ + "' loaded from pack: '" + p_i47727_3_ + "'\n" + bytearrayoutputstream;
      }

      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }
   }
}
