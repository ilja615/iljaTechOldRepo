package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OptimizeWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Object2IntMap<RegistryKey<World>> DIMENSION_COLORS = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityStrategy()), (p_212346_0_) -> {
      p_212346_0_.put(World.OVERWORLD, -13408734);
      p_212346_0_.put(World.NETHER, -10075085);
      p_212346_0_.put(World.END, -8943531);
      p_212346_0_.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldOptimizer upgrader;

   @Nullable
   public static OptimizeWorldScreen create(Minecraft p_239025_0_, BooleanConsumer p_239025_1_, DataFixer p_239025_2_, SaveFormat.LevelSave p_239025_3_, boolean p_239025_4_) {
      DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();

      try (Minecraft.PackManager minecraft$packmanager = p_239025_0_.makeServerStem(dynamicregistries$impl, Minecraft::loadDataPacks, Minecraft::loadWorldData, false, p_239025_3_)) {
         IServerConfiguration iserverconfiguration = minecraft$packmanager.worldData();
         p_239025_3_.saveDataTag(dynamicregistries$impl, iserverconfiguration);
         ImmutableSet<RegistryKey<World>> immutableset = iserverconfiguration.worldGenSettings().levels();
         return new OptimizeWorldScreen(p_239025_1_, p_239025_2_, p_239025_3_, iserverconfiguration.getLevelSettings(), p_239025_4_, immutableset);
      } catch (Exception exception) {
         LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
         return null;
      }
   }

   private OptimizeWorldScreen(BooleanConsumer p_i232319_1_, DataFixer p_i232319_2_, SaveFormat.LevelSave p_i232319_3_, WorldSettings p_i232319_4_, boolean p_i232319_5_, ImmutableSet<RegistryKey<World>> p_i232319_6_) {
      super(new TranslationTextComponent("optimizeWorld.title", p_i232319_4_.levelName()));
      this.callback = p_i232319_1_;
      this.upgrader = new WorldOptimizer(p_i232319_3_, p_i232319_2_, p_i232319_6_, p_i232319_5_);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, DialogTexts.GUI_CANCEL, (p_214331_1_) -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }));
   }

   public void tick() {
      if (this.upgrader.isFinished()) {
         this.callback.accept(true);
      }

   }

   public void onClose() {
      this.callback.accept(false);
   }

   public void removed() {
      this.upgrader.cancel();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, 16777215);
      int i = this.width / 2 - 150;
      int j = this.width / 2 + 150;
      int k = this.height / 4 + 100;
      int l = k + 10;
      drawCenteredString(p_230430_1_, this.font, this.upgrader.getStatus(), this.width / 2, k - 9 - 2, 10526880);
      if (this.upgrader.getTotalChunks() > 0) {
         fill(p_230430_1_, i - 1, k - 1, j + 1, l + 1, -16777216);
         drawString(p_230430_1_, this.font, new TranslationTextComponent("optimizeWorld.info.converted", this.upgrader.getConverted()), i, 40, 10526880);
         drawString(p_230430_1_, this.font, new TranslationTextComponent("optimizeWorld.info.skipped", this.upgrader.getSkipped()), i, 40 + 9 + 3, 10526880);
         drawString(p_230430_1_, this.font, new TranslationTextComponent("optimizeWorld.info.total", this.upgrader.getTotalChunks()), i, 40 + (9 + 3) * 2, 10526880);
         int i1 = 0;

         for(RegistryKey<World> registrykey : this.upgrader.levels()) {
            int j1 = MathHelper.floor(this.upgrader.dimensionProgress(registrykey) * (float)(j - i));
            fill(p_230430_1_, i + i1, k, i + i1 + j1, l, DIMENSION_COLORS.getInt(registrykey));
            i1 += j1;
         }

         int k1 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         drawCenteredString(p_230430_1_, this.font, k1 + " / " + this.upgrader.getTotalChunks(), this.width / 2, k + 2 * 9 + 2, 10526880);
         drawCenteredString(p_230430_1_, this.font, MathHelper.floor(this.upgrader.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
