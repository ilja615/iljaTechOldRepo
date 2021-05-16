package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.gui.overlay.SubtitleOverlayGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.FoodStats;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextProcessing;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IngameGui extends AbstractGui {
   protected static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
   private static final ITextComponent DEMO_EXPIRED_TEXT = new TranslationTextComponent("demo.demoExpired");
   protected final Random random = new Random();
   protected final Minecraft minecraft;
   protected final ItemRenderer itemRenderer;
   protected final NewChatGui chat;
   protected int tickCount;
   @Nullable
   protected ITextComponent overlayMessageString;
   protected int overlayMessageTime;
   protected boolean animateOverlayMessageColor;
   public float vignetteBrightness = 1.0F;
   protected int toolHighlightTimer;
   protected ItemStack lastToolHighlight = ItemStack.EMPTY;
   protected final DebugOverlayGui debugScreen;
   protected final SubtitleOverlayGui subtitleOverlay;
   protected final SpectatorGui spectatorGui;
   protected final PlayerTabOverlayGui tabList;
   protected final BossOverlayGui bossOverlay;
   protected int titleTime;
   @Nullable
   protected ITextComponent title;
   @Nullable
   protected ITextComponent subtitle;
   protected int titleFadeInTime;
   protected int titleStayTime;
   protected int titleFadeOutTime;
   protected int lastHealth;
   protected int displayHealth;
   protected long lastHealthTime;
   protected long healthBlinkTime;
   protected int screenWidth;
   protected int screenHeight;
   protected final Map<ChatType, List<IChatListener>> chatListeners = Maps.newHashMap();

   public IngameGui(Minecraft p_i46325_1_) {
      this.minecraft = p_i46325_1_;
      this.itemRenderer = p_i46325_1_.getItemRenderer();
      this.debugScreen = new DebugOverlayGui(p_i46325_1_);
      this.spectatorGui = new SpectatorGui(p_i46325_1_);
      this.chat = new NewChatGui(p_i46325_1_);
      this.tabList = new PlayerTabOverlayGui(p_i46325_1_, this);
      this.bossOverlay = new BossOverlayGui(p_i46325_1_);
      this.subtitleOverlay = new SubtitleOverlayGui(p_i46325_1_);

      for(ChatType chattype : ChatType.values()) {
         this.chatListeners.put(chattype, Lists.newArrayList());
      }

      IChatListener ichatlistener = NarratorChatListener.INSTANCE;
      this.chatListeners.get(ChatType.CHAT).add(new NormalChatListener(p_i46325_1_));
      this.chatListeners.get(ChatType.CHAT).add(ichatlistener);
      this.chatListeners.get(ChatType.SYSTEM).add(new NormalChatListener(p_i46325_1_));
      this.chatListeners.get(ChatType.SYSTEM).add(ichatlistener);
      this.chatListeners.get(ChatType.GAME_INFO).add(new OverlayChatListener(p_i46325_1_));
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleStayTime = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(MatrixStack p_238445_1_, float p_238445_2_) {
      this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
      this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
      FontRenderer fontrenderer = this.getFont();
      RenderSystem.enableBlend();
      if (Minecraft.useFancyGraphics()) {
         this.renderVignette(this.minecraft.getCameraEntity());
      } else {
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
      }

      ItemStack itemstack = this.minecraft.player.inventory.getArmor(3);
      if (this.minecraft.options.getCameraType().isFirstPerson() && itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
         this.renderPumpkin();
      }

      float f = MathHelper.lerp(p_238445_2_, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
      if (f > 0.0F && !this.minecraft.player.hasEffect(Effects.CONFUSION)) {
         this.renderPortalOverlay(f);
      }

      if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
         this.spectatorGui.renderHotbar(p_238445_1_, p_238445_2_);
      } else if (!this.minecraft.options.hideGui) {
         this.renderHotbar(p_238445_2_, p_238445_1_);
      }

      if (!this.minecraft.options.hideGui) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         RenderSystem.enableBlend();
         RenderSystem.enableAlphaTest();
         this.renderCrosshair(p_238445_1_);
         RenderSystem.defaultBlendFunc();
         this.minecraft.getProfiler().push("bossHealth");
         this.bossOverlay.render(p_238445_1_);
         this.minecraft.getProfiler().pop();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth(p_238445_1_);
         }

         this.renderVehicleHealth(p_238445_1_);
         RenderSystem.disableBlend();
         int i = this.screenWidth / 2 - 91;
         if (this.minecraft.player.isRidingJumpable()) {
            this.renderJumpMeter(p_238445_1_, i);
         } else if (this.minecraft.gameMode.hasExperience()) {
            this.renderExperienceBar(p_238445_1_, i);
         }

         if (this.minecraft.options.heldItemTooltips && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName(p_238445_1_);
         } else if (this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderTooltip(p_238445_1_);
         }
      }

      if (this.minecraft.player.getSleepTimer() > 0) {
         this.minecraft.getProfiler().push("sleep");
         RenderSystem.disableDepthTest();
         RenderSystem.disableAlphaTest();
         float f2 = (float)this.minecraft.player.getSleepTimer();
         float f1 = f2 / 100.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F - (f2 - 100.0F) / 10.0F;
         }

         int j = (int)(220.0F * f1) << 24 | 1052704;
         fill(p_238445_1_, 0, 0, this.screenWidth, this.screenHeight, j);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableDepthTest();
         this.minecraft.getProfiler().pop();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.minecraft.isDemo()) {
         this.renderDemoOverlay(p_238445_1_);
      }

      this.renderEffects(p_238445_1_);
      if (this.minecraft.options.renderDebug) {
         this.debugScreen.render(p_238445_1_);
      }

      if (!this.minecraft.options.hideGui) {
         if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
            this.minecraft.getProfiler().push("overlayMessage");
            float f3 = (float)this.overlayMessageTime - p_238445_2_;
            int i1 = (int)(f3 * 255.0F / 20.0F);
            if (i1 > 255) {
               i1 = 255;
            }

            if (i1 > 8) {
               RenderSystem.pushMatrix();
               RenderSystem.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight - 68), 0.0F);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               int k1 = 16777215;
               if (this.animateOverlayMessageColor) {
                  k1 = MathHelper.hsvToRgb(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               int k = i1 << 24 & -16777216;
               int l = fontrenderer.width(this.overlayMessageString);
               this.drawBackdrop(p_238445_1_, fontrenderer, -4, l, 16777215 | k);
               fontrenderer.draw(p_238445_1_, this.overlayMessageString, (float)(-l / 2), -4.0F, k1 | k);
               RenderSystem.disableBlend();
               RenderSystem.popMatrix();
            }

            this.minecraft.getProfiler().pop();
         }

         if (this.title != null && this.titleTime > 0) {
            this.minecraft.getProfiler().push("titleAndSubtitle");
            float f4 = (float)this.titleTime - p_238445_2_;
            int j1 = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
               float f5 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - f4;
               j1 = (int)(f5 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
               j1 = (int)(f4 * 255.0F / (float)this.titleFadeOutTime);
            }

            j1 = MathHelper.clamp(j1, 0, 255);
            if (j1 > 8) {
               RenderSystem.pushMatrix();
               RenderSystem.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), 0.0F);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.pushMatrix();
               RenderSystem.scalef(4.0F, 4.0F, 4.0F);
               int l1 = j1 << 24 & -16777216;
               int i2 = fontrenderer.width(this.title);
               this.drawBackdrop(p_238445_1_, fontrenderer, -10, i2, 16777215 | l1);
               fontrenderer.drawShadow(p_238445_1_, this.title, (float)(-i2 / 2), -10.0F, 16777215 | l1);
               RenderSystem.popMatrix();
               if (this.subtitle != null) {
                  RenderSystem.pushMatrix();
                  RenderSystem.scalef(2.0F, 2.0F, 2.0F);
                  int k2 = fontrenderer.width(this.subtitle);
                  this.drawBackdrop(p_238445_1_, fontrenderer, 5, k2, 16777215 | l1);
                  fontrenderer.drawShadow(p_238445_1_, this.subtitle, (float)(-k2 / 2), 5.0F, 16777215 | l1);
                  RenderSystem.popMatrix();
               }

               RenderSystem.disableBlend();
               RenderSystem.popMatrix();
            }

            this.minecraft.getProfiler().pop();
         }

         this.subtitleOverlay.render(p_238445_1_);
         Scoreboard scoreboard = this.minecraft.level.getScoreboard();
         ScoreObjective scoreobjective = null;
         ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.minecraft.player.getScoreboardName());
         if (scoreplayerteam != null) {
            int j2 = scoreplayerteam.getColor().getId();
            if (j2 >= 0) {
               scoreobjective = scoreboard.getDisplayObjective(3 + j2);
            }
         }

         ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getDisplayObjective(1);
         if (scoreobjective1 != null) {
            this.displayScoreboardSidebar(p_238445_1_, scoreobjective1);
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableAlphaTest();
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, (float)(this.screenHeight - 48), 0.0F);
         this.minecraft.getProfiler().push("chat");
         this.chat.render(p_238445_1_, this.tickCount);
         this.minecraft.getProfiler().pop();
         RenderSystem.popMatrix();
         scoreobjective1 = scoreboard.getDisplayObjective(0);
         if (!this.minecraft.options.keyPlayerList.isDown() || this.minecraft.isLocalServer() && this.minecraft.player.connection.getOnlinePlayers().size() <= 1 && scoreobjective1 == null) {
            this.tabList.setVisible(false);
         } else {
            this.tabList.setVisible(true);
            this.tabList.render(p_238445_1_, this.screenWidth, scoreboard, scoreobjective1);
         }
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableAlphaTest();
   }

   protected void drawBackdrop(MatrixStack p_238448_1_, FontRenderer p_238448_2_, int p_238448_3_, int p_238448_4_, int p_238448_5_) {
      int i = this.minecraft.options.getBackgroundColor(0.0F);
      if (i != 0) {
         int j = -p_238448_4_ / 2;
         fill(p_238448_1_, j - 2, p_238448_3_ - 2, j + p_238448_4_ + 2, p_238448_3_ + 9 + 2, ColorHelper.PackedColor.multiply(i, p_238448_5_));
      }

   }

   protected void renderCrosshair(MatrixStack p_238456_1_) {
      GameSettings gamesettings = this.minecraft.options;
      if (gamesettings.getCameraType().isFirstPerson()) {
         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (gamesettings.renderDebug && !gamesettings.hideGui && !this.minecraft.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo) {
               RenderSystem.pushMatrix();
               RenderSystem.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), (float)this.getBlitOffset());
               ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getMainCamera();
               RenderSystem.rotatef(activerenderinfo.getXRot(), -1.0F, 0.0F, 0.0F);
               RenderSystem.rotatef(activerenderinfo.getYRot(), 0.0F, 1.0F, 0.0F);
               RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
               RenderSystem.renderCrosshair(10);
               RenderSystem.popMatrix();
            } else {
               RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               int i = 15;
               this.blit(p_238456_1_, (this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
               if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                  float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                  boolean flag = false;
                  if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                     flag = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                     flag = flag & this.minecraft.crosshairPickEntity.isAlive();
                  }

                  int j = this.screenHeight / 2 - 7 + 16;
                  int k = this.screenWidth / 2 - 8;
                  if (flag) {
                     this.blit(p_238456_1_, k, j, 68, 94, 16, 16);
                  } else if (f < 1.0F) {
                     int l = (int)(f * 17.0F);
                     this.blit(p_238456_1_, k, j, 36, 94, 16, 4);
                     this.blit(p_238456_1_, k, j, 52, 94, l, 4);
                  }
               }
            }

         }
      }
   }

   private boolean canRenderCrosshairForSpectator(RayTraceResult p_212913_1_) {
      if (p_212913_1_ == null) {
         return false;
      } else if (p_212913_1_.getType() == RayTraceResult.Type.ENTITY) {
         return ((EntityRayTraceResult)p_212913_1_).getEntity() instanceof INamedContainerProvider;
      } else if (p_212913_1_.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)p_212913_1_).getBlockPos();
         World world = this.minecraft.level;
         return world.getBlockState(blockpos).getMenuProvider(world, blockpos) != null;
      } else {
         return false;
      }
   }

   protected void renderEffects(MatrixStack p_238444_1_) {
      Collection<EffectInstance> collection = this.minecraft.player.getActiveEffects();
      if (!collection.isEmpty()) {
         RenderSystem.enableBlend();
         int i = 0;
         int j = 0;
         PotionSpriteUploader potionspriteuploader = this.minecraft.getMobEffectTextures();
         List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
         this.minecraft.getTextureManager().bind(ContainerScreen.INVENTORY_LOCATION);

         for(EffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
            Effect effect = effectinstance.getEffect();
            if (!effectinstance.shouldRenderHUD()) continue;
            // Rebind in case previous renderHUDEffect changed texture
            this.minecraft.getTextureManager().bind(ContainerScreen.INVENTORY_LOCATION);
            if (effectinstance.showIcon()) {
               int k = this.screenWidth;
               int l = 1;
               if (this.minecraft.isDemo()) {
                  l += 15;
               }

               if (effect.isBeneficial()) {
                  ++i;
                  k = k - 25 * i;
               } else {
                  ++j;
                  k = k - 25 * j;
                  l += 26;
               }

               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               float f = 1.0F;
               if (effectinstance.isAmbient()) {
                  this.blit(p_238444_1_, k, l, 165, 166, 24, 24);
               } else {
                  this.blit(p_238444_1_, k, l, 141, 166, 24, 24);
                  if (effectinstance.getDuration() <= 200) {
                     int i1 = 10 - effectinstance.getDuration() / 20;
                     f = MathHelper.clamp((float)effectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)effectinstance.getDuration() * (float)Math.PI / 5.0F) * MathHelper.clamp((float)i1 / 10.0F * 0.25F, 0.0F, 0.25F);
                  }
               }

               TextureAtlasSprite textureatlassprite = potionspriteuploader.get(effect);
               int j1 = k;
               int k1 = l;
               float f1 = f;
               list.add(() -> {
                  this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
                  RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
                  blit(p_238444_1_, j1 + 3, k1 + 3, this.getBlitOffset(), 18, 18, textureatlassprite);
               });
               effectinstance.renderHUDEffect(this, p_238444_1_, k, l, this.getBlitOffset(), f);
            }
         }

         list.forEach(Runnable::run);
      }
   }

   protected void renderHotbar(float p_238443_1_, MatrixStack p_238443_2_) {
      PlayerEntity playerentity = this.getCameraPlayer();
      if (playerentity != null) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
         ItemStack itemstack = playerentity.getOffhandItem();
         HandSide handside = playerentity.getMainArm().getOpposite();
         int i = this.screenWidth / 2;
         int j = this.getBlitOffset();
         int k = 182;
         int l = 91;
         this.setBlitOffset(-90);
         this.blit(p_238443_2_, i - 91, this.screenHeight - 22, 0, 0, 182, 22);
         this.blit(p_238443_2_, i - 91 - 1 + playerentity.inventory.selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);
         if (!itemstack.isEmpty()) {
            if (handside == HandSide.LEFT) {
               this.blit(p_238443_2_, i - 91 - 29, this.screenHeight - 23, 24, 22, 29, 24);
            } else {
               this.blit(p_238443_2_, i + 91, this.screenHeight - 23, 53, 22, 29, 24);
            }
         }

         this.setBlitOffset(j);
         RenderSystem.enableRescaleNormal();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();

         for(int i1 = 0; i1 < 9; ++i1) {
            int j1 = i - 90 + i1 * 20 + 2;
            int k1 = this.screenHeight - 16 - 3;
            this.renderSlot(j1, k1, p_238443_1_, playerentity, playerentity.inventory.items.get(i1));
         }

         if (!itemstack.isEmpty()) {
            int i2 = this.screenHeight - 16 - 3;
            if (handside == HandSide.LEFT) {
               this.renderSlot(i - 91 - 26, i2, p_238443_1_, playerentity, itemstack);
            } else {
               this.renderSlot(i + 91 + 10, i2, p_238443_1_, playerentity, itemstack);
            }
         }

         if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
            float f = this.minecraft.player.getAttackStrengthScale(0.0F);
            if (f < 1.0F) {
               int j2 = this.screenHeight - 20;
               int k2 = i + 91 + 6;
               if (handside == HandSide.RIGHT) {
                  k2 = i - 91 - 22;
               }

               this.minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
               int l1 = (int)(f * 19.0F);
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.blit(p_238443_2_, k2, j2, 0, 94, 18, 18);
               this.blit(p_238443_2_, k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
            }
         }

         RenderSystem.disableRescaleNormal();
         RenderSystem.disableBlend();
      }
   }

   public void renderJumpMeter(MatrixStack p_238446_1_, int p_238446_2_) {
      this.minecraft.getProfiler().push("jumpBar");
      this.minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
      float f = this.minecraft.player.getJumpRidingScale();
      int i = 182;
      int j = (int)(f * 183.0F);
      int k = this.screenHeight - 32 + 3;
      this.blit(p_238446_1_, p_238446_2_, k, 0, 84, 182, 5);
      if (j > 0) {
         this.blit(p_238446_1_, p_238446_2_, k, 0, 89, j, 5);
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderExperienceBar(MatrixStack p_238454_1_, int p_238454_2_) {
      this.minecraft.getProfiler().push("expBar");
      this.minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
      int i = this.minecraft.player.getXpNeededForNextLevel();
      if (i > 0) {
         int j = 182;
         int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
         int l = this.screenHeight - 32 + 3;
         this.blit(p_238454_1_, p_238454_2_, l, 0, 64, 182, 5);
         if (k > 0) {
            this.blit(p_238454_1_, p_238454_2_, l, 0, 69, k, 5);
         }
      }

      this.minecraft.getProfiler().pop();
      if (this.minecraft.player.experienceLevel > 0) {
         this.minecraft.getProfiler().push("expLevel");
         String s = "" + this.minecraft.player.experienceLevel;
         int i1 = (this.screenWidth - this.getFont().width(s)) / 2;
         int j1 = this.screenHeight - 31 - 4;
         this.getFont().draw(p_238454_1_, s, (float)(i1 + 1), (float)j1, 0);
         this.getFont().draw(p_238454_1_, s, (float)(i1 - 1), (float)j1, 0);
         this.getFont().draw(p_238454_1_, s, (float)i1, (float)(j1 + 1), 0);
         this.getFont().draw(p_238454_1_, s, (float)i1, (float)(j1 - 1), 0);
         this.getFont().draw(p_238454_1_, s, (float)i1, (float)j1, 8453920);
         this.minecraft.getProfiler().pop();
      }

   }

   public void renderSelectedItemName(MatrixStack p_238453_1_) {
      this.minecraft.getProfiler().push("selectedItemName");
      if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
         IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color);
         if (this.lastToolHighlight.hasCustomHoverName()) {
            iformattabletextcomponent.withStyle(TextFormatting.ITALIC);
         }

         ITextComponent highlightTip = this.lastToolHighlight.getHighlightTip(iformattabletextcomponent);
         int i = this.getFont().width(highlightTip);
         int j = (this.screenWidth - i) / 2;
         int k = this.screenHeight - 59;
         if (!this.minecraft.gameMode.canHurtPlayer()) {
            k += 14;
         }

         int l = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
         if (l > 255) {
            l = 255;
         }

         if (l > 0) {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            fill(p_238453_1_, j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
            FontRenderer font = lastToolHighlight.getItem().getFontRenderer(lastToolHighlight);
            if (font == null) {
               this.getFont().drawShadow(p_238453_1_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
            } else {
               j = (this.screenWidth - font.width(highlightTip)) / 2;
               font.drawShadow(p_238453_1_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
            }
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderDemoOverlay(MatrixStack p_238455_1_) {
      this.minecraft.getProfiler().push("demo");
      ITextComponent itextcomponent;
      if (this.minecraft.level.getGameTime() >= 120500L) {
         itextcomponent = DEMO_EXPIRED_TEXT;
      } else {
         itextcomponent = new TranslationTextComponent("demo.remainingTime", StringUtils.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
      }

      int i = this.getFont().width(itextcomponent);
      this.getFont().drawShadow(p_238455_1_, itextcomponent, (float)(this.screenWidth - i - 10), 5.0F, 16777215);
      this.minecraft.getProfiler().pop();
   }

   protected void displayScoreboardSidebar(MatrixStack p_238447_1_, ScoreObjective p_238447_2_) {
      Scoreboard scoreboard = p_238447_2_.getScoreboard();
      Collection<Score> collection = scoreboard.getPlayerScores(p_238447_2_);
      List<Score> list = collection.stream().filter((p_212911_0_) -> {
         return p_212911_0_.getOwner() != null && !p_212911_0_.getOwner().startsWith("#");
      }).collect(Collectors.toList());
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      List<Pair<Score, ITextComponent>> list1 = Lists.newArrayListWithCapacity(collection.size());
      ITextComponent itextcomponent = p_238447_2_.getDisplayName();
      int i = this.getFont().width(itextcomponent);
      int j = i;
      int k = this.getFont().width(": ");

      for(Score score : collection) {
         ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getOwner());
         ITextComponent itextcomponent1 = ScorePlayerTeam.formatNameForTeam(scoreplayerteam, new StringTextComponent(score.getOwner()));
         list1.add(Pair.of(score, itextcomponent1));
         j = Math.max(j, this.getFont().width(itextcomponent1) + k + this.getFont().width(Integer.toString(score.getScore())));
      }

      int i2 = collection.size() * 9;
      int j2 = this.screenHeight / 2 + i2 / 3;
      int k2 = 3;
      int l2 = this.screenWidth - j - 3;
      int l = 0;
      int i1 = this.minecraft.options.getBackgroundColor(0.3F);
      int j1 = this.minecraft.options.getBackgroundColor(0.4F);

      for(Pair<Score, ITextComponent> pair : list1) {
         ++l;
         Score score1 = pair.getFirst();
         ITextComponent itextcomponent2 = pair.getSecond();
         String s = TextFormatting.RED + "" + score1.getScore();
         int k1 = j2 - l * 9;
         int l1 = this.screenWidth - 3 + 2;
         fill(p_238447_1_, l2 - 2, k1, l1, k1 + 9, i1);
         this.getFont().draw(p_238447_1_, itextcomponent2, (float)l2, (float)k1, -1);
         this.getFont().draw(p_238447_1_, s, (float)(l1 - this.getFont().width(s)), (float)k1, -1);
         if (l == collection.size()) {
            fill(p_238447_1_, l2 - 2, k1 - 9 - 1, l1, k1 - 1, j1);
            fill(p_238447_1_, l2 - 2, k1 - 1, l1, k1, i1);
            this.getFont().draw(p_238447_1_, itextcomponent, (float)(l2 + j / 2 - i / 2), (float)(k1 - 9), -1);
         }
      }

   }

   private PlayerEntity getCameraPlayer() {
      return !(this.minecraft.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.minecraft.getCameraEntity();
   }

   private LivingEntity getPlayerVehicleWithHealth() {
      PlayerEntity playerentity = this.getCameraPlayer();
      if (playerentity != null) {
         Entity entity = playerentity.getVehicle();
         if (entity == null) {
            return null;
         }

         if (entity instanceof LivingEntity) {
            return (LivingEntity)entity;
         }
      }

      return null;
   }

   private int getVehicleMaxHearts(LivingEntity p_212306_1_) {
      if (p_212306_1_ != null && p_212306_1_.showVehicleHealth()) {
         float f = p_212306_1_.getMaxHealth();
         int i = (int)(f + 0.5F) / 2;
         if (i > 30) {
            i = 30;
         }

         return i;
      } else {
         return 0;
      }
   }

   private int getVisibleVehicleHeartRows(int p_212302_1_) {
      return (int)Math.ceil((double)p_212302_1_ / 10.0D);
   }

   private void renderPlayerHealth(MatrixStack p_238457_1_) {
      PlayerEntity playerentity = this.getCameraPlayer();
      if (playerentity != null) {
         int i = MathHelper.ceil(playerentity.getHealth());
         boolean flag = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
         long j = Util.getMillis();
         if (i < this.lastHealth && playerentity.invulnerableTime > 0) {
            this.lastHealthTime = j;
            this.healthBlinkTime = (long)(this.tickCount + 20);
         } else if (i > this.lastHealth && playerentity.invulnerableTime > 0) {
            this.lastHealthTime = j;
            this.healthBlinkTime = (long)(this.tickCount + 10);
         }

         if (j - this.lastHealthTime > 1000L) {
            this.lastHealth = i;
            this.displayHealth = i;
            this.lastHealthTime = j;
         }

         this.lastHealth = i;
         int k = this.displayHealth;
         this.random.setSeed((long)(this.tickCount * 312871));
         FoodStats foodstats = playerentity.getFoodData();
         int l = foodstats.getFoodLevel();
         int i1 = this.screenWidth / 2 - 91;
         int j1 = this.screenWidth / 2 + 91;
         int k1 = this.screenHeight - 39;
         float f = (float)playerentity.getAttributeValue(Attributes.MAX_HEALTH);
         int l1 = MathHelper.ceil(playerentity.getAbsorptionAmount());
         int i2 = MathHelper.ceil((f + (float)l1) / 2.0F / 10.0F);
         int j2 = Math.max(10 - (i2 - 2), 3);
         int k2 = k1 - (i2 - 1) * j2 - 10;
         int l2 = k1 - 10;
         int i3 = l1;
         int j3 = playerentity.getArmorValue();
         int k3 = -1;
         if (playerentity.hasEffect(Effects.REGENERATION)) {
            k3 = this.tickCount % MathHelper.ceil(f + 5.0F);
         }

         this.minecraft.getProfiler().push("armor");

         for(int l3 = 0; l3 < 10; ++l3) {
            if (j3 > 0) {
               int i4 = i1 + l3 * 8;
               if (l3 * 2 + 1 < j3) {
                  this.blit(p_238457_1_, i4, k2, 34, 9, 9, 9);
               }

               if (l3 * 2 + 1 == j3) {
                  this.blit(p_238457_1_, i4, k2, 25, 9, 9, 9);
               }

               if (l3 * 2 + 1 > j3) {
                  this.blit(p_238457_1_, i4, k2, 16, 9, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().popPush("health");

         for(int l5 = MathHelper.ceil((f + (float)l1) / 2.0F) - 1; l5 >= 0; --l5) {
            int i6 = 16;
            if (playerentity.hasEffect(Effects.POISON)) {
               i6 += 36;
            } else if (playerentity.hasEffect(Effects.WITHER)) {
               i6 += 72;
            }

            int j4 = 0;
            if (flag) {
               j4 = 1;
            }

            int k4 = MathHelper.ceil((float)(l5 + 1) / 10.0F) - 1;
            int l4 = i1 + l5 % 10 * 8;
            int i5 = k1 - k4 * j2;
            if (i <= 4) {
               i5 += this.random.nextInt(2);
            }

            if (i3 <= 0 && l5 == k3) {
               i5 -= 2;
            }

            int j5 = 0;
            if (playerentity.level.getLevelData().isHardcore()) {
               j5 = 5;
            }

            this.blit(p_238457_1_, l4, i5, 16 + j4 * 9, 9 * j5, 9, 9);
            if (flag) {
               if (l5 * 2 + 1 < k) {
                  this.blit(p_238457_1_, l4, i5, i6 + 54, 9 * j5, 9, 9);
               }

               if (l5 * 2 + 1 == k) {
                  this.blit(p_238457_1_, l4, i5, i6 + 63, 9 * j5, 9, 9);
               }
            }

            if (i3 > 0) {
               if (i3 == l1 && l1 % 2 == 1) {
                  this.blit(p_238457_1_, l4, i5, i6 + 153, 9 * j5, 9, 9);
                  --i3;
               } else {
                  this.blit(p_238457_1_, l4, i5, i6 + 144, 9 * j5, 9, 9);
                  i3 -= 2;
               }
            } else {
               if (l5 * 2 + 1 < i) {
                  this.blit(p_238457_1_, l4, i5, i6 + 36, 9 * j5, 9, 9);
               }

               if (l5 * 2 + 1 == i) {
                  this.blit(p_238457_1_, l4, i5, i6 + 45, 9 * j5, 9, 9);
               }
            }
         }

         LivingEntity livingentity = this.getPlayerVehicleWithHealth();
         int j6 = this.getVehicleMaxHearts(livingentity);
         if (j6 == 0) {
            this.minecraft.getProfiler().popPush("food");

            for(int k6 = 0; k6 < 10; ++k6) {
               int i7 = k1;
               int k7 = 16;
               int i8 = 0;
               if (playerentity.hasEffect(Effects.HUNGER)) {
                  k7 += 36;
                  i8 = 13;
               }

               if (playerentity.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (l * 3 + 1) == 0) {
                  i7 = k1 + (this.random.nextInt(3) - 1);
               }

               int k8 = j1 - k6 * 8 - 9;
               this.blit(p_238457_1_, k8, i7, 16 + i8 * 9, 27, 9, 9);
               if (k6 * 2 + 1 < l) {
                  this.blit(p_238457_1_, k8, i7, k7 + 36, 27, 9, 9);
               }

               if (k6 * 2 + 1 == l) {
                  this.blit(p_238457_1_, k8, i7, k7 + 45, 27, 9, 9);
               }
            }

            l2 -= 10;
         }

         this.minecraft.getProfiler().popPush("air");
         int l6 = playerentity.getMaxAirSupply();
         int j7 = Math.min(playerentity.getAirSupply(), l6);
         if (playerentity.isEyeInFluid(FluidTags.WATER) || j7 < l6) {
            int l7 = this.getVisibleVehicleHeartRows(j6) - 1;
            l2 = l2 - l7 * 10;
            int j8 = MathHelper.ceil((double)(j7 - 2) * 10.0D / (double)l6);
            int l8 = MathHelper.ceil((double)j7 * 10.0D / (double)l6) - j8;

            for(int k5 = 0; k5 < j8 + l8; ++k5) {
               if (k5 < j8) {
                  this.blit(p_238457_1_, j1 - k5 * 8 - 9, l2, 16, 18, 9, 9);
               } else {
                  this.blit(p_238457_1_, j1 - k5 * 8 - 9, l2, 25, 18, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void renderVehicleHealth(MatrixStack p_238458_1_) {
      LivingEntity livingentity = this.getPlayerVehicleWithHealth();
      if (livingentity != null) {
         int i = this.getVehicleMaxHearts(livingentity);
         if (i != 0) {
            int j = (int)Math.ceil((double)livingentity.getHealth());
            this.minecraft.getProfiler().popPush("mountHealth");
            int k = this.screenHeight - 39;
            int l = this.screenWidth / 2 + 91;
            int i1 = k;
            int j1 = 0;

            for(boolean flag = false; i > 0; j1 += 20) {
               int k1 = Math.min(i, 10);
               i -= k1;

               for(int l1 = 0; l1 < k1; ++l1) {
                  int i2 = 52;
                  int j2 = 0;
                  int k2 = l - l1 * 8 - 9;
                  this.blit(p_238458_1_, k2, i1, 52 + j2 * 9, 9, 9, 9);
                  if (l1 * 2 + 1 + j1 < j) {
                     this.blit(p_238458_1_, k2, i1, 88, 9, 9, 9);
                  }

                  if (l1 * 2 + 1 + j1 == j) {
                     this.blit(p_238458_1_, k2, i1, 97, 9, 9, 9);
                  }
               }

               i1 -= 10;
            }

         }
      }
   }

   protected void renderPumpkin() {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableAlphaTest();
      this.minecraft.getTextureManager().bind(PUMPKIN_BLUR_LOCATION);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
      tessellator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableAlphaTest();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void updateVignetteBrightness(Entity p_212307_1_) {
      if (p_212307_1_ != null) {
         float f = MathHelper.clamp(1.0F - p_212307_1_.getBrightness(), 0.0F, 1.0F);
         this.vignetteBrightness = (float)((double)this.vignetteBrightness + (double)(f - this.vignetteBrightness) * 0.01D);
      }
   }

   protected void renderVignette(Entity p_212303_1_) {
      WorldBorder worldborder = this.minecraft.level.getWorldBorder();
      float f = (float)worldborder.getDistanceToBorder(p_212303_1_);
      double d0 = Math.min(worldborder.getLerpSpeed() * (double)worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getLerpTarget() - worldborder.getSize()));
      double d1 = Math.max((double)worldborder.getWarningBlocks(), d0);
      if ((double)f < d1) {
         f = 1.0F - (float)((double)f / d1);
      } else {
         f = 0.0F;
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      if (f > 0.0F) {
         RenderSystem.color4f(0.0F, f, f, 1.0F);
      } else {
         RenderSystem.color4f(this.vignetteBrightness, this.vignetteBrightness, this.vignetteBrightness, 1.0F);
      }

      this.minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
      tessellator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
   }

   protected void renderPortalOverlay(float p_194805_1_) {
      if (p_194805_1_ < 1.0F) {
         p_194805_1_ = p_194805_1_ * p_194805_1_;
         p_194805_1_ = p_194805_1_ * p_194805_1_;
         p_194805_1_ = p_194805_1_ * 0.8F + 0.2F;
      }

      RenderSystem.disableAlphaTest();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, p_194805_1_);
      this.minecraft.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
      TextureAtlasSprite textureatlassprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
      float f = textureatlassprite.getU0();
      float f1 = textureatlassprite.getV0();
      float f2 = textureatlassprite.getU1();
      float f3 = textureatlassprite.getV1();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(f, f3).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(f2, f3).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(f2, f1).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(f, f1).endVertex();
      tessellator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableAlphaTest();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderSlot(int p_184044_1_, int p_184044_2_, float p_184044_3_, PlayerEntity p_184044_4_, ItemStack p_184044_5_) {
      if (!p_184044_5_.isEmpty()) {
         float f = (float)p_184044_5_.getPopTime() - p_184044_3_;
         if (f > 0.0F) {
            RenderSystem.pushMatrix();
            float f1 = 1.0F + f / 5.0F;
            RenderSystem.translatef((float)(p_184044_1_ + 8), (float)(p_184044_2_ + 12), 0.0F);
            RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            RenderSystem.translatef((float)(-(p_184044_1_ + 8)), (float)(-(p_184044_2_ + 12)), 0.0F);
         }

         this.itemRenderer.renderAndDecorateItem(p_184044_4_, p_184044_5_, p_184044_1_, p_184044_2_);
         if (f > 0.0F) {
            RenderSystem.popMatrix();
         }

         this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, p_184044_5_, p_184044_1_, p_184044_2_);
      }
   }

   public void tick() {
      if (this.overlayMessageTime > 0) {
         --this.overlayMessageTime;
      }

      if (this.titleTime > 0) {
         --this.titleTime;
         if (this.titleTime <= 0) {
            this.title = null;
            this.subtitle = null;
         }
      }

      ++this.tickCount;
      Entity entity = this.minecraft.getCameraEntity();
      if (entity != null) {
         this.updateVignetteBrightness(entity);
      }

      if (this.minecraft.player != null) {
         ItemStack itemstack = this.minecraft.player.inventory.getSelected();
         if (itemstack.isEmpty()) {
            this.toolHighlightTimer = 0;
         } else if (!this.lastToolHighlight.isEmpty() && itemstack.getItem() == this.lastToolHighlight.getItem() && (itemstack.getHoverName().equals(this.lastToolHighlight.getHoverName()) && itemstack.getHighlightTip(itemstack.getHoverName()).equals(lastToolHighlight.getHighlightTip(lastToolHighlight.getHoverName())))) {
            if (this.toolHighlightTimer > 0) {
               --this.toolHighlightTimer;
            }
         } else {
            this.toolHighlightTimer = 40;
         }

         this.lastToolHighlight = itemstack;
      }

   }

   public void setNowPlaying(ITextComponent p_238451_1_) {
      this.setOverlayMessage(new TranslationTextComponent("record.nowPlaying", p_238451_1_), true);
   }

   public void setOverlayMessage(ITextComponent p_175188_1_, boolean p_175188_2_) {
      this.overlayMessageString = p_175188_1_;
      this.overlayMessageTime = 60;
      this.animateOverlayMessageColor = p_175188_2_;
   }

   public void setTitles(@Nullable ITextComponent p_238452_1_, @Nullable ITextComponent p_238452_2_, int p_238452_3_, int p_238452_4_, int p_238452_5_) {
      if (p_238452_1_ == null && p_238452_2_ == null && p_238452_3_ < 0 && p_238452_4_ < 0 && p_238452_5_ < 0) {
         this.title = null;
         this.subtitle = null;
         this.titleTime = 0;
      } else if (p_238452_1_ != null) {
         this.title = p_238452_1_;
         this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
      } else if (p_238452_2_ != null) {
         this.subtitle = p_238452_2_;
      } else {
         if (p_238452_3_ >= 0) {
            this.titleFadeInTime = p_238452_3_;
         }

         if (p_238452_4_ >= 0) {
            this.titleStayTime = p_238452_4_;
         }

         if (p_238452_5_ >= 0) {
            this.titleFadeOutTime = p_238452_5_;
         }

         if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
         }

      }
   }

   public UUID guessChatUUID(ITextComponent p_244795_1_) {
      String s = TextProcessing.getPlainText(p_244795_1_);
      String s1 = org.apache.commons.lang3.StringUtils.substringBetween(s, "<", ">");
      return s1 == null ? Util.NIL_UUID : this.minecraft.getPlayerSocialManager().getDiscoveredUUID(s1);
   }

   public void handleChat(ChatType p_238450_1_, ITextComponent p_238450_2_, UUID p_238450_3_) {
      if (!this.minecraft.isBlocked(p_238450_3_)) {
         if (!this.minecraft.options.hideMatchedNames || !this.minecraft.isBlocked(this.guessChatUUID(p_238450_2_))) {
            for(IChatListener ichatlistener : this.chatListeners.get(p_238450_1_)) {
               ichatlistener.handle(p_238450_1_, p_238450_2_, p_238450_3_);
            }

         }
      }
   }

   public NewChatGui getChat() {
      return this.chat;
   }

   public int getGuiTicks() {
      return this.tickCount;
   }

   public FontRenderer getFont() {
      return this.minecraft.font;
   }

   public SpectatorGui getSpectatorGui() {
      return this.spectatorGui;
   }

   public PlayerTabOverlayGui getTabList() {
      return this.tabList;
   }

   public void onDisconnected() {
      this.tabList.reset();
      this.bossOverlay.reset();
      this.minecraft.getToasts().clear();
   }

   public BossOverlayGui getBossOverlay() {
      return this.bossOverlay;
   }

   public void clearCache() {
      this.debugScreen.clearChunkCache();
   }
}
