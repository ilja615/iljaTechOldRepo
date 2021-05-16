package net.minecraft.client.entity.player;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BiomeSoundHandler;
import net.minecraft.client.audio.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.RidingMinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditMinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.screen.JigsawScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CMarkRecipeSeenPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity {
   public final ClientPlayNetHandler connection;
   private final StatisticsManager stats;
   private final ClientRecipeBook recipeBook;
   private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   private double xLast;
   private double yLast1;
   private double zLast;
   private float yRotLast;
   private float xRotLast;
   private boolean lastOnGround;
   private boolean crouching;
   private boolean wasShiftKeyDown;
   private boolean wasSprinting;
   private int positionReminder;
   private boolean flashOnSetHealth;
   private String serverBrand;
   public MovementInput input;
   protected final Minecraft minecraft;
   protected int sprintTriggerTime;
   public int sprintTime;
   public float yBob;
   public float xBob;
   public float yBobO;
   public float xBobO;
   private int jumpRidingTicks;
   private float jumpRidingScale;
   public float portalTime;
   public float oPortalTime;
   private boolean startedUsingItem;
   private Hand usingItemHand;
   private boolean handsBusy;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int waterVisionTime;
   private boolean showDeathScreen = true;

   public ClientPlayerEntity(Minecraft p_i232461_1_, ClientWorld p_i232461_2_, ClientPlayNetHandler p_i232461_3_, StatisticsManager p_i232461_4_, ClientRecipeBook p_i232461_5_, boolean p_i232461_6_, boolean p_i232461_7_) {
      super(p_i232461_2_, p_i232461_3_.getLocalGameProfile());
      this.minecraft = p_i232461_1_;
      this.connection = p_i232461_3_;
      this.stats = p_i232461_4_;
      this.recipeBook = p_i232461_5_;
      this.wasShiftKeyDown = p_i232461_6_;
      this.wasSprinting = p_i232461_7_;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, p_i232461_1_.getSoundManager()));
      this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
      this.ambientSoundHandlers.add(new BiomeSoundHandler(this, p_i232461_1_.getSoundManager(), p_i232461_2_.getBiomeManager()));
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_);
      return false;
   }

   public void heal(float p_70691_1_) {
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         if (p_184205_1_ instanceof AbstractMinecartEntity) {
            this.minecraft.getSoundManager().play(new RidingMinecartTickableSound(this, (AbstractMinecartEntity)p_184205_1_));
         }

         if (p_184205_1_ instanceof BoatEntity) {
            this.yRotO = p_184205_1_.yRot;
            this.yRot = p_184205_1_.yRot;
            this.setYHeadRot(p_184205_1_.yRot);
         }

         return true;
      }
   }

   public void removeVehicle() {
      super.removeVehicle();
      this.handsBusy = false;
   }

   public float getViewXRot(float p_195050_1_) {
      return this.xRot;
   }

   public float getViewYRot(float p_195046_1_) {
      return this.isPassenger() ? super.getViewYRot(p_195046_1_) : this.yRot;
   }

   public void tick() {
      if (this.level.hasChunkAt(new BlockPos(this.getX(), 0.0D, this.getZ()))) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.send(new CPlayerPacket.RotationPacket(this.yRot, this.xRot, this.onGround));
            this.connection.send(new CInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isControlledByLocalInstance()) {
               this.connection.send(new CMoveVehiclePacket(entity));
            }
         } else {
            this.sendPosition();
         }

         for(IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
            iambientsoundhandler.tick();
         }

      }
   }

   public float getCurrentMood() {
      for(IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
         if (iambientsoundhandler instanceof BiomeSoundHandler) {
            return ((BiomeSoundHandler)iambientsoundhandler).getMoodiness();
         }
      }

      return 0.0F;
   }

   private void sendPosition() {
      boolean flag = this.isSprinting();
      if (flag != this.wasSprinting) {
         CEntityActionPacket.Action centityactionpacket$action = flag ? CEntityActionPacket.Action.START_SPRINTING : CEntityActionPacket.Action.STOP_SPRINTING;
         this.connection.send(new CEntityActionPacket(this, centityactionpacket$action));
         this.wasSprinting = flag;
      }

      boolean flag3 = this.isShiftKeyDown();
      if (flag3 != this.wasShiftKeyDown) {
         CEntityActionPacket.Action centityactionpacket$action1 = flag3 ? CEntityActionPacket.Action.PRESS_SHIFT_KEY : CEntityActionPacket.Action.RELEASE_SHIFT_KEY;
         this.connection.send(new CEntityActionPacket(this, centityactionpacket$action1));
         this.wasShiftKeyDown = flag3;
      }

      if (this.isControlledCamera()) {
         double d4 = this.getX() - this.xLast;
         double d0 = this.getY() - this.yLast1;
         double d1 = this.getZ() - this.zLast;
         double d2 = (double)(this.yRot - this.yRotLast);
         double d3 = (double)(this.xRot - this.xRotLast);
         ++this.positionReminder;
         boolean flag1 = d4 * d4 + d0 * d0 + d1 * d1 > 9.0E-4D || this.positionReminder >= 20;
         boolean flag2 = d2 != 0.0D || d3 != 0.0D;
         if (this.isPassenger()) {
            Vector3d vector3d = this.getDeltaMovement();
            this.connection.send(new CPlayerPacket.PositionRotationPacket(vector3d.x, -999.0D, vector3d.z, this.yRot, this.xRot, this.onGround));
            flag1 = false;
         } else if (flag1 && flag2) {
            this.connection.send(new CPlayerPacket.PositionRotationPacket(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot, this.onGround));
         } else if (flag1) {
            this.connection.send(new CPlayerPacket.PositionPacket(this.getX(), this.getY(), this.getZ(), this.onGround));
         } else if (flag2) {
            this.connection.send(new CPlayerPacket.RotationPacket(this.yRot, this.xRot, this.onGround));
         } else if (this.lastOnGround != this.onGround) {
            this.connection.send(new CPlayerPacket(this.onGround));
         }

         if (flag1) {
            this.xLast = this.getX();
            this.yLast1 = this.getY();
            this.zLast = this.getZ();
            this.positionReminder = 0;
         }

         if (flag2) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
         }

         this.lastOnGround = this.onGround;
         this.autoJumpEnabled = this.minecraft.options.autoJump;
      }

   }

   public boolean drop(boolean p_225609_1_) {
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_225609_1_ ? CPlayerDiggingPacket.Action.DROP_ALL_ITEMS : CPlayerDiggingPacket.Action.DROP_ITEM;
      this.connection.send(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
      return this.inventory.removeItem(this.inventory.selected, p_225609_1_ && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1) != ItemStack.EMPTY;
   }

   public void chat(String p_71165_1_) {
      this.connection.send(new CChatMessagePacket(p_71165_1_));
   }

   public void swing(Hand p_184609_1_) {
      super.swing(p_184609_1_);
      this.connection.send(new CAnimateHandPacket(p_184609_1_));
   }

   public void respawn() {
      this.connection.send(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
   }

   protected void actuallyHurt(DamageSource p_70665_1_, float p_70665_2_) {
      if (!this.isInvulnerableTo(p_70665_1_)) {
         this.setHealth(this.getHealth() - p_70665_2_);
      }
   }

   public void closeContainer() {
      this.connection.send(new CCloseWindowPacket(this.containerMenu.containerId));
      this.clientSideCloseContainer();
   }

   public void clientSideCloseContainer() {
      this.inventory.setCarried(ItemStack.EMPTY);
      super.closeContainer();
      this.minecraft.setScreen((Screen)null);
   }

   public void hurtTo(float p_71150_1_) {
      if (this.flashOnSetHealth) {
         float f = this.getHealth() - p_71150_1_;
         if (f <= 0.0F) {
            this.setHealth(p_71150_1_);
            if (f < 0.0F) {
               this.invulnerableTime = 10;
            }
         } else {
            this.lastHurt = f;
            this.setHealth(this.getHealth());
            this.invulnerableTime = 20;
            this.actuallyHurt(DamageSource.GENERIC, f);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
         }
      } else {
         this.setHealth(p_71150_1_);
         this.flashOnSetHealth = true;
      }

   }

   public void onUpdateAbilities() {
      this.connection.send(new CPlayerAbilitiesPacket(this.abilities));
   }

   public boolean isLocalPlayer() {
      return true;
   }

   public boolean isSuppressingSlidingDownLadder() {
      return !this.abilities.flying && super.isSuppressingSlidingDownLadder();
   }

   public boolean canSpawnSprintParticle() {
      return !this.abilities.flying && super.canSpawnSprintParticle();
   }

   public boolean canSpawnSoulSpeedParticle() {
      return !this.abilities.flying && super.canSpawnSoulSpeedParticle();
   }

   protected void sendRidingJump() {
      this.connection.send(new CEntityActionPacket(this, CEntityActionPacket.Action.START_RIDING_JUMP, MathHelper.floor(this.getJumpRidingScale() * 100.0F)));
   }

   public void sendOpenInventory() {
      this.connection.send(new CEntityActionPacket(this, CEntityActionPacket.Action.OPEN_INVENTORY));
   }

   public void setServerBrand(String p_175158_1_) {
      this.serverBrand = p_175158_1_;
   }

   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatisticsManager getStats() {
      return this.stats;
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(IRecipe<?> p_193103_1_) {
      if (this.recipeBook.willHighlight(p_193103_1_)) {
         this.recipeBook.removeHighlight(p_193103_1_);
         this.connection.send(new CMarkRecipeSeenPacket(p_193103_1_));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int p_184839_1_) {
      this.permissionLevel = p_184839_1_;
   }

   public void displayClientMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      if (p_146105_2_) {
         this.minecraft.gui.setOverlayMessage(p_146105_1_, false);
      } else {
         this.minecraft.gui.getChat().addMessage(p_146105_1_);
      }

   }

   private void moveTowardsClosestSpace(double p_244389_1_, double p_244389_3_) {
      BlockPos blockpos = new BlockPos(p_244389_1_, this.getY(), p_244389_3_);
      if (this.suffocatesAt(blockpos)) {
         double d0 = p_244389_1_ - (double)blockpos.getX();
         double d1 = p_244389_3_ - (double)blockpos.getZ();
         Direction direction = null;
         double d2 = Double.MAX_VALUE;
         Direction[] adirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

         for(Direction direction1 : adirection) {
            double d3 = direction1.getAxis().choose(d0, 0.0D, d1);
            double d4 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d3 : d3;
            if (d4 < d2 && !this.suffocatesAt(blockpos.relative(direction1))) {
               d2 = d4;
               direction = direction1;
            }
         }

         if (direction != null) {
            Vector3d vector3d = this.getDeltaMovement();
            if (direction.getAxis() == Direction.Axis.X) {
               this.setDeltaMovement(0.1D * (double)direction.getStepX(), vector3d.y, vector3d.z);
            } else {
               this.setDeltaMovement(vector3d.x, vector3d.y, 0.1D * (double)direction.getStepZ());
            }
         }

      }
   }

   private boolean suffocatesAt(BlockPos p_205027_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      AxisAlignedBB axisalignedbb1 = (new AxisAlignedBB((double)p_205027_1_.getX(), axisalignedbb.minY, (double)p_205027_1_.getZ(), (double)p_205027_1_.getX() + 1.0D, axisalignedbb.maxY, (double)p_205027_1_.getZ() + 1.0D)).deflate(1.0E-7D);
      return !this.level.noBlockCollision(this, axisalignedbb1, (p_243494_1_, p_243494_2_) -> {
         return p_243494_1_.isSuffocating(this.level, p_243494_2_);
      });
   }

   public void setSprinting(boolean p_70031_1_) {
      super.setSprinting(p_70031_1_);
      this.sprintTime = 0;
   }

   public void setExperienceValues(float p_71152_1_, int p_71152_2_, int p_71152_3_) {
      this.experienceProgress = p_71152_1_;
      this.totalExperience = p_71152_2_;
      this.experienceLevel = p_71152_3_;
   }

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      this.minecraft.gui.getChat().addMessage(p_145747_1_);
   }

   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ >= 24 && p_70103_1_ <= 28) {
         this.setPermissionLevel(p_70103_1_ - 24);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void setShowDeathScreen(boolean p_228355_1_) {
      this.showDeathScreen = p_228355_1_;
   }

   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(this, p_184185_1_, this.getSoundSource(), p_184185_2_, p_184185_3_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_184185_1_ = event.getSound();
      p_184185_2_ = event.getVolume();
      p_184185_3_ = event.getPitch();
      this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), p_184185_1_, this.getSoundSource(), p_184185_2_, p_184185_3_, false);
   }

   public void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), p_213823_1_, p_213823_2_, p_213823_3_, p_213823_4_, false);
   }

   public boolean isEffectiveAi() {
      return true;
   }

   public void startUsingItem(Hand p_184598_1_) {
      ItemStack itemstack = this.getItemInHand(p_184598_1_);
      if (!itemstack.isEmpty() && !this.isUsingItem()) {
         super.startUsingItem(p_184598_1_);
         this.startedUsingItem = true;
         this.usingItemHand = p_184598_1_;
      }
   }

   public boolean isUsingItem() {
      return this.startedUsingItem;
   }

   public void stopUsingItem() {
      super.stopUsingItem();
      this.startedUsingItem = false;
   }

   public Hand getUsedItemHand() {
      return this.usingItemHand;
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      super.onSyncedDataUpdated(p_184206_1_);
      if (DATA_LIVING_ENTITY_FLAGS.equals(p_184206_1_)) {
         boolean flag = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
         Hand hand = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (flag && !this.startedUsingItem) {
            this.startUsingItem(hand);
         } else if (!flag && this.startedUsingItem) {
            this.stopUsingItem();
         }
      }

      if (DATA_SHARED_FLAGS_ID.equals(p_184206_1_) && this.isFallFlying() && !this.wasFallFlying) {
         this.minecraft.getSoundManager().play(new ElytraSound(this));
      }

   }

   public boolean isRidingJumpable() {
      Entity entity = this.getVehicle();
      return this.isPassenger() && entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump();
   }

   public float getJumpRidingScale() {
      return this.jumpRidingScale;
   }

   public void openTextEdit(SignTileEntity p_175141_1_) {
      this.minecraft.setScreen(new EditSignScreen(p_175141_1_));
   }

   public void openMinecartCommandBlock(CommandBlockLogic p_184809_1_) {
      this.minecraft.setScreen(new EditMinecartCommandBlockScreen(p_184809_1_));
   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
      this.minecraft.setScreen(new CommandBlockScreen(p_184824_1_));
   }

   public void openStructureBlock(StructureBlockTileEntity p_189807_1_) {
      this.minecraft.setScreen(new EditStructureScreen(p_189807_1_));
   }

   public void openJigsawBlock(JigsawTileEntity p_213826_1_) {
      this.minecraft.setScreen(new JigsawScreen(p_213826_1_));
   }

   public void openItemGui(ItemStack p_184814_1_, Hand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITABLE_BOOK) {
         this.minecraft.setScreen(new EditBookScreen(this, p_184814_1_, p_184814_2_));
      }

   }

   public void crit(Entity p_71009_1_) {
      this.minecraft.particleEngine.createTrackingEmitter(p_71009_1_, ParticleTypes.CRIT);
   }

   public void magicCrit(Entity p_71047_1_) {
      this.minecraft.particleEngine.createTrackingEmitter(p_71047_1_, ParticleTypes.ENCHANTED_HIT);
   }

   public boolean isShiftKeyDown() {
      return this.input != null && this.input.shiftKeyDown;
   }

   public boolean isCrouching() {
      return this.crouching;
   }

   public boolean isMovingSlowly() {
      return this.isCrouching() || this.isVisuallyCrawling();
   }

   public void serverAiStep() {
      super.serverAiStep();
      if (this.isControlledCamera()) {
         this.xxa = this.input.leftImpulse;
         this.zza = this.input.forwardImpulse;
         this.jumping = this.input.jumping;
         this.yBobO = this.yBob;
         this.xBobO = this.xBob;
         this.xBob = (float)((double)this.xBob + (double)(this.xRot - this.xBob) * 0.5D);
         this.yBob = (float)((double)this.yBob + (double)(this.yRot - this.yBob) * 0.5D);
      }

   }

   protected boolean isControlledCamera() {
      return this.minecraft.getCameraEntity() == this;
   }

   public void aiStep() {
      ++this.sprintTime;
      if (this.sprintTriggerTime > 0) {
         --this.sprintTriggerTime;
      }

      this.handleNetherPortalClient();
      boolean flag = this.input.jumping;
      boolean flag1 = this.input.shiftKeyDown;
      boolean flag2 = this.hasEnoughImpulseToStartSprinting();
      this.crouching = !this.abilities.flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
      this.input.tick(this.isMovingSlowly());
      net.minecraftforge.client.ForgeHooksClient.onInputUpdate(this, this.input);
      this.minecraft.getTutorial().onInput(this.input);
      if (this.isUsingItem() && !this.isPassenger()) {
         this.input.leftImpulse *= 0.2F;
         this.input.forwardImpulse *= 0.2F;
         this.sprintTriggerTime = 0;
      }

      boolean flag3 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         flag3 = true;
         this.input.jumping = true;
      }

      if (!this.noPhysics) {
         this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35D, this.getZ() + (double)this.getBbWidth() * 0.35D);
         this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35D, this.getZ() - (double)this.getBbWidth() * 0.35D);
         this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35D, this.getZ() - (double)this.getBbWidth() * 0.35D);
         this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35D, this.getZ() + (double)this.getBbWidth() * 0.35D);
      }

      if (flag1) {
         this.sprintTriggerTime = 0;
      }

      boolean flag4 = (float)this.getFoodData().getFoodLevel() > 6.0F || this.abilities.mayfly;
      if ((this.onGround || this.isUnderWater()) && !flag1 && !flag2 && this.hasEnoughImpulseToStartSprinting() && !this.isSprinting() && flag4 && !this.isUsingItem() && !this.hasEffect(Effects.BLINDNESS)) {
         if (this.sprintTriggerTime <= 0 && !this.minecraft.options.keySprint.isDown()) {
            this.sprintTriggerTime = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.isUnderWater()) && this.hasEnoughImpulseToStartSprinting() && flag4 && !this.isUsingItem() && !this.hasEffect(Effects.BLINDNESS) && this.minecraft.options.keySprint.isDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag5 = !this.input.hasForwardImpulse() || !flag4;
         boolean flag6 = flag5 || this.horizontalCollision || this.isInWater() && !this.isUnderWater();
         if (this.isSwimming()) {
            if (!this.onGround && !this.input.shiftKeyDown && flag5 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      boolean flag7 = false;
      if (this.abilities.mayfly) {
         if (this.minecraft.gameMode.isAlwaysFlying()) {
            if (!this.abilities.flying) {
               this.abilities.flying = true;
               flag7 = true;
               this.onUpdateAbilities();
            }
         } else if (!flag && this.input.jumping && !flag3) {
            if (this.jumpTriggerTime == 0) {
               this.jumpTriggerTime = 7;
            } else if (!this.isSwimming()) {
               this.abilities.flying = !this.abilities.flying;
               flag7 = true;
               this.onUpdateAbilities();
               this.jumpTriggerTime = 0;
            }
         }
      }

      if (this.input.jumping && !flag7 && !flag && !this.abilities.flying && !this.isPassenger() && !this.onClimbable()) {
         ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.CHEST);
         if (itemstack.canElytraFly(this) && this.tryToStartFallFlying()) {
            this.connection.send(new CEntityActionPacket(this, CEntityActionPacket.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isFallFlying();
      if (this.isInWater() && this.input.shiftKeyDown && this.isAffectedByFluids()) {
         this.goDownInWater();
      }

      if (this.isEyeInFluid(FluidTags.WATER)) {
         int i = this.isSpectator() ? 10 : 1;
         this.waterVisionTime = MathHelper.clamp(this.waterVisionTime + i, 0, 600);
      } else if (this.waterVisionTime > 0) {
         this.isEyeInFluid(FluidTags.WATER);
         this.waterVisionTime = MathHelper.clamp(this.waterVisionTime - 10, 0, 600);
      }

      if (this.abilities.flying && this.isControlledCamera()) {
         int j = 0;
         if (this.input.shiftKeyDown) {
            --j;
         }

         if (this.input.jumping) {
            ++j;
         }

         if (j != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)((float)j * this.abilities.getFlyingSpeed() * 3.0F), 0.0D));
         }
      }

      if (this.isRidingJumpable()) {
         IJumpingMount ijumpingmount = (IJumpingMount)this.getVehicle();
         if (this.jumpRidingTicks < 0) {
            ++this.jumpRidingTicks;
            if (this.jumpRidingTicks == 0) {
               this.jumpRidingScale = 0.0F;
            }
         }

         if (flag && !this.input.jumping) {
            this.jumpRidingTicks = -10;
            ijumpingmount.onPlayerJump(MathHelper.floor(this.getJumpRidingScale() * 100.0F));
            this.sendRidingJump();
         } else if (!flag && this.input.jumping) {
            this.jumpRidingTicks = 0;
            this.jumpRidingScale = 0.0F;
         } else if (flag) {
            ++this.jumpRidingTicks;
            if (this.jumpRidingTicks < 10) {
               this.jumpRidingScale = (float)this.jumpRidingTicks * 0.1F;
            } else {
               this.jumpRidingScale = 0.8F + 2.0F / (float)(this.jumpRidingTicks - 9) * 0.1F;
            }
         }
      } else {
         this.jumpRidingScale = 0.0F;
      }

      super.aiStep();
      if (this.onGround && this.abilities.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
         this.abilities.flying = false;
         this.onUpdateAbilities();
      }

   }

   private void handleNetherPortalClient() {
      this.oPortalTime = this.portalTime;
      if (this.isInsidePortal) {
         if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen()) {
            if (this.minecraft.screen instanceof ContainerScreen) {
               this.closeContainer();
            }

            this.minecraft.setScreen((Screen)null);
         }

         if (this.portalTime == 0.0F) {
            this.minecraft.getSoundManager().play(SimpleSound.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
         }

         this.portalTime += 0.0125F;
         if (this.portalTime >= 1.0F) {
            this.portalTime = 1.0F;
         }

         this.isInsidePortal = false;
      } else if (this.hasEffect(Effects.CONFUSION) && this.getEffect(Effects.CONFUSION).getDuration() > 60) {
         this.portalTime += 0.006666667F;
         if (this.portalTime > 1.0F) {
            this.portalTime = 1.0F;
         }
      } else {
         if (this.portalTime > 0.0F) {
            this.portalTime -= 0.05F;
         }

         if (this.portalTime < 0.0F) {
            this.portalTime = 0.0F;
         }
      }

      this.processPortalCooldown();
   }

   public void rideTick() {
      super.rideTick();
      this.handsBusy = false;
      if (this.getVehicle() instanceof BoatEntity) {
         BoatEntity boatentity = (BoatEntity)this.getVehicle();
         boatentity.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
         this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
      }

   }

   public boolean isHandsBusy() {
      return this.handsBusy;
   }

   @Nullable
   public EffectInstance removeEffectNoUpdate(@Nullable Effect p_184596_1_) {
      if (p_184596_1_ == Effects.CONFUSION) {
         this.oPortalTime = 0.0F;
         this.portalTime = 0.0F;
      }

      return super.removeEffectNoUpdate(p_184596_1_);
   }

   public void move(MoverType p_213315_1_, Vector3d p_213315_2_) {
      double d0 = this.getX();
      double d1 = this.getZ();
      super.move(p_213315_1_, p_213315_2_);
      this.updateAutoJump((float)(this.getX() - d0), (float)(this.getZ() - d1));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float p_189810_1_, float p_189810_2_) {
      if (this.canAutoJump()) {
         Vector3d vector3d = this.position();
         Vector3d vector3d1 = vector3d.add((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         Vector3d vector3d2 = new Vector3d((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         float f = this.getSpeed();
         float f1 = (float)vector3d2.lengthSqr();
         if (f1 <= 0.001F) {
            Vector2f vector2f = this.input.getMoveVector();
            float f2 = f * vector2f.x;
            float f3 = f * vector2f.y;
            float f4 = MathHelper.sin(this.yRot * ((float)Math.PI / 180F));
            float f5 = MathHelper.cos(this.yRot * ((float)Math.PI / 180F));
            vector3d2 = new Vector3d((double)(f2 * f5 - f3 * f4), vector3d2.y, (double)(f3 * f5 + f2 * f4));
            f1 = (float)vector3d2.lengthSqr();
            if (f1 <= 0.001F) {
               return;
            }
         }

         float f12 = MathHelper.fastInvSqrt(f1);
         Vector3d vector3d12 = vector3d2.scale((double)f12);
         Vector3d vector3d13 = this.getForward();
         float f13 = (float)(vector3d13.x * vector3d12.x + vector3d13.z * vector3d12.z);
         if (!(f13 < -0.15F)) {
            ISelectionContext iselectioncontext = ISelectionContext.of(this);
            BlockPos blockpos = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.getCollisionShape(this.level, blockpos, iselectioncontext).isEmpty()) {
               blockpos = blockpos.above();
               BlockState blockstate1 = this.level.getBlockState(blockpos);
               if (blockstate1.getCollisionShape(this.level, blockpos, iselectioncontext).isEmpty()) {
                  float f6 = 7.0F;
                  float f7 = 1.2F;
                  if (this.hasEffect(Effects.JUMP)) {
                     f7 += (float)(this.getEffect(Effects.JUMP).getAmplifier() + 1) * 0.75F;
                  }

                  float f8 = Math.max(f * 7.0F, 1.0F / f12);
                  Vector3d vector3d4 = vector3d1.add(vector3d12.scale((double)f8));
                  float f9 = this.getBbWidth();
                  float f10 = this.getBbHeight();
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vector3d, vector3d4.add(0.0D, (double)f10, 0.0D))).inflate((double)f9, 0.0D, (double)f9);
                  Vector3d lvt_19_1_ = vector3d.add(0.0D, (double)0.51F, 0.0D);
                  vector3d4 = vector3d4.add(0.0D, (double)0.51F, 0.0D);
                  Vector3d vector3d5 = vector3d12.cross(new Vector3d(0.0D, 1.0D, 0.0D));
                  Vector3d vector3d6 = vector3d5.scale((double)(f9 * 0.5F));
                  Vector3d vector3d7 = lvt_19_1_.subtract(vector3d6);
                  Vector3d vector3d8 = vector3d4.subtract(vector3d6);
                  Vector3d vector3d9 = lvt_19_1_.add(vector3d6);
                  Vector3d vector3d10 = vector3d4.add(vector3d6);
                  Iterator<AxisAlignedBB> iterator = this.level.getCollisions(this, axisalignedbb, (p_239205_0_) -> {
                     return true;
                  }).flatMap((p_212329_0_) -> {
                     return p_212329_0_.toAabbs().stream();
                  }).iterator();
                  float f11 = Float.MIN_VALUE;

                  while(iterator.hasNext()) {
                     AxisAlignedBB axisalignedbb1 = iterator.next();
                     if (axisalignedbb1.intersects(vector3d7, vector3d8) || axisalignedbb1.intersects(vector3d9, vector3d10)) {
                        f11 = (float)axisalignedbb1.maxY;
                        Vector3d vector3d11 = axisalignedbb1.getCenter();
                        BlockPos blockpos1 = new BlockPos(vector3d11);

                        for(int i = 1; (float)i < f7; ++i) {
                           BlockPos blockpos2 = blockpos1.above(i);
                           BlockState blockstate2 = this.level.getBlockState(blockpos2);
                           VoxelShape voxelshape;
                           if (!(voxelshape = blockstate2.getCollisionShape(this.level, blockpos2, iselectioncontext)).isEmpty()) {
                              f11 = (float)voxelshape.max(Direction.Axis.Y) + (float)blockpos2.getY();
                              if ((double)f11 - this.getY() > (double)f7) {
                                 return;
                              }
                           }

                           if (i > 1) {
                              blockpos = blockpos.above();
                              BlockState blockstate3 = this.level.getBlockState(blockpos);
                              if (!blockstate3.getCollisionShape(this.level, blockpos, iselectioncontext).isEmpty()) {
                                 return;
                              }
                           }
                        }
                        break;
                     }
                  }

                  if (f11 != Float.MIN_VALUE) {
                     float f14 = (float)((double)f11 - this.getY());
                     if (!(f14 <= 0.5F) && !(f14 > f7)) {
                        this.autoJumpTime = 1;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean canAutoJump() {
      return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0D;
   }

   private boolean isMoving() {
      Vector2f vector2f = this.input.getMoveVector();
      return vector2f.x != 0.0F || vector2f.y != 0.0F;
   }

   private boolean hasEnoughImpulseToStartSprinting() {
      double d0 = 0.8D;
      return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8D;
   }

   public float getWaterVision() {
      if (!this.isEyeInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float f = 600.0F;
         float f1 = 100.0F;
         if ((float)this.waterVisionTime >= 600.0F) {
            return 1.0F;
         } else {
            float f2 = MathHelper.clamp((float)this.waterVisionTime / 100.0F, 0.0F, 1.0F);
            float f3 = (float)this.waterVisionTime < 100.0F ? 0.0F : MathHelper.clamp(((float)this.waterVisionTime - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
         }
      }
   }

   public boolean isUnderWater() {
      return this.wasUnderwater;
   }

   protected boolean updateIsUnderwater() {
      boolean flag = this.wasUnderwater;
      boolean flag1 = super.updateIsUnderwater();
      if (this.isSpectator()) {
         return this.wasUnderwater;
      } else {
         if (!flag && flag1) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (flag && !flag1) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.wasUnderwater;
      }
   }

   public Vector3d getRopeHoldPosition(float p_241843_1_) {
      if (this.minecraft.options.getCameraType().isFirstPerson()) {
         float f = MathHelper.lerp(p_241843_1_ * 0.5F, this.yRot, this.yRotO) * ((float)Math.PI / 180F);
         float f1 = MathHelper.lerp(p_241843_1_ * 0.5F, this.xRot, this.xRotO) * ((float)Math.PI / 180F);
         double d0 = this.getMainArm() == HandSide.RIGHT ? -1.0D : 1.0D;
         Vector3d vector3d = new Vector3d(0.39D * d0, -0.6D, 0.3D);
         return vector3d.xRot(-f1).yRot(-f).add(this.getEyePosition(p_241843_1_));
      } else {
         return super.getRopeHoldPosition(p_241843_1_);
      }
   }

   public void updateSyncFields(ClientPlayerEntity old) {
      this.xLast = old.xLast;
      this.yLast1 = old.yLast1;
      this.zLast = old.zLast;
      this.yRotLast = old.yRotLast;
      this.xRotLast = old.xRotLast;
      this.lastOnGround = old.lastOnGround;
      this.wasShiftKeyDown = old.wasShiftKeyDown;
      this.wasSprinting = old.wasSprinting;
      this.positionReminder = old.positionReminder;
   }
}
