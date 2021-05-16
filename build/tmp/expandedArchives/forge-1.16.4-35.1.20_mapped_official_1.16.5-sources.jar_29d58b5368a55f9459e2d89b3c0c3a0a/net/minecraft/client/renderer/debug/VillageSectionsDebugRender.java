package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillageSectionsDebugRender implements DebugRenderer.IDebugRenderer {
   private final Set<SectionPos> villageSections = Sets.newHashSet();

   VillageSectionsDebugRender() {
   }

   public void clear() {
      this.villageSections.clear();
   }

   public void setVillageSection(SectionPos p_239378_1_) {
      this.villageSections.add(p_239378_1_);
   }

   public void setNotVillageSection(SectionPos p_239379_1_) {
      this.villageSections.remove(p_239379_1_);
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.doRender(p_225619_3_, p_225619_5_, p_225619_7_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
   }

   private void doRender(double p_239376_1_, double p_239376_3_, double p_239376_5_) {
      BlockPos blockpos = new BlockPos(p_239376_1_, p_239376_3_, p_239376_5_);
      this.villageSections.forEach((p_239377_1_) -> {
         if (blockpos.closerThan(p_239377_1_.center(), 60.0D)) {
            highlightVillageSection(p_239377_1_);
         }

      });
   }

   private static void highlightVillageSection(SectionPos p_239380_0_) {
      float f = 1.0F;
      BlockPos blockpos = p_239380_0_.center();
      BlockPos blockpos1 = blockpos.offset(-1.0D, -1.0D, -1.0D);
      BlockPos blockpos2 = blockpos.offset(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderFilledBox(blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
   }
}
