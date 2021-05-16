package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT - 1 + 4 - 1) / 4;
   private final ModelRenderer flag;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;
   private ItemStack bannerStack = ItemStack.EMPTY;
   private ItemStack dyeStack = ItemStack.EMPTY;
   private ItemStack patternStack = ItemStack.EMPTY;
   private boolean displayPatterns;
   private boolean displaySpecialPattern;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex = 1;

   public LoomScreen(LoomContainer p_i51081_1_, PlayerInventory p_i51081_2_, ITextComponent p_i51081_3_) {
      super(p_i51081_1_, p_i51081_2_, p_i51081_3_);
      this.flag = BannerTileEntityRenderer.makeFlag();
      p_i51081_1_.registerUpdateListener(this::containerChanged);
      this.titleLabelY -= 2;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
   }

   protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
      this.renderBackground(p_230450_1_);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      Slot slot = this.menu.getBannerSlot();
      Slot slot1 = this.menu.getDyeSlot();
      Slot slot2 = this.menu.getPatternSlot();
      Slot slot3 = this.menu.getResultSlot();
      if (!slot.hasItem()) {
         this.blit(p_230450_1_, i + slot.x, j + slot.y, this.imageWidth, 0, 16, 16);
      }

      if (!slot1.hasItem()) {
         this.blit(p_230450_1_, i + slot1.x, j + slot1.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!slot2.hasItem()) {
         this.blit(p_230450_1_, i + slot2.x, j + slot2.y, this.imageWidth + 32, 0, 16, 16);
      }

      int k = (int)(41.0F * this.scrollOffs);
      this.blit(p_230450_1_, i + 119, j + 13 + k, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      RenderHelper.setupForFlatItems();
      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
         p_230450_1_.pushPose();
         p_230450_1_.translate((double)(i + 139), (double)(j + 52), 0.0D);
         p_230450_1_.scale(24.0F, -24.0F, 1.0F);
         p_230450_1_.translate(0.5D, 0.5D, 0.5D);
         float f = 0.6666667F;
         p_230450_1_.scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.flag.xRot = 0.0F;
         this.flag.y = -32.0F;
         BannerTileEntityRenderer.renderPatterns(p_230450_1_, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
         p_230450_1_.popPose();
         irendertypebuffer$impl.endBatch();
      } else if (this.hasMaxPatterns) {
         this.blit(p_230450_1_, i + slot3.x - 2, j + slot3.y - 2, this.imageWidth, 17, 17, 16);
      }

      if (this.displayPatterns) {
         int j2 = i + 60;
         int l2 = j + 13;
         int l = this.startIndex + 16;

         for(int i1 = this.startIndex; i1 < l && i1 < BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT; ++i1) {
            int j1 = i1 - this.startIndex;
            int k1 = j2 + j1 % 4 * 14;
            int l1 = l2 + j1 / 4 * 14;
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            int i2 = this.imageHeight;
            if (i1 == this.menu.getSelectedBannerPatternIndex()) {
               i2 += 14;
            } else if (p_230450_3_ >= k1 && p_230450_4_ >= l1 && p_230450_3_ < k1 + 14 && p_230450_4_ < l1 + 14) {
               i2 += 28;
            }

            this.blit(p_230450_1_, k1, l1, 0, i2, 14, 14);
            this.renderPattern(i1, k1, l1);
         }
      } else if (this.displaySpecialPattern) {
         int k2 = i + 60;
         int i3 = j + 13;
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         this.blit(p_230450_1_, k2, i3, 0, this.imageHeight, 14, 14);
         int j3 = this.menu.getSelectedBannerPatternIndex();
         this.renderPattern(j3, k2, i3);
      }

      RenderHelper.setupFor3DItems();
   }

   private void renderPattern(int p_228190_1_, int p_228190_2_, int p_228190_3_) {
      ItemStack itemstack = new ItemStack(Items.GRAY_BANNER);
      CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("BlockEntityTag");
      ListNBT listnbt = (new BannerPattern.Builder()).addPattern(BannerPattern.BASE, DyeColor.GRAY).addPattern(BannerPattern.values()[p_228190_1_], DyeColor.WHITE).toListTag();
      compoundnbt.put("Patterns", listnbt);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.pushPose();
      matrixstack.translate((double)((float)p_228190_2_ + 0.5F), (double)(p_228190_3_ + 16), 0.0D);
      matrixstack.scale(6.0F, -6.0F, 1.0F);
      matrixstack.translate(0.5D, 0.5D, 0.0D);
      matrixstack.translate(0.5D, 0.5D, 0.5D);
      float f = 0.6666667F;
      matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.createPatterns(DyeColor.GRAY, BannerTileEntity.getItemPatterns(itemstack));
      BannerTileEntityRenderer.renderPatterns(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, list);
      matrixstack.popPose();
      irendertypebuffer$impl.endBatch();
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      this.scrolling = false;
      if (this.displayPatterns) {
         int i = this.leftPos + 60;
         int j = this.topPos + 13;
         int k = this.startIndex + 16;

         for(int l = this.startIndex; l < k; ++l) {
            int i1 = l - this.startIndex;
            double d0 = p_231044_1_ - (double)(i + i1 % 4 * 14);
            double d1 = p_231044_3_ - (double)(j + i1 / 4 * 14);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
               return true;
            }
         }

         i = this.leftPos + 119;
         j = this.topPos + 9;
         if (p_231044_1_ >= (double)i && p_231044_1_ < (double)(i + 12) && p_231044_3_ >= (double)j && p_231044_3_ < (double)(j + 56)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
   }

   public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      if (this.scrolling && this.displayPatterns) {
         int i = this.topPos + 13;
         int j = i + 56;
         this.scrollOffs = ((float)p_231045_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         int k = TOTAL_PATTERN_ROWS - 4;
         int l = (int)((double)(this.scrollOffs * (float)k) + 0.5D);
         if (l < 0) {
            l = 0;
         }

         this.startIndex = 1 + l * 4;
         return true;
      } else {
         return super.mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_);
      }
   }

   public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      if (this.displayPatterns) {
         int i = TOTAL_PATTERN_ROWS - 4;
         this.scrollOffs = (float)((double)this.scrollOffs - p_231043_5_ / (double)i);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = 1 + (int)((double)(this.scrollOffs * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack itemstack = this.menu.getResultSlot().getItem();
      if (itemstack.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = BannerTileEntity.createPatterns(((BannerItem)itemstack.getItem()).getColor(), BannerTileEntity.getItemPatterns(itemstack));
      }

      ItemStack itemstack1 = this.menu.getBannerSlot().getItem();
      ItemStack itemstack2 = this.menu.getDyeSlot().getItem();
      ItemStack itemstack3 = this.menu.getPatternSlot().getItem();
      CompoundNBT compoundnbt = itemstack1.getOrCreateTagElement("BlockEntityTag");
      this.hasMaxPatterns = compoundnbt.contains("Patterns", 9) && !itemstack1.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(itemstack1, this.bannerStack) || !ItemStack.matches(itemstack2, this.dyeStack) || !ItemStack.matches(itemstack3, this.patternStack)) {
         this.displayPatterns = !itemstack1.isEmpty() && !itemstack2.isEmpty() && itemstack3.isEmpty() && !this.hasMaxPatterns;
         this.displaySpecialPattern = !this.hasMaxPatterns && !itemstack3.isEmpty() && !itemstack1.isEmpty() && !itemstack2.isEmpty();
      }

      this.bannerStack = itemstack1.copy();
      this.dyeStack = itemstack2.copy();
      this.patternStack = itemstack3.copy();
   }
}
