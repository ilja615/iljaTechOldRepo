package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex assetIndex;

   public VirtualAssetsPack(ResourceIndex p_i48115_1_) {
      super("minecraft", "realms");
      this.assetIndex = p_i48115_1_;
   }

   @Nullable
   protected InputStream getResourceAsStream(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      if (p_195782_1_ == ResourcePackType.CLIENT_RESOURCES) {
         File file1 = this.assetIndex.getFile(p_195782_2_);
         if (file1 != null && file1.exists()) {
            try {
               return new FileInputStream(file1);
            } catch (FileNotFoundException filenotfoundexception) {
            }
         }
      }

      return super.getResourceAsStream(p_195782_1_, p_195782_2_);
   }

   public boolean hasResource(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      if (p_195764_1_ == ResourcePackType.CLIENT_RESOURCES) {
         File file1 = this.assetIndex.getFile(p_195764_2_);
         if (file1 != null && file1.exists()) {
            return true;
         }
      }

      return super.hasResource(p_195764_1_, p_195764_2_);
   }

   @Nullable
   protected InputStream getResourceAsStream(String p_200010_1_) {
      File file1 = this.assetIndex.getRootFile(p_200010_1_);
      if (file1 != null && file1.exists()) {
         try {
            return new FileInputStream(file1);
         } catch (FileNotFoundException filenotfoundexception) {
         }
      }

      return super.getResourceAsStream(p_200010_1_);
   }

   public Collection<ResourceLocation> getResources(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      Collection<ResourceLocation> collection = super.getResources(p_225637_1_, p_225637_2_, p_225637_3_, p_225637_4_, p_225637_5_);
      collection.addAll(this.assetIndex.getFiles(p_225637_3_, p_225637_2_, p_225637_4_, p_225637_5_));
      return collection;
   }
}
