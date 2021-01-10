package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends ContainerScreen<EnchantmentContainer> {
   /** The ResourceLocation containing the Enchantment GUI texture location */
   private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   /** The ResourceLocation containing the texture for the Book rendered above the enchantment table */
   private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
   /** The ModelBook instance used for rendering the book on the Enchantment table */
   private static final BookModel MODEL_BOOK = new BookModel();
   /** A Random instance for use with the enchantment gui */
   private final Random random = new Random();
   public int ticks;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last = ItemStack.EMPTY;

   public EnchantmentScreen(EnchantmentContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
      super(container, playerInventory, textComponent);
   }

   public void tick() {
      super.tick();
      this.tickBook();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;

      for(int k = 0; k < 3; ++k) {
         double d0 = mouseX - (double)(i + 60);
         double d1 = mouseY - (double)(j + 14 + 19 * k);
         if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.container.enchantItem(this.minecraft.player, k)) {
            this.minecraft.playerController.sendEnchantPacket((this.container).windowId, k);
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderHelper.setupGuiFlatDiffuseLighting();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
      RenderSystem.matrixMode(5889);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      int k = (int)this.minecraft.getMainWindow().getGuiScaleFactor();
      RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
      RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
      RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.matrixMode(5888);
      matrixStack.push();
      MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
      matrixstack$entry.getMatrix().setIdentity();
      matrixstack$entry.getNormal().setIdentity();
      matrixStack.translate(0.0D, (double)3.3F, 1984.0D);
      float f = 5.0F;
      matrixStack.scale(5.0F, 5.0F, 5.0F);
      matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
      matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
      float f1 = MathHelper.lerp(partialTicks, this.oOpen, this.open);
      matrixStack.translate((double)((1.0F - f1) * 0.2F), (double)((1.0F - f1) * 0.1F), (double)((1.0F - f1) * 0.25F));
      float f2 = -(1.0F - f1) * 90.0F - 90.0F;
      matrixStack.rotate(Vector3f.YP.rotationDegrees(f2));
      matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
      float f3 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.25F;
      float f4 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      RenderSystem.enableRescaleNormal();
      MODEL_BOOK.setBookState(0.0F, f3, f4, f1);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
      IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(MODEL_BOOK.getRenderType(ENCHANTMENT_TABLE_BOOK_TEXTURE));
      MODEL_BOOK.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      irendertypebuffer$impl.finish();
      matrixStack.pop();
      RenderSystem.matrixMode(5889);
      RenderSystem.viewport(0, 0, this.minecraft.getMainWindow().getFramebufferWidth(), this.minecraft.getMainWindow().getFramebufferHeight());
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
      RenderHelper.setupGui3DDiffuseLighting();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator((long)this.container.func_217005_f());
      int l = this.container.getLapisAmount();

      for(int i1 = 0; i1 < 3; ++i1) {
         int j1 = i + 60;
         int k1 = j1 + 20;
         this.setBlitOffset(0);
         this.minecraft.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int l1 = (this.container).enchantLevels[i1];
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (l1 == 0) {
            this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
         } else {
            String s = "" + l1;
            int i2 = 86 - this.font.getStringWidth(s);
            ITextProperties itextproperties = EnchantmentNameParts.getInstance().getGalacticEnchantmentName(this.font, i2);
            int j2 = 6839882;
            if (((l < i1 + 1 || this.minecraft.player.experienceLevel < l1) && !this.minecraft.player.abilities.isCreativeMode) || this.container.enchantClue[i1] == -1) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
               this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
               this.blit(matrixStack, j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16);
               this.font.func_238418_a_(itextproperties, k1, j + 16 + 19 * i1, i2, (j2 & 16711422) >> 1);
               j2 = 4226832;
            } else {
               int k2 = x - (i + 60);
               int l2 = y - (j + 14 + 19 * i1);
               if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                  this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 204, 108, 19);
                  j2 = 16777088;
               } else {
                  this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 166, 108, 19);
               }

               this.blit(matrixStack, j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16);
               this.font.func_238418_a_(itextproperties, k1, j + 16 + 19 * i1, i2, j2);
               j2 = 8453920;
            }

            this.font.drawStringWithShadow(matrixStack, s, (float)(k1 + 86 - this.font.getStringWidth(s)), (float)(j + 16 + 19 * i1 + 7), j2);
         }
      }

   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      partialTicks = this.minecraft.getRenderPartialTicks();
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
      boolean flag = this.minecraft.player.abilities.isCreativeMode;
      int i = this.container.getLapisAmount();

      for(int j = 0; j < 3; ++j) {
         int k = (this.container).enchantLevels[j];
         Enchantment enchantment = Enchantment.getEnchantmentByID((this.container).enchantClue[j]);
         int l = (this.container).worldClue[j];
         int i1 = j + 1;
         if (this.isPointInRegion(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0) {
            List<ITextComponent> list = Lists.newArrayList();
            list.add((new TranslationTextComponent("container.enchant.clue", enchantment == null ? "" : enchantment.getDisplayName(l))).mergeStyle(TextFormatting.WHITE));
            if(enchantment == null) {
               list.add(new StringTextComponent(""));
               list.add(new TranslationTextComponent("forge.container.enchant.limitedEnchantability").mergeStyle(TextFormatting.RED));
            } else if (!flag) {
               list.add(StringTextComponent.EMPTY);
               if (this.minecraft.player.experienceLevel < k) {
                  list.add((new TranslationTextComponent("container.enchant.level.requirement", (this.container).enchantLevels[j])).mergeStyle(TextFormatting.RED));
               } else {
                  IFormattableTextComponent iformattabletextcomponent;
                  if (i1 == 1) {
                     iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.one");
                  } else {
                     iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.many", i1);
                  }

                  list.add(iformattabletextcomponent.mergeStyle(i >= i1 ? TextFormatting.GRAY : TextFormatting.RED));
                  IFormattableTextComponent iformattabletextcomponent1;
                  if (i1 == 1) {
                     iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.one");
                  } else {
                     iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.many", i1);
                  }

                  list.add(iformattabletextcomponent1.mergeStyle(TextFormatting.GRAY));
               }
            }

            this.func_243308_b(matrixStack, list, mouseX, mouseY);
            break;
         }
      }

   }

   public void tickBook() {
      ItemStack itemstack = this.container.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(itemstack, this.last)) {
         this.last = itemstack;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      ++this.ticks;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean flag = false;

      for(int i = 0; i < 3; ++i) {
         if ((this.container).enchantLevels[i] != 0) {
            flag = true;
         }
      }

      if (flag) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
      float f1 = (this.flipT - this.flip) * 0.4F;
      float f = 0.2F;
      f1 = MathHelper.clamp(f1, -0.2F, 0.2F);
      this.flipA += (f1 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
