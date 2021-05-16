package net.minecraft.client.gui.widget.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcePackList extends ExtendedList<ResourcePackList.ResourcePackEntry> {
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent INCOMPATIBLE_TITLE = new TranslationTextComponent("pack.incompatible");
   private static final ITextComponent INCOMPATIBLE_CONFIRM_TITLE = new TranslationTextComponent("pack.incompatible.confirm.title");
   private final ITextComponent title;

   public ResourcePackList(Minecraft p_i241200_1_, int p_i241200_2_, int p_i241200_3_, ITextComponent p_i241200_4_) {
      super(p_i241200_1_, p_i241200_2_, p_i241200_3_, 32, p_i241200_3_ - 55 + 4, 36);
      this.title = p_i241200_4_;
      this.centerListVertically = false;
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
   }

   protected void renderHeader(MatrixStack p_230448_1_, int p_230448_2_, int p_230448_3_, Tessellator p_230448_4_) {
      ITextComponent itextcomponent = (new StringTextComponent("")).append(this.title).withStyle(TextFormatting.UNDERLINE, TextFormatting.BOLD);
      this.minecraft.font.draw(p_230448_1_, itextcomponent, (float)(p_230448_2_ + this.width / 2 - this.minecraft.font.width(itextcomponent) / 2), (float)Math.min(this.y0 + 3, p_230448_3_), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResourcePackEntry extends ExtendedList.AbstractListEntry<ResourcePackList.ResourcePackEntry> {
      private ResourcePackList parent;
      protected final Minecraft minecraft;
      protected final Screen screen;
      private final PackLoadingManager.IPack pack;
      private final IReorderingProcessor nameDisplayCache;
      private final IBidiRenderer descriptionDisplayCache;
      private final IReorderingProcessor incompatibleNameDisplayCache;
      private final IBidiRenderer incompatibleDescriptionDisplayCache;

      public ResourcePackEntry(Minecraft p_i241201_1_, ResourcePackList p_i241201_2_, Screen p_i241201_3_, PackLoadingManager.IPack p_i241201_4_) {
         this.minecraft = p_i241201_1_;
         this.screen = p_i241201_3_;
         this.pack = p_i241201_4_;
         this.parent = p_i241201_2_;
         this.nameDisplayCache = cacheName(p_i241201_1_, p_i241201_4_.getTitle());
         this.descriptionDisplayCache = cacheDescription(p_i241201_1_, p_i241201_4_.getExtendedDescription());
         this.incompatibleNameDisplayCache = cacheName(p_i241201_1_, ResourcePackList.INCOMPATIBLE_TITLE);
         this.incompatibleDescriptionDisplayCache = cacheDescription(p_i241201_1_, p_i241201_4_.getCompatibility().getDescription());
      }

      private static IReorderingProcessor cacheName(Minecraft p_244424_0_, ITextComponent p_244424_1_) {
         int i = p_244424_0_.font.width(p_244424_1_);
         if (i > 157) {
            ITextProperties itextproperties = ITextProperties.composite(p_244424_0_.font.substrByWidth(p_244424_1_, 157 - p_244424_0_.font.width("...")), ITextProperties.of("..."));
            return LanguageMap.getInstance().getVisualOrder(itextproperties);
         } else {
            return p_244424_1_.getVisualOrderText();
         }
      }

      private static IBidiRenderer cacheDescription(Minecraft p_244425_0_, ITextComponent p_244425_1_) {
         return IBidiRenderer.create(p_244425_0_.font, p_244425_1_, 157, 2);
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         PackCompatibility packcompatibility = this.pack.getCompatibility();
         if (!packcompatibility.isCompatible()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.fill(p_230432_1_, p_230432_4_ - 1, p_230432_3_ - 1, p_230432_4_ + p_230432_5_ - 9, p_230432_3_ + p_230432_6_ + 1, -8978432);
         }

         this.minecraft.getTextureManager().bind(this.pack.getIconTexture());
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 32, 32);
         IReorderingProcessor ireorderingprocessor = this.nameDisplayCache;
         IBidiRenderer ibidirenderer = this.descriptionDisplayCache;
         if (this.showHoverOverlay() && (this.minecraft.options.touchscreen || p_230432_9_)) {
            this.minecraft.getTextureManager().bind(ResourcePackList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = p_230432_7_ - p_230432_4_;
            int j = p_230432_8_ - p_230432_3_;
            if (!this.pack.getCompatibility().isCompatible()) {
               ireorderingprocessor = this.incompatibleNameDisplayCache;
               ibidirenderer = this.incompatibleDescriptionDisplayCache;
            }

            if (this.pack.canSelect()) {
               if (i < 32) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.pack.canUnselect()) {
                  if (i < 16) {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveUp()) {
                  if (i < 32 && i > 16 && j < 16) {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveDown()) {
                  if (i < 32 && i > 16 && j > 16) {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         this.minecraft.font.drawShadow(p_230432_1_, ireorderingprocessor, (float)(p_230432_4_ + 32 + 2), (float)(p_230432_3_ + 1), 16777215);
         ibidirenderer.renderLeftAligned(p_230432_1_, p_230432_4_ + 32 + 2, p_230432_3_ + 12, 10, 8421504);
      }

      private boolean showHoverOverlay() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         double d0 = p_231044_1_ - (double)this.parent.getRowLeft();
         double d1 = p_231044_3_ - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
         if (this.showHoverOverlay() && d0 <= 32.0D) {
            if (this.pack.canSelect()) {
               PackCompatibility packcompatibility = this.pack.getCompatibility();
               if (packcompatibility.isCompatible()) {
                  this.pack.select();
               } else {
                  ITextComponent itextcomponent = packcompatibility.getConfirmation();
                  this.minecraft.setScreen(new ConfirmScreen((p_238921_1_) -> {
                     this.minecraft.setScreen(this.screen);
                     if (p_238921_1_) {
                        this.pack.select();
                     }

                  }, ResourcePackList.INCOMPATIBLE_CONFIRM_TITLE, itextcomponent));
               }

               return true;
            }

            if (d0 < 16.0D && this.pack.canUnselect()) {
               this.pack.unselect();
               return true;
            }

            if (d0 > 16.0D && d1 < 16.0D && this.pack.canMoveUp()) {
               this.pack.moveUp();
               return true;
            }

            if (d0 > 16.0D && d1 > 16.0D && this.pack.canMoveDown()) {
               this.pack.moveDown();
               return true;
            }
         }

         return false;
      }
   }
}
