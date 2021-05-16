package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private float xMouse;
   private float yMouse;
   private final RecipeBookGui recipeBookComponent = new RecipeBookGui();
   private boolean recipeBookComponentInitialized;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(PlayerEntity p_i1094_1_) {
      super(p_i1094_1_.inventoryMenu, p_i1094_1_.inventory, new TranslationTextComponent("container.crafting"));
      this.passEvents = true;
      this.titleLabelX = 97;
   }

   public void tick() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeScreen(this.minecraft.player));
      } else {
         this.recipeBookComponent.tick();
      }
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeScreen(this.minecraft.player));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
         this.recipeBookComponentInitialized = true;
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
         this.children.add(this.recipeBookComponent);
         this.setInitialFocus(this.recipeBookComponent);
         this.addButton(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_214086_1_) -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            ((ImageButton)p_214086_1_).setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
      }
   }

   protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
      this.font.draw(p_230451_1_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.doRenderEffects = !this.recipeBookComponent.isVisible();
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_230430_1_, p_230430_4_, p_230430_2_, p_230430_3_);
         this.recipeBookComponent.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      } else {
         this.recipeBookComponent.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         this.recipeBookComponent.renderGhostRecipe(p_230430_1_, this.leftPos, this.topPos, false, p_230430_4_);
      }

      this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
      this.recipeBookComponent.renderTooltip(p_230430_1_, this.leftPos, this.topPos, p_230430_2_, p_230430_3_);
      this.xMouse = (float)p_230430_2_;
      this.yMouse = (float)p_230430_3_;
   }

   protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      renderEntityInInventory(i + 51, j + 75, 30, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);
   }

   public static void renderEntityInInventory(int p_228187_0_, int p_228187_1_, int p_228187_2_, float p_228187_3_, float p_228187_4_, LivingEntity p_228187_5_) {
      float f = (float)Math.atan((double)(p_228187_3_ / 40.0F));
      float f1 = (float)Math.atan((double)(p_228187_4_ / 40.0F));
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)p_228187_0_, (float)p_228187_1_, 1050.0F);
      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.translate(0.0D, 0.0D, 1000.0D);
      matrixstack.scale((float)p_228187_2_, (float)p_228187_2_, (float)p_228187_2_);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
      quaternion.mul(quaternion1);
      matrixstack.mulPose(quaternion);
      float f2 = p_228187_5_.yBodyRot;
      float f3 = p_228187_5_.yRot;
      float f4 = p_228187_5_.xRot;
      float f5 = p_228187_5_.yHeadRotO;
      float f6 = p_228187_5_.yHeadRot;
      p_228187_5_.yBodyRot = 180.0F + f * 20.0F;
      p_228187_5_.yRot = 180.0F + f * 40.0F;
      p_228187_5_.xRot = -f1 * 20.0F;
      p_228187_5_.yHeadRot = p_228187_5_.yRot;
      p_228187_5_.yHeadRotO = p_228187_5_.yRot;
      EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderermanager.overrideCameraOrientation(quaternion1);
      entityrenderermanager.setRenderShadow(false);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> {
         entityrenderermanager.render(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
      });
      irendertypebuffer$impl.endBatch();
      entityrenderermanager.setRenderShadow(true);
      p_228187_5_.yBodyRot = f2;
      p_228187_5_.yRot = f3;
      p_228187_5_.xRot = f4;
      p_228187_5_.yHeadRotO = f5;
      p_228187_5_.yHeadRot = f6;
      RenderSystem.popMatrix();
   }

   protected boolean isHovering(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (this.recipeBookComponent.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? false : super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
      }
   }

   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(p_195361_1_, p_195361_3_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_195361_7_) && flag;
   }

   protected void slotClicked(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.slotClicked(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.recipeBookComponent.slotClicked(p_184098_1_);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void removed() {
      if (this.recipeBookComponentInitialized) {
         this.recipeBookComponent.removed();
      }

      super.removed();
   }

   public RecipeBookGui getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}
