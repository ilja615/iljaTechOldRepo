package net.minecraft.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

   @OnlyIn(Dist.CLIENT)
   public static String findAvailableName(Path p_214992_0_, String p_214992_1_, String p_214992_2_) throws IOException {
      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_214992_1_ = p_214992_1_.replace(c0, '_');
      }

      p_214992_1_ = p_214992_1_.replaceAll("[./\"]", "_");
      if (RESERVED_WINDOWS_FILENAMES.matcher(p_214992_1_).matches()) {
         p_214992_1_ = "_" + p_214992_1_ + "_";
      }

      Matcher matcher = COPY_COUNTER_PATTERN.matcher(p_214992_1_);
      int j = 0;
      if (matcher.matches()) {
         p_214992_1_ = matcher.group("name");
         j = Integer.parseInt(matcher.group("count"));
      }

      if (p_214992_1_.length() > 255 - p_214992_2_.length()) {
         p_214992_1_ = p_214992_1_.substring(0, 255 - p_214992_2_.length());
      }

      while(true) {
         String s = p_214992_1_;
         if (j != 0) {
            String s1 = " (" + j + ")";
            int i = 255 - s1.length();
            if (p_214992_1_.length() > i) {
               s = p_214992_1_.substring(0, i);
            }

            s = s + s1;
         }

         s = s + p_214992_2_;
         Path path = p_214992_0_.resolve(s);

         try {
            Path path1 = Files.createDirectory(path);
            Files.deleteIfExists(path1);
            return p_214992_0_.relativize(path1).toString();
         } catch (FileAlreadyExistsException filealreadyexistsexception) {
            ++j;
         }
      }
   }

   public static boolean isPathNormalized(Path p_214995_0_) {
      Path path = p_214995_0_.normalize();
      return path.equals(p_214995_0_);
   }

   public static boolean isPathPortable(Path p_214994_0_) {
      for(Path path : p_214994_0_) {
         if (RESERVED_WINDOWS_FILENAMES.matcher(path.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path createPathToResource(Path p_214993_0_, String p_214993_1_, String p_214993_2_) {
      String s = p_214993_1_ + p_214993_2_;
      Path path = Paths.get(s);
      if (path.endsWith(p_214993_2_)) {
         throw new InvalidPathException(s, "empty resource name");
      } else {
         return p_214993_0_.resolve(path);
      }
   }
}
