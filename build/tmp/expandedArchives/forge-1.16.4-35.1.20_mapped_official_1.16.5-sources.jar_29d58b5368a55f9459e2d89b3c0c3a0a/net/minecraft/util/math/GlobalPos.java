package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public final class GlobalPos {
   public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create((p_239647_0_) -> {
      return p_239647_0_.group(World.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(p_239647_0_, GlobalPos::of);
   });
   private final RegistryKey<World> dimension;
   private final BlockPos pos;

   private GlobalPos(RegistryKey<World> p_i232508_1_, BlockPos p_i232508_2_) {
      this.dimension = p_i232508_1_;
      this.pos = p_i232508_2_;
   }

   public static GlobalPos of(RegistryKey<World> p_239648_0_, BlockPos p_239648_1_) {
      return new GlobalPos(p_239648_0_, p_239648_1_);
   }

   public RegistryKey<World> dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         GlobalPos globalpos = (GlobalPos)p_equals_1_;
         return Objects.equals(this.dimension, globalpos.dimension) && Objects.equals(this.pos, globalpos.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.dimension, this.pos);
   }

   public String toString() {
      return this.dimension.toString() + " " + this.pos;
   }
}
