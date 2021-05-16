package net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemPickupParticle extends Particle {
   private final RenderTypeBuffers renderBuffers;
   private final Entity itemEntity;
   private final Entity target;
   private int life;
   private final EntityRendererManager entityRenderDispatcher;

   public ItemPickupParticle(EntityRendererManager p_i232400_1_, RenderTypeBuffers p_i232400_2_, ClientWorld p_i232400_3_, Entity p_i232400_4_, Entity p_i232400_5_) {
      this(p_i232400_1_, p_i232400_2_, p_i232400_3_, p_i232400_4_, p_i232400_5_, p_i232400_4_.getDeltaMovement());
   }

   private ItemPickupParticle(EntityRendererManager p_i232401_1_, RenderTypeBuffers p_i232401_2_, ClientWorld p_i232401_3_, Entity p_i232401_4_, Entity p_i232401_5_, Vector3d p_i232401_6_) {
      super(p_i232401_3_, p_i232401_4_.getX(), p_i232401_4_.getY(), p_i232401_4_.getZ(), p_i232401_6_.x, p_i232401_6_.y, p_i232401_6_.z);
      this.renderBuffers = p_i232401_2_;
      this.itemEntity = this.getSafeCopy(p_i232401_4_);
      this.target = p_i232401_5_;
      this.entityRenderDispatcher = p_i232401_1_;
   }

   private Entity getSafeCopy(Entity p_239181_1_) {
      return (Entity)(!(p_239181_1_ instanceof ItemEntity) ? p_239181_1_ : ((ItemEntity)p_239181_1_).copy());
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   public void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      float f = ((float)this.life + p_225606_3_) / 3.0F;
      f = f * f;
      double d0 = MathHelper.lerp((double)p_225606_3_, this.target.xOld, this.target.getX());
      double d1 = MathHelper.lerp((double)p_225606_3_, this.target.yOld, this.target.getY()) + 0.5D;
      double d2 = MathHelper.lerp((double)p_225606_3_, this.target.zOld, this.target.getZ());
      double d3 = MathHelper.lerp((double)f, this.itemEntity.getX(), d0);
      double d4 = MathHelper.lerp((double)f, this.itemEntity.getY(), d1);
      double d5 = MathHelper.lerp((double)f, this.itemEntity.getZ(), d2);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderBuffers.bufferSource();
      Vector3d vector3d = p_225606_2_.getPosition();
      this.entityRenderDispatcher.render(this.itemEntity, d3 - vector3d.x(), d4 - vector3d.y(), d5 - vector3d.z(), this.itemEntity.yRot, p_225606_3_, new MatrixStack(), irendertypebuffer$impl, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, p_225606_3_));
      irendertypebuffer$impl.endBatch();
   }

   public void tick() {
      ++this.life;
      if (this.life == 3) {
         this.remove();
      }

   }
}
