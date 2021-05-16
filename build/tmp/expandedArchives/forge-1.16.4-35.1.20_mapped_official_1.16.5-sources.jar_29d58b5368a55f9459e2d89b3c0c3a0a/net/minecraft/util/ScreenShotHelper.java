package net.minecraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void grab(File p_148260_0_, int p_148260_1_, int p_148260_2_, Framebuffer p_148260_3_, Consumer<ITextComponent> p_148260_4_) {
      grab(p_148260_0_, (String)null, p_148260_1_, p_148260_2_, p_148260_3_, p_148260_4_);
   }

   public static void grab(File p_148259_0_, @Nullable String p_148259_1_, int p_148259_2_, int p_148259_3_, Framebuffer p_148259_4_, Consumer<ITextComponent> p_148259_5_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            _grab(p_148259_0_, p_148259_1_, p_148259_2_, p_148259_3_, p_148259_4_, p_148259_5_);
         });
      } else {
         _grab(p_148259_0_, p_148259_1_, p_148259_2_, p_148259_3_, p_148259_4_, p_148259_5_);
      }

   }

   private static void _grab(File p_228051_0_, @Nullable String p_228051_1_, int p_228051_2_, int p_228051_3_, Framebuffer p_228051_4_, Consumer<ITextComponent> p_228051_5_) {
      NativeImage nativeimage = takeScreenshot(p_228051_2_, p_228051_3_, p_228051_4_);
      File file1 = new File(p_228051_0_, "screenshots");
      file1.mkdir();
      File file2;
      if (p_228051_1_ == null) {
         file2 = getFile(file1);
      } else {
         file2 = new File(file1, p_228051_1_);
      }

      net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeimage, file2);
      if (event.isCanceled()) {
         p_228051_5_.accept(event.getCancelMessage());
         return;
      }
      final File target = event.getScreenshotFile();

      Util.ioPool().execute(() -> {
         try {
            nativeimage.writeToFile(target);
            ITextComponent itextcomponent = (new StringTextComponent(file2.getName())).withStyle(TextFormatting.UNDERLINE).withStyle((p_238335_1_) -> {
               return p_238335_1_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, target.getAbsolutePath()));
            });
            if (event.getResultMessage() != null)
               p_228051_5_.accept(event.getResultMessage());
            else
               p_228051_5_.accept(new TranslationTextComponent("screenshot.success", itextcomponent));
         } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
            p_228051_5_.accept(new TranslationTextComponent("screenshot.failure", exception.getMessage()));
         } finally {
            nativeimage.close();
         }

      });
   }

   public static NativeImage takeScreenshot(int p_198052_0_, int p_198052_1_, Framebuffer p_198052_2_) {
      p_198052_0_ = p_198052_2_.width;
      p_198052_1_ = p_198052_2_.height;
      NativeImage nativeimage = new NativeImage(p_198052_0_, p_198052_1_, false);
      RenderSystem.bindTexture(p_198052_2_.getColorTextureId());
      nativeimage.downloadTexture(0, true);
      nativeimage.flipY();
      return nativeimage;
   }

   private static File getFile(File p_74290_0_) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         File file1 = new File(p_74290_0_, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}
