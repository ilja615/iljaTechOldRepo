package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayConfig implements IFeatureConfig {
   public static final Codec<EndGatewayConfig> CODEC = RecordCodecBuilder.create((p_236524_0_) -> {
      return p_236524_0_.group(BlockPos.CODEC.optionalFieldOf("exit").forGetter((p_236525_0_) -> {
         return p_236525_0_.exit;
      }), Codec.BOOL.fieldOf("exact").forGetter((p_236523_0_) -> {
         return p_236523_0_.exact;
      })).apply(p_236524_0_, EndGatewayConfig::new);
   });
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfig(Optional<BlockPos> p_i49882_1_, boolean p_i49882_2_) {
      this.exit = p_i49882_1_;
      this.exact = p_i49882_2_;
   }

   public static EndGatewayConfig knownExit(BlockPos p_214702_0_, boolean p_214702_1_) {
      return new EndGatewayConfig(Optional.of(p_214702_0_), p_214702_1_);
   }

   public static EndGatewayConfig delayedExitSearch() {
      return new EndGatewayConfig(Optional.empty(), false);
   }

   public Optional<BlockPos> getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }
}
