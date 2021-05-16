package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

public class BlockMaterialMatcher implements Predicate<BlockState> {
   private static final BlockMaterialMatcher AIR = new BlockMaterialMatcher(Material.AIR) {
      public boolean test(@Nullable BlockState p_test_1_) {
         return p_test_1_ != null && p_test_1_.isAir();
      }
   };
   private final Material material;

   private BlockMaterialMatcher(Material p_i47150_1_) {
      this.material = p_i47150_1_;
   }

   public static BlockMaterialMatcher forMaterial(Material p_189886_0_) {
      return p_189886_0_ == Material.AIR ? AIR : new BlockMaterialMatcher(p_189886_0_);
   }

   public boolean test(@Nullable BlockState p_test_1_) {
      return p_test_1_ != null && p_test_1_.getMaterial() == this.material;
   }
}
