package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.registry.DynamicRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PropertyManager<T extends PropertyManager<T>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties properties;

   public PropertyManager(Properties p_i50717_1_) {
      this.properties = p_i50717_1_;
   }

   public static Properties loadFromFile(Path p_218969_0_) {
      Properties properties = new Properties();

      try (InputStream inputstream = Files.newInputStream(p_218969_0_)) {
         properties.load(inputstream);
      } catch (IOException ioexception) {
         LOGGER.error("Failed to load properties from file: " + p_218969_0_);
      }

      return properties;
   }

   public void store(Path p_218970_1_) {
      try (OutputStream outputstream = Files.newOutputStream(p_218970_1_)) {
         net.minecraftforge.common.util.SortedProperties.store(properties, outputstream, "Minecraft server properties");
      } catch (IOException ioexception) {
         LOGGER.error("Failed to store properties to file: " + p_218970_1_);
      }

   }

   private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> p_218963_0_) {
      return (p_218975_1_) -> {
         try {
            return p_218963_0_.apply(p_218975_1_);
         } catch (NumberFormatException numberformatexception) {
            return (V)null;
         }
      };
   }

   protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> p_218964_0_, Function<String, V> p_218964_1_) {
      return (p_218971_2_) -> {
         try {
            return p_218964_0_.apply(Integer.parseInt(p_218971_2_));
         } catch (NumberFormatException numberformatexception) {
            return p_218964_1_.apply(p_218971_2_);
         }
      };
   }

   @Nullable
   private String getStringRaw(String p_218976_1_) {
      return (String)this.properties.get(p_218976_1_);
   }

   @Nullable
   protected <V> V getLegacy(String p_218984_1_, Function<String, V> p_218984_2_) {
      String s = this.getStringRaw(p_218984_1_);
      if (s == null) {
         return (V)null;
      } else {
         this.properties.remove(p_218984_1_);
         return p_218984_2_.apply(s);
      }
   }

   protected <V> V get(String p_218983_1_, Function<String, V> p_218983_2_, Function<V, String> p_218983_3_, V p_218983_4_) {
      String s = this.getStringRaw(p_218983_1_);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_218983_2_.apply(s) : null), p_218983_4_);
      this.properties.put(p_218983_1_, p_218983_3_.apply(v));
      return v;
   }

   protected <V> PropertyManager<T>.Property<V> getMutable(String p_218981_1_, Function<String, V> p_218981_2_, Function<V, String> p_218981_3_, V p_218981_4_) {
      String s = this.getStringRaw(p_218981_1_);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_218981_2_.apply(s) : null), p_218981_4_);
      this.properties.put(p_218981_1_, p_218981_3_.apply(v));
      return new PropertyManager.Property(p_218981_1_, v, p_218981_3_);
   }

   protected <V> V get(String p_218977_1_, Function<String, V> p_218977_2_, UnaryOperator<V> p_218977_3_, Function<V, String> p_218977_4_, V p_218977_5_) {
      return this.get(p_218977_1_, (p_218972_2_) -> {
         V v = p_218977_2_.apply(p_218972_2_);
         return (V)(v != null ? p_218977_3_.apply(v) : null);
      }, p_218977_4_, p_218977_5_);
   }

   protected <V> V get(String p_218979_1_, Function<String, V> p_218979_2_, V p_218979_3_) {
      return this.get(p_218979_1_, p_218979_2_, Objects::toString, p_218979_3_);
   }

   protected <V> PropertyManager<T>.Property<V> getMutable(String p_218965_1_, Function<String, V> p_218965_2_, V p_218965_3_) {
      return this.getMutable(p_218965_1_, p_218965_2_, Objects::toString, p_218965_3_);
   }

   protected String get(String p_218973_1_, String p_218973_2_) {
      return this.get(p_218973_1_, Function.identity(), Function.identity(), p_218973_2_);
   }

   @Nullable
   protected String getLegacyString(String p_218980_1_) {
      return this.getLegacy(p_218980_1_, Function.identity());
   }

   protected int get(String p_218968_1_, int p_218968_2_) {
      return this.get(p_218968_1_, wrapNumberDeserializer(Integer::parseInt), p_218968_2_);
   }

   protected PropertyManager<T>.Property<Integer> getMutable(String p_218974_1_, int p_218974_2_) {
      return this.getMutable(p_218974_1_, wrapNumberDeserializer(Integer::parseInt), p_218974_2_);
   }

   protected int get(String p_218962_1_, UnaryOperator<Integer> p_218962_2_, int p_218962_3_) {
      return this.get(p_218962_1_, wrapNumberDeserializer(Integer::parseInt), p_218962_2_, Objects::toString, p_218962_3_);
   }

   protected long get(String p_218967_1_, long p_218967_2_) {
      return this.get(p_218967_1_, wrapNumberDeserializer(Long::parseLong), p_218967_2_);
   }

   protected boolean get(String p_218982_1_, boolean p_218982_2_) {
      return this.get(p_218982_1_, Boolean::valueOf, p_218982_2_);
   }

   protected PropertyManager<T>.Property<Boolean> getMutable(String p_218961_1_, boolean p_218961_2_) {
      return this.getMutable(p_218961_1_, Boolean::valueOf, p_218961_2_);
   }

   @Nullable
   protected Boolean getLegacyBoolean(String p_218978_1_) {
      return this.getLegacy(p_218978_1_, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties properties = new Properties();
      properties.putAll(this.properties);
      return properties;
   }

   protected abstract T reload(DynamicRegistries p_241881_1_, Properties p_241881_2_);

   public class Property<V> implements Supplier<V> {
      private final String key;
      private final V value;
      private final Function<V, String> serializer;

      private Property(String p_i50880_2_, V p_i50880_3_, Function<V, String> p_i50880_4_) {
         this.key = p_i50880_2_;
         this.value = p_i50880_3_;
         this.serializer = p_i50880_4_;
      }

      public V get() {
         return this.value;
      }

      public T update(DynamicRegistries p_244381_1_, V p_244381_2_) {
         Properties properties = PropertyManager.this.cloneProperties();
         properties.put(this.key, this.serializer.apply(p_244381_2_));
         return PropertyManager.this.reload(p_244381_1_, properties);
      }
   }
}
