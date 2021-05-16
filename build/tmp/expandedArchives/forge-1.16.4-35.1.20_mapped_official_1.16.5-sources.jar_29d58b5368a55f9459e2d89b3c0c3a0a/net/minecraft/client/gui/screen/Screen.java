package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends FocusableGui implements IScreen, IRenderable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
   protected final ITextComponent title;
   protected final List<IGuiEventListener> children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List<Widget> buttons = Lists.newArrayList();
   public boolean passEvents;
   protected FontRenderer font;
   private URI clickedLink;

   protected Screen(ITextComponent p_i51108_1_) {
      this.title = p_i51108_1_;
   }

   public ITextComponent getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      }

   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (p_231046_1_ == 258) {
         boolean flag = !hasShiftDown();
         if (!this.changeFocus(flag)) {
            this.changeFocus(flag);
         }

         return false;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.setScreen((Screen)null);
   }

   protected <T extends Widget> T addButton(T p_230480_1_) {
      this.buttons.add(p_230480_1_);
      return this.addWidget(p_230480_1_);
   }

   protected <T extends IGuiEventListener> T addWidget(T p_230481_1_) {
      this.children.add(p_230481_1_);
      return p_230481_1_;
   }

   protected void renderTooltip(MatrixStack p_230457_1_, ItemStack p_230457_2_, int p_230457_3_, int p_230457_4_) {
      FontRenderer font = p_230457_2_.getItem().getFontRenderer(p_230457_2_);
      net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(p_230457_2_);
      this.renderWrappedToolTip(p_230457_1_, this.getTooltipFromItem(p_230457_2_), p_230457_3_, p_230457_4_, (font == null ? this.font : font));
      net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
   }

   public List<ITextComponent> getTooltipFromItem(ItemStack p_231151_1_) {
      return p_231151_1_.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
   }

   public void renderTooltip(MatrixStack p_238652_1_, ITextComponent p_238652_2_, int p_238652_3_, int p_238652_4_) {
      this.renderComponentTooltip(p_238652_1_, Arrays.asList(p_238652_2_), p_238652_3_, p_238652_4_);
   }

   public void renderComponentTooltip(MatrixStack p_243308_1_, List<ITextComponent> p_243308_2_, int p_243308_3_, int p_243308_4_) {
      this.renderWrappedToolTip(p_243308_1_, p_243308_2_, p_243308_3_, p_243308_4_, font);
   }
   public void renderWrappedToolTip(MatrixStack matrixStack, List<? extends net.minecraft.util.text.ITextProperties> tooltips, int mouseX, int mouseY, FontRenderer font) {
      net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, tooltips, mouseX, mouseY, width, height, -1, font);
   }

   public void renderTooltip(MatrixStack p_238654_1_, List<? extends IReorderingProcessor> p_238654_2_, int p_238654_3_, int p_238654_4_) {
      this.renderToolTip(p_238654_1_, p_238654_2_, p_238654_3_, p_238654_4_, font);
   }
   public void renderToolTip(MatrixStack p_238654_1_, List<? extends IReorderingProcessor> p_238654_2_, int p_238654_3_, int p_238654_4_, FontRenderer font) {
      if (!p_238654_2_.isEmpty()) {
         int i = 0;

         for(IReorderingProcessor ireorderingprocessor : p_238654_2_) {
            int j = this.font.width(ireorderingprocessor);
            if (j > i) {
               i = j;
            }
         }

         int i2 = p_238654_3_ + 12;
         int j2 = p_238654_4_ - 12;
         int k = 8;
         if (p_238654_2_.size() > 1) {
            k += 2 + (p_238654_2_.size() - 1) * 10;
         }

         if (i2 + i > this.width) {
            i2 -= 28 + i;
         }

         if (j2 + k + 6 > this.height) {
            j2 = this.height - k - 6;
         }

         p_238654_1_.pushPose();
         int l = -267386864;
         int i1 = 1347420415;
         int j1 = 1344798847;
         int k1 = 400;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuilder();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         Matrix4f matrix4f = p_238654_1_.last().pose();
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 4, i2 + i + 3, j2 - 3, 400, -267386864, -267386864);
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 + k + 3, i2 + i + 3, j2 + k + 4, 400, -267386864, -267386864);
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3, i2 + i + 3, j2 + k + 3, 400, -267386864, -267386864);
         fillGradient(matrix4f, bufferbuilder, i2 - 4, j2 - 3, i2 - 3, j2 + k + 3, 400, -267386864, -267386864);
         fillGradient(matrix4f, bufferbuilder, i2 + i + 3, j2 - 3, i2 + i + 4, j2 + k + 3, 400, -267386864, -267386864);
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3 + 1, i2 - 3 + 1, j2 + k + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(matrix4f, bufferbuilder, i2 + i + 2, j2 - 3 + 1, i2 + i + 3, j2 + k + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3, i2 + i + 3, j2 - 3 + 1, 400, 1347420415, 1347420415);
         fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 + k + 2, i2 + i + 3, j2 + k + 3, 400, 1344798847, 1344798847);
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.shadeModel(7425);
         bufferbuilder.end();
         WorldVertexBufferUploader.end(bufferbuilder);
         RenderSystem.shadeModel(7424);
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
         p_238654_1_.translate(0.0D, 0.0D, 400.0D);

         for(int l1 = 0; l1 < p_238654_2_.size(); ++l1) {
            IReorderingProcessor ireorderingprocessor1 = p_238654_2_.get(l1);
            if (ireorderingprocessor1 != null) {
               this.font.drawInBatch(ireorderingprocessor1, (float)i2, (float)j2, -1, true, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
            }

            if (l1 == 0) {
               j2 += 2;
            }

            j2 += 10;
         }

         irendertypebuffer$impl.endBatch();
         p_238654_1_.popPose();
      }
   }

   protected void renderComponentHoverEffect(MatrixStack p_238653_1_, @Nullable Style p_238653_2_, int p_238653_3_, int p_238653_4_) {
      if (p_238653_2_ != null && p_238653_2_.getHoverEvent() != null) {
         HoverEvent hoverevent = p_238653_2_.getHoverEvent();
         HoverEvent.ItemHover hoverevent$itemhover = hoverevent.getValue(HoverEvent.Action.SHOW_ITEM);
         if (hoverevent$itemhover != null) {
            this.renderTooltip(p_238653_1_, hoverevent$itemhover.getItemStack(), p_238653_3_, p_238653_4_);
         } else {
            HoverEvent.EntityHover hoverevent$entityhover = hoverevent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (hoverevent$entityhover != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(p_238653_1_, hoverevent$entityhover.getTooltipLines(), p_238653_3_, p_238653_4_);
               }
            } else {
               ITextComponent itextcomponent = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
               if (itextcomponent != null) {
                  this.renderTooltip(p_238653_1_, this.minecraft.font.split(itextcomponent, Math.max(this.width / 2, 200)), p_238653_3_, p_238653_4_);
               }
            }
         }

      }
   }

   protected void insertText(String p_231155_1_, boolean p_231155_2_) {
   }

   public boolean handleComponentClicked(@Nullable Style p_230455_1_) {
      if (p_230455_1_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_230455_1_.getClickEvent();
         if (hasShiftDown()) {
            if (p_230455_1_.getInsertion() != null) {
               this.insertText(p_230455_1_.getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.options.chatLinks) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.options.chatLinksPrompt) {
                     this.clickedLink = uri;
                     this.minecraft.setScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                  } else {
                     this.openLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(clickevent.getValue(), false);
            } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardHandler.setClipboard(clickevent.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", (Object)clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String p_231161_1_) {
      this.sendMessage(p_231161_1_, true);
   }

   public void sendMessage(String p_231159_1_, boolean p_231159_2_) {
      p_231159_1_ = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(p_231159_1_);
      if (p_231159_1_.isEmpty()) return;
      if (p_231159_2_) {
         this.minecraft.gui.getChat().addRecentChat(p_231159_1_);
      }
      //if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return; //Forge: TODO Client command re-write

      this.minecraft.player.chat(p_231159_1_);
   }

   public void init(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
      this.minecraft = p_231158_1_;
      this.itemRenderer = p_231158_1_.getItemRenderer();
      this.font = p_231158_1_.font;
      this.width = p_231158_2_;
      this.height = p_231158_3_;
      java.util.function.Consumer<Widget> remove = (b) -> {
         buttons.remove(b);
         children.remove(b);
      };
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
      this.buttons.clear();
      this.children.clear();
      this.setFocused((IGuiEventListener)null);
      this.init();
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
   }

   public List<? extends IGuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground(MatrixStack p_230446_1_) {
      this.renderBackground(p_230446_1_, 0);
   }

   public void renderBackground(MatrixStack p_238651_1_, int p_238651_2_) {
      if (this.minecraft.level != null) {
         this.fillGradient(p_238651_1_, 0, 0, this.width, this.height, -1072689136, -804253680);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, p_238651_1_));
      } else {
         this.renderDirtBackground(p_238651_2_);
      }

   }

   public void renderDirtBackground(int p_231165_1_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / 32.0F + (float)p_231165_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)p_231165_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / 32.0F, (float)p_231165_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)p_231165_1_).color(64, 64, 64, 255).endVertex();
      tessellator.end();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, new MatrixStack()));
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean p_231162_1_) {
      if (p_231162_1_) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI p_231156_1_) {
      Util.getPlatform().openUri(p_231156_1_);
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isCut(int p_231166_0_) {
      return p_231166_0_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int p_231168_0_) {
      return p_231168_0_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int p_231169_0_) {
      return p_231169_0_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int p_231170_0_) {
      return p_231170_0_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
   }

   public static void wrapScreenError(Runnable p_231153_0_, String p_231153_1_, String p_231153_2_) {
      try {
         p_231153_0_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, p_231153_1_);
         CrashReportCategory crashreportcategory = crashreport.addCategory("Affected screen");
         crashreportcategory.setDetail("Screen name", () -> {
            return p_231153_2_;
         });
         throw new ReportedException(crashreport);
      }
   }

   protected boolean isValidCharacterForName(String p_231154_1_, char p_231154_2_, int p_231154_3_) {
      int i = p_231154_1_.indexOf(58);
      int j = p_231154_1_.indexOf(47);
      if (p_231154_2_ == ':') {
         return (j == -1 || p_231154_3_ <= j) && i == -1;
      } else if (p_231154_2_ == '/') {
         return p_231154_3_ > i;
      } else {
         return p_231154_2_ == '_' || p_231154_2_ == '-' || p_231154_2_ >= 'a' && p_231154_2_ <= 'z' || p_231154_2_ >= '0' && p_231154_2_ <= '9' || p_231154_2_ == '.';
      }
   }

   public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
      return true;
   }

   public void onFilesDrop(List<Path> p_230476_1_) {
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }
}
