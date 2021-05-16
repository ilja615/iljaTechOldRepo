package net.minecraft.world.gen.layer;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(IAreaFactory<LazyArea> p_i48639_1_) {
      this.area = p_i48639_1_.make();
   }

   public Biome get(Registry<Biome> p_242936_1_, int p_242936_2_, int p_242936_3_) {
      int i = this.area.get(p_242936_2_, p_242936_3_);
      RegistryKey<Biome> registrykey = BiomeRegistry.byId(i);
      if (registrykey == null) {
         throw new IllegalStateException("Unknown biome id emitted by layers: " + i);
      } else {
         Biome biome = p_242936_1_.get(registrykey);
         if (biome == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Unknown biome id: " + i));
            } else {
               LOGGER.warn("Unknown biome id: ", (int)i);
               return p_242936_1_.get(BiomeRegistry.byId(0));
            }
         } else {
            return biome;
         }
      }
   }
}
