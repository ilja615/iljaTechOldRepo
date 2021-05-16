package net.minecraft.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResourceManager {
   Set<String> getNamespaces();

   IResource getResource(ResourceLocation p_199002_1_) throws IOException;

   boolean hasResource(ResourceLocation p_219533_1_);

   List<IResource> getResources(ResourceLocation p_199004_1_) throws IOException;

   Collection<ResourceLocation> listResources(String p_199003_1_, Predicate<String> p_199003_2_);

   @OnlyIn(Dist.CLIENT)
   Stream<IResourcePack> listPacks();

   public static enum Instance implements IResourceManager {
      INSTANCE;

      public Set<String> getNamespaces() {
         return ImmutableSet.of();
      }

      public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
         throw new FileNotFoundException(p_199002_1_.toString());
      }

      public boolean hasResource(ResourceLocation p_219533_1_) {
         return false;
      }

      public List<IResource> getResources(ResourceLocation p_199004_1_) {
         return ImmutableList.of();
      }

      public Collection<ResourceLocation> listResources(String p_199003_1_, Predicate<String> p_199003_2_) {
         return ImmutableSet.of();
      }

      @OnlyIn(Dist.CLIENT)
      public Stream<IResourcePack> listPacks() {
         return Stream.of();
      }
   }
}
