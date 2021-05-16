package net.minecraft.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResourcePack extends AutoCloseable, net.minecraftforge.common.extensions.IForgeResourcePack {
   @OnlyIn(Dist.CLIENT)
   InputStream getRootResource(String p_195763_1_) throws IOException;

   InputStream getResource(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException;

   Collection<ResourceLocation> getResources(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_);

   boolean hasResource(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_);

   Set<String> getNamespaces(ResourcePackType p_195759_1_);

   @Nullable
   <T> T getMetadataSection(IMetadataSectionSerializer<T> p_195760_1_) throws IOException;

   String getName();

   void close();
}
