package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Template> structureRepository = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private IResourceManager resourceManager;
   private final Path generatedDir;

   public TemplateManager(IResourceManager p_i232119_1_, SaveFormat.LevelSave p_i232119_2_, DataFixer p_i232119_3_) {
      this.resourceManager = p_i232119_1_;
      this.fixerUpper = p_i232119_3_;
      this.generatedDir = p_i232119_2_.getLevelPath(FolderName.GENERATED_DIR).normalize();
   }

   public Template getOrCreate(ResourceLocation p_200220_1_) {
      Template template = this.get(p_200220_1_);
      if (template == null) {
         template = new Template();
         this.structureRepository.put(p_200220_1_, template);
      }

      return template;
   }

   @Nullable
   public Template get(ResourceLocation p_200219_1_) {
      return this.structureRepository.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
         Template template = this.loadFromGenerated(p_209204_1_);
         return template != null ? template : this.loadFromResource(p_209204_1_);
      });
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.resourceManager = p_195410_1_;
      this.structureRepository.clear();
   }

   @Nullable
   private Template loadFromResource(ResourceLocation p_209201_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

      try (IResource iresource = this.resourceManager.getResource(resourcelocation)) {
         return this.readStructure(iresource.getInputStream());
      } catch (FileNotFoundException filenotfoundexception) {
         return null;
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, throwable.toString());
         return null;
      }
   }

   @Nullable
   private Template loadFromGenerated(ResourceLocation p_195428_1_) {
      if (!this.generatedDir.toFile().isDirectory()) {
         return null;
      } else {
         Path path = this.createAndValidatePathToStructure(p_195428_1_, ".nbt");

         try (InputStream inputstream = new FileInputStream(path.toFile())) {
            return this.readStructure(inputstream);
         } catch (FileNotFoundException filenotfoundexception) {
            return null;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't load structure from {}", path, ioexception);
            return null;
         }
      }
   }

   private Template readStructure(InputStream p_209205_1_) throws IOException {
      CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_209205_1_);
      return this.readStructure(compoundnbt);
   }

   public Template readStructure(CompoundNBT p_227458_1_) {
      if (!p_227458_1_.contains("DataVersion", 99)) {
         p_227458_1_.putInt("DataVersion", 500);
      }

      Template template = new Template();
      template.load(NBTUtil.update(this.fixerUpper, DefaultTypeReferences.STRUCTURE, p_227458_1_, p_227458_1_.getInt("DataVersion")));
      return template;
   }

   public boolean save(ResourceLocation p_195429_1_) {
      Template template = this.structureRepository.get(p_195429_1_);
      if (template == null) {
         return false;
      } else {
         Path path = this.createAndValidatePathToStructure(p_195429_1_, ".nbt");
         Path path1 = path.getParent();
         if (path1 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
            } catch (IOException ioexception) {
               LOGGER.error("Failed to create parent directory: {}", (Object)path1);
               return false;
            }

            CompoundNBT compoundnbt = template.save(new CompoundNBT());

            try (OutputStream outputstream = new FileOutputStream(path.toFile())) {
               CompressedStreamTools.writeCompressed(compoundnbt, outputstream);
               return true;
            } catch (Throwable throwable) {
               return false;
            }
         }
      }
   }

   public Path createPathToStructure(ResourceLocation p_209509_1_, String p_209509_2_) {
      try {
         Path path = this.generatedDir.resolve(p_209509_1_.getNamespace());
         Path path1 = path.resolve("structures");
         return FileUtil.createPathToResource(path1, p_209509_1_.getPath(), p_209509_2_);
      } catch (InvalidPathException invalidpathexception) {
         throw new ResourceLocationException("Invalid resource path: " + p_209509_1_, invalidpathexception);
      }
   }

   private Path createAndValidatePathToStructure(ResourceLocation p_209510_1_, String p_209510_2_) {
      if (p_209510_1_.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + p_209510_1_);
      } else {
         Path path = this.createPathToStructure(p_209510_1_, p_209510_2_);
         if (path.startsWith(this.generatedDir) && FileUtil.isPathNormalized(path) && FileUtil.isPathPortable(path)) {
            return path;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + path);
         }
      }
   }

   public void remove(ResourceLocation p_189941_1_) {
      this.structureRepository.remove(p_189941_1_);
   }
}
