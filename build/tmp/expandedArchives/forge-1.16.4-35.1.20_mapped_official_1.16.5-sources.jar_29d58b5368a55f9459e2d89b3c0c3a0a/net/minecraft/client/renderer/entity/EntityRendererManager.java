package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRendererManager {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(new ResourceLocation("textures/misc/shadow.png"));
   public final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();
   private final Map<String, PlayerRenderer> playerRenderers = Maps.newHashMap();
   private final PlayerRenderer defaultPlayerRenderer;
   private final FontRenderer font;
   public final TextureManager textureManager;
   private World level;
   public ActiveRenderInfo camera;
   private Quaternion cameraOrientation;
   public Entity crosshairPickEntity;
   public final GameSettings options;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   public <E extends Entity> int getPackedLightCoords(E p_229085_1_, float p_229085_2_) {
      return this.getRenderer(p_229085_1_).getPackedLightCoords(p_229085_1_, p_229085_2_);
   }

   public <T extends Entity> void register(EntityType<T> p_229087_1_, EntityRenderer<? super T> p_229087_2_) {
      this.renderers.put(p_229087_1_, p_229087_2_);
   }

   private void registerRenderers(net.minecraft.client.renderer.ItemRenderer p_229097_1_, IReloadableResourceManager p_229097_2_) {
      this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudRenderer(this));
      this.register(EntityType.ARMOR_STAND, new ArmorStandRenderer(this));
      this.register(EntityType.ARROW, new TippedArrowRenderer(this));
      this.register(EntityType.BAT, new BatRenderer(this));
      this.register(EntityType.BEE, new BeeRenderer(this));
      this.register(EntityType.BLAZE, new BlazeRenderer(this));
      this.register(EntityType.BOAT, new BoatRenderer(this));
      this.register(EntityType.CAT, new CatRenderer(this));
      this.register(EntityType.CAVE_SPIDER, new CaveSpiderRenderer(this));
      this.register(EntityType.CHEST_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.CHICKEN, new ChickenRenderer(this));
      this.register(EntityType.COD, new CodRenderer(this));
      this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.COW, new CowRenderer(this));
      this.register(EntityType.CREEPER, new CreeperRenderer(this));
      this.register(EntityType.DOLPHIN, new DolphinRenderer(this));
      this.register(EntityType.DONKEY, new ChestedHorseRenderer<>(this, 0.87F));
      this.register(EntityType.DRAGON_FIREBALL, new DragonFireballRenderer(this));
      this.register(EntityType.DROWNED, new DrownedRenderer(this));
      this.register(EntityType.EGG, new SpriteRenderer<>(this, p_229097_1_));
      this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianRenderer(this));
      this.register(EntityType.END_CRYSTAL, new EnderCrystalRenderer(this));
      this.register(EntityType.ENDER_DRAGON, new EnderDragonRenderer(this));
      this.register(EntityType.ENDERMAN, new EndermanRenderer(this));
      this.register(EntityType.ENDERMITE, new EndermiteRenderer(this));
      this.register(EntityType.ENDER_PEARL, new SpriteRenderer<>(this, p_229097_1_));
      this.register(EntityType.EVOKER_FANGS, new EvokerFangsRenderer(this));
      this.register(EntityType.EVOKER, new EvokerRenderer<>(this));
      this.register(EntityType.EXPERIENCE_BOTTLE, new SpriteRenderer<>(this, p_229097_1_));
      this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbRenderer(this));
      this.register(EntityType.EYE_OF_ENDER, new SpriteRenderer<>(this, p_229097_1_, 1.0F, true));
      this.register(EntityType.FALLING_BLOCK, new FallingBlockRenderer(this));
      this.register(EntityType.FIREBALL, new SpriteRenderer<>(this, p_229097_1_, 3.0F, true));
      this.register(EntityType.FIREWORK_ROCKET, new FireworkRocketRenderer(this, p_229097_1_));
      this.register(EntityType.FISHING_BOBBER, new FishRenderer(this));
      this.register(EntityType.FOX, new FoxRenderer(this));
      this.register(EntityType.FURNACE_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.GHAST, new GhastRenderer(this));
      this.register(EntityType.GIANT, new GiantZombieRenderer(this, 6.0F));
      this.register(EntityType.GUARDIAN, new GuardianRenderer(this));
      this.register(EntityType.HOGLIN, new HoglinRenderer(this));
      this.register(EntityType.HOPPER_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.HORSE, new HorseRenderer(this));
      this.register(EntityType.HUSK, new HuskRenderer(this));
      this.register(EntityType.ILLUSIONER, new IllusionerRenderer(this));
      this.register(EntityType.IRON_GOLEM, new IronGolemRenderer(this));
      this.register(EntityType.ITEM, new ItemRenderer(this, p_229097_1_));
      this.register(EntityType.ITEM_FRAME, new ItemFrameRenderer(this, p_229097_1_));
      this.register(EntityType.LEASH_KNOT, new LeashKnotRenderer(this));
      this.register(EntityType.LIGHTNING_BOLT, new LightningBoltRenderer(this));
      this.register(EntityType.LLAMA, new LlamaRenderer(this));
      this.register(EntityType.LLAMA_SPIT, new LlamaSpitRenderer(this));
      this.register(EntityType.MAGMA_CUBE, new MagmaCubeRenderer(this));
      this.register(EntityType.MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.MOOSHROOM, new MooshroomRenderer(this));
      this.register(EntityType.MULE, new ChestedHorseRenderer<>(this, 0.92F));
      this.register(EntityType.OCELOT, new OcelotRenderer(this));
      this.register(EntityType.PAINTING, new PaintingRenderer(this));
      this.register(EntityType.PANDA, new PandaRenderer(this));
      this.register(EntityType.PARROT, new ParrotRenderer(this));
      this.register(EntityType.PHANTOM, new PhantomRenderer(this));
      this.register(EntityType.PIG, new PigRenderer(this));
      this.register(EntityType.PIGLIN, new PiglinRenderer(this, false));
      this.register(EntityType.PIGLIN_BRUTE, new PiglinRenderer(this, false));
      this.register(EntityType.PILLAGER, new PillagerRenderer(this));
      this.register(EntityType.POLAR_BEAR, new PolarBearRenderer(this));
      this.register(EntityType.POTION, new SpriteRenderer<>(this, p_229097_1_));
      this.register(EntityType.PUFFERFISH, new PufferfishRenderer(this));
      this.register(EntityType.RABBIT, new RabbitRenderer(this));
      this.register(EntityType.RAVAGER, new RavagerRenderer(this));
      this.register(EntityType.SALMON, new SalmonRenderer(this));
      this.register(EntityType.SHEEP, new SheepRenderer(this));
      this.register(EntityType.SHULKER_BULLET, new ShulkerBulletRenderer(this));
      this.register(EntityType.SHULKER, new ShulkerRenderer(this));
      this.register(EntityType.SILVERFISH, new SilverfishRenderer(this));
      this.register(EntityType.SKELETON_HORSE, new UndeadHorseRenderer(this));
      this.register(EntityType.SKELETON, new SkeletonRenderer(this));
      this.register(EntityType.SLIME, new SlimeRenderer(this));
      this.register(EntityType.SMALL_FIREBALL, new SpriteRenderer<>(this, p_229097_1_, 0.75F, true));
      this.register(EntityType.SNOWBALL, new SpriteRenderer<>(this, p_229097_1_));
      this.register(EntityType.SNOW_GOLEM, new SnowManRenderer(this));
      this.register(EntityType.SPAWNER_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowRenderer(this));
      this.register(EntityType.SPIDER, new SpiderRenderer<>(this));
      this.register(EntityType.SQUID, new SquidRenderer(this));
      this.register(EntityType.STRAY, new StrayRenderer(this));
      this.register(EntityType.TNT_MINECART, new TNTMinecartRenderer(this));
      this.register(EntityType.TNT, new TNTRenderer(this));
      this.register(EntityType.TRADER_LLAMA, new LlamaRenderer(this));
      this.register(EntityType.TRIDENT, new TridentRenderer(this));
      this.register(EntityType.TROPICAL_FISH, new TropicalFishRenderer(this));
      this.register(EntityType.TURTLE, new TurtleRenderer(this));
      this.register(EntityType.VEX, new VexRenderer(this));
      this.register(EntityType.VILLAGER, new VillagerRenderer(this, p_229097_2_));
      this.register(EntityType.VINDICATOR, new VindicatorRenderer(this));
      this.register(EntityType.WANDERING_TRADER, new WanderingTraderRenderer(this));
      this.register(EntityType.WITCH, new WitchRenderer(this));
      this.register(EntityType.WITHER, new WitherRenderer(this));
      this.register(EntityType.WITHER_SKELETON, new WitherSkeletonRenderer(this));
      this.register(EntityType.WITHER_SKULL, new WitherSkullRenderer(this));
      this.register(EntityType.WOLF, new WolfRenderer(this));
      this.register(EntityType.ZOGLIN, new ZoglinRenderer(this));
      this.register(EntityType.ZOMBIE_HORSE, new UndeadHorseRenderer(this));
      this.register(EntityType.ZOMBIE, new ZombieRenderer(this));
      this.register(EntityType.ZOMBIFIED_PIGLIN, new PiglinRenderer(this, true));
      this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer(this, p_229097_2_));
      this.register(EntityType.STRIDER, new StriderRenderer(this));
   }

   public EntityRendererManager(TextureManager p_i226034_1_, net.minecraft.client.renderer.ItemRenderer p_i226034_2_, IReloadableResourceManager p_i226034_3_, FontRenderer p_i226034_4_, GameSettings p_i226034_5_) {
      this.textureManager = p_i226034_1_;
      this.font = p_i226034_4_;
      this.options = p_i226034_5_;
      this.registerRenderers(p_i226034_2_, p_i226034_3_);
      this.defaultPlayerRenderer = new PlayerRenderer(this);
      this.playerRenderers.put("default", this.defaultPlayerRenderer);
      this.playerRenderers.put("slim", new PlayerRenderer(this, true));
   }

   //FORGE: Validate the existence of a renderer for each entity type after modded entity renderers have been added
   public void validateRendererExistence() {
      for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
         if (entitytype != EntityType.PLAYER && !this.renderers.containsKey(entitytype)) {
            throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getKey(entitytype));
         }
      }

   }

   public <T extends Entity> EntityRenderer<? super T> getRenderer(T p_78713_1_) {
      if (p_78713_1_ instanceof AbstractClientPlayerEntity) {
         String s = ((AbstractClientPlayerEntity)p_78713_1_).getModelName();
         PlayerRenderer playerrenderer = this.playerRenderers.get(s);
         return (EntityRenderer<? super T>) (playerrenderer != null ? playerrenderer : this.defaultPlayerRenderer);
      } else {
         return (EntityRenderer<? super T>) this.renderers.get(p_78713_1_.getType());
      }
   }

   public void prepare(World p_229088_1_, ActiveRenderInfo p_229088_2_, Entity p_229088_3_) {
      this.level = p_229088_1_;
      this.camera = p_229088_2_;
      this.cameraOrientation = p_229088_2_.rotation();
      this.crosshairPickEntity = p_229088_3_;
   }

   public void overrideCameraOrientation(Quaternion p_229089_1_) {
      this.cameraOrientation = p_229089_1_;
   }

   public void setRenderShadow(boolean p_178633_1_) {
      this.shouldRenderShadow = p_178633_1_;
   }

   public void setRenderHitBoxes(boolean p_178629_1_) {
      this.renderHitBoxes = p_178629_1_;
   }

   public boolean shouldRenderHitBoxes() {
      return this.renderHitBoxes;
   }

   public <E extends Entity> boolean shouldRender(E p_229086_1_, ClippingHelper p_229086_2_, double p_229086_3_, double p_229086_5_, double p_229086_7_) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(p_229086_1_);
      return entityrenderer.shouldRender(p_229086_1_, p_229086_2_, p_229086_3_, p_229086_5_, p_229086_7_);
   }

   public <E extends Entity> void render(E p_229084_1_, double p_229084_2_, double p_229084_4_, double p_229084_6_, float p_229084_8_, float p_229084_9_, MatrixStack p_229084_10_, IRenderTypeBuffer p_229084_11_, int p_229084_12_) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(p_229084_1_);

      try {
         Vector3d vector3d = entityrenderer.getRenderOffset(p_229084_1_, p_229084_9_);
         double d2 = p_229084_2_ + vector3d.x();
         double d3 = p_229084_4_ + vector3d.y();
         double d0 = p_229084_6_ + vector3d.z();
         p_229084_10_.pushPose();
         p_229084_10_.translate(d2, d3, d0);
         entityrenderer.render(p_229084_1_, p_229084_8_, p_229084_9_, p_229084_10_, p_229084_11_, p_229084_12_);
         if (p_229084_1_.displayFireAnimation()) {
            this.renderFlame(p_229084_10_, p_229084_11_, p_229084_1_);
         }

         p_229084_10_.translate(-vector3d.x(), -vector3d.y(), -vector3d.z());
         if (this.options.entityShadows && this.shouldRenderShadow && entityrenderer.shadowRadius > 0.0F && !p_229084_1_.isInvisible()) {
            double d1 = this.distanceToSqr(p_229084_1_.getX(), p_229084_1_.getY(), p_229084_1_.getZ());
            float f = (float)((1.0D - d1 / 256.0D) * (double)entityrenderer.shadowStrength);
            if (f > 0.0F) {
               renderShadow(p_229084_10_, p_229084_11_, p_229084_1_, f, p_229084_9_, this.level, entityrenderer.shadowRadius);
            }
         }

         if (this.renderHitBoxes && !p_229084_1_.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.renderHitbox(p_229084_10_, p_229084_11_.getBuffer(RenderType.lines()), p_229084_1_, p_229084_9_);
         }

         p_229084_10_.popPose();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
         p_229084_1_.fillCrashReportCategory(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
         crashreportcategory1.setDetail("Assigned renderer", entityrenderer);
         crashreportcategory1.setDetail("Location", CrashReportCategory.formatLocation(p_229084_2_, p_229084_4_, p_229084_6_));
         crashreportcategory1.setDetail("Rotation", p_229084_8_);
         crashreportcategory1.setDetail("Delta", p_229084_9_);
         throw new ReportedException(crashreport);
      }
   }

   private void renderHitbox(MatrixStack p_229093_1_, IVertexBuilder p_229093_2_, Entity p_229093_3_, float p_229093_4_) {
      float f = p_229093_3_.getBbWidth() / 2.0F;
      this.renderBox(p_229093_1_, p_229093_2_, p_229093_3_, 1.0F, 1.0F, 1.0F);
      if (p_229093_3_ instanceof EnderDragonEntity) {
         double d0 = -MathHelper.lerp((double)p_229093_4_, p_229093_3_.xOld, p_229093_3_.getX());
         double d1 = -MathHelper.lerp((double)p_229093_4_, p_229093_3_.yOld, p_229093_3_.getY());
         double d2 = -MathHelper.lerp((double)p_229093_4_, p_229093_3_.zOld, p_229093_3_.getZ());

         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)p_229093_3_).getSubEntities()) {
            p_229093_1_.pushPose();
            double d3 = d0 + MathHelper.lerp((double)p_229093_4_, enderdragonpartentity.xOld, enderdragonpartentity.getX());
            double d4 = d1 + MathHelper.lerp((double)p_229093_4_, enderdragonpartentity.yOld, enderdragonpartentity.getY());
            double d5 = d2 + MathHelper.lerp((double)p_229093_4_, enderdragonpartentity.zOld, enderdragonpartentity.getZ());
            p_229093_1_.translate(d3, d4, d5);
            this.renderBox(p_229093_1_, p_229093_2_, enderdragonpartentity, 0.25F, 1.0F, 0.0F);
            p_229093_1_.popPose();
         }
      }

      if (p_229093_3_ instanceof LivingEntity) {
         float f1 = 0.01F;
         WorldRenderer.renderLineBox(p_229093_1_, p_229093_2_, (double)(-f), (double)(p_229093_3_.getEyeHeight() - 0.01F), (double)(-f), (double)f, (double)(p_229093_3_.getEyeHeight() + 0.01F), (double)f, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Vector3d vector3d = p_229093_3_.getViewVector(p_229093_4_);
      Matrix4f matrix4f = p_229093_1_.last().pose();
      p_229093_2_.vertex(matrix4f, 0.0F, p_229093_3_.getEyeHeight(), 0.0F).color(0, 0, 255, 255).endVertex();
      p_229093_2_.vertex(matrix4f, (float)(vector3d.x * 2.0D), (float)((double)p_229093_3_.getEyeHeight() + vector3d.y * 2.0D), (float)(vector3d.z * 2.0D)).color(0, 0, 255, 255).endVertex();
   }

   private void renderBox(MatrixStack p_229094_1_, IVertexBuilder p_229094_2_, Entity p_229094_3_, float p_229094_4_, float p_229094_5_, float p_229094_6_) {
      AxisAlignedBB axisalignedbb = p_229094_3_.getBoundingBox().move(-p_229094_3_.getX(), -p_229094_3_.getY(), -p_229094_3_.getZ());
      WorldRenderer.renderLineBox(p_229094_1_, p_229094_2_, axisalignedbb, p_229094_4_, p_229094_5_, p_229094_6_, 1.0F);
   }

   private void renderFlame(MatrixStack p_229095_1_, IRenderTypeBuffer p_229095_2_, Entity p_229095_3_) {
      TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite textureatlassprite1 = ModelBakery.FIRE_1.sprite();
      p_229095_1_.pushPose();
      float f = p_229095_3_.getBbWidth() * 1.4F;
      p_229095_1_.scale(f, f, f);
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = p_229095_3_.getBbHeight() / f;
      float f4 = 0.0F;
      p_229095_1_.mulPose(Vector3f.YP.rotationDegrees(-this.camera.getYRot()));
      p_229095_1_.translate(0.0D, 0.0D, (double)(-0.3F + (float)((int)f3) * 0.02F));
      float f5 = 0.0F;
      int i = 0;
      IVertexBuilder ivertexbuilder = p_229095_2_.getBuffer(Atlases.cutoutBlockSheet());

      for(MatrixStack.Entry matrixstack$entry = p_229095_1_.last(); f3 > 0.0F; ++i) {
         TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
         float f6 = textureatlassprite2.getU0();
         float f7 = textureatlassprite2.getV0();
         float f8 = textureatlassprite2.getU1();
         float f9 = textureatlassprite2.getV1();
         if (i / 2 % 2 == 0) {
            float f10 = f8;
            f8 = f6;
            f6 = f10;
         }

         fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
         fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
         fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
         fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
         f3 -= 0.45F;
         f4 -= 0.45F;
         f1 *= 0.9F;
         f5 += 0.03F;
      }

      p_229095_1_.popPose();
   }

   private static void fireVertex(MatrixStack.Entry p_229090_0_, IVertexBuilder p_229090_1_, float p_229090_2_, float p_229090_3_, float p_229090_4_, float p_229090_5_, float p_229090_6_) {
      p_229090_1_.vertex(p_229090_0_.pose(), p_229090_2_, p_229090_3_, p_229090_4_).color(255, 255, 255, 255).uv(p_229090_5_, p_229090_6_).overlayCoords(0, 10).uv2(240).normal(p_229090_0_.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void renderShadow(MatrixStack p_229096_0_, IRenderTypeBuffer p_229096_1_, Entity p_229096_2_, float p_229096_3_, float p_229096_4_, IWorldReader p_229096_5_, float p_229096_6_) {
      float f = p_229096_6_;
      if (p_229096_2_ instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)p_229096_2_;
         if (mobentity.isBaby()) {
            f = p_229096_6_ * 0.5F;
         }
      }

      double d2 = MathHelper.lerp((double)p_229096_4_, p_229096_2_.xOld, p_229096_2_.getX());
      double d0 = MathHelper.lerp((double)p_229096_4_, p_229096_2_.yOld, p_229096_2_.getY());
      double d1 = MathHelper.lerp((double)p_229096_4_, p_229096_2_.zOld, p_229096_2_.getZ());
      int i = MathHelper.floor(d2 - (double)f);
      int j = MathHelper.floor(d2 + (double)f);
      int k = MathHelper.floor(d0 - (double)f);
      int l = MathHelper.floor(d0);
      int i1 = MathHelper.floor(d1 - (double)f);
      int j1 = MathHelper.floor(d1 + (double)f);
      MatrixStack.Entry matrixstack$entry = p_229096_0_.last();
      IVertexBuilder ivertexbuilder = p_229096_1_.getBuffer(SHADOW_RENDER_TYPE);

      for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
         renderBlockShadow(matrixstack$entry, ivertexbuilder, p_229096_5_, blockpos, d2, d0, d1, f, p_229096_3_);
      }

   }

   private static void renderBlockShadow(MatrixStack.Entry p_229092_0_, IVertexBuilder p_229092_1_, IWorldReader p_229092_2_, BlockPos p_229092_3_, double p_229092_4_, double p_229092_6_, double p_229092_8_, float p_229092_10_, float p_229092_11_) {
      BlockPos blockpos = p_229092_3_.below();
      BlockState blockstate = p_229092_2_.getBlockState(blockpos);
      if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE && p_229092_2_.getMaxLocalRawBrightness(p_229092_3_) > 3) {
         if (blockstate.isCollisionShapeFullBlock(p_229092_2_, blockpos)) {
            VoxelShape voxelshape = blockstate.getShape(p_229092_2_, p_229092_3_.below());
            if (!voxelshape.isEmpty()) {
               float f = (float)(((double)p_229092_11_ - (p_229092_6_ - (double)p_229092_3_.getY()) / 2.0D) * 0.5D * (double)p_229092_2_.getBrightness(p_229092_3_));
               if (f >= 0.0F) {
                  if (f > 1.0F) {
                     f = 1.0F;
                  }

                  AxisAlignedBB axisalignedbb = voxelshape.bounds();
                  double d0 = (double)p_229092_3_.getX() + axisalignedbb.minX;
                  double d1 = (double)p_229092_3_.getX() + axisalignedbb.maxX;
                  double d2 = (double)p_229092_3_.getY() + axisalignedbb.minY;
                  double d3 = (double)p_229092_3_.getZ() + axisalignedbb.minZ;
                  double d4 = (double)p_229092_3_.getZ() + axisalignedbb.maxZ;
                  float f1 = (float)(d0 - p_229092_4_);
                  float f2 = (float)(d1 - p_229092_4_);
                  float f3 = (float)(d2 - p_229092_6_);
                  float f4 = (float)(d3 - p_229092_8_);
                  float f5 = (float)(d4 - p_229092_8_);
                  float f6 = -f1 / 2.0F / p_229092_10_ + 0.5F;
                  float f7 = -f2 / 2.0F / p_229092_10_ + 0.5F;
                  float f8 = -f4 / 2.0F / p_229092_10_ + 0.5F;
                  float f9 = -f5 / 2.0F / p_229092_10_ + 0.5F;
                  shadowVertex(p_229092_0_, p_229092_1_, f, f1, f3, f4, f6, f8);
                  shadowVertex(p_229092_0_, p_229092_1_, f, f1, f3, f5, f6, f9);
                  shadowVertex(p_229092_0_, p_229092_1_, f, f2, f3, f5, f7, f9);
                  shadowVertex(p_229092_0_, p_229092_1_, f, f2, f3, f4, f7, f8);
               }

            }
         }
      }
   }

   private static void shadowVertex(MatrixStack.Entry p_229091_0_, IVertexBuilder p_229091_1_, float p_229091_2_, float p_229091_3_, float p_229091_4_, float p_229091_5_, float p_229091_6_, float p_229091_7_) {
      p_229091_1_.vertex(p_229091_0_.pose(), p_229091_3_, p_229091_4_, p_229091_5_).color(1.0F, 1.0F, 1.0F, p_229091_2_).uv(p_229091_6_, p_229091_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_229091_0_.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   public void setLevel(@Nullable World p_78717_1_) {
      this.level = p_78717_1_;
      if (p_78717_1_ == null) {
         this.camera = null;
      }

   }

   public double distanceToSqr(Entity p_229099_1_) {
      return this.camera.getPosition().distanceToSqr(p_229099_1_.position());
   }

   public double distanceToSqr(double p_78714_1_, double p_78714_3_, double p_78714_5_) {
      return this.camera.getPosition().distanceToSqr(p_78714_1_, p_78714_3_, p_78714_5_);
   }

   public Quaternion cameraOrientation() {
      return this.cameraOrientation;
   }

   public FontRenderer getFont() {
      return this.font;
   }

   public Map<String, PlayerRenderer> getSkinMap() {
      return (Map<String, PlayerRenderer>) java.util.Collections.unmodifiableMap(playerRenderers);
   }
}
