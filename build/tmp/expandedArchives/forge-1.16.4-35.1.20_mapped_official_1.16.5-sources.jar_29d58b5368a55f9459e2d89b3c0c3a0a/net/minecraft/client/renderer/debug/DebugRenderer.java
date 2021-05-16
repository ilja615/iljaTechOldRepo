package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final PathfindingDebugRenderer pathfindingRenderer = new PathfindingDebugRenderer();
   public final DebugRenderer.IDebugRenderer waterDebugRenderer;
   public final DebugRenderer.IDebugRenderer chunkBorderRenderer;
   public final DebugRenderer.IDebugRenderer heightMapRenderer;
   public final DebugRenderer.IDebugRenderer collisionBoxRenderer;
   public final DebugRenderer.IDebugRenderer neighborsUpdateRenderer;
   public final CaveDebugRenderer caveRenderer;
   public final StructureDebugRenderer structureRenderer;
   public final DebugRenderer.IDebugRenderer lightDebugRenderer;
   public final DebugRenderer.IDebugRenderer worldGenAttemptRenderer;
   public final DebugRenderer.IDebugRenderer solidFaceRenderer;
   public final DebugRenderer.IDebugRenderer chunkRenderer;
   public final PointOfInterestDebugRenderer brainDebugRenderer;
   public final VillageSectionsDebugRender villageSectionsDebugRenderer;
   public final BeeDebugRenderer beeDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final EntityAIDebugRenderer goalSelectorRenderer;
   public final GameTestDebugRenderer gameTestDebugRenderer;
   private boolean renderChunkborder;

   public DebugRenderer(Minecraft p_i46557_1_) {
      this.waterDebugRenderer = new WaterDebugRenderer(p_i46557_1_);
      this.chunkBorderRenderer = new ChunkBorderDebugRenderer(p_i46557_1_);
      this.heightMapRenderer = new HeightMapDebugRenderer(p_i46557_1_);
      this.collisionBoxRenderer = new CollisionBoxDebugRenderer(p_i46557_1_);
      this.neighborsUpdateRenderer = new NeighborsUpdateDebugRenderer(p_i46557_1_);
      this.caveRenderer = new CaveDebugRenderer();
      this.structureRenderer = new StructureDebugRenderer(p_i46557_1_);
      this.lightDebugRenderer = new LightDebugRenderer(p_i46557_1_);
      this.worldGenAttemptRenderer = new WorldGenAttemptsDebugRenderer();
      this.solidFaceRenderer = new SolidFaceDebugRenderer(p_i46557_1_);
      this.chunkRenderer = new ChunkInfoDebugRenderer(p_i46557_1_);
      this.brainDebugRenderer = new PointOfInterestDebugRenderer(p_i46557_1_);
      this.villageSectionsDebugRenderer = new VillageSectionsDebugRender();
      this.beeDebugRenderer = new BeeDebugRenderer(p_i46557_1_);
      this.raidDebugRenderer = new RaidDebugRenderer(p_i46557_1_);
      this.goalSelectorRenderer = new EntityAIDebugRenderer(p_i46557_1_);
      this.gameTestDebugRenderer = new GameTestDebugRenderer();
   }

   public void clear() {
      this.pathfindingRenderer.clear();
      this.waterDebugRenderer.clear();
      this.chunkBorderRenderer.clear();
      this.heightMapRenderer.clear();
      this.collisionBoxRenderer.clear();
      this.neighborsUpdateRenderer.clear();
      this.caveRenderer.clear();
      this.structureRenderer.clear();
      this.lightDebugRenderer.clear();
      this.worldGenAttemptRenderer.clear();
      this.solidFaceRenderer.clear();
      this.chunkRenderer.clear();
      this.brainDebugRenderer.clear();
      this.villageSectionsDebugRenderer.clear();
      this.beeDebugRenderer.clear();
      this.raidDebugRenderer.clear();
      this.goalSelectorRenderer.clear();
      this.gameTestDebugRenderer.clear();
   }

   public boolean switchRenderChunkborder() {
      this.renderChunkborder = !this.renderChunkborder;
      return this.renderChunkborder;
   }

   public void render(MatrixStack p_229019_1_, IRenderTypeBuffer.Impl p_229019_2_, double p_229019_3_, double p_229019_5_, double p_229019_7_) {
      if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
         this.chunkBorderRenderer.render(p_229019_1_, p_229019_2_, p_229019_3_, p_229019_5_, p_229019_7_);
      }

      this.gameTestDebugRenderer.render(p_229019_1_, p_229019_2_, p_229019_3_, p_229019_5_, p_229019_7_);
   }

   public static Optional<Entity> getTargetedEntity(@Nullable Entity p_217728_0_, int p_217728_1_) {
      if (p_217728_0_ == null) {
         return Optional.empty();
      } else {
         Vector3d vector3d = p_217728_0_.getEyePosition(1.0F);
         Vector3d vector3d1 = p_217728_0_.getViewVector(1.0F).scale((double)p_217728_1_);
         Vector3d vector3d2 = vector3d.add(vector3d1);
         AxisAlignedBB axisalignedbb = p_217728_0_.getBoundingBox().expandTowards(vector3d1).inflate(1.0D);
         int i = p_217728_1_ * p_217728_1_;
         Predicate<Entity> predicate = (p_217727_0_) -> {
            return !p_217727_0_.isSpectator() && p_217727_0_.isPickable();
         };
         EntityRayTraceResult entityraytraceresult = ProjectileHelper.getEntityHitResult(p_217728_0_, vector3d, vector3d2, axisalignedbb, predicate, (double)i);
         if (entityraytraceresult == null) {
            return Optional.empty();
         } else {
            return vector3d.distanceToSqr(entityraytraceresult.getLocation()) > (double)i ? Optional.empty() : Optional.of(entityraytraceresult.getEntity());
         }
      }
   }

   public static void renderFilledBox(BlockPos p_217735_0_, BlockPos p_217735_1_, float p_217735_2_, float p_217735_3_, float p_217735_4_, float p_217735_5_) {
      ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (activerenderinfo.isInitialized()) {
         Vector3d vector3d = activerenderinfo.getPosition().reverse();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(p_217735_0_, p_217735_1_)).move(vector3d);
         renderFilledBox(axisalignedbb, p_217735_2_, p_217735_3_, p_217735_4_, p_217735_5_);
      }
   }

   public static void renderFilledBox(BlockPos p_217736_0_, float p_217736_1_, float p_217736_2_, float p_217736_3_, float p_217736_4_, float p_217736_5_) {
      ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (activerenderinfo.isInitialized()) {
         Vector3d vector3d = activerenderinfo.getPosition().reverse();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(p_217736_0_)).move(vector3d).inflate((double)p_217736_1_);
         renderFilledBox(axisalignedbb, p_217736_2_, p_217736_3_, p_217736_4_, p_217736_5_);
      }
   }

   public static void renderFilledBox(AxisAlignedBB p_217730_0_, float p_217730_1_, float p_217730_2_, float p_217730_3_, float p_217730_4_) {
      renderFilledBox(p_217730_0_.minX, p_217730_0_.minY, p_217730_0_.minZ, p_217730_0_.maxX, p_217730_0_.maxY, p_217730_0_.maxZ, p_217730_1_, p_217730_2_, p_217730_3_, p_217730_4_);
   }

   public static void renderFilledBox(double p_217733_0_, double p_217733_2_, double p_217733_4_, double p_217733_6_, double p_217733_8_, double p_217733_10_, float p_217733_12_, float p_217733_13_, float p_217733_14_, float p_217733_15_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
      WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, p_217733_0_, p_217733_2_, p_217733_4_, p_217733_6_, p_217733_8_, p_217733_10_, p_217733_12_, p_217733_13_, p_217733_14_, p_217733_15_);
      tessellator.end();
   }

   public static void renderFloatingText(String p_217731_0_, int p_217731_1_, int p_217731_2_, int p_217731_3_, int p_217731_4_) {
      renderFloatingText(p_217731_0_, (double)p_217731_1_ + 0.5D, (double)p_217731_2_ + 0.5D, (double)p_217731_3_ + 0.5D, p_217731_4_);
   }

   public static void renderFloatingText(String p_217732_0_, double p_217732_1_, double p_217732_3_, double p_217732_5_, int p_217732_7_) {
      renderFloatingText(p_217732_0_, p_217732_1_, p_217732_3_, p_217732_5_, p_217732_7_, 0.02F);
   }

   public static void renderFloatingText(String p_217729_0_, double p_217729_1_, double p_217729_3_, double p_217729_5_, int p_217729_7_, float p_217729_8_) {
      renderFloatingText(p_217729_0_, p_217729_1_, p_217729_3_, p_217729_5_, p_217729_7_, p_217729_8_, true, 0.0F, false);
   }

   public static void renderFloatingText(String p_217734_0_, double p_217734_1_, double p_217734_3_, double p_217734_5_, int p_217734_7_, float p_217734_8_, boolean p_217734_9_, float p_217734_10_, boolean p_217734_11_) {
      Minecraft minecraft = Minecraft.getInstance();
      ActiveRenderInfo activerenderinfo = minecraft.gameRenderer.getMainCamera();
      if (activerenderinfo.isInitialized() && minecraft.getEntityRenderDispatcher().options != null) {
         FontRenderer fontrenderer = minecraft.font;
         double d0 = activerenderinfo.getPosition().x;
         double d1 = activerenderinfo.getPosition().y;
         double d2 = activerenderinfo.getPosition().z;
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(p_217734_1_ - d0), (float)(p_217734_3_ - d1) + 0.07F, (float)(p_217734_5_ - d2));
         RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
         RenderSystem.multMatrix(new Matrix4f(activerenderinfo.rotation()));
         RenderSystem.scalef(p_217734_8_, -p_217734_8_, p_217734_8_);
         RenderSystem.enableTexture();
         if (p_217734_11_) {
            RenderSystem.disableDepthTest();
         } else {
            RenderSystem.enableDepthTest();
         }

         RenderSystem.depthMask(true);
         RenderSystem.scalef(-1.0F, 1.0F, 1.0F);
         float f = p_217734_9_ ? (float)(-fontrenderer.width(p_217734_0_)) / 2.0F : 0.0F;
         f = f - p_217734_10_ / p_217734_8_;
         RenderSystem.enableAlphaTest();
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
         fontrenderer.drawInBatch(p_217734_0_, f, 0.0F, p_217734_7_, false, TransformationMatrix.identity().getMatrix(), irendertypebuffer$impl, p_217734_11_, 0, 15728880);
         irendertypebuffer$impl.endBatch();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         RenderSystem.popMatrix();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IDebugRenderer {
      void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_);

      default void clear() {
      }
   }
}
