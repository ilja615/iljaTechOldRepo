package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   public static Path generatedDir;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> clientObject;
   private static final Map<ResourcePackType, FileSystem> JAR_FILESYSTEM_BY_TYPE = Util.make(Maps.newHashMap(), (p_217809_0_) -> {
      synchronized(VanillaPack.class) {
         for(ResourcePackType resourcepacktype : ResourcePackType.values()) {
            URL url = VanillaPack.class.getResource("/" + resourcepacktype.getDirectory() + "/.mcassetsroot");

            try {
               URI uri = url.toURI();
               if ("jar".equals(uri.getScheme())) {
                  FileSystem filesystem;
                  try {
                     filesystem = FileSystems.getFileSystem(uri);
                  } catch (FileSystemNotFoundException filesystemnotfoundexception) {
                     filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                  }

                  p_217809_0_.put(resourcepacktype, filesystem);
               }
            } catch (IOException | URISyntaxException urisyntaxexception) {
               LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)urisyntaxexception);
            }
         }

      }
   });
   public final Set<String> namespaces;

   public VanillaPack(String... p_i47912_1_) {
      this.namespaces = ImmutableSet.copyOf(p_i47912_1_);
   }

   public InputStream getRootResource(String p_195763_1_) throws IOException {
      if (!p_195763_1_.contains("/") && !p_195763_1_.contains("\\")) {
         if (generatedDir != null) {
            Path path = generatedDir.resolve(p_195763_1_);
            if (Files.exists(path)) {
               return Files.newInputStream(path);
            }
         }

         return this.getResourceAsStream(p_195763_1_);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResource(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException {
      InputStream inputstream = this.getResourceAsStream(p_195761_1_, p_195761_2_);
      if (inputstream != null) {
         return inputstream;
      } else {
         throw new FileNotFoundException(p_195761_2_.getPath());
      }
   }

   public Collection<ResourceLocation> getResources(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      if (generatedDir != null) {
         try {
            getResources(set, p_225637_4_, p_225637_2_, generatedDir.resolve(p_225637_1_.getDirectory()), p_225637_3_, p_225637_5_);
         } catch (IOException ioexception1) {
         }

         if (p_225637_1_ == ResourcePackType.CLIENT_RESOURCES) {
            Enumeration<URL> enumeration = null;

            try {
               enumeration = clientObject.getClassLoader().getResources(p_225637_1_.getDirectory() + "/");
            } catch (IOException ioexception) {
            }

            while(enumeration != null && enumeration.hasMoreElements()) {
               try {
                  URI uri = enumeration.nextElement().toURI();
                  if ("file".equals(uri.getScheme())) {
                     getResources(set, p_225637_4_, p_225637_2_, Paths.get(uri), p_225637_3_, p_225637_5_);
                  }
               } catch (IOException | URISyntaxException urisyntaxexception1) {
               }
            }
         }
      }

      try {
         URL url1 = VanillaPack.class.getResource("/" + p_225637_1_.getDirectory() + "/.mcassetsroot");
         if (url1 == null) {
            LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return set;
         }

         URI uri1 = url1.toURI();
         if ("file".equals(uri1.getScheme())) {
            URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()));
            Path path = Paths.get(url.toURI());
            getResources(set, p_225637_4_, p_225637_2_, path, p_225637_3_, p_225637_5_);
         } else if ("jar".equals(uri1.getScheme())) {
            Path path1 = JAR_FILESYSTEM_BY_TYPE.get(p_225637_1_).getPath("/" + p_225637_1_.getDirectory());
            getResources(set, p_225637_4_, "minecraft", path1, p_225637_3_, p_225637_5_);
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uri1);
         }
      } catch (NoSuchFileException | FileNotFoundException filenotfoundexception) {
      } catch (IOException | URISyntaxException urisyntaxexception) {
         LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)urisyntaxexception);
      }

      return set;
   }

   private static void getResources(Collection<ResourceLocation> p_229867_0_, int p_229867_1_, String p_229867_2_, Path p_229867_3_, String p_229867_4_, Predicate<String> p_229867_5_) throws IOException {
      Path path = p_229867_3_.resolve(p_229867_2_);

      try (Stream<Path> stream = Files.walk(path.resolve(p_229867_4_), p_229867_1_)) {
         stream.filter((p_229868_1_) -> {
            return !p_229868_1_.endsWith(".mcmeta") && Files.isRegularFile(p_229868_1_) && p_229867_5_.test(p_229868_1_.getFileName().toString());
         }).map((p_229866_2_) -> {
            return new ResourceLocation(p_229867_2_, path.relativize(p_229866_2_).toString().replaceAll("\\\\", "/"));
         }).forEach(p_229867_0_::add);
      }

   }

   @Nullable
   protected InputStream getResourceAsStream(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      String s = createPath(p_195782_1_, p_195782_2_);
      if (generatedDir != null) {
         Path path = generatedDir.resolve(p_195782_1_.getDirectory() + "/" + p_195782_2_.getNamespace() + "/" + p_195782_2_.getPath());
         if (Files.exists(path)) {
            try {
               return Files.newInputStream(path);
            } catch (IOException ioexception1) {
            }
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return isResourceUrlValid(s, url) ? getExtraInputStream(p_195782_1_, s) : null;
      } catch (IOException ioexception) {
         return VanillaPack.class.getResourceAsStream(s);
      }
   }

   private static String createPath(ResourcePackType p_223458_0_, ResourceLocation p_223458_1_) {
      return "/" + p_223458_0_.getDirectory() + "/" + p_223458_1_.getNamespace() + "/" + p_223458_1_.getPath();
   }

   private static boolean isResourceUrlValid(String p_223459_0_, @Nullable URL p_223459_1_) throws IOException {
      return p_223459_1_ != null && (p_223459_1_.getProtocol().equals("jar") || FolderPack.validatePath(new File(p_223459_1_.getFile()), p_223459_0_));
   }

   @Nullable
   protected InputStream getResourceAsStream(String p_200010_1_) {
      return getExtraInputStream(ResourcePackType.SERVER_DATA, "/" + p_200010_1_);
   }

   public boolean hasResource(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      String s = createPath(p_195764_1_, p_195764_2_);
      if (generatedDir != null) {
         Path path = generatedDir.resolve(p_195764_1_.getDirectory() + "/" + p_195764_2_.getNamespace() + "/" + p_195764_2_.getPath());
         if (Files.exists(path)) {
            return true;
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return isResourceUrlValid(s, url);
      } catch (IOException ioexception) {
         return false;
      }
   }

   public Set<String> getNamespaces(ResourcePackType p_195759_1_) {
      return this.namespaces;
   }

   @Nullable
   public <T> T getMetadataSection(IMetadataSectionSerializer<T> p_195760_1_) throws IOException {
      try (InputStream inputstream = this.getRootResource("pack.mcmeta")) {
         return ResourcePack.getMetadataFromStream(p_195760_1_, inputstream);
      } catch (FileNotFoundException | RuntimeException runtimeexception) {
         return (T)null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }

   //Vanilla used to just grab from the classpath, this breaks dev environments, and Forge runtime
   //as forge ships vanilla assets in an 'extra' jar with no classes.
   //So find that extra jar using the .mcassetsroot marker.
   private InputStream getExtraInputStream(ResourcePackType type, String resource) {
      try {
         FileSystem fs = JAR_FILESYSTEM_BY_TYPE.get(type);
         if (fs != null)
            return Files.newInputStream(fs.getPath(resource));
         return VanillaPack.class.getResourceAsStream(resource);
      } catch (IOException e) {
         return VanillaPack.class.getResourceAsStream(resource);
      }
   }
}
