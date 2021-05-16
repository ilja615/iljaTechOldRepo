package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> IGNORED = Sets.newHashSet(Items.AIR);
   public float blitOffset;
   private final ItemModelMesher itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
      this.textureManager = p_i46552_1_;
      this.itemModelShaper = new net.minecraftforge.client.ItemModelMesherForge(p_i46552_2_);

      for(Item item : Registry.ITEM) {
         if (!IGNORED.contains(item)) {
            this.itemModelShaper.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = p_i46552_3_;
   }

   public ItemModelMesher getItemModelShaper() {
      return this.itemModelShaper;
   }

   public void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, direction, random), p_229114_2_, p_229114_3_, p_229114_4_);
      }

      random.setSeed(42L);
      this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, (Direction)null, random), p_229114_2_, p_229114_3_, p_229114_4_);
   }

   public void render(ItemStack p_229111_1_, ItemCameraTransforms.TransformType p_229111_2_, boolean p_229111_3_, MatrixStack p_229111_4_, IRenderTypeBuffer p_229111_5_, int p_229111_6_, int p_229111_7_, IBakedModel p_229111_8_) {
      if (!p_229111_1_.isEmpty()) {
         p_229111_4_.pushPose();
         boolean flag = p_229111_2_ == ItemCameraTransforms.TransformType.GUI || p_229111_2_ == ItemCameraTransforms.TransformType.GROUND || p_229111_2_ == ItemCameraTransforms.TransformType.FIXED;
         if (p_229111_1_.getItem() == Items.TRIDENT && flag) {
            p_229111_8_ = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
         }

         p_229111_8_ = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(p_229111_4_, p_229111_8_, p_229111_2_, p_229111_3_);
         p_229111_4_.translate(-0.5D, -0.5D, -0.5D);
         if (!p_229111_8_.isCustomRenderer() && (p_229111_1_.getItem() != Items.TRIDENT || flag)) {
            boolean flag1;
            if (p_229111_2_ != ItemCameraTransforms.TransformType.GUI && !p_229111_2_.firstPerson() && p_229111_1_.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)p_229111_1_.getItem()).getBlock();
               flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               flag1 = true;
            }
            if (p_229111_8_.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(this, p_229111_8_, p_229111_1_, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_, flag1); }
            else {
            RenderType rendertype = RenderTypeLookup.getRenderType(p_229111_1_, flag1);
            IVertexBuilder ivertexbuilder;
            if (p_229111_1_.getItem() == Items.COMPASS && p_229111_1_.hasFoil()) {
               p_229111_4_.pushPose();
               MatrixStack.Entry matrixstack$entry = p_229111_4_.last();
               if (p_229111_2_ == ItemCameraTransforms.TransformType.GUI) {
                  matrixstack$entry.pose().multiply(0.5F);
               } else if (p_229111_2_.firstPerson()) {
                  matrixstack$entry.pose().multiply(0.75F);
               }

               if (flag1) {
                  ivertexbuilder = getCompassFoilBufferDirect(p_229111_5_, rendertype, matrixstack$entry);
               } else {
                  ivertexbuilder = getCompassFoilBuffer(p_229111_5_, rendertype, matrixstack$entry);
               }

               p_229111_4_.popPose();
            } else if (flag1) {
               ivertexbuilder = getFoilBufferDirect(p_229111_5_, rendertype, true, p_229111_1_.hasFoil());
            } else {
               ivertexbuilder = getFoilBuffer(p_229111_5_, rendertype, true, p_229111_1_.hasFoil());
            }

            this.renderModelLists(p_229111_8_, p_229111_1_, p_229111_6_, p_229111_7_, p_229111_4_, ivertexbuilder);
            }
         } else {
            p_229111_1_.getItem().getItemStackTileEntityRenderer().renderByItem(p_229111_1_, p_229111_2_, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_);
         }

         p_229111_4_.popPose();
      }
   }

   public static IVertexBuilder getArmorFoilBuffer(IRenderTypeBuffer p_239386_0_, RenderType p_239386_1_, boolean p_239386_2_, boolean p_239386_3_) {
      return p_239386_3_ ? VertexBuilderUtils.create(p_239386_0_.getBuffer(p_239386_2_ ? RenderType.armorGlint() : RenderType.armorEntityGlint()), p_239386_0_.getBuffer(p_239386_1_)) : p_239386_0_.getBuffer(p_239386_1_);
   }

   public static IVertexBuilder getCompassFoilBuffer(IRenderTypeBuffer p_241731_0_, RenderType p_241731_1_, MatrixStack.Entry p_241731_2_) {
      return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(p_241731_0_.getBuffer(RenderType.glint()), p_241731_2_.pose(), p_241731_2_.normal()), p_241731_0_.getBuffer(p_241731_1_));
   }

   public static IVertexBuilder getCompassFoilBufferDirect(IRenderTypeBuffer p_241732_0_, RenderType p_241732_1_, MatrixStack.Entry p_241732_2_) {
      return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(p_241732_0_.getBuffer(RenderType.glintDirect()), p_241732_2_.pose(), p_241732_2_.normal()), p_241732_0_.getBuffer(p_241732_1_));
   }

   public static IVertexBuilder getFoilBuffer(IRenderTypeBuffer p_229113_0_, RenderType p_229113_1_, boolean p_229113_2_, boolean p_229113_3_) {
      if (p_229113_3_) {
         return Minecraft.useShaderTransparency() && p_229113_1_ == Atlases.translucentItemSheet() ? VertexBuilderUtils.create(p_229113_0_.getBuffer(RenderType.glintTranslucent()), p_229113_0_.getBuffer(p_229113_1_)) : VertexBuilderUtils.create(p_229113_0_.getBuffer(p_229113_2_ ? RenderType.glint() : RenderType.entityGlint()), p_229113_0_.getBuffer(p_229113_1_));
      } else {
         return p_229113_0_.getBuffer(p_229113_1_);
      }
   }

   public static IVertexBuilder getFoilBufferDirect(IRenderTypeBuffer p_239391_0_, RenderType p_239391_1_, boolean p_239391_2_, boolean p_239391_3_) {
      return p_239391_3_ ? VertexBuilderUtils.create(p_239391_0_.getBuffer(p_239391_2_ ? RenderType.glintDirect() : RenderType.entityGlintDirect()), p_239391_0_.getBuffer(p_239391_1_)) : p_239391_0_.getBuffer(p_239391_1_);
   }

   public void renderQuadList(MatrixStack p_229112_1_, IVertexBuilder p_229112_2_, List<BakedQuad> p_229112_3_, ItemStack p_229112_4_, int p_229112_5_, int p_229112_6_) {
      boolean flag = !p_229112_4_.isEmpty();
      MatrixStack.Entry matrixstack$entry = p_229112_1_.last();

      for(BakedQuad bakedquad : p_229112_3_) {
         int i = -1;
         if (flag && bakedquad.isTinted()) {
            i = this.itemColors.getColor(p_229112_4_, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         p_229112_2_.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, p_229112_5_, p_229112_6_, true);
      }

   }

   public IBakedModel getModel(ItemStack p_184393_1_, @Nullable World p_184393_2_, @Nullable LivingEntity p_184393_3_) {
      Item item = p_184393_1_.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelShaper.getItemModel(p_184393_1_);
      }

      ClientWorld clientworld = p_184393_2_ instanceof ClientWorld ? (ClientWorld)p_184393_2_ : null;
      IBakedModel ibakedmodel1 = ibakedmodel.getOverrides().resolve(ibakedmodel, p_184393_1_, clientworld, p_184393_3_);
      return ibakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : ibakedmodel1;
   }

   public void renderStatic(ItemStack p_229110_1_, ItemCameraTransforms.TransformType p_229110_2_, int p_229110_3_, int p_229110_4_, MatrixStack p_229110_5_, IRenderTypeBuffer p_229110_6_) {
      this.renderStatic((LivingEntity)null, p_229110_1_, p_229110_2_, false, p_229110_5_, p_229110_6_, (World)null, p_229110_3_, p_229110_4_);
   }

   public void renderStatic(@Nullable LivingEntity p_229109_1_, ItemStack p_229109_2_, ItemCameraTransforms.TransformType p_229109_3_, boolean p_229109_4_, MatrixStack p_229109_5_, IRenderTypeBuffer p_229109_6_, @Nullable World p_229109_7_, int p_229109_8_, int p_229109_9_) {
      if (!p_229109_2_.isEmpty()) {
         IBakedModel ibakedmodel = this.getModel(p_229109_2_, p_229109_7_, p_229109_1_);
         this.render(p_229109_2_, p_229109_3_, p_229109_4_, p_229109_5_, p_229109_6_, p_229109_8_, p_229109_9_, ibakedmodel);
      }
   }

   public void renderGuiItem(ItemStack p_175042_1_, int p_175042_2_, int p_175042_3_) {
      this.renderGuiItem(p_175042_1_, p_175042_2_, p_175042_3_, this.getModel(p_175042_1_, (World)null, (LivingEntity)null));
   }

   protected void renderGuiItem(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_) {
      RenderSystem.pushMatrix();
      this.textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
      this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef((float)p_191962_2_, (float)p_191962_3_, 100.0F + this.blitOffset);
      RenderSystem.translatef(8.0F, 8.0F, 0.0F);
      RenderSystem.scalef(1.0F, -1.0F, 1.0F);
      RenderSystem.scalef(16.0F, 16.0F, 16.0F);
      MatrixStack matrixstack = new MatrixStack();
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flag = !p_191962_4_.usesBlockLight();
      if (flag) {
         RenderHelper.setupForFlatItems();
      }

      this.render(p_191962_1_, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, p_191962_4_);
      irendertypebuffer$impl.endBatch();
      RenderSystem.enableDepthTest();
      if (flag) {
         RenderHelper.setupFor3DItems();
      }

      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
   }

   public void renderAndDecorateItem(ItemStack p_180450_1_, int p_180450_2_, int p_180450_3_) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, p_180450_1_, p_180450_2_, p_180450_3_);
   }

   public void renderAndDecorateFakeItem(ItemStack p_239390_1_, int p_239390_2_, int p_239390_3_) {
      this.tryRenderGuiItem((LivingEntity)null, p_239390_1_, p_239390_2_, p_239390_3_);
   }

   public void renderAndDecorateItem(LivingEntity p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
      this.tryRenderGuiItem(p_184391_1_, p_184391_2_, p_184391_3_, p_184391_4_);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity p_239387_1_, ItemStack p_239387_2_, int p_239387_3_, int p_239387_4_) {
      if (!p_239387_2_.isEmpty()) {
         this.blitOffset += 50.0F;

         try {
            this.renderGuiItem(p_239387_2_, p_239387_3_, p_239387_4_, this.getModel(p_239387_2_, (World)null, p_239387_1_));
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> {
               return String.valueOf((Object)p_239387_2_.getItem());
            });
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(p_239387_2_.getItem().getRegistryName()));
            crashreportcategory.setDetail("Item Damage", () -> {
               return String.valueOf(p_239387_2_.getDamageValue());
            });
            crashreportcategory.setDetail("Item NBT", () -> {
               return String.valueOf((Object)p_239387_2_.getTag());
            });
            crashreportcategory.setDetail("Item Foil", () -> {
               return String.valueOf(p_239387_2_.hasFoil());
            });
            throw new ReportedException(crashreport);
         }

         this.blitOffset -= 50.0F;
      }
   }

   public void renderGuiItemDecorations(FontRenderer p_175030_1_, ItemStack p_175030_2_, int p_175030_3_, int p_175030_4_) {
      this.renderGuiItemDecorations(p_175030_1_, p_175030_2_, p_175030_3_, p_175030_4_, (String)null);
   }

   public void renderGuiItemDecorations(FontRenderer p_180453_1_, ItemStack p_180453_2_, int p_180453_3_, int p_180453_4_, @Nullable String p_180453_5_) {
      if (!p_180453_2_.isEmpty()) {
         MatrixStack matrixstack = new MatrixStack();
         if (p_180453_2_.getCount() != 1 || p_180453_5_ != null) {
            String s = p_180453_5_ == null ? String.valueOf(p_180453_2_.getCount()) : p_180453_5_;
            matrixstack.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
            p_180453_1_.drawInBatch(s, (float)(p_180453_3_ + 19 - 2 - p_180453_1_.width(s)), (float)(p_180453_4_ + 6 + 3), 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
         }

         if (p_180453_2_.getItem().showDurabilityBar(p_180453_2_)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            double health = p_180453_2_.getItem().getDurabilityForDisplay(p_180453_2_);
            int i = Math.round(13.0F - (float)health * 13.0F);
            int j = p_180453_2_.getItem().getRGBDurabilityForDisplay(p_180453_2_);
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
         float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(p_180453_2_.getItem(), Minecraft.getInstance().getFrameTime());
         if (f3 > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
            this.fillRect(bufferbuilder1, p_180453_3_, p_180453_4_ + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
      p_181565_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      Tessellator.getInstance().end();
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.itemModelShaper.rebuildCache();
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}
