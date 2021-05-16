package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class MarginedStructureStart<C extends IFeatureConfig> extends StructureStart<C> {
   public MarginedStructureStart(Structure<C> p_i225874_1_, int p_i225874_2_, int p_i225874_3_, MutableBoundingBox p_i225874_4_, int p_i225874_5_, long p_i225874_6_) {
      super(p_i225874_1_, p_i225874_2_, p_i225874_3_, p_i225874_4_, p_i225874_5_, p_i225874_6_);
   }

   protected void calculateBoundingBox() {
      super.calculateBoundingBox();
      int i = 12;
      this.boundingBox.x0 -= 12;
      this.boundingBox.y0 -= 12;
      this.boundingBox.z0 -= 12;
      this.boundingBox.x1 += 12;
      this.boundingBox.y1 += 12;
      this.boundingBox.z1 += 12;
   }
}
