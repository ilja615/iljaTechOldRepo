package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.structure.RuinedPortalStructure;

public class RuinedPortalFeature implements IFeatureConfig {
   public static final Codec<RuinedPortalFeature> CODEC = RuinedPortalStructure.Location.CODEC.fieldOf("portal_type").xmap(RuinedPortalFeature::new, (p_236629_0_) -> {
      return p_236629_0_.portalType;
   }).codec();
   public final RuinedPortalStructure.Location portalType;

   public RuinedPortalFeature(RuinedPortalStructure.Location p_i232016_1_) {
      this.portalType = p_i232016_1_;
   }
}
