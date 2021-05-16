package net.minecraft.client.gui.widget.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractList<E extends AbstractList.AbstractListEntry<E>> extends FocusableGui implements IRenderable {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractList.SimpleArrayList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;

   public AbstractList(Minecraft p_i51146_1_, int p_i51146_2_, int p_i51146_3_, int p_i51146_4_, int p_i51146_5_, int p_i51146_6_) {
      this.minecraft = p_i51146_1_;
      this.width = p_i51146_2_;
      this.height = p_i51146_3_;
      this.y0 = p_i51146_4_;
      this.y1 = p_i51146_5_;
      this.itemHeight = p_i51146_6_;
      this.x0 = 0;
      this.x1 = p_i51146_2_;
   }

   public void setRenderSelection(boolean p_230943_1_) {
      this.renderSelection = p_230943_1_;
   }

   protected void setRenderHeader(boolean p_230944_1_, int p_230944_2_) {
      this.renderHeader = p_230944_1_;
      this.headerHeight = p_230944_2_;
      if (!p_230944_1_) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E p_241215_1_) {
      this.selected = p_241215_1_;
   }

   public void setRenderBackground(boolean p_244605_1_) {
      this.renderBackground = p_244605_1_;
   }

   public void setRenderTopAndBottom(boolean p_244606_1_) {
      this.renderTopAndBottom = p_244606_1_;
   }

   @Nullable
   public E getFocused() {
      return (E)(super.getFocused());
   }

   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<E> p_230942_1_) {
      this.children.clear();
      this.children.addAll(p_230942_1_);
   }

   protected E getEntry(int p_230953_1_) {
      return this.children().get(p_230953_1_);
   }

   protected int addEntry(E p_230513_1_) {
      this.children.add(p_230513_1_);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int p_230957_1_) {
      return Objects.equals(this.getSelected(), this.children().get(p_230957_1_));
   }

   @Nullable
   protected final E getEntryAtPosition(double p_230933_1_, double p_230933_3_) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int i1 = MathHelper.floor(p_230933_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int j1 = i1 / this.itemHeight;
      return (E)(p_230933_1_ < (double)this.getScrollbarPosition() && p_230933_1_ >= (double)k && p_230933_1_ <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
   }

   public void updateSize(int p_230940_1_, int p_230940_2_, int p_230940_3_, int p_230940_4_) {
      this.width = p_230940_1_;
      this.height = p_230940_2_;
      this.y0 = p_230940_3_;
      this.y1 = p_230940_4_;
      this.x0 = 0;
      this.x1 = p_230940_1_;
   }

   public void setLeftPos(int p_230959_1_) {
      this.x0 = p_230959_1_;
      this.x1 = p_230959_1_ + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int p_230938_1_, int p_230938_2_) {
   }

   protected void renderHeader(MatrixStack p_230448_1_, int p_230448_2_, int p_230448_3_, Tessellator p_230448_4_) {
   }

   protected void renderBackground(MatrixStack p_230433_1_) {
   }

   protected void renderDecorations(MatrixStack p_230447_1_, int p_230447_2_, int p_230447_3_) {
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      int i = this.getScrollbarPosition();
      int j = i + 6;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      if (this.renderBackground) {
         this.minecraft.getTextureManager().bind(AbstractGui.BACKGROUND_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float f = 32.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         tessellator.end();
      }

      int j1 = this.getRowLeft();
      int k = this.y0 + 4 - (int)this.getScrollAmount();
      if (this.renderHeader) {
         this.renderHeader(p_230430_1_, j1, k, tessellator);
      }

      this.renderList(p_230430_1_, j1, k, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.renderTopAndBottom) {
         this.minecraft.getTextureManager().bind(AbstractGui.BACKGROUND_LOCATION);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(519);
         float f1 = 32.0F;
         int l = -100;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)this.y0, -100.0D).uv(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y0, -100.0D).uv((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), 0.0D, -100.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.height, -100.0D).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.height, -100.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y1, -100.0D).uv((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, -100.0D).uv(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         tessellator.end();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         RenderSystem.disableAlphaTest();
         RenderSystem.shadeModel(7425);
         RenderSystem.disableTexture();
         int i1 = 4;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
         tessellator.end();
      }

      int k1 = this.getMaxScroll();
      if (k1 > 0) {
         RenderSystem.disableTexture();
         int l1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         l1 = MathHelper.clamp(l1, 32, this.y1 - this.y0 - 8);
         int i2 = (int)this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
         if (i2 < this.y0) {
            i2 = this.y0;
         }

         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)i, (double)this.y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)this.y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
         tessellator.end();
      }

      this.renderDecorations(p_230430_1_, p_230430_2_, p_230430_3_);
      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
      RenderSystem.enableAlphaTest();
      RenderSystem.disableBlend();
   }

   protected void centerScrollOn(E p_230951_1_) {
      this.setScrollAmount((double)(this.children().indexOf(p_230951_1_) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(E p_230954_1_) {
      int i = this.getRowTop(this.children().indexOf(p_230954_1_));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.y1 - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int p_230937_1_) {
      this.setScrollAmount(this.getScrollAmount() + (double)p_230937_1_);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double p_230932_1_) {
      this.scrollAmount = MathHelper.clamp(p_230932_1_, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   protected void updateScrollingState(double p_230947_1_, double p_230947_3_, int p_230947_5_) {
      this.scrolling = p_230947_5_ == 0 && p_230947_1_ >= (double)this.getScrollbarPosition() && p_230947_1_ < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      this.updateScrollingState(p_231044_1_, p_231044_3_, p_231044_5_);
      if (!this.isMouseOver(p_231044_1_, p_231044_3_)) {
         return false;
      } else {
         E e = this.getEntryAtPosition(p_231044_1_, p_231044_3_);
         if (e != null) {
            if (e.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
               this.setFocused(e);
               this.setDragging(true);
               return true;
            }
         } else if (p_231044_5_ == 0) {
            this.clickedHeader((int)(p_231044_1_ - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(p_231044_3_ - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
      }

      return false;
   }

   public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      if (super.mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_)) {
         return true;
      } else if (p_231045_5_ == 0 && this.scrolling) {
         if (p_231045_3_ < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if (p_231045_3_ > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double d0 = (double)Math.max(1, this.getMaxScroll());
            int i = this.y1 - this.y0;
            int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0D, d0 / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + p_231045_8_ * d1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      this.setScrollAmount(this.getScrollAmount() - p_231043_5_ * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (p_231046_1_ == 264) {
         this.moveSelection(AbstractList.Ordering.DOWN);
         return true;
      } else if (p_231046_1_ == 265) {
         this.moveSelection(AbstractList.Ordering.UP);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(AbstractList.Ordering p_241219_1_) {
      this.moveSelection(p_241219_1_, (p_241573_0_) -> {
         return true;
      });
   }

   protected void refreshSelection() {
      E e = this.getSelected();
      if (e != null) {
         this.setSelected(e);
         this.ensureVisible(e);
      }

   }

   protected void moveSelection(AbstractList.Ordering p_241572_1_, Predicate<E> p_241572_2_) {
      int i = p_241572_1_ == AbstractList.Ordering.UP ? -1 : 1;
      if (!this.children().isEmpty()) {
         int j = this.children().indexOf(this.getSelected());

         while(true) {
            int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
            if (j == k) {
               break;
            }

            E e = this.children().get(k);
            if (p_241572_2_.test(e)) {
               this.setSelected(e);
               this.ensureVisible(e);
               break;
            }

            j = k;
         }
      }

   }

   public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
      return p_231047_3_ >= (double)this.y0 && p_231047_3_ <= (double)this.y1 && p_231047_1_ >= (double)this.x0 && p_231047_1_ <= (double)this.x1;
   }

   protected void renderList(MatrixStack p_238478_1_, int p_238478_2_, int p_238478_3_, int p_238478_4_, int p_238478_5_, float p_238478_6_) {
      int i = this.getItemCount();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();

      for(int j = 0; j < i; ++j) {
         int k = this.getRowTop(j);
         int l = this.getRowBottom(j);
         if (l >= this.y0 && k <= this.y1) {
            int i1 = p_238478_3_ + j * this.itemHeight + this.headerHeight;
            int j1 = this.itemHeight - 4;
            E e = this.getEntry(j);
            int k1 = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
               int l1 = this.x0 + this.width / 2 - k1 / 2;
               int i2 = this.x0 + this.width / 2 + k1 / 2;
               RenderSystem.disableTexture();
               float f = this.isFocused() ? 1.0F : 0.5F;
               RenderSystem.color4f(f, f, f, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.vertex((double)l1, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 - 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)l1, (double)(i1 - 2), 0.0D).endVertex();
               tessellator.end();
               RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 - 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 - 1), 0.0D).endVertex();
               tessellator.end();
               RenderSystem.enableTexture();
            }

            int j2 = this.getRowLeft();
            e.render(p_238478_1_, j, k, j2, k1, j1, p_238478_4_, p_238478_5_, this.isMouseOver((double)p_238478_4_, (double)p_238478_5_) && Objects.equals(this.getEntryAtPosition((double)p_238478_4_, (double)p_238478_5_), e), p_238478_6_);
         }
      }

   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int p_230962_1_) {
      return this.y0 + 4 - (int)this.getScrollAmount() + p_230962_1_ * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int p_230948_1_) {
      return this.getRowTop(p_230948_1_) + this.itemHeight;
   }

   protected boolean isFocused() {
      return false;
   }

   protected E remove(int p_230964_1_) {
      E e = this.children.get(p_230964_1_);
      return (E)(this.removeEntry(this.children.get(p_230964_1_)) ? e : null);
   }

   protected boolean removeEntry(E p_230956_1_) {
      boolean flag = this.children.remove(p_230956_1_);
      if (flag && p_230956_1_ == this.getSelected()) {
         this.setSelected((E)null);
      }

      return flag;
   }

   private void bindEntryToSelf(AbstractList.AbstractListEntry<E> p_238480_1_) {
      p_238480_1_.list = this;
   }

   public int getWidth() { return this.width; }
   public int getHeight() { return this.height; }
   public int getTop() { return this.y0; }
   public int getBottom() { return this.y1; }
   public int getLeft() { return this.x0; }
   public int getRight() { return this.x1; }

   @OnlyIn(Dist.CLIENT)
   public abstract static class AbstractListEntry<E extends AbstractList.AbstractListEntry<E>> implements IGuiEventListener {
      @Deprecated
      protected AbstractList<E> list;

      public abstract void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_);

      public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
         return Objects.equals(this.list.getEntryAtPosition(p_231047_1_, p_231047_3_), this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Ordering {
      UP,
      DOWN;
   }

   @OnlyIn(Dist.CLIENT)
   class SimpleArrayList extends java.util.AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      private SimpleArrayList() {
      }

      public E get(int p_get_1_) {
         return this.delegate.get(p_get_1_);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int p_set_1_, E p_set_2_) {
         E e = this.delegate.set(p_set_1_, p_set_2_);
         AbstractList.this.bindEntryToSelf(p_set_2_);
         return e;
      }

      public void add(int p_add_1_, E p_add_2_) {
         this.delegate.add(p_add_1_, p_add_2_);
         AbstractList.this.bindEntryToSelf(p_add_2_);
      }

      public E remove(int p_remove_1_) {
         return this.delegate.remove(p_remove_1_);
      }
   }
}
