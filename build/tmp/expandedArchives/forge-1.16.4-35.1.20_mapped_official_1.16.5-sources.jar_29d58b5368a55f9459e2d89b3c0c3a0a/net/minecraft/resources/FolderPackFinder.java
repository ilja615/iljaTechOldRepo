package net.minecraft.resources;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
   private static final FileFilter RESOURCEPACK_FILTER = (p_195731_0_) -> {
      boolean flag = p_195731_0_.isFile() && p_195731_0_.getName().endsWith(".zip");
      boolean flag1 = p_195731_0_.isDirectory() && (new File(p_195731_0_, "pack.mcmeta")).isFile();
      return flag || flag1;
   };
   private final File folder;
   private final IPackNameDecorator packSource;

   public FolderPackFinder(File p_i231420_1_, IPackNameDecorator p_i231420_2_) {
      this.folder = p_i231420_1_;
      this.packSource = p_i231420_2_;
   }

   public void loadPacks(Consumer<ResourcePackInfo> p_230230_1_, ResourcePackInfo.IFactory p_230230_2_) {
      if (!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] afile = this.folder.listFiles(RESOURCEPACK_FILTER);
      if (afile != null) {
         for(File file1 : afile) {
            String s = "file/" + file1.getName();
            ResourcePackInfo resourcepackinfo = ResourcePackInfo.create(s, false, this.createSupplier(file1), p_230230_2_, ResourcePackInfo.Priority.TOP, this.packSource);
            if (resourcepackinfo != null) {
               p_230230_1_.accept(resourcepackinfo);
            }
         }

      }
   }

   private Supplier<IResourcePack> createSupplier(File p_195733_1_) {
      return p_195733_1_.isDirectory() ? () -> {
         return new FolderPack(p_195733_1_);
      } : () -> {
         return new FilePack(p_195733_1_);
      };
   }
}
