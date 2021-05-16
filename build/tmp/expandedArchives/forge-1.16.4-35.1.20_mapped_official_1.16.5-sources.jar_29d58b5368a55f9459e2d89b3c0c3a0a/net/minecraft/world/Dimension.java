package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

public final class Dimension {
   public static final Codec<Dimension> CODEC = RecordCodecBuilder.create((p_236061_0_) -> {
      return p_236061_0_.group(DimensionType.CODEC.fieldOf("type").forGetter(Dimension::typeSupplier), ChunkGenerator.CODEC.fieldOf("generator").forGetter(Dimension::generator)).apply(p_236061_0_, p_236061_0_.stable(Dimension::new));
   });
   public static final RegistryKey<Dimension> OVERWORLD = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("overworld"));
   public static final RegistryKey<Dimension> NETHER = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_nether"));
   public static final RegistryKey<Dimension> END = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_end"));
   private static final LinkedHashSet<RegistryKey<Dimension>> BUILTIN_ORDER = Sets.newLinkedHashSet(ImmutableList.of(OVERWORLD, NETHER, END));
   private final Supplier<DimensionType> type;
   private final ChunkGenerator generator;

   public Dimension(Supplier<DimensionType> p_i231900_1_, ChunkGenerator p_i231900_2_) {
      this.type = p_i231900_1_;
      this.generator = p_i231900_2_;
   }

   public Supplier<DimensionType> typeSupplier() {
      return this.type;
   }

   public DimensionType type() {
      return this.type.get();
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public static SimpleRegistry<Dimension> sortMap(SimpleRegistry<Dimension> p_236062_0_) {
      SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());

      for(RegistryKey<Dimension> registrykey : BUILTIN_ORDER) {
         Dimension dimension = p_236062_0_.get(registrykey);
         if (dimension != null) {
            simpleregistry.register(registrykey, dimension, p_236062_0_.lifecycle(dimension));
         }
      }

      for(Entry<RegistryKey<Dimension>, Dimension> entry : p_236062_0_.entrySet()) {
         RegistryKey<Dimension> registrykey1 = entry.getKey();
         if (!BUILTIN_ORDER.contains(registrykey1)) {
            simpleregistry.register(registrykey1, entry.getValue(), p_236062_0_.lifecycle(entry.getValue()));
         }
      }

      return simpleregistry;
   }

   public static boolean stable(long p_236060_0_, SimpleRegistry<Dimension> p_236060_2_) {
      List<Entry<RegistryKey<Dimension>, Dimension>> list = Lists.newArrayList(p_236060_2_.entrySet());
      if (list.size() != BUILTIN_ORDER.size()) {
         return false;
      } else {
         Entry<RegistryKey<Dimension>, Dimension> entry = list.get(0);
         Entry<RegistryKey<Dimension>, Dimension> entry1 = list.get(1);
         Entry<RegistryKey<Dimension>, Dimension> entry2 = list.get(2);
         if (entry.getKey() == OVERWORLD && entry1.getKey() == NETHER && entry2.getKey() == END) {
            if (!entry.getValue().type().equalTo(DimensionType.DEFAULT_OVERWORLD) && entry.getValue().type() != DimensionType.DEFAULT_OVERWORLD_CAVES) {
               return false;
            } else if (!entry1.getValue().type().equalTo(DimensionType.DEFAULT_NETHER)) {
               return false;
            } else if (!entry2.getValue().type().equalTo(DimensionType.DEFAULT_END)) {
               return false;
            } else if (entry1.getValue().generator() instanceof NoiseChunkGenerator && entry2.getValue().generator() instanceof NoiseChunkGenerator) {
               NoiseChunkGenerator noisechunkgenerator = (NoiseChunkGenerator)entry1.getValue().generator();
               NoiseChunkGenerator noisechunkgenerator1 = (NoiseChunkGenerator)entry2.getValue().generator();
               if (!noisechunkgenerator.stable(p_236060_0_, DimensionSettings.NETHER)) {
                  return false;
               } else if (!noisechunkgenerator1.stable(p_236060_0_, DimensionSettings.END)) {
                  return false;
               } else if (!(noisechunkgenerator.getBiomeSource() instanceof NetherBiomeProvider)) {
                  return false;
               } else {
                  NetherBiomeProvider netherbiomeprovider = (NetherBiomeProvider)noisechunkgenerator.getBiomeSource();
                  if (!netherbiomeprovider.stable(p_236060_0_)) {
                     return false;
                  } else if (!(noisechunkgenerator1.getBiomeSource() instanceof EndBiomeProvider)) {
                     return false;
                  } else {
                     EndBiomeProvider endbiomeprovider = (EndBiomeProvider)noisechunkgenerator1.getBiomeSource();
                     return endbiomeprovider.stable(p_236060_0_);
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
