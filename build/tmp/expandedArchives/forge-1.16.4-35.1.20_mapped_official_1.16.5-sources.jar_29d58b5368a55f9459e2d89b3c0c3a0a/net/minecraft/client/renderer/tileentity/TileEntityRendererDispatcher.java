package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererDispatcher {
   private final Map<TileEntityType<?>, TileEntityRenderer<?>> renderers = Maps.newHashMap();
   public static final TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   private final BufferBuilder singleRenderBuffer = new BufferBuilder(256);
   public FontRenderer font;
   public TextureManager textureManager;
   public World level;
   public ActiveRenderInfo camera;
   public RayTraceResult cameraHitResult;

   private TileEntityRendererDispatcher() {
      this.register(TileEntityType.SIGN, new SignTileEntityRenderer(this));
      this.register(TileEntityType.MOB_SPAWNER, new MobSpawnerTileEntityRenderer(this));
      this.register(TileEntityType.PISTON, new PistonTileEntityRenderer(this));
      this.register(TileEntityType.CHEST, new ChestTileEntityRenderer<>(this));
      this.register(TileEntityType.ENDER_CHEST, new ChestTileEntityRenderer<>(this));
      this.register(TileEntityType.TRAPPED_CHEST, new ChestTileEntityRenderer<>(this));
      this.register(TileEntityType.ENCHANTING_TABLE, new EnchantmentTableTileEntityRenderer(this));
      this.register(TileEntityType.LECTERN, new LecternTileEntityRenderer(this));
      this.register(TileEntityType.END_PORTAL, new EndPortalTileEntityRenderer<>(this));
      this.register(TileEntityType.END_GATEWAY, new EndGatewayTileEntityRenderer(this));
      this.register(TileEntityType.BEACON, new BeaconTileEntityRenderer(this));
      this.register(TileEntityType.SKULL, new SkullTileEntityRenderer(this));
      this.register(TileEntityType.BANNER, new BannerTileEntityRenderer(this));
      this.register(TileEntityType.STRUCTURE_BLOCK, new StructureTileEntityRenderer(this));
      this.register(TileEntityType.SHULKER_BOX, new ShulkerBoxTileEntityRenderer(new ShulkerModel(), this));
      this.register(TileEntityType.BED, new BedTileEntityRenderer(this));
      this.register(TileEntityType.CONDUIT, new ConduitTileEntityRenderer(this));
      this.register(TileEntityType.BELL, new BellTileEntityRenderer(this));
      this.register(TileEntityType.CAMPFIRE, new CampfireTileEntityRenderer(this));
   }

   private <E extends TileEntity> void register(TileEntityType<E> p_228854_1_, TileEntityRenderer<E> p_228854_2_) {
      this.renderers.put(p_228854_1_, p_228854_2_);
   }

   @Nullable
   public <E extends TileEntity> TileEntityRenderer<E> getRenderer(E p_147547_1_) {
      return (TileEntityRenderer<E>)this.renderers.get(p_147547_1_.getType());
   }

   public void prepare(World p_217665_1_, TextureManager p_217665_2_, FontRenderer p_217665_3_, ActiveRenderInfo p_217665_4_, RayTraceResult p_217665_5_) {
      if (this.level != p_217665_1_) {
         this.setLevel(p_217665_1_);
      }

      this.textureManager = p_217665_2_;
      this.camera = p_217665_4_;
      this.font = p_217665_3_;
      this.cameraHitResult = p_217665_5_;
   }

   public <E extends TileEntity> void render(E p_228850_1_, float p_228850_2_, MatrixStack p_228850_3_, IRenderTypeBuffer p_228850_4_) {
      if (Vector3d.atCenterOf(p_228850_1_.getBlockPos()).closerThan(this.camera.getPosition(), p_228850_1_.getViewDistance())) {
         TileEntityRenderer<E> tileentityrenderer = this.getRenderer(p_228850_1_);
         if (tileentityrenderer != null) {
            if (p_228850_1_.hasLevel() && p_228850_1_.getType().isValid(p_228850_1_.getBlockState().getBlock())) {
               tryRender(p_228850_1_, () -> {
                  setupAndRender(tileentityrenderer, p_228850_1_, p_228850_2_, p_228850_3_, p_228850_4_);
               });
            }
         }
      }
   }

   private static <T extends TileEntity> void setupAndRender(TileEntityRenderer<T> p_228855_0_, T p_228855_1_, float p_228855_2_, MatrixStack p_228855_3_, IRenderTypeBuffer p_228855_4_) {
      World world = p_228855_1_.getLevel();
      int i;
      if (world != null) {
         i = WorldRenderer.getLightColor(world, p_228855_1_.getBlockPos());
      } else {
         i = 15728880;
      }

      p_228855_0_.render(p_228855_1_, p_228855_2_, p_228855_3_, p_228855_4_, i, OverlayTexture.NO_OVERLAY);
   }

   public <E extends TileEntity> boolean renderItem(E p_228852_1_, MatrixStack p_228852_2_, IRenderTypeBuffer p_228852_3_, int p_228852_4_, int p_228852_5_) {
      TileEntityRenderer<E> tileentityrenderer = this.getRenderer(p_228852_1_);
      if (tileentityrenderer == null) {
         return true;
      } else {
         tryRender(p_228852_1_, () -> {
            tileentityrenderer.render(p_228852_1_, 0.0F, p_228852_2_, p_228852_3_, p_228852_4_, p_228852_5_);
         });
         return false;
      }
   }

   private static void tryRender(TileEntity p_228853_0_, Runnable p_228853_1_) {
      try {
         p_228853_1_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Block Entity");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block Entity Details");
         p_228853_0_.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void setLevel(@Nullable World p_147543_1_) {
      this.level = p_147543_1_;
      if (p_147543_1_ == null) {
         this.camera = null;
      }

   }

   public FontRenderer getFont() {
      return this.font;
   }

   //Internal, Do not call Use ClientRegistry.
   public synchronized <T extends TileEntity> void setSpecialRendererInternal(TileEntityType<T> tileEntityType, TileEntityRenderer<? super T> specialRenderer) {
      this.renderers.put(tileEntityType, specialRenderer);
   }
}
