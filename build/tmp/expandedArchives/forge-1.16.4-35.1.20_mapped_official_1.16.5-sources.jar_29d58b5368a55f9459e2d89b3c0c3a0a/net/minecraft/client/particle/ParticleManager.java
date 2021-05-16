package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleManager implements IFutureReloadListener {
   private static final List<IParticleRenderType> RENDER_ORDER = ImmutableList.of(IParticleRenderType.TERRAIN_SHEET, IParticleRenderType.PARTICLE_SHEET_OPAQUE, IParticleRenderType.PARTICLE_SHEET_LIT, IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, IParticleRenderType.CUSTOM);
   protected ClientWorld level;
   private final Map<IParticleRenderType, Queue<Particle>> particles = Maps.newIdentityHashMap();
   private final Queue<EmitterParticle> trackingEmitters = Queues.newArrayDeque();
   private final TextureManager textureManager;
   private final Random random = new Random();
   private final Map<ResourceLocation, IParticleFactory<?>> providers = new java.util.HashMap<>();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
   private final Map<ResourceLocation, ParticleManager.AnimatedSpriteImpl> spriteSets = Maps.newHashMap();
   private final AtlasTexture textureAtlas = new AtlasTexture(AtlasTexture.LOCATION_PARTICLES);

   public ParticleManager(ClientWorld p_i232413_1_, TextureManager p_i232413_2_) {
      p_i232413_2_.register(this.textureAtlas.location(), this.textureAtlas);
      this.level = p_i232413_1_;
      this.textureManager = p_i232413_2_;
      this.registerProviders();
   }

   private void registerProviders() {
      this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobFactory::new);
      this.register(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerFactory::new);
      this.register(ParticleTypes.BARRIER, new BarrierParticle.Factory());
      this.register(ParticleTypes.BLOCK, new DiggingParticle.Factory());
      this.register(ParticleTypes.BUBBLE, BubbleParticle.Factory::new);
      this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
      this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
      this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireParticle.CozySmokeFactory::new);
      this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireParticle.SignalSmokeFactory::new);
      this.register(ParticleTypes.CLOUD, CloudParticle.Factory::new);
      this.register(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFactory::new);
      this.register(ParticleTypes.CRIT, CritParticle.Factory::new);
      this.register(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
      this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorFactory::new);
      this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
      this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedFactory::new);
      this.register(ParticleTypes.DRIPPING_LAVA, DripParticle.DrippingLavaFactory::new);
      this.register(ParticleTypes.FALLING_LAVA, DripParticle.FallingLavaFactory::new);
      this.register(ParticleTypes.LANDING_LAVA, DripParticle.LandingLavaFactory::new);
      this.register(ParticleTypes.DRIPPING_WATER, DripParticle.DrippingWaterFactory::new);
      this.register(ParticleTypes.FALLING_WATER, DripParticle.FallingWaterFactory::new);
      this.register(ParticleTypes.DUST, RedstoneParticle.Factory::new);
      this.register(ParticleTypes.EFFECT, SpellParticle.Factory::new);
      this.register(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Factory());
      this.register(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicFactory::new);
      this.register(ParticleTypes.ENCHANT, EnchantmentTableParticle.EnchantmentTable::new);
      this.register(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
      this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobFactory::new);
      this.register(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionParticle.Factory());
      this.register(ParticleTypes.EXPLOSION, LargeExplosionParticle.Factory::new);
      this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Factory::new);
      this.register(ParticleTypes.FIREWORK, FireworkParticle.SparkFactory::new);
      this.register(ParticleTypes.FISHING, WaterWakeParticle.Factory::new);
      this.register(ParticleTypes.FLAME, FlameParticle.Factory::new);
      this.register(ParticleTypes.SOUL, SoulParticle.Factory::new);
      this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
      this.register(ParticleTypes.FLASH, FireworkParticle.OverlayFactory::new);
      this.register(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerFactory::new);
      this.register(ParticleTypes.HEART, HeartParticle.Factory::new);
      this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
      this.register(ParticleTypes.ITEM, new BreakingParticle.Factory());
      this.register(ParticleTypes.ITEM_SLIME, new BreakingParticle.SlimeFactory());
      this.register(ParticleTypes.ITEM_SNOWBALL, new BreakingParticle.SnowballFactory());
      this.register(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Factory::new);
      this.register(ParticleTypes.LAVA, LavaParticle.Factory::new);
      this.register(ParticleTypes.MYCELIUM, SuspendedTownParticle.Factory::new);
      this.register(ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusFactory::new);
      this.register(ParticleTypes.NOTE, NoteParticle.Factory::new);
      this.register(ParticleTypes.POOF, PoofParticle.Factory::new);
      this.register(ParticleTypes.PORTAL, PortalParticle.Factory::new);
      this.register(ParticleTypes.RAIN, RainParticle.Factory::new);
      this.register(ParticleTypes.SMOKE, SmokeParticle.Factory::new);
      this.register(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
      this.register(ParticleTypes.SPIT, SpitParticle.Factory::new);
      this.register(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
      this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemOfUndyingParticle.Factory::new);
      this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
      this.register(ParticleTypes.UNDERWATER, UnderwaterParticle.UnderwaterFactory::new);
      this.register(ParticleTypes.SPLASH, SplashParticle.Factory::new);
      this.register(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
      this.register(ParticleTypes.DRIPPING_HONEY, DripParticle.DrippingHoneyFactory::new);
      this.register(ParticleTypes.FALLING_HONEY, DripParticle.FallingHoneyFactory::new);
      this.register(ParticleTypes.LANDING_HONEY, DripParticle.LandingHoneyFactory::new);
      this.register(ParticleTypes.FALLING_NECTAR, DripParticle.FallingNectarFactory::new);
      this.register(ParticleTypes.ASH, AshParticle.Factory::new);
      this.register(ParticleTypes.CRIMSON_SPORE, UnderwaterParticle.CrimsonSporeFactory::new);
      this.register(ParticleTypes.WARPED_SPORE, UnderwaterParticle.WarpedSporeFactory::new);
      this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.DrippingObsidianTearFactory::new);
      this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.FallingObsidianTearFactory::new);
      this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.LandingObsidianTearFactory::new);
      this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
      this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
   }

   public <T extends IParticleData> void register(ParticleType<T> p_199283_1_, IParticleFactory<T> p_199283_2_) {
      this.providers.put(Registry.PARTICLE_TYPE.getKey(p_199283_1_), p_199283_2_);
   }

   public <T extends IParticleData> void register(ParticleType<T> p_215234_1_, ParticleManager.IParticleMetaFactory<T> p_215234_2_) {
      ParticleManager.AnimatedSpriteImpl particlemanager$animatedspriteimpl = new ParticleManager.AnimatedSpriteImpl();
      this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(p_215234_1_), particlemanager$animatedspriteimpl);
      this.providers.put(Registry.PARTICLE_TYPE.getKey(p_215234_1_), p_215234_2_.create(particlemanager$animatedspriteimpl));
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      Map<ResourceLocation, List<ResourceLocation>> map = Maps.newConcurrentMap();
      CompletableFuture<?>[] completablefuture = Registry.PARTICLE_TYPE.keySet().stream().map((p_215228_4_) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadParticleDescription(p_215226_2_, p_215228_4_, map);
         }, p_215226_5_);
      }).toArray((p_215239_0_) -> {
         return new CompletableFuture[p_215239_0_];
      });
      return CompletableFuture.allOf(completablefuture).thenApplyAsync((p_228344_4_) -> {
         p_215226_3_.startTick();
         p_215226_3_.push("stitching");
         AtlasTexture.SheetData atlastexture$sheetdata = this.textureAtlas.prepareToStitch(p_215226_2_, map.values().stream().flatMap(Collection::stream), p_215226_3_, 0);
         p_215226_3_.pop();
         p_215226_3_.endTick();
         return atlastexture$sheetdata;
      }, p_215226_5_).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_215229_3_) -> {
         this.particles.clear();
         p_215226_4_.startTick();
         p_215226_4_.push("upload");
         this.textureAtlas.reload(p_215229_3_);
         p_215226_4_.popPush("bindSpriteSets");
         TextureAtlasSprite textureatlassprite = this.textureAtlas.getSprite(MissingTextureSprite.getLocation());
         map.forEach((p_215227_2_, p_215227_3_) -> {
            ImmutableList<TextureAtlasSprite> immutablelist = p_215227_3_.isEmpty() ? ImmutableList.of(textureatlassprite) : p_215227_3_.stream().map(this.textureAtlas::getSprite).collect(ImmutableList.toImmutableList());
            this.spriteSets.get(p_215227_2_).rebind(immutablelist);
         });
         p_215226_4_.pop();
         p_215226_4_.endTick();
      }, p_215226_6_);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private void loadParticleDescription(IResourceManager p_215236_1_, ResourceLocation p_215236_2_, Map<ResourceLocation, List<ResourceLocation>> p_215236_3_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_215236_2_.getNamespace(), "particles/" + p_215236_2_.getPath() + ".json");

      try (
         IResource iresource = p_215236_1_.getResource(resourcelocation);
         Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
      ) {
         TexturesParticle texturesparticle = TexturesParticle.fromJson(JSONUtils.parse(reader));
         List<ResourceLocation> list = texturesparticle.getTextures();
         boolean flag = this.spriteSets.containsKey(p_215236_2_);
         if (list == null) {
            if (flag) {
               throw new IllegalStateException("Missing texture list for particle " + p_215236_2_);
            }
         } else {
            if (!flag) {
               throw new IllegalStateException("Redundant texture list for particle " + p_215236_2_);
            }

            p_215236_3_.put(p_215236_2_, list.stream().map((p_228349_0_) -> {
               return new ResourceLocation(p_228349_0_.getNamespace(), "particle/" + p_228349_0_.getPath());
            }).collect(Collectors.toList()));
         }

      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load description for particle " + p_215236_2_, ioexception);
      }
   }

   public void createTrackingEmitter(Entity p_199282_1_, IParticleData p_199282_2_) {
      this.trackingEmitters.add(new EmitterParticle(this.level, p_199282_1_, p_199282_2_));
   }

   public void createTrackingEmitter(Entity p_199281_1_, IParticleData p_199281_2_, int p_199281_3_) {
      this.trackingEmitters.add(new EmitterParticle(this.level, p_199281_1_, p_199281_2_, p_199281_3_));
   }

   @Nullable
   public Particle createParticle(IParticleData p_199280_1_, double p_199280_2_, double p_199280_4_, double p_199280_6_, double p_199280_8_, double p_199280_10_, double p_199280_12_) {
      Particle particle = this.makeParticle(p_199280_1_, p_199280_2_, p_199280_4_, p_199280_6_, p_199280_8_, p_199280_10_, p_199280_12_);
      if (particle != null) {
         this.add(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends IParticleData> Particle makeParticle(T p_199927_1_, double p_199927_2_, double p_199927_4_, double p_199927_6_, double p_199927_8_, double p_199927_10_, double p_199927_12_) {
      IParticleFactory<T> iparticlefactory = (IParticleFactory<T>)this.providers.get(Registry.PARTICLE_TYPE.getKey(p_199927_1_.getType()));
      return iparticlefactory == null ? null : iparticlefactory.createParticle(p_199927_1_, this.level, p_199927_2_, p_199927_4_, p_199927_6_, p_199927_8_, p_199927_10_, p_199927_12_);
   }

   public void add(Particle p_78873_1_) {
      this.particlesToAdd.add(p_78873_1_);
   }

   public void tick() {
      this.particles.forEach((p_228347_1_, p_228347_2_) -> {
         this.level.getProfiler().push(p_228347_1_.toString());
         this.tickParticleList(p_228347_2_);
         this.level.getProfiler().pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         List<EmitterParticle> list = Lists.newArrayList();

         for(EmitterParticle emitterparticle : this.trackingEmitters) {
            emitterparticle.tick();
            if (!emitterparticle.isAlive()) {
               list.add(emitterparticle);
            }
         }

         this.trackingEmitters.removeAll(list);
      }

      Particle particle;
      if (!this.particlesToAdd.isEmpty()) {
         while((particle = this.particlesToAdd.poll()) != null) {
            this.particles.computeIfAbsent(particle.getRenderType(), (p_228346_0_) -> {
               return EvictingQueue.create(16384);
            }).add(particle);
         }
      }

   }

   private void tickParticleList(Collection<Particle> p_187240_1_) {
      if (!p_187240_1_.isEmpty()) {
         Iterator<Particle> iterator = p_187240_1_.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               iterator.remove();
            }
         }
      }

   }

   private void tickParticle(Particle p_178923_1_) {
      try {
         p_178923_1_.tick();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
         crashreportcategory.setDetail("Particle", p_178923_1_::toString);
         crashreportcategory.setDetail("Particle Type", p_178923_1_.getRenderType()::toString);
         throw new ReportedException(crashreport);
      }
   }

   /**@deprecated Forge: use {@link #renderParticles(MatrixStack, IRenderTypeBuffer.Impl, LightTexture, ActiveRenderInfo, float, net.minecraft.client.renderer.culling.ClippingHelper)} with ClippingHelper as additional parameter*/
   @Deprecated
   public void render(MatrixStack p_228345_1_, IRenderTypeBuffer.Impl p_228345_2_, LightTexture p_228345_3_, ActiveRenderInfo p_228345_4_, float p_228345_5_) {
      renderParticles(p_228345_1_, p_228345_2_, p_228345_3_, p_228345_4_, p_228345_5_, null);
   }

   public void renderParticles(MatrixStack p_228345_1_, IRenderTypeBuffer.Impl p_228345_2_, LightTexture p_228345_3_, ActiveRenderInfo p_228345_4_, float p_228345_5_, @Nullable net.minecraft.client.renderer.culling.ClippingHelper clippingHelper) {
      p_228345_3_.turnOnLightLayer();
      Runnable enable = () -> {
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableDepthTest();
      RenderSystem.enableFog();
         RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
         RenderSystem.enableTexture();
         RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
      };
      RenderSystem.pushMatrix();
      RenderSystem.multMatrix(p_228345_1_.last().pose());

      for(IParticleRenderType iparticlerendertype : this.particles.keySet()) { // Forge: allow custom IParticleRenderType's
         if (iparticlerendertype == IParticleRenderType.NO_RENDER) continue;
         enable.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
         Iterable<Particle> iterable = this.particles.get(iparticlerendertype);
         if (iterable != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            iparticlerendertype.begin(bufferbuilder, this.textureManager);

            for(Particle particle : iterable) {
               if (clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox())) continue;
               try {
                  particle.render(bufferbuilder, p_228345_4_, p_228345_5_);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                  crashreportcategory.setDetail("Particle", particle::toString);
                  crashreportcategory.setDetail("Particle Type", iparticlerendertype::toString);
                  throw new ReportedException(crashreport);
               }
            }

            iparticlerendertype.end(tessellator);
         }
      }

      RenderSystem.popMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
      RenderSystem.disableBlend();
      RenderSystem.defaultAlphaFunc();
      p_228345_3_.turnOffLightLayer();
      RenderSystem.disableFog();
   }

   public void setLevel(@Nullable ClientWorld p_78870_1_) {
      this.level = p_78870_1_;
      this.particles.clear();
      this.trackingEmitters.clear();
   }

   public void destroy(BlockPos p_180533_1_, BlockState p_180533_2_) {
      if (!p_180533_2_.isAir(this.level, p_180533_1_) && !p_180533_2_.addDestroyEffects(this.level, p_180533_1_, this)) {
         VoxelShape voxelshape = p_180533_2_.getShape(this.level, p_180533_1_);
         double d0 = 0.25D;
         voxelshape.forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
            double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
            double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
            double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 < j; ++i1) {
                  for(int j1 = 0; j1 < k; ++j1) {
                     double d4 = ((double)l + 0.5D) / (double)i;
                     double d5 = ((double)i1 + 0.5D) / (double)j;
                     double d6 = ((double)j1 + 0.5D) / (double)k;
                     double d7 = d4 * d1 + p_228348_3_;
                     double d8 = d5 * d2 + p_228348_5_;
                     double d9 = d6 * d3 + p_228348_7_;
                     this.add((new DiggingParticle(this.level, (double)p_180533_1_.getX() + d7, (double)p_180533_1_.getY() + d8, (double)p_180533_1_.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, p_180533_2_)).init(p_180533_1_));
                  }
               }
            }

         });
      }
   }

   public void crack(BlockPos p_180532_1_, Direction p_180532_2_) {
      BlockState blockstate = this.level.getBlockState(p_180532_1_);
      if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
         int i = p_180532_1_.getX();
         int j = p_180532_1_.getY();
         int k = p_180532_1_.getZ();
         float f = 0.1F;
         AxisAlignedBB axisalignedbb = blockstate.getShape(this.level, p_180532_1_).bounds();
         double d0 = (double)i + this.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2F) + (double)0.1F + axisalignedbb.minX;
         double d1 = (double)j + this.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2F) + (double)0.1F + axisalignedbb.minY;
         double d2 = (double)k + this.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2F) + (double)0.1F + axisalignedbb.minZ;
         if (p_180532_2_ == Direction.DOWN) {
            d1 = (double)j + axisalignedbb.minY - (double)0.1F;
         }

         if (p_180532_2_ == Direction.UP) {
            d1 = (double)j + axisalignedbb.maxY + (double)0.1F;
         }

         if (p_180532_2_ == Direction.NORTH) {
            d2 = (double)k + axisalignedbb.minZ - (double)0.1F;
         }

         if (p_180532_2_ == Direction.SOUTH) {
            d2 = (double)k + axisalignedbb.maxZ + (double)0.1F;
         }

         if (p_180532_2_ == Direction.WEST) {
            d0 = (double)i + axisalignedbb.minX - (double)0.1F;
         }

         if (p_180532_2_ == Direction.EAST) {
            d0 = (double)i + axisalignedbb.maxX + (double)0.1F;
         }

         this.add((new DiggingParticle(this.level, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate)).init(p_180532_1_).setPower(0.2F).scale(0.6F));
      }
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   public void addBlockHitEffects(BlockPos pos, net.minecraft.util.math.BlockRayTraceResult target) {
      BlockState state = level.getBlockState(pos);
      if (!state.addHitEffects(level, target, this))
         crack(pos, target.getDirection());
   }

   @OnlyIn(Dist.CLIENT)
   class AnimatedSpriteImpl implements IAnimatedSprite {
      private List<TextureAtlasSprite> sprites;

      private AnimatedSpriteImpl() {
      }

      public TextureAtlasSprite get(int p_217591_1_, int p_217591_2_) {
         return this.sprites.get(p_217591_1_ * (this.sprites.size() - 1) / p_217591_2_);
      }

      public TextureAtlasSprite get(Random p_217590_1_) {
         return this.sprites.get(p_217590_1_.nextInt(this.sprites.size()));
      }

      public void rebind(List<TextureAtlasSprite> p_217592_1_) {
         this.sprites = ImmutableList.copyOf(p_217592_1_);
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface IParticleMetaFactory<T extends IParticleData> {
      IParticleFactory<T> create(IAnimatedSprite p_create_1_);
   }
}
