package net.minecraft.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPack extends ResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final boolean ON_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
   private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

   public FolderPack(File p_i47914_1_) {
      super(p_i47914_1_);
   }

   public static boolean validatePath(File p_195777_0_, String p_195777_1_) throws IOException {
      String s = p_195777_0_.getCanonicalPath();
      if (ON_WINDOWS) {
         s = BACKSLASH_MATCHER.replaceFrom(s, '/');
      }

      return s.endsWith(p_195777_1_);
   }

   protected InputStream getResource(String p_195766_1_) throws IOException {
      File file1 = this.getFile(p_195766_1_);
      if (file1 == null) {
         throw new ResourcePackFileNotFoundException(this.file, p_195766_1_);
      } else {
         return new FileInputStream(file1);
      }
   }

   protected boolean hasResource(String p_195768_1_) {
      return this.getFile(p_195768_1_) != null;
   }

   @Nullable
   private File getFile(String p_195776_1_) {
      try {
         File file1 = new File(this.file, p_195776_1_);
         if (file1.isFile() && validatePath(file1, p_195776_1_)) {
            return file1;
         }
      } catch (IOException ioexception) {
      }

      return null;
   }

   public Set<String> getNamespaces(ResourcePackType p_195759_1_) {
      Set<String> set = Sets.newHashSet();
      File file1 = new File(this.file, p_195759_1_.getDirectory());
      File[] afile = file1.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
      if (afile != null) {
         for(File file2 : afile) {
            String s = getRelativePath(file1, file2);
            if (s.equals(s.toLowerCase(Locale.ROOT))) {
               set.add(s.substring(0, s.length() - 1));
            } else {
               this.logWarning(s);
            }
         }
      }

      return set;
   }

   public void close() {
   }

   public Collection<ResourceLocation> getResources(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      File file1 = new File(this.file, p_225637_1_.getDirectory());
      List<ResourceLocation> list = Lists.newArrayList();
      this.listResources(new File(new File(file1, p_225637_2_), p_225637_3_), p_225637_4_, p_225637_2_, list, p_225637_3_ + "/", p_225637_5_);
      return list;
   }

   private void listResources(File p_199546_1_, int p_199546_2_, String p_199546_3_, List<ResourceLocation> p_199546_4_, String p_199546_5_, Predicate<String> p_199546_6_) {
      File[] afile = p_199546_1_.listFiles();
      if (afile != null) {
         for(File file1 : afile) {
            if (file1.isDirectory()) {
               if (p_199546_2_ > 0) {
                  this.listResources(file1, p_199546_2_ - 1, p_199546_3_, p_199546_4_, p_199546_5_ + file1.getName() + "/", p_199546_6_);
               }
            } else if (!file1.getName().endsWith(".mcmeta") && p_199546_6_.test(file1.getName())) {
               try {
                  p_199546_4_.add(new ResourceLocation(p_199546_3_, p_199546_5_ + file1.getName()));
               } catch (ResourceLocationException resourcelocationexception) {
                  LOGGER.error(resourcelocationexception.getMessage());
               }
            }
         }
      }

   }
}
