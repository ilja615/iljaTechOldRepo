package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SNBTToNBTConverter implements IDataProvider {
   @Nullable
   private static final Path dumpSnbtTo = null;
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;
   private final List<SNBTToNBTConverter.ITransformer> filters = Lists.newArrayList();

   public SNBTToNBTConverter(DataGenerator p_i48257_1_) {
      this.generator = p_i48257_1_;
   }

   public SNBTToNBTConverter addFilter(SNBTToNBTConverter.ITransformer p_225369_1_) {
      this.filters.add(p_225369_1_);
      return this;
   }

   private CompoundNBT applyFilters(String p_225368_1_, CompoundNBT p_225368_2_) {
      CompoundNBT compoundnbt = p_225368_2_;

      for(SNBTToNBTConverter.ITransformer snbttonbtconverter$itransformer : this.filters) {
         compoundnbt = snbttonbtconverter$itransformer.apply(p_225368_1_, compoundnbt);
      }

      return compoundnbt;
   }

   public void run(DirectoryCache p_200398_1_) throws IOException {
      Path path = this.generator.getOutputFolder();
      List<CompletableFuture<SNBTToNBTConverter.TaskResult>> list = Lists.newArrayList();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_200422_0_) -> {
            return p_200422_0_.toString().endsWith(".snbt");
         }).forEach((p_229447_3_) -> {
            list.add(CompletableFuture.supplyAsync(() -> {
               return this.readStructure(p_229447_3_, this.getName(path1, p_229447_3_));
            }, Util.backgroundExecutor()));
         });
      }

      Util.sequence(list).join().stream().filter(Objects::nonNull).forEach((p_229445_3_) -> {
         this.storeStructureIfChanged(p_200398_1_, p_229445_3_, path);
      });
   }

   public String getName() {
      return "SNBT -> NBT";
   }

   private String getName(Path p_200423_1_, Path p_200423_2_) {
      String s = p_200423_1_.relativize(p_200423_2_).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".snbt".length());
   }

   @Nullable
   private SNBTToNBTConverter.TaskResult readStructure(Path p_229446_1_, String p_229446_2_) {
      try (BufferedReader bufferedreader = Files.newBufferedReader(p_229446_1_)) {
         String s = IOUtils.toString((Reader)bufferedreader);
         CompoundNBT compoundnbt = this.applyFilters(p_229446_2_, JsonToNBT.parseTag(s));
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         CompressedStreamTools.writeCompressed(compoundnbt, bytearrayoutputstream);
         byte[] abyte = bytearrayoutputstream.toByteArray();
         String s1 = SHA1.hashBytes(abyte).toString();
         String s2;
         if (dumpSnbtTo != null) {
            s2 = compoundnbt.getPrettyDisplay("    ", 0).getString() + "\n";
         } else {
            s2 = null;
         }

         return new SNBTToNBTConverter.TaskResult(p_229446_2_, abyte, s2, s1);
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", p_229446_2_, p_229446_1_, commandsyntaxexception);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", p_229446_2_, p_229446_1_, ioexception);
      }

      return null;
   }

   private void storeStructureIfChanged(DirectoryCache p_229444_1_, SNBTToNBTConverter.TaskResult p_229444_2_, Path p_229444_3_) {
      if (p_229444_2_.snbtPayload != null) {
         Path path = dumpSnbtTo.resolve(p_229444_2_.name + ".snbt");

         try {
            FileUtils.write(path.toFile(), p_229444_2_.snbtPayload, StandardCharsets.UTF_8);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't write structure SNBT {} at {}", p_229444_2_.name, path, ioexception);
         }
      }

      Path path1 = p_229444_3_.resolve(p_229444_2_.name + ".nbt");

      try {
         if (!Objects.equals(p_229444_1_.getHash(path1), p_229444_2_.hash) || !Files.exists(path1)) {
            Files.createDirectories(path1.getParent());

            try (OutputStream outputstream = Files.newOutputStream(path1)) {
               outputstream.write(p_229444_2_.payload);
            }
         }

         p_229444_1_.putNew(path1, p_229444_2_.hash);
      } catch (IOException ioexception1) {
         LOGGER.error("Couldn't write structure {} at {}", p_229444_2_.name, path1, ioexception1);
      }

   }

   @FunctionalInterface
   public interface ITransformer {
      CompoundNBT apply(String p_225371_1_, CompoundNBT p_225371_2_);
   }

   static class TaskResult {
      private final String name;
      private final byte[] payload;
      @Nullable
      private final String snbtPayload;
      private final String hash;

      public TaskResult(String p_i232551_1_, byte[] p_i232551_2_, @Nullable String p_i232551_3_, String p_i232551_4_) {
         this.name = p_i232551_1_;
         this.payload = p_i232551_2_;
         this.snbtPayload = p_i232551_3_;
         this.hash = p_i232551_4_;
      }
   }
}
