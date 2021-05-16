package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.FireworkParticle;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientWorld extends World {
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
   private final ClientPlayNetHandler connection;
   private final WorldRenderer levelRenderer;
   private final ClientWorld.ClientWorldInfo clientLevelData;
   private final DimensionRenderInfo effects;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
   private Scoreboard scoreboard = new Scoreboard();
   private final Map<String, MapData> mapData = Maps.newHashMap();
   private int skyFlashTime;
   private final Object2ObjectArrayMap<ColorResolver, ColorCache> tintCaches = Util.make(new Object2ObjectArrayMap<>(3), (p_228319_0_) -> {
      p_228319_0_.put(BiomeColors.GRASS_COLOR_RESOLVER, new ColorCache());
      p_228319_0_.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new ColorCache());
      p_228319_0_.put(BiomeColors.WATER_COLOR_RESOLVER, new ColorCache());
   });
   private final ClientChunkProvider chunkSource;

   public ClientWorld(ClientPlayNetHandler p_i242067_1_, ClientWorld.ClientWorldInfo p_i242067_2_, RegistryKey<World> p_i242067_3_, DimensionType p_i242067_4_, int p_i242067_5_, Supplier<IProfiler> p_i242067_6_, WorldRenderer p_i242067_7_, boolean p_i242067_8_, long p_i242067_9_) {
      super(p_i242067_2_, p_i242067_3_, p_i242067_4_, p_i242067_6_, true, p_i242067_8_, p_i242067_9_);
      this.connection = p_i242067_1_;
      this.chunkSource = new ClientChunkProvider(this, p_i242067_5_);
      this.clientLevelData = p_i242067_2_;
      this.levelRenderer = p_i242067_7_;
      this.effects = DimensionRenderInfo.forType(p_i242067_4_);
      this.setDefaultSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.gatherCapabilities();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
   }

   public DimensionRenderInfo effects() {
      return this.effects;
   }

   public void tick(BooleanSupplier p_72835_1_) {
      this.getWorldBorder().tick();
      this.tickTime();
      this.getProfiler().push("blocks");
      this.chunkSource.tick(p_72835_1_);
      this.getProfiler().pop();
   }

   private void tickTime() {
      this.setGameTime(this.levelData.getGameTime() + 1L);
      if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
         this.setDayTime(this.levelData.getDayTime() + 1L);
      }

   }

   public void setGameTime(long p_239134_1_) {
      this.clientLevelData.setGameTime(p_239134_1_);
   }

   public void setDayTime(long p_72877_1_) {
      if (p_72877_1_ < 0L) {
         p_72877_1_ = -p_72877_1_;
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
      } else {
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, (MinecraftServer)null);
      }

      this.clientLevelData.setDayTime(p_72877_1_);
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.entitiesById.values();
   }

   public void tickEntities() {
      IProfiler iprofiler = this.getProfiler();
      iprofiler.push("entities");
      ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Entry<Entity> entry = objectiterator.next();
         Entity entity = entry.getValue();
         if (!entity.isPassenger()) {
            iprofiler.push("tick");
            if (!entity.removed) {
               this.guardEntityTick(this::tickNonPassenger, entity);
            }

            iprofiler.pop();
            iprofiler.push("remove");
            if (entity.removed) {
               objectiterator.remove();
               this.onEntityRemoved(entity);
            }

            iprofiler.pop();
         }
      }

      this.tickBlockEntities();
      iprofiler.pop();
   }

   public void tickNonPassenger(Entity p_217418_1_) {
      if (!(p_217418_1_ instanceof PlayerEntity) && !this.getChunkSource().isEntityTickingChunk(p_217418_1_)) {
         this.updateChunkPos(p_217418_1_);
      } else {
         p_217418_1_.setPosAndOldPos(p_217418_1_.getX(), p_217418_1_.getY(), p_217418_1_.getZ());
         p_217418_1_.yRotO = p_217418_1_.yRot;
         p_217418_1_.xRotO = p_217418_1_.xRot;
         if (p_217418_1_.inChunk || p_217418_1_.isSpectator()) {
            ++p_217418_1_.tickCount;
            this.getProfiler().push(() -> {
               return Registry.ENTITY_TYPE.getKey(p_217418_1_.getType()).toString();
            });
            if (p_217418_1_.canUpdate())
            p_217418_1_.tick();
            this.getProfiler().pop();
         }

         this.updateChunkPos(p_217418_1_);
         if (p_217418_1_.inChunk) {
            for(Entity entity : p_217418_1_.getPassengers()) {
               this.tickPassenger(p_217418_1_, entity);
            }
         }

      }
   }

   public void tickPassenger(Entity p_217420_1_, Entity p_217420_2_) {
      if (!p_217420_2_.removed && p_217420_2_.getVehicle() == p_217420_1_) {
         if (p_217420_2_ instanceof PlayerEntity || this.getChunkSource().isEntityTickingChunk(p_217420_2_)) {
            p_217420_2_.setPosAndOldPos(p_217420_2_.getX(), p_217420_2_.getY(), p_217420_2_.getZ());
            p_217420_2_.yRotO = p_217420_2_.yRot;
            p_217420_2_.xRotO = p_217420_2_.xRot;
            if (p_217420_2_.inChunk) {
               ++p_217420_2_.tickCount;
               p_217420_2_.rideTick();
            }

            this.updateChunkPos(p_217420_2_);
            if (p_217420_2_.inChunk) {
               for(Entity entity : p_217420_2_.getPassengers()) {
                  this.tickPassenger(p_217420_2_, entity);
               }
            }

         }
      } else {
         p_217420_2_.stopRiding();
      }
   }

   private void updateChunkPos(Entity p_217423_1_) {
      if (p_217423_1_.checkAndResetUpdateChunkPos()) {
         this.getProfiler().push("chunkCheck");
         int i = MathHelper.floor(p_217423_1_.getX() / 16.0D);
         int j = MathHelper.floor(p_217423_1_.getY() / 16.0D);
         int k = MathHelper.floor(p_217423_1_.getZ() / 16.0D);
         if (!p_217423_1_.inChunk || p_217423_1_.xChunk != i || p_217423_1_.yChunk != j || p_217423_1_.zChunk != k) {
            if (p_217423_1_.inChunk && this.hasChunk(p_217423_1_.xChunk, p_217423_1_.zChunk)) {
               this.getChunk(p_217423_1_.xChunk, p_217423_1_.zChunk).removeEntity(p_217423_1_, p_217423_1_.yChunk);
            }

            if (!p_217423_1_.checkAndResetForcedChunkAdditionFlag() && !this.hasChunk(i, k)) {
               if (p_217423_1_.inChunk) {
                  LOGGER.warn("Entity {} left loaded chunk area", (Object)p_217423_1_);
               }

               p_217423_1_.inChunk = false;
            } else {
               this.getChunk(i, k).addEntity(p_217423_1_);
            }
         }

         this.getProfiler().pop();
      }
   }

   public void unload(Chunk p_217409_1_) {
      this.blockEntitiesToUnload.addAll(p_217409_1_.getBlockEntities().values());
      this.chunkSource.getLightEngine().enableLightSources(p_217409_1_.getPos(), false);
   }

   public void onChunkLoaded(int p_228323_1_, int p_228323_2_) {
      this.tintCaches.forEach((p_228316_2_, p_228316_3_) -> {
         p_228316_3_.invalidateForChunk(p_228323_1_, p_228323_2_);
      });
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((p_228320_0_, p_228320_1_) -> {
         p_228320_1_.invalidateAll();
      });
   }

   public boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return true;
   }

   public int getEntityCount() {
      return this.entitiesById.size();
   }

   public void addPlayer(int p_217408_1_, AbstractClientPlayerEntity p_217408_2_) {
      this.addEntity(p_217408_1_, p_217408_2_);
      this.players.add(p_217408_2_);
   }

   public void putNonPlayerEntity(int p_217411_1_, Entity p_217411_2_) {
      this.addEntity(p_217411_1_, p_217411_2_);
   }

   private void addEntity(int p_217424_1_, Entity p_217424_2_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_217424_2_, this))) return;
      this.removeEntity(p_217424_1_);
      this.entitiesById.put(p_217424_1_, p_217424_2_);
      this.getChunkSource().getChunk(MathHelper.floor(p_217424_2_.getX() / 16.0D), MathHelper.floor(p_217424_2_.getZ() / 16.0D), ChunkStatus.FULL, true).addEntity(p_217424_2_);
      p_217424_2_.onAddedToWorld();
   }

   public void removeEntity(int p_217413_1_) {
      Entity entity = this.entitiesById.remove(p_217413_1_);
      if (entity != null) {
         entity.remove();
         this.onEntityRemoved(entity);
      }

   }

   private void onEntityRemoved(Entity p_217414_1_) {
      p_217414_1_.unRide();
      if (p_217414_1_.inChunk) {
         this.getChunk(p_217414_1_.xChunk, p_217414_1_.zChunk).removeEntity(p_217414_1_);
      }

      this.players.remove(p_217414_1_);
      p_217414_1_.onRemovedFromWorld();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveWorldEvent(p_217414_1_, this));
   }

   public void reAddEntitiesToChunk(Chunk p_217417_1_) {
      for(Entry<Entity> entry : this.entitiesById.int2ObjectEntrySet()) {
         Entity entity = entry.getValue();
         int i = MathHelper.floor(entity.getX() / 16.0D);
         int j = MathHelper.floor(entity.getZ() / 16.0D);
         if (i == p_217417_1_.getPos().x && j == p_217417_1_.getPos().z) {
            p_217417_1_.addEntity(entity);
         }
      }

   }

   @Nullable
   public Entity getEntity(int p_73045_1_) {
      return this.entitiesById.get(p_73045_1_);
   }

   public void setKnownState(BlockPos p_195597_1_, BlockState p_195597_2_) {
      this.setBlock(p_195597_1_, p_195597_2_, 19);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect(new TranslationTextComponent("multiplayer.status.quitting"));
   }

   public void animateTick(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
      int i = 32;
      Random random = new Random();
      boolean flag = false;
      if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
         for(ItemStack itemstack : this.minecraft.player.getHandSlots()) {
            if (itemstack.getItem() == Blocks.BARRIER.asItem()) {
               flag = true;
               break;
            }
         }
      }

      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j = 0; j < 667; ++j) {
         this.doAnimateTick(p_73029_1_, p_73029_2_, p_73029_3_, 16, random, flag, blockpos$mutable);
         this.doAnimateTick(p_73029_1_, p_73029_2_, p_73029_3_, 32, random, flag, blockpos$mutable);
      }

   }

   public void doAnimateTick(int p_184153_1_, int p_184153_2_, int p_184153_3_, int p_184153_4_, Random p_184153_5_, boolean p_184153_6_, BlockPos.Mutable p_184153_7_) {
      int i = p_184153_1_ + this.random.nextInt(p_184153_4_) - this.random.nextInt(p_184153_4_);
      int j = p_184153_2_ + this.random.nextInt(p_184153_4_) - this.random.nextInt(p_184153_4_);
      int k = p_184153_3_ + this.random.nextInt(p_184153_4_) - this.random.nextInt(p_184153_4_);
      p_184153_7_.set(i, j, k);
      BlockState blockstate = this.getBlockState(p_184153_7_);
      blockstate.getBlock().animateTick(blockstate, this, p_184153_7_, p_184153_5_);
      FluidState fluidstate = this.getFluidState(p_184153_7_);
      if (!fluidstate.isEmpty()) {
         fluidstate.animateTick(this, p_184153_7_, p_184153_5_);
         IParticleData iparticledata = fluidstate.getDripParticle();
         if (iparticledata != null && this.random.nextInt(10) == 0) {
            boolean flag = blockstate.isFaceSturdy(this, p_184153_7_, Direction.DOWN);
            BlockPos blockpos = p_184153_7_.below();
            this.trySpawnDripParticles(blockpos, this.getBlockState(blockpos), iparticledata, flag);
         }
      }

      if (p_184153_6_ && blockstate.is(Blocks.BARRIER)) {
         this.addParticle(ParticleTypes.BARRIER, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 0.0D, 0.0D, 0.0D);
      }

      if (!blockstate.isCollisionShapeFullBlock(this, p_184153_7_)) {
         this.getBiome(p_184153_7_).getAmbientParticle().ifPresent((p_239135_2_) -> {
            if (p_239135_2_.canSpawn(this.random)) {
               this.addParticle(p_239135_2_.getOptions(), (double)p_184153_7_.getX() + this.random.nextDouble(), (double)p_184153_7_.getY() + this.random.nextDouble(), (double)p_184153_7_.getZ() + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

         });
      }

   }

   private void trySpawnDripParticles(BlockPos p_211530_1_, BlockState p_211530_2_, IParticleData p_211530_3_, boolean p_211530_4_) {
      if (p_211530_2_.getFluidState().isEmpty()) {
         VoxelShape voxelshape = p_211530_2_.getCollisionShape(this, p_211530_1_);
         double d0 = voxelshape.max(Direction.Axis.Y);
         if (d0 < 1.0D) {
            if (p_211530_4_) {
               this.spawnFluidParticle((double)p_211530_1_.getX(), (double)(p_211530_1_.getX() + 1), (double)p_211530_1_.getZ(), (double)(p_211530_1_.getZ() + 1), (double)(p_211530_1_.getY() + 1) - 0.05D, p_211530_3_);
            }
         } else if (!p_211530_2_.is(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.min(Direction.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_211530_1_.below();
               BlockState blockstate = this.getBlockState(blockpos);
               VoxelShape voxelshape1 = blockstate.getCollisionShape(this, blockpos);
               double d2 = voxelshape1.max(Direction.Axis.Y);
               if (d2 < 1.0D && blockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos p_211835_1_, IParticleData p_211835_2_, VoxelShape p_211835_3_, double p_211835_4_) {
      this.spawnFluidParticle((double)p_211835_1_.getX() + p_211835_3_.min(Direction.Axis.X), (double)p_211835_1_.getX() + p_211835_3_.max(Direction.Axis.X), (double)p_211835_1_.getZ() + p_211835_3_.min(Direction.Axis.Z), (double)p_211835_1_.getZ() + p_211835_3_.max(Direction.Axis.Z), p_211835_4_, p_211835_2_);
   }

   private void spawnFluidParticle(double p_211834_1_, double p_211834_3_, double p_211834_5_, double p_211834_7_, double p_211834_9_, IParticleData p_211834_11_) {
      this.addParticle(p_211834_11_, MathHelper.lerp(this.random.nextDouble(), p_211834_1_, p_211834_3_), p_211834_9_, MathHelper.lerp(this.random.nextDouble(), p_211834_5_, p_211834_7_), 0.0D, 0.0D, 0.0D);
   }

   public void removeAllPendingEntityRemovals() {
      ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Entry<Entity> entry = objectiterator.next();
         Entity entity = entry.getValue();
         if (entity.removed) {
            objectiterator.remove();
            this.onEntityRemoved(entity);
         }
      }

   }

   public CrashReportCategory fillReportDetails(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = super.fillReportDetails(p_72914_1_);
      crashreportcategory.setDetail("Server brand", () -> {
         return this.minecraft.player.getServerBrand();
      });
      crashreportcategory.setDetail("Server type", () -> {
         return this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return crashreportcategory;
   }

   public void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_184148_1_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_184148_8_ = event.getSound();
      p_184148_9_ = event.getCategory();
      p_184148_10_ = event.getVolume();
      if (p_184148_1_ == this.minecraft.player) {
         this.playLocalSound(p_184148_2_, p_184148_4_, p_184148_6_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_, false);
      }

   }

   public void playSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_217384_1_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_217384_3_ = event.getSound();
      p_217384_4_ = event.getCategory();
      p_217384_5_ = event.getVolume();
      if (p_217384_1_ == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityTickableSound(p_217384_3_, p_217384_4_, p_217384_2_));
      }

   }

   public void playLocalSound(BlockPos p_184156_1_, SoundEvent p_184156_2_, SoundCategory p_184156_3_, float p_184156_4_, float p_184156_5_, boolean p_184156_6_) {
      this.playLocalSound((double)p_184156_1_.getX() + 0.5D, (double)p_184156_1_.getY() + 0.5D, (double)p_184156_1_.getZ() + 0.5D, p_184156_2_, p_184156_3_, p_184156_4_, p_184156_5_, p_184156_6_);
   }

   public void playLocalSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
      double d0 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(p_184134_1_, p_184134_3_, p_184134_5_);
      SimpleSound simplesound = new SimpleSound(p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, p_184134_1_, p_184134_3_, p_184134_5_);
      if (p_184134_11_ && d0 > 100.0D) {
         double d1 = Math.sqrt(d0) / 40.0D;
         this.minecraft.getSoundManager().playDelayed(simplesound, (int)(d1 * 20.0D));
      } else {
         this.minecraft.getSoundManager().play(simplesound);
      }

   }

   public void createFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable CompoundNBT p_92088_13_) {
      this.minecraft.particleEngine.add(new FireworkParticle.Starter(this, p_92088_1_, p_92088_3_, p_92088_5_, p_92088_7_, p_92088_9_, p_92088_11_, this.minecraft.particleEngine, p_92088_13_));
   }

   public void sendPacketToServer(IPacket<?> p_184135_1_) {
      this.connection.send(p_184135_1_);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard p_96443_1_) {
      this.scoreboard = p_96443_1_;
   }

   public ITickList<Block> getBlockTicks() {
      return EmptyTickList.empty();
   }

   public ITickList<Fluid> getLiquidTicks() {
      return EmptyTickList.empty();
   }

   public ClientChunkProvider getChunkSource() {
      return this.chunkSource;
   }

   @Nullable
   public MapData getMapData(String p_217406_1_) {
      return this.mapData.get(p_217406_1_);
   }

   public void setMapData(MapData p_217399_1_) {
      this.mapData.put(p_217399_1_.getId(), p_217399_1_);
   }

   public int getFreeMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public ITagCollectionSupplier getTagManager() {
      return this.connection.getTags();
   }

   public DynamicRegistries registryAccess() {
      return this.connection.registryAccess();
   }

   public void sendBlockUpdated(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_) {
      this.levelRenderer.blockChanged(this, p_184138_1_, p_184138_2_, p_184138_3_, p_184138_4_);
   }

   public void setBlocksDirty(BlockPos p_225319_1_, BlockState p_225319_2_, BlockState p_225319_3_) {
      this.levelRenderer.setBlockDirty(p_225319_1_, p_225319_2_, p_225319_3_);
   }

   public void setSectionDirtyWithNeighbors(int p_217427_1_, int p_217427_2_, int p_217427_3_) {
      this.levelRenderer.setSectionDirtyWithNeighbors(p_217427_1_, p_217427_2_, p_217427_3_);
   }

   public void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
      this.levelRenderer.destroyBlockProgress(p_175715_1_, p_175715_2_, p_175715_3_);
   }

   public void globalLevelEvent(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
      this.levelRenderer.globalLevelEvent(p_175669_1_, p_175669_2_, p_175669_3_);
   }

   public void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
      try {
         this.levelRenderer.levelEvent(p_217378_1_, p_217378_2_, p_217378_3_, p_217378_4_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Playing level event");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Level event being played");
         crashreportcategory.setDetail("Block coordinates", CrashReportCategory.formatLocation(p_217378_3_));
         crashreportcategory.setDetail("Event source", p_217378_1_);
         crashreportcategory.setDetail("Event type", p_217378_2_);
         crashreportcategory.setDetail("Event data", p_217378_4_);
         throw new ReportedException(crashreport);
      }
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
      this.levelRenderer.addParticle(p_195594_1_, p_195594_1_.getType().getOverrideLimiter(), p_195594_2_, p_195594_4_, p_195594_6_, p_195594_8_, p_195594_10_, p_195594_12_);
   }

   public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
      this.levelRenderer.addParticle(p_195590_1_, p_195590_1_.getType().getOverrideLimiter() || p_195590_2_, p_195590_3_, p_195590_5_, p_195590_7_, p_195590_9_, p_195590_11_, p_195590_13_);
   }

   public void addAlwaysVisibleParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
      this.levelRenderer.addParticle(p_195589_1_, false, true, p_195589_2_, p_195589_4_, p_195589_6_, p_195589_8_, p_195589_10_, p_195589_12_);
   }

   public void addAlwaysVisibleParticle(IParticleData p_217404_1_, boolean p_217404_2_, double p_217404_3_, double p_217404_5_, double p_217404_7_, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
      this.levelRenderer.addParticle(p_217404_1_, p_217404_1_.getType().getOverrideLimiter() || p_217404_2_, true, p_217404_3_, p_217404_5_, p_217404_7_, p_217404_9_, p_217404_11_, p_217404_13_);
   }

   public List<AbstractClientPlayerEntity> players() {
      return this.players;
   }

   public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
   }

   public float getSkyDarken(float p_228326_1_) {
      float f = this.getTimeOfDay(p_228326_1_);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainLevel(p_228326_1_) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderLevel(p_228326_1_) * 5.0F) / 16.0D));
      return f1 * 0.8F + 0.2F;
   }

   public Vector3d getSkyColor(BlockPos p_228318_1_, float p_228318_2_) {
      float f = this.getTimeOfDay(p_228318_2_);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      Biome biome = this.getBiome(p_228318_1_);
      int i = biome.getSkyColor();
      float f2 = (float)(i >> 16 & 255) / 255.0F;
      float f3 = (float)(i >> 8 & 255) / 255.0F;
      float f4 = (float)(i & 255) / 255.0F;
      f2 = f2 * f1;
      f3 = f3 * f1;
      f4 = f4 * f1;
      float f5 = this.getRainLevel(p_228318_2_);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.75F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      float f9 = this.getThunderLevel(p_228318_2_);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.75F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      if (this.skyFlashTime > 0) {
         float f11 = (float)this.skyFlashTime - p_228318_2_;
         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         f11 = f11 * 0.45F;
         f2 = f2 * (1.0F - f11) + 0.8F * f11;
         f3 = f3 * (1.0F - f11) + 0.8F * f11;
         f4 = f4 * (1.0F - f11) + 1.0F * f11;
      }

      return new Vector3d((double)f2, (double)f3, (double)f4);
   }

   public Vector3d getCloudColor(float p_228328_1_) {
      float f = this.getTimeOfDay(p_228328_1_);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = this.getRainLevel(p_228328_1_);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.95F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      f2 = f2 * (f1 * 0.9F + 0.1F);
      f3 = f3 * (f1 * 0.9F + 0.1F);
      f4 = f4 * (f1 * 0.85F + 0.15F);
      float f9 = this.getThunderLevel(p_228328_1_);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.95F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vector3d((double)f2, (double)f3, (double)f4);
   }

   public float getStarBrightness(float p_228330_1_) {
      float f = this.getTimeOfDay(p_228330_1_);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      return f1 * f1 * 0.5F;
   }

   public int getSkyFlashTime() {
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int p_225605_1_) {
      this.skyFlashTime = p_225605_1_;
   }

   public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
      boolean flag = this.effects().constantAmbientLight();
      if (!p_230487_2_) {
         return flag ? 0.9F : 1.0F;
      } else {
         switch(p_230487_1_) {
         case DOWN:
            return flag ? 0.9F : 0.5F;
         case UP:
            return flag ? 0.9F : 1.0F;
         case NORTH:
         case SOUTH:
            return 0.8F;
         case WEST:
         case EAST:
            return 0.6F;
         default:
            return 1.0F;
         }
      }
   }

   public int getBlockTint(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      ColorCache colorcache = this.tintCaches.get(p_225525_2_);
      return colorcache.getColor(p_225525_1_, () -> {
         return this.calculateBlockTint(p_225525_1_, p_225525_2_);
      });
   }

   public int calculateBlockTint(BlockPos p_228321_1_, ColorResolver p_228321_2_) {
      int i = Minecraft.getInstance().options.biomeBlendRadius;
      if (i == 0) {
         return p_228321_2_.getColor(this.getBiome(p_228321_1_), (double)p_228321_1_.getX(), (double)p_228321_1_.getZ());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int i1 = 0;
         CubeCoordinateIterator cubecoordinateiterator = new CubeCoordinateIterator(p_228321_1_.getX() - i, p_228321_1_.getY(), p_228321_1_.getZ() - i, p_228321_1_.getX() + i, p_228321_1_.getY(), p_228321_1_.getZ() + i);

         int j1;
         for(BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(); cubecoordinateiterator.advance(); i1 += j1 & 255) {
            blockpos$mutable.set(cubecoordinateiterator.nextX(), cubecoordinateiterator.nextY(), cubecoordinateiterator.nextZ());
            j1 = p_228321_2_.getColor(this.getBiome(blockpos$mutable), (double)blockpos$mutable.getX(), (double)blockpos$mutable.getZ());
            k += (j1 & 16711680) >> 16;
            l += (j1 & '\uff00') >> 8;
         }

         return (k / j & 255) << 16 | (l / j & 255) << 8 | i1 / j & 255;
      }
   }

   public BlockPos getSharedSpawnPos() {
      BlockPos blockpos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if (!this.getWorldBorder().isWithinBounds(blockpos)) {
         blockpos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public float getSharedSpawnAngle() {
      return this.levelData.getSpawnAngle();
   }

   public void setDefaultSpawnPos(BlockPos p_239136_1_, float p_239136_2_) {
      this.levelData.setSpawn(p_239136_1_, p_239136_2_);
   }

   public String toString() {
      return "ClientLevel";
   }

   public ClientWorld.ClientWorldInfo getLevelData() {
      return this.clientLevelData;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ClientWorldInfo implements ISpawnWorldInfo {
      private final boolean hardcore;
      private final GameRules gameRules;
      private final boolean isFlat;
      private int xSpawn;
      private int ySpawn;
      private int zSpawn;
      private float spawnAngle;
      private long gameTime;
      private long dayTime;
      private boolean raining;
      private Difficulty difficulty;
      private boolean difficultyLocked;

      public ClientWorldInfo(Difficulty p_i232338_1_, boolean p_i232338_2_, boolean p_i232338_3_) {
         this.difficulty = p_i232338_1_;
         this.hardcore = p_i232338_2_;
         this.isFlat = p_i232338_3_;
         this.gameRules = new GameRules();
      }

      public int getXSpawn() {
         return this.xSpawn;
      }

      public int getYSpawn() {
         return this.ySpawn;
      }

      public int getZSpawn() {
         return this.zSpawn;
      }

      public float getSpawnAngle() {
         return this.spawnAngle;
      }

      public long getGameTime() {
         return this.gameTime;
      }

      public long getDayTime() {
         return this.dayTime;
      }

      public void setXSpawn(int p_76058_1_) {
         this.xSpawn = p_76058_1_;
      }

      public void setYSpawn(int p_76056_1_) {
         this.ySpawn = p_76056_1_;
      }

      public void setZSpawn(int p_76087_1_) {
         this.zSpawn = p_76087_1_;
      }

      public void setSpawnAngle(float p_241859_1_) {
         this.spawnAngle = p_241859_1_;
      }

      public void setGameTime(long p_239155_1_) {
         this.gameTime = p_239155_1_;
      }

      public void setDayTime(long p_239158_1_) {
         this.dayTime = p_239158_1_;
      }

      public void setSpawn(BlockPos p_176143_1_, float p_176143_2_) {
         this.xSpawn = p_176143_1_.getX();
         this.ySpawn = p_176143_1_.getY();
         this.zSpawn = p_176143_1_.getZ();
         this.spawnAngle = p_176143_2_;
      }

      public boolean isThundering() {
         return false;
      }

      public boolean isRaining() {
         return this.raining;
      }

      public void setRaining(boolean p_76084_1_) {
         this.raining = p_76084_1_;
      }

      public boolean isHardcore() {
         return this.hardcore;
      }

      public GameRules getGameRules() {
         return this.gameRules;
      }

      public Difficulty getDifficulty() {
         return this.difficulty;
      }

      public boolean isDifficultyLocked() {
         return this.difficultyLocked;
      }

      public void fillCrashReportCategory(CrashReportCategory p_85118_1_) {
         ISpawnWorldInfo.super.fillCrashReportCategory(p_85118_1_);
      }

      public void setDifficulty(Difficulty p_239156_1_) {
         net.minecraftforge.common.ForgeHooks.onDifficultyChange(p_239156_1_, this.difficulty);
         this.difficulty = p_239156_1_;
      }

      public void setDifficultyLocked(boolean p_239157_1_) {
         this.difficultyLocked = p_239157_1_;
      }

      public double getHorizonHeight() {
         return this.isFlat ? 0.0D : 63.0D;
      }

      public double getClearColorScale() {
         return this.isFlat ? 1.0D : 0.03125D;
      }
   }
}
