package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NBTToSNBTConverter(DataGenerator p_i48258_1_) {
      this.generator = p_i48258_1_;
   }

   public void run(DirectoryCache p_200398_1_) throws IOException {
      Path path = this.generator.getOutputFolder();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_200416_0_) -> {
            return p_200416_0_.toString().endsWith(".nbt");
         }).forEach((p_200415_3_) -> {
            convertStructure(p_200415_3_, this.getName(path1, p_200415_3_), path);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getName(Path p_200417_1_, Path p_200417_2_) {
      String s = p_200417_1_.relativize(p_200417_2_).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".nbt".length());
   }

   @Nullable
   public static Path convertStructure(Path p_229443_0_, String p_229443_1_, Path p_229443_2_) {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(Files.newInputStream(p_229443_0_));
         ITextComponent itextcomponent = compoundnbt.getPrettyDisplay("    ", 0);
         String s = itextcomponent.getString() + "\n";
         Path path = p_229443_2_.resolve(p_229443_1_ + ".snbt");
         Files.createDirectories(path.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
            bufferedwriter.write(s);
         }

         LOGGER.info("Converted {} from NBT to SNBT", (Object)p_229443_1_);
         return path;
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", p_229443_1_, p_229443_0_, ioexception);
         return null;
      }
   }
}
