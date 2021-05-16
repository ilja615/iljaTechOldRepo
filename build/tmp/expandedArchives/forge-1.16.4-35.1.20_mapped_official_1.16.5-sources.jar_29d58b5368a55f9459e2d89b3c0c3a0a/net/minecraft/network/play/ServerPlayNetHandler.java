package net.minecraft.network.play;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CJigsawBlockGeneratePacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMarkRecipeSeenPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateRecipeBookStatusPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.filter.IChatFilter;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetHandler implements IServerPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   public final NetworkManager connection;
   private final MinecraftServer server;
   public ServerPlayerEntity player;
   private int tickCount;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
   private int chatSpamTickCount;
   private int dropSpamTickCount;
   private final Int2ShortMap expectedAcks = new Int2ShortOpenHashMap();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lastVehicle;
   private double vehicleFirstGoodX;
   private double vehicleFirstGoodY;
   private double vehicleFirstGoodZ;
   private double vehicleLastGoodX;
   private double vehicleLastGoodY;
   private double vehicleLastGoodZ;
   private Vector3d awaitingPositionFromClient;
   private int awaitingTeleport;
   private int awaitingTeleportTime;
   private boolean clientIsFloating;
   private int aboveGroundTickCount;
   private boolean clientVehicleIsFloating;
   private int aboveGroundVehicleTickCount;
   private int receivedMovePacketCount;
   private int knownMovePacketCount;

   public ServerPlayNetHandler(MinecraftServer p_i1530_1_, NetworkManager p_i1530_2_, ServerPlayerEntity p_i1530_3_) {
      this.server = p_i1530_1_;
      this.connection = p_i1530_2_;
      p_i1530_2_.setListener(this);
      this.player = p_i1530_3_;
      p_i1530_3_.connection = this;
      IChatFilter ichatfilter = p_i1530_3_.getTextFilter();
      if (ichatfilter != null) {
         ichatfilter.join();
      }

   }

   public void tick() {
      this.resetPosition();
      this.player.xo = this.player.getX();
      this.player.yo = this.player.getY();
      this.player.zo = this.player.getZ();
      this.player.doTick();
      this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.yRot, this.player.xRot);
      ++this.tickCount;
      this.knownMovePacketCount = this.receivedMovePacketCount;
      if (this.clientIsFloating && !this.player.isSleeping()) {
         if (++this.aboveGroundTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", (Object)this.player.getName().getString());
            this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying"));
            return;
         }
      } else {
         this.clientIsFloating = false;
         this.aboveGroundTickCount = 0;
      }

      this.lastVehicle = this.player.getRootVehicle();
      if (this.lastVehicle != this.player && this.lastVehicle.getControllingPassenger() == this.player) {
         this.vehicleFirstGoodX = this.lastVehicle.getX();
         this.vehicleFirstGoodY = this.lastVehicle.getY();
         this.vehicleFirstGoodZ = this.lastVehicle.getZ();
         this.vehicleLastGoodX = this.lastVehicle.getX();
         this.vehicleLastGoodY = this.lastVehicle.getY();
         this.vehicleLastGoodZ = this.lastVehicle.getZ();
         if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
            if (++this.aboveGroundVehicleTickCount > 80) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", (Object)this.player.getName().getString());
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying"));
               return;
            }
         } else {
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
         }
      } else {
         this.lastVehicle = null;
         this.clientVehicleIsFloating = false;
         this.aboveGroundVehicleTickCount = 0;
      }

      this.server.getProfiler().push("keepAlive");
      long i = Util.getMillis();
      if (i - this.keepAliveTime >= 15000L) {
         if (this.keepAlivePending) {
            this.disconnect(new TranslationTextComponent("disconnect.timeout"));
         } else {
            this.keepAlivePending = true;
            this.keepAliveTime = i;
            this.keepAliveChallenge = i;
            this.send(new SKeepAlivePacket(this.keepAliveChallenge));
         }
      }

      this.server.getProfiler().pop();
      if (this.chatSpamTickCount > 0) {
         --this.chatSpamTickCount;
      }

      if (this.dropSpamTickCount > 0) {
         --this.dropSpamTickCount;
      }

      if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.idling"));
      }

   }

   public void resetPosition() {
      this.firstGoodX = this.player.getX();
      this.firstGoodY = this.player.getY();
      this.firstGoodZ = this.player.getZ();
      this.lastGoodX = this.player.getX();
      this.lastGoodY = this.player.getY();
      this.lastGoodZ = this.player.getZ();
   }

   public NetworkManager getConnection() {
      return this.connection;
   }

   private boolean isSingleplayerOwner() {
      return this.server.isSingleplayerOwner(this.player.getGameProfile());
   }

   public void disconnect(ITextComponent p_194028_1_) {
      this.connection.send(new SDisconnectPacket(p_194028_1_), (p_210161_2_) -> {
         this.connection.disconnect(p_194028_1_);
      });
      this.connection.setReadOnly();
      this.server.executeBlocking(this.connection::handleDisconnection);
   }

   private <T> void filterTextPacket(T p_244533_1_, Consumer<T> p_244533_2_, BiFunction<IChatFilter, T, CompletableFuture<Optional<T>>> p_244533_3_) {
      ThreadTaskExecutor<?> threadtaskexecutor = this.player.getLevel().getServer();
      Consumer<T> consumer = (p_244545_2_) -> {
         if (this.getConnection().isConnected()) {
            p_244533_2_.accept(p_244545_2_);
         } else {
            LOGGER.debug("Ignoring packet due to disconnection");
         }

      };
      IChatFilter ichatfilter = this.player.getTextFilter();
      if (ichatfilter != null) {
         p_244533_3_.apply(ichatfilter, p_244533_1_).thenAcceptAsync((p_244539_1_) -> {
            p_244539_1_.ifPresent(consumer);
         }, threadtaskexecutor);
      } else {
         threadtaskexecutor.execute(() -> {
            consumer.accept(p_244533_1_);
         });
      }

   }

   private void filterTextPacket(String p_244535_1_, Consumer<String> p_244535_2_) {
      this.filterTextPacket(p_244535_1_, p_244535_2_, IChatFilter::processStreamMessage);
   }

   private void filterTextPacket(List<String> p_244537_1_, Consumer<List<String>> p_244537_2_) {
      this.filterTextPacket(p_244537_1_, p_244537_2_, IChatFilter::processMessageBundle);
   }

   public void handlePlayerInput(CInputPacket p_147358_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147358_1_, this, this.player.getLevel());
      this.player.setPlayerInput(p_147358_1_.getXxa(), p_147358_1_.getZza(), p_147358_1_.isJumping(), p_147358_1_.isShiftKeyDown());
   }

   private static boolean containsInvalidValues(CPlayerPacket p_183006_0_) {
      if (Doubles.isFinite(p_183006_0_.getX(0.0D)) && Doubles.isFinite(p_183006_0_.getY(0.0D)) && Doubles.isFinite(p_183006_0_.getZ(0.0D)) && Floats.isFinite(p_183006_0_.getXRot(0.0F)) && Floats.isFinite(p_183006_0_.getYRot(0.0F))) {
         return Math.abs(p_183006_0_.getX(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getY(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getZ(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean containsInvalidValues(CMoveVehiclePacket p_184341_0_) {
      return !Doubles.isFinite(p_184341_0_.getX()) || !Doubles.isFinite(p_184341_0_.getY()) || !Doubles.isFinite(p_184341_0_.getZ()) || !Floats.isFinite(p_184341_0_.getXRot()) || !Floats.isFinite(p_184341_0_.getYRot());
   }

   public void handleMoveVehicle(CMoveVehiclePacket p_184338_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184338_1_, this, this.player.getLevel());
      if (containsInvalidValues(p_184338_1_)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity entity = this.player.getRootVehicle();
         if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
            ServerWorld serverworld = this.player.getLevel();
            double d0 = entity.getX();
            double d1 = entity.getY();
            double d2 = entity.getZ();
            double d3 = p_184338_1_.getX();
            double d4 = p_184338_1_.getY();
            double d5 = p_184338_1_.getZ();
            float f = p_184338_1_.getYRot();
            float f1 = p_184338_1_.getXRot();
            double d6 = d3 - this.vehicleFirstGoodX;
            double d7 = d4 - this.vehicleFirstGoodY;
            double d8 = d5 - this.vehicleFirstGoodZ;
            double d9 = entity.getDeltaMovement().lengthSqr();
            double d10 = d6 * d6 + d7 * d7 + d8 * d8;
            if (d10 - d9 > 100.0D && !this.isSingleplayerOwner()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
               this.connection.send(new SMoveVehiclePacket(entity));
               return;
            }

            boolean flag = serverworld.noCollision(entity, entity.getBoundingBox().deflate(0.0625D));
            d6 = d3 - this.vehicleLastGoodX;
            d7 = d4 - this.vehicleLastGoodY - 1.0E-6D;
            d8 = d5 - this.vehicleLastGoodZ;
            entity.move(MoverType.PLAYER, new Vector3d(d6, d7, d8));
            d6 = d3 - entity.getX();
            d7 = d4 - entity.getY();
            if (d7 > -0.5D || d7 < 0.5D) {
               d7 = 0.0D;
            }

            d8 = d5 - entity.getZ();
            d10 = d6 * d6 + d7 * d7 + d8 * d8;
            boolean flag1 = false;
            if (d10 > 0.0625D) {
               flag1 = true;
               LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), this.player.getName().getString(), Math.sqrt(d10));
            }

            entity.absMoveTo(d3, d4, d5, f, f1);
            this.player.absMoveTo(d3, d4, d5, this.player.yRot, this.player.xRot); // Forge - Resync player position on vehicle moving
            boolean flag2 = serverworld.noCollision(entity, entity.getBoundingBox().deflate(0.0625D));
            if (flag && (flag1 || !flag2)) {
               entity.absMoveTo(d0, d1, d2, f, f1);
               this.player.absMoveTo(d3, d4, d5, this.player.yRot, this.player.xRot); // Forge - Resync player position on vehicle moving
               this.connection.send(new SMoveVehiclePacket(entity));
               return;
            }

            this.player.getLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.getX() - d0, this.player.getY() - d1, this.player.getZ() - d2);
            this.clientVehicleIsFloating = d7 >= -0.03125D && !this.server.isFlightAllowed() && this.noBlocksAround(entity);
            this.vehicleLastGoodX = entity.getX();
            this.vehicleLastGoodY = entity.getY();
            this.vehicleLastGoodZ = entity.getZ();
         }

      }
   }

   private boolean noBlocksAround(Entity p_241162_1_) {
      return BlockPos.betweenClosedStream(p_241162_1_.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D)).allMatch(b -> p_241162_1_.level.getBlockState(b).isAir(p_241162_1_.level, b));
   }

   public void handleAcceptTeleportPacket(CConfirmTeleportPacket p_184339_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184339_1_, this, this.player.getLevel());
      if (p_184339_1_.getId() == this.awaitingTeleport) {
         this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
         this.lastGoodX = this.awaitingPositionFromClient.x;
         this.lastGoodY = this.awaitingPositionFromClient.y;
         this.lastGoodZ = this.awaitingPositionFromClient.z;
         if (this.player.isChangingDimension()) {
            this.player.hasChangedDimension();
         }

         this.awaitingPositionFromClient = null;
      }

   }

   public void handleRecipeBookSeenRecipePacket(CMarkRecipeSeenPacket p_191984_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_191984_1_, this, this.player.getLevel());
      this.server.getRecipeManager().byKey(p_191984_1_.getRecipe()).ifPresent(this.player.getRecipeBook()::removeHighlight);
   }

   public void handleRecipeBookChangeSettingsPacket(CUpdateRecipeBookStatusPacket p_241831_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_241831_1_, this, this.player.getLevel());
      this.player.getRecipeBook().setBookSetting(p_241831_1_.getBookType(), p_241831_1_.isOpen(), p_241831_1_.isFiltering());
   }

   public void handleSeenAdvancements(CSeenAdvancementsPacket p_194027_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_194027_1_, this, this.player.getLevel());
      if (p_194027_1_.getAction() == CSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation resourcelocation = p_194027_1_.getTab();
         Advancement advancement = this.server.getAdvancements().getAdvancement(resourcelocation);
         if (advancement != null) {
            this.player.getAdvancements().setSelectedTab(advancement);
         }
      }

   }

   public void handleCustomCommandSuggestions(CTabCompletePacket p_195518_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_195518_1_, this, this.player.getLevel());
      StringReader stringreader = new StringReader(p_195518_1_.getCommand());
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      ParseResults<CommandSource> parseresults = this.server.getCommands().getDispatcher().parse(stringreader, this.player.createCommandSourceStack());
      this.server.getCommands().getDispatcher().getCompletionSuggestions(parseresults).thenAccept((p_195519_2_) -> {
         this.connection.send(new STabCompletePacket(p_195518_1_.getId(), p_195519_2_));
      });
   }

   public void handleSetCommandBlock(CUpdateCommandBlockPacket p_210153_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210153_1_, this, this.player.getLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled"), Util.NIL_UUID);
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed"), Util.NIL_UUID);
      } else {
         CommandBlockLogic commandblocklogic = null;
         CommandBlockTileEntity commandblocktileentity = null;
         BlockPos blockpos = p_210153_1_.getPos();
         TileEntity tileentity = this.player.level.getBlockEntity(blockpos);
         if (tileentity instanceof CommandBlockTileEntity) {
            commandblocktileentity = (CommandBlockTileEntity)tileentity;
            commandblocklogic = commandblocktileentity.getCommandBlock();
         }

         String s = p_210153_1_.getCommand();
         boolean flag = p_210153_1_.isTrackOutput();
         if (commandblocklogic != null) {
            CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
            Direction direction = this.player.level.getBlockState(blockpos).getValue(CommandBlockBlock.FACING);
            switch(p_210153_1_.getMode()) {
            case SEQUENCE:
               BlockState blockstate1 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(blockpos, blockstate1.setValue(CommandBlockBlock.FACING, direction).setValue(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
               break;
            case AUTO:
               BlockState blockstate = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(blockpos, blockstate.setValue(CommandBlockBlock.FACING, direction).setValue(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
               break;
            case REDSTONE:
            default:
               BlockState blockstate2 = Blocks.COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(blockpos, blockstate2.setValue(CommandBlockBlock.FACING, direction).setValue(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
            }

            tileentity.clearRemoved();
            this.player.level.setBlockEntity(blockpos, tileentity);
            commandblocklogic.setCommand(s);
            commandblocklogic.setTrackOutput(flag);
            if (!flag) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocktileentity.setAutomatic(p_210153_1_.isAutomatic());
            if (commandblocktileentity$mode != p_210153_1_.getMode()) {
               commandblocktileentity.onModeSwitch();
            }

            commandblocklogic.onUpdated();
            if (!StringUtils.isNullOrEmpty(s)) {
               this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", s), Util.NIL_UUID);
            }
         }

      }
   }

   public void handleSetCommandMinecart(CUpdateMinecartCommandBlockPacket p_210158_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210158_1_, this, this.player.getLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled"), Util.NIL_UUID);
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed"), Util.NIL_UUID);
      } else {
         CommandBlockLogic commandblocklogic = p_210158_1_.getCommandBlock(this.player.level);
         if (commandblocklogic != null) {
            commandblocklogic.setCommand(p_210158_1_.getCommand());
            commandblocklogic.setTrackOutput(p_210158_1_.isTrackOutput());
            if (!p_210158_1_.isTrackOutput()) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocklogic.onUpdated();
            this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", p_210158_1_.getCommand()), Util.NIL_UUID);
         }

      }
   }

   public void handlePickItem(CPickItemPacket p_210152_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210152_1_, this, this.player.getLevel());
      this.player.inventory.pickSlot(p_210152_1_.getSlot());
      this.player.connection.send(new SSetSlotPacket(-2, this.player.inventory.selected, this.player.inventory.getItem(this.player.inventory.selected)));
      this.player.connection.send(new SSetSlotPacket(-2, p_210152_1_.getSlot(), this.player.inventory.getItem(p_210152_1_.getSlot())));
      this.player.connection.send(new SHeldItemChangePacket(this.player.inventory.selected));
   }

   public void handleRenameItem(CRenameItemPacket p_210155_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210155_1_, this, this.player.getLevel());
      if (this.player.containerMenu instanceof RepairContainer) {
         RepairContainer repaircontainer = (RepairContainer)this.player.containerMenu;
         String s = SharedConstants.filterText(p_210155_1_.getName());
         if (s.length() <= 35) {
            repaircontainer.setItemName(s);
         }
      }

   }

   public void handleSetBeaconPacket(CUpdateBeaconPacket p_210154_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210154_1_, this, this.player.getLevel());
      if (this.player.containerMenu instanceof BeaconContainer) {
         ((BeaconContainer)this.player.containerMenu).updateEffects(p_210154_1_.getPrimary(), p_210154_1_.getSecondary());
      }

   }

   public void handleSetStructureBlock(CUpdateStructureBlockPacket p_210157_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210157_1_, this, this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos blockpos = p_210157_1_.getPos();
         BlockState blockstate = this.player.level.getBlockState(blockpos);
         TileEntity tileentity = this.player.level.getBlockEntity(blockpos);
         if (tileentity instanceof StructureBlockTileEntity) {
            StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)tileentity;
            structureblocktileentity.setMode(p_210157_1_.getMode());
            structureblocktileentity.setStructureName(p_210157_1_.getName());
            structureblocktileentity.setStructurePos(p_210157_1_.getOffset());
            structureblocktileentity.setStructureSize(p_210157_1_.getSize());
            structureblocktileentity.setMirror(p_210157_1_.getMirror());
            structureblocktileentity.setRotation(p_210157_1_.getRotation());
            structureblocktileentity.setMetaData(p_210157_1_.getData());
            structureblocktileentity.setIgnoreEntities(p_210157_1_.isIgnoreEntities());
            structureblocktileentity.setShowAir(p_210157_1_.isShowAir());
            structureblocktileentity.setShowBoundingBox(p_210157_1_.isShowBoundingBox());
            structureblocktileentity.setIntegrity(p_210157_1_.getIntegrity());
            structureblocktileentity.setSeed(p_210157_1_.getSeed());
            if (structureblocktileentity.hasStructureName()) {
               String s = structureblocktileentity.getStructureName();
               if (p_210157_1_.getUpdateType() == StructureBlockTileEntity.UpdateCommand.SAVE_AREA) {
                  if (structureblocktileentity.saveStructure()) {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.save_success", s), false);
                  } else {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.save_failure", s), false);
                  }
               } else if (p_210157_1_.getUpdateType() == StructureBlockTileEntity.UpdateCommand.LOAD_AREA) {
                  if (!structureblocktileentity.isStructureLoadable()) {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.load_not_found", s), false);
                  } else if (structureblocktileentity.loadStructure(this.player.getLevel())) {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.load_success", s), false);
                  } else {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.load_prepare", s), false);
                  }
               } else if (p_210157_1_.getUpdateType() == StructureBlockTileEntity.UpdateCommand.SCAN_AREA) {
                  if (structureblocktileentity.detectSize()) {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.size_success", s), false);
                  } else {
                     this.player.displayClientMessage(new TranslationTextComponent("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.displayClientMessage(new TranslationTextComponent("structure_block.invalid_structure_name", p_210157_1_.getName()), false);
            }

            structureblocktileentity.setChanged();
            this.player.level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
         }

      }
   }

   public void handleSetJigsawBlock(CUpdateJigsawBlockPacket p_217262_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217262_1_, this, this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos blockpos = p_217262_1_.getPos();
         BlockState blockstate = this.player.level.getBlockState(blockpos);
         TileEntity tileentity = this.player.level.getBlockEntity(blockpos);
         if (tileentity instanceof JigsawTileEntity) {
            JigsawTileEntity jigsawtileentity = (JigsawTileEntity)tileentity;
            jigsawtileentity.setName(p_217262_1_.getName());
            jigsawtileentity.setTarget(p_217262_1_.getTarget());
            jigsawtileentity.setPool(p_217262_1_.getPool());
            jigsawtileentity.setFinalState(p_217262_1_.getFinalState());
            jigsawtileentity.setJoint(p_217262_1_.getJoint());
            jigsawtileentity.setChanged();
            this.player.level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
         }

      }
   }

   public void handleJigsawGenerate(CJigsawBlockGeneratePacket p_230549_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_230549_1_, this, this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos blockpos = p_230549_1_.getPos();
         TileEntity tileentity = this.player.level.getBlockEntity(blockpos);
         if (tileentity instanceof JigsawTileEntity) {
            JigsawTileEntity jigsawtileentity = (JigsawTileEntity)tileentity;
            jigsawtileentity.generate(this.player.getLevel(), p_230549_1_.levels(), p_230549_1_.keepJigsaws());
         }

      }
   }

   public void handleSelectTrade(CSelectTradePacket p_210159_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_210159_1_, this, this.player.getLevel());
      int i = p_210159_1_.getItem();
      Container container = this.player.containerMenu;
      if (container instanceof MerchantContainer) {
         MerchantContainer merchantcontainer = (MerchantContainer)container;
         merchantcontainer.setSelectionHint(i);
         merchantcontainer.tryMoveItems(i);
      }

   }

   public void handleEditBook(CEditBookPacket p_210156_1_) {
      ItemStack itemstack = p_210156_1_.getBook();
      if (itemstack.getItem() == Items.WRITABLE_BOOK) {
         CompoundNBT compoundnbt = itemstack.getTag();
         if (WritableBookItem.makeSureTagIsValid(compoundnbt)) {
            List<String> list = Lists.newArrayList();
            boolean flag = p_210156_1_.isSigning();
            if (flag) {
               list.add(compoundnbt.getString("title"));
            }

            ListNBT listnbt = compoundnbt.getList("pages", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               list.add(listnbt.getString(i));
            }

            int j = p_210156_1_.getSlot();
            if (PlayerInventory.isHotbarSlot(j) || j == 40) {
               this.filterTextPacket(list, flag ? (p_244543_2_) -> {
                  this.signBook(p_244543_2_.get(0), p_244543_2_.subList(1, p_244543_2_.size()), j);
               } : (p_244531_2_) -> {
                  this.updateBookContents(p_244531_2_, j);
               });
            }
         }
      }
   }

   private void updateBookContents(List<String> p_244536_1_, int p_244536_2_) {
      ItemStack itemstack = this.player.inventory.getItem(p_244536_2_);
      if (itemstack.getItem() == Items.WRITABLE_BOOK) {
         ListNBT listnbt = new ListNBT();
         p_244536_1_.stream().map(StringNBT::valueOf).forEach(listnbt::add);
         itemstack.addTagElement("pages", listnbt);
      }
   }

   private void signBook(String p_244534_1_, List<String> p_244534_2_, int p_244534_3_) {
      ItemStack itemstack = this.player.inventory.getItem(p_244534_3_);
      if (itemstack.getItem() == Items.WRITABLE_BOOK) {
         ItemStack itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
         CompoundNBT compoundnbt = itemstack.getTag();
         if (compoundnbt != null) {
            itemstack1.setTag(compoundnbt.copy());
         }

         itemstack1.addTagElement("author", StringNBT.valueOf(this.player.getName().getString()));
         itemstack1.addTagElement("title", StringNBT.valueOf(p_244534_1_));
         ListNBT listnbt = new ListNBT();

         for(String s : p_244534_2_) {
            ITextComponent itextcomponent = new StringTextComponent(s);
            String s1 = ITextComponent.Serializer.toJson(itextcomponent);
            listnbt.add(StringNBT.valueOf(s1));
         }

         itemstack1.addTagElement("pages", listnbt);
         this.player.inventory.setItem(p_244534_3_, itemstack1);
      }
   }

   public void handleEntityTagQuery(CQueryEntityNBTPacket p_211526_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_211526_1_, this, this.player.getLevel());
      if (this.player.hasPermissions(2)) {
         Entity entity = this.player.getLevel().getEntity(p_211526_1_.getEntityId());
         if (entity != null) {
            CompoundNBT compoundnbt = entity.saveWithoutId(new CompoundNBT());
            this.player.connection.send(new SQueryNBTResponsePacket(p_211526_1_.getTransactionId(), compoundnbt));
         }

      }
   }

   public void handleBlockEntityTagQuery(CQueryTileEntityNBTPacket p_211525_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_211525_1_, this, this.player.getLevel());
      if (this.player.hasPermissions(2)) {
         TileEntity tileentity = this.player.getLevel().getBlockEntity(p_211525_1_.getPos());
         CompoundNBT compoundnbt = tileentity != null ? tileentity.save(new CompoundNBT()) : null;
         this.player.connection.send(new SQueryNBTResponsePacket(p_211525_1_.getTransactionId(), compoundnbt));
      }
   }

   public void handleMovePlayer(CPlayerPacket p_147347_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147347_1_, this, this.player.getLevel());
      if (containsInvalidValues(p_147347_1_)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_player_movement"));
      } else {
         ServerWorld serverworld = this.player.getLevel();
         if (!this.player.wonGame) {
            if (this.tickCount == 0) {
               this.resetPosition();
            }

            if (this.awaitingPositionFromClient != null) {
               if (this.tickCount - this.awaitingTeleportTime > 20) {
                  this.awaitingTeleportTime = this.tickCount;
                  this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
               }

            } else {
               this.awaitingTeleportTime = this.tickCount;
               if (this.player.isPassenger()) {
                  this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), p_147347_1_.getYRot(this.player.yRot), p_147347_1_.getXRot(this.player.xRot));
                  this.player.getLevel().getChunkSource().move(this.player);
               } else {
                  double d0 = this.player.getX();
                  double d1 = this.player.getY();
                  double d2 = this.player.getZ();
                  double d3 = this.player.getY();
                  double d4 = p_147347_1_.getX(this.player.getX());
                  double d5 = p_147347_1_.getY(this.player.getY());
                  double d6 = p_147347_1_.getZ(this.player.getZ());
                  float f = p_147347_1_.getYRot(this.player.yRot);
                  float f1 = p_147347_1_.getXRot(this.player.xRot);
                  double d7 = d4 - this.firstGoodX;
                  double d8 = d5 - this.firstGoodY;
                  double d9 = d6 - this.firstGoodZ;
                  double d10 = this.player.getDeltaMovement().lengthSqr();
                  double d11 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (this.player.isSleeping()) {
                     if (d11 > 1.0D) {
                        this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), p_147347_1_.getYRot(this.player.yRot), p_147347_1_.getXRot(this.player.xRot));
                     }

                  } else {
                     ++this.receivedMovePacketCount;
                     int i = this.receivedMovePacketCount - this.knownMovePacketCount;
                     if (i > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                        i = 1;
                     }

                     if (!this.player.isChangingDimension() && (!this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                        float f2 = this.player.isFallFlying() ? 300.0F : 100.0F;
                        if (d11 - d10 > (double)(f2 * (float)i) && !this.isSingleplayerOwner()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                           this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.yRot, this.player.xRot);
                           return;
                        }
                     }

                     AxisAlignedBB axisalignedbb = this.player.getBoundingBox();
                     d7 = d4 - this.lastGoodX;
                     d8 = d5 - this.lastGoodY;
                     d9 = d6 - this.lastGoodZ;
                     boolean flag = d8 > 0.0D;
                     if (this.player.isOnGround() && !p_147347_1_.isOnGround() && flag) {
                        this.player.jumpFromGround();
                     }

                     this.player.move(MoverType.PLAYER, new Vector3d(d7, d8, d9));
                     d7 = d4 - this.player.getX();
                     d8 = d5 - this.player.getY();
                     if (d8 > -0.5D || d8 < 0.5D) {
                        d8 = 0.0D;
                     }

                     d9 = d6 - this.player.getZ();
                     d11 = d7 * d7 + d8 * d8 + d9 * d9;
                     boolean flag1 = false;
                     if (!this.player.isChangingDimension() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        flag1 = true;
                        LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
                     }

                     this.player.absMoveTo(d4, d5, d6, f, f1);
                     if (this.player.noPhysics || this.player.isSleeping() || (!flag1 || !serverworld.noCollision(this.player, axisalignedbb)) && !this.isPlayerCollidingWithAnythingNew(serverworld, axisalignedbb)) {
                        this.clientIsFloating = d8 >= -0.03125D && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.mayfly && !this.player.hasEffect(Effects.LEVITATION) && !this.player.isFallFlying() && this.noBlocksAround(this.player);
                        this.player.getLevel().getChunkSource().move(this.player);
                        this.player.doCheckFallDamage(this.player.getY() - d3, p_147347_1_.isOnGround());
                        this.player.setOnGround(p_147347_1_.isOnGround());
                        if (flag) {
                           this.player.fallDistance = 0.0F;
                        }

                        this.player.checkMovementStatistics(this.player.getX() - d0, this.player.getY() - d1, this.player.getZ() - d2);
                        this.lastGoodX = this.player.getX();
                        this.lastGoodY = this.player.getY();
                        this.lastGoodZ = this.player.getZ();
                     } else {
                        this.teleport(d0, d1, d2, f, f1);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerCollidingWithAnythingNew(IWorldReader p_241163_1_, AxisAlignedBB p_241163_2_) {
      Stream<VoxelShape> stream = p_241163_1_.getCollisions(this.player, this.player.getBoundingBox().deflate((double)1.0E-5F), (p_241167_0_) -> {
         return true;
      });
      VoxelShape voxelshape = VoxelShapes.create(p_241163_2_.deflate((double)1.0E-5F));
      return stream.anyMatch((p_241164_1_) -> {
         return !VoxelShapes.joinIsNotEmpty(p_241164_1_, voxelshape, IBooleanFunction.AND);
      });
   }

   public void teleport(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
      this.teleport(p_147364_1_, p_147364_3_, p_147364_5_, p_147364_7_, p_147364_8_, Collections.emptySet());
   }

   public void teleport(double p_175089_1_, double p_175089_3_, double p_175089_5_, float p_175089_7_, float p_175089_8_, Set<SPlayerPositionLookPacket.Flags> p_175089_9_) {
      double d0 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.X) ? this.player.getX() : 0.0D;
      double d1 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Y) ? this.player.getY() : 0.0D;
      double d2 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Z) ? this.player.getZ() : 0.0D;
      float f = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Y_ROT) ? this.player.yRot : 0.0F;
      float f1 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.X_ROT) ? this.player.xRot : 0.0F;
      this.awaitingPositionFromClient = new Vector3d(p_175089_1_, p_175089_3_, p_175089_5_);
      if (++this.awaitingTeleport == Integer.MAX_VALUE) {
         this.awaitingTeleport = 0;
      }

      this.awaitingTeleportTime = this.tickCount;
      this.player.absMoveTo(p_175089_1_, p_175089_3_, p_175089_5_, p_175089_7_, p_175089_8_);
      this.player.connection.send(new SPlayerPositionLookPacket(p_175089_1_ - d0, p_175089_3_ - d1, p_175089_5_ - d2, p_175089_7_ - f, p_175089_8_ - f1, p_175089_9_, this.awaitingTeleport));
   }

   public void handlePlayerAction(CPlayerDiggingPacket p_147345_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147345_1_, this, this.player.getLevel());
      BlockPos blockpos = p_147345_1_.getPos();
      this.player.resetLastActionTime();
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_147345_1_.getAction();
      switch(cplayerdiggingpacket$action) {
      case SWAP_ITEM_WITH_OFFHAND:
         if (!this.player.isSpectator()) {
            ItemStack itemstack = this.player.getItemInHand(Hand.OFF_HAND);
            this.player.setItemInHand(Hand.OFF_HAND, this.player.getItemInHand(Hand.MAIN_HAND));
            this.player.setItemInHand(Hand.MAIN_HAND, itemstack);
            this.player.stopUsingItem();
         }

         return;
      case DROP_ITEM:
         if (!this.player.isSpectator()) {
            this.player.drop(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if (!this.player.isSpectator()) {
            this.player.drop(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.player.releaseUsingItem();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         this.player.gameMode.handleBlockBreakAction(blockpos, cplayerdiggingpacket$action, p_147345_1_.getDirection(), this.server.getMaxBuildHeight());
         return;
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   private static boolean wasBlockPlacementAttempt(ServerPlayerEntity p_241166_0_, ItemStack p_241166_1_) {
      if (p_241166_1_.isEmpty()) {
         return false;
      } else {
         Item item = p_241166_1_.getItem();
         return (item instanceof BlockItem || item instanceof BucketItem) && !p_241166_0_.getCooldowns().isOnCooldown(item);
      }
   }

   public void handleUseItemOn(CPlayerTryUseItemOnBlockPacket p_184337_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184337_1_, this, this.player.getLevel());
      ServerWorld serverworld = this.player.getLevel();
      Hand hand = p_184337_1_.getHand();
      ItemStack itemstack = this.player.getItemInHand(hand);
      BlockRayTraceResult blockraytraceresult = p_184337_1_.getHitResult();
      BlockPos blockpos = blockraytraceresult.getBlockPos();
      Direction direction = blockraytraceresult.getDirection();
      this.player.resetLastActionTime();
      if (blockpos.getY() < this.server.getMaxBuildHeight()) {
         double dist = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 3;
         dist *= dist;
         if (this.awaitingPositionFromClient == null && this.player.distanceToSqr((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) < dist && serverworld.mayInteract(this.player, blockpos)) {
            ActionResultType actionresulttype = this.player.gameMode.useItemOn(this.player, serverworld, itemstack, hand, blockraytraceresult);
            if (direction == Direction.UP && !actionresulttype.consumesAction() && blockpos.getY() >= this.server.getMaxBuildHeight() - 1 && wasBlockPlacementAttempt(this.player, itemstack)) {
               ITextComponent itextcomponent = (new TranslationTextComponent("build.tooHigh", this.server.getMaxBuildHeight())).withStyle(TextFormatting.RED);
               this.player.connection.send(new SChatPacket(itextcomponent, ChatType.GAME_INFO, Util.NIL_UUID));
            } else if (actionresulttype.shouldSwing()) {
               this.player.swing(hand, true);
            }
         }
      } else {
         ITextComponent itextcomponent1 = (new TranslationTextComponent("build.tooHigh", this.server.getMaxBuildHeight())).withStyle(TextFormatting.RED);
         this.player.connection.send(new SChatPacket(itextcomponent1, ChatType.GAME_INFO, Util.NIL_UUID));
      }

      this.player.connection.send(new SChangeBlockPacket(serverworld, blockpos));
      this.player.connection.send(new SChangeBlockPacket(serverworld, blockpos.relative(direction)));
   }

   public void handleUseItem(CPlayerTryUseItemPacket p_147346_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147346_1_, this, this.player.getLevel());
      ServerWorld serverworld = this.player.getLevel();
      Hand hand = p_147346_1_.getHand();
      ItemStack itemstack = this.player.getItemInHand(hand);
      this.player.resetLastActionTime();
      if (!itemstack.isEmpty()) {
         ActionResultType actionresulttype = this.player.gameMode.useItem(this.player, serverworld, itemstack, hand);
         if (actionresulttype.shouldSwing()) {
            this.player.swing(hand, true);
         }

      }
   }

   public void handleTeleportToEntityPacket(CSpectatePacket p_175088_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175088_1_, this, this.player.getLevel());
      if (this.player.isSpectator()) {
         for(ServerWorld serverworld : this.server.getAllLevels()) {
            Entity entity = p_175088_1_.getEntity(serverworld);
            if (entity != null) {
               this.player.teleportTo(serverworld, entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
               return;
            }
         }
      }

   }

   public void handleResourcePackResponse(CResourcePackStatusPacket p_175086_1_) {
   }

   public void handlePaddleBoat(CSteerBoatPacket p_184340_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184340_1_, this, this.player.getLevel());
      Entity entity = this.player.getVehicle();
      if (entity instanceof BoatEntity) {
         ((BoatEntity)entity).setPaddleState(p_184340_1_.getLeft(), p_184340_1_.getRight());
      }

   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), p_147231_1_.getString());
      this.server.invalidateStatus();
      this.server.getPlayerList().broadcastMessage((new TranslationTextComponent("multiplayer.player.left", this.player.getDisplayName())).withStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      IChatFilter ichatfilter = this.player.getTextFilter();
      if (ichatfilter != null) {
         ichatfilter.leave();
      }

      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }

   }

   public void send(IPacket<?> p_147359_1_) {
      this.send(p_147359_1_, (GenericFutureListener<? extends Future<? super Void>>)null);
   }

   public void send(IPacket<?> p_211148_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_211148_2_) {
      if (p_211148_1_ instanceof SChatPacket) {
         SChatPacket schatpacket = (SChatPacket)p_211148_1_;
         ChatVisibility chatvisibility = this.player.getChatVisibility();
         if (chatvisibility == ChatVisibility.HIDDEN && schatpacket.getType() != ChatType.GAME_INFO) {
            return;
         }

         if (chatvisibility == ChatVisibility.SYSTEM && !schatpacket.isSystem()) {
            return;
         }
      }

      try {
         this.connection.send(p_211148_1_, p_211148_2_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Sending packet");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Packet being sent");
         crashreportcategory.setDetail("Packet class", () -> {
            return p_211148_1_.getClass().getCanonicalName();
         });
         throw new ReportedException(crashreport);
      }
   }

   public void handleSetCarriedItem(CHeldItemChangePacket p_147355_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147355_1_, this, this.player.getLevel());
      if (p_147355_1_.getSlot() >= 0 && p_147355_1_.getSlot() < PlayerInventory.getSelectionSize()) {
         if (this.player.inventory.selected != p_147355_1_.getSlot() && this.player.getUsedItemHand() == Hand.MAIN_HAND) {
            this.player.stopUsingItem();
         }

         this.player.inventory.selected = p_147355_1_.getSlot();
         this.player.resetLastActionTime();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", (Object)this.player.getName().getString());
      }
   }

   public void handleChat(CChatMessagePacket p_147354_1_) {
      String s = org.apache.commons.lang3.StringUtils.normalizeSpace(p_147354_1_.getMessage());
      if (s.startsWith("/")) {
         PacketThreadUtil.ensureRunningOnSameThread(p_147354_1_, this, this.player.getLevel());
         this.handleChat(s);
      } else {
         this.filterTextPacket(s, this::handleChat);
      }

   }

   private void handleChat(String p_244548_1_) {
      if (this.player.getChatVisibility() == ChatVisibility.HIDDEN) {
         this.send(new SChatPacket((new TranslationTextComponent("chat.cannotSend")).withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID));
      } else {
         this.player.resetLastActionTime();

         for(int i = 0; i < p_244548_1_.length(); ++i) {
            if (!SharedConstants.isAllowedChatCharacter(p_244548_1_.charAt(i))) {
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.illegal_characters"));
               return;
            }
         }

         if (p_244548_1_.startsWith("/")) {
            this.handleCommand(p_244548_1_);
         } else {
            ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", this.player.getDisplayName(), net.minecraftforge.common.ForgeHooks.newChatWithLinks(p_244548_1_));
            itextcomponent = net.minecraftforge.common.ForgeHooks.onServerChatEvent(this, p_244548_1_, itextcomponent);
            if (itextcomponent == null) return;
            this.server.getPlayerList().broadcastMessage(itextcomponent, ChatType.CHAT, this.player.getUUID());
         }

         this.chatSpamTickCount += 20;
         if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(new TranslationTextComponent("disconnect.spam"));
         }

      }
   }

   private void handleCommand(String p_147361_1_) {
      this.server.getCommands().performCommand(this.player.createCommandSourceStack(), p_147361_1_);
   }

   public void handleAnimate(CAnimateHandPacket p_175087_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175087_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      this.player.swing(p_175087_1_.getHand());
   }

   public void handlePlayerCommand(CEntityActionPacket p_147357_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147357_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      switch(p_147357_1_.getAction()) {
      case PRESS_SHIFT_KEY:
         this.player.setShiftKeyDown(true);
         break;
      case RELEASE_SHIFT_KEY:
         this.player.setShiftKeyDown(false);
         break;
      case START_SPRINTING:
         this.player.setSprinting(true);
         break;
      case STOP_SPRINTING:
         this.player.setSprinting(false);
         break;
      case STOP_SLEEPING:
         if (this.player.isSleeping()) {
            this.player.stopSleepInBed(false, true);
            this.awaitingPositionFromClient = this.player.position();
         }
         break;
      case START_RIDING_JUMP:
         if (this.player.getVehicle() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount1 = (IJumpingMount)this.player.getVehicle();
            int i = p_147357_1_.getData();
            if (ijumpingmount1.canJump() && i > 0) {
               ijumpingmount1.handleStartJump(i);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.player.getVehicle() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount = (IJumpingMount)this.player.getVehicle();
            ijumpingmount.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if (this.player.getVehicle() instanceof AbstractHorseEntity) {
            ((AbstractHorseEntity)this.player.getVehicle()).openInventory(this.player);
         }
         break;
      case START_FALL_FLYING:
         if (!this.player.tryToStartFallFlying()) {
            this.player.stopFallFlying();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void handleInteract(CUseEntityPacket p_147340_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147340_1_, this, this.player.getLevel());
      ServerWorld serverworld = this.player.getLevel();
      Entity entity = p_147340_1_.getTarget(serverworld);
      this.player.resetLastActionTime();
      this.player.setShiftKeyDown(p_147340_1_.isUsingSecondaryAction());
      if (entity != null) {
         double d0 = 36.0D;
         if (this.player.distanceToSqr(entity) < 36.0D) {
            Hand hand = p_147340_1_.getHand();
            ItemStack itemstack = hand != null ? this.player.getItemInHand(hand).copy() : ItemStack.EMPTY;
            Optional<ActionResultType> optional = Optional.empty();
            if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT) {
               optional = Optional.of(this.player.interactOn(entity, hand));
            } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT_AT) {
               if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, p_147340_1_.getLocation(), hand) != null) return;
               optional = Optional.of(entity.interactAt(this.player, p_147340_1_.getLocation(), hand));
            } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.ATTACK) {
               if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == this.player) {
                  this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
                  LOGGER.warn("Player {} tried to attack an invalid entity", (Object)this.player.getName().getString());
                  return;
               }

               this.player.attack(entity);
            }

            if (optional.isPresent() && optional.get().consumesAction()) {
               CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(this.player, itemstack, entity);
               if (optional.get().shouldSwing()) {
                  this.player.swing(hand, true);
               }
            }
         }
      }

   }

   public void handleClientCommand(CClientStatusPacket p_147342_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147342_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      CClientStatusPacket.State cclientstatuspacket$state = p_147342_1_.getAction();
      switch(cclientstatuspacket$state) {
      case PERFORM_RESPAWN:
         if (this.player.wonGame) {
            this.player.wonGame = false;
            this.player = this.server.getPlayerList().respawn(this.player, true);
            CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
         } else {
            if (this.player.getHealth() > 0.0F) {
               return;
            }

            this.player = this.server.getPlayerList().respawn(this.player, false);
            if (this.server.isHardcore()) {
               this.player.setGameMode(GameType.SPECTATOR);
               this.player.getLevel().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(false, this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   public void handleContainerClose(CCloseWindowPacket p_147356_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147356_1_, this, this.player.getLevel());
      this.player.doCloseContainer();
   }

   public void handleContainerClick(CClickWindowPacket p_147351_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147351_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == p_147351_1_.getContainerId() && this.player.containerMenu.isSynched(this.player)) {
         if (this.player.isSpectator()) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.player.containerMenu.slots.size(); ++i) {
               nonnulllist.add(this.player.containerMenu.slots.get(i).getItem());
            }

            this.player.refreshContainer(this.player.containerMenu, nonnulllist);
         } else {
            ItemStack itemstack1 = this.player.containerMenu.clicked(p_147351_1_.getSlotNum(), p_147351_1_.getButtonNum(), p_147351_1_.getClickType(), this.player);
            if (ItemStack.matches(p_147351_1_.getItem(), itemstack1)) {
               this.player.connection.send(new SConfirmTransactionPacket(p_147351_1_.getContainerId(), p_147351_1_.getUid(), true));
               this.player.ignoreSlotUpdateHack = true;
               this.player.containerMenu.broadcastChanges();
               this.player.broadcastCarriedItem();
               this.player.ignoreSlotUpdateHack = false;
            } else {
               this.expectedAcks.put(this.player.containerMenu.containerId, p_147351_1_.getUid());
               this.player.connection.send(new SConfirmTransactionPacket(p_147351_1_.getContainerId(), p_147351_1_.getUid(), false));
               this.player.containerMenu.setSynched(this.player, false);
               NonNullList<ItemStack> nonnulllist1 = NonNullList.create();

               for(int j = 0; j < this.player.containerMenu.slots.size(); ++j) {
                  ItemStack itemstack = this.player.containerMenu.slots.get(j).getItem();
                  nonnulllist1.add(itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
               }

               this.player.refreshContainer(this.player.containerMenu, nonnulllist1);
            }
         }
      }

   }

   public void handlePlaceRecipe(CPlaceRecipePacket p_194308_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_194308_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      if (!this.player.isSpectator() && this.player.containerMenu.containerId == p_194308_1_.getContainerId() && this.player.containerMenu.isSynched(this.player) && this.player.containerMenu instanceof RecipeBookContainer) {
         this.server.getRecipeManager().byKey(p_194308_1_.getRecipe()).ifPresent((p_241165_2_) -> {
            ((RecipeBookContainer)this.player.containerMenu).handlePlacement(p_194308_1_.isShiftDown(), p_241165_2_, this.player);
         });
      }
   }

   public void handleContainerButtonClick(CEnchantItemPacket p_147338_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147338_1_, this, this.player.getLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == p_147338_1_.getContainerId() && this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.clickMenuButton(this.player, p_147338_1_.getButtonId());
         this.player.containerMenu.broadcastChanges();
      }

   }

   public void handleSetCreativeModeSlot(CCreativeInventoryActionPacket p_147344_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147344_1_, this, this.player.getLevel());
      if (this.player.gameMode.isCreative()) {
         boolean flag = p_147344_1_.getSlotNum() < 0;
         ItemStack itemstack = p_147344_1_.getItem();
         CompoundNBT compoundnbt = itemstack.getTagElement("BlockEntityTag");
         if (!itemstack.isEmpty() && compoundnbt != null && compoundnbt.contains("x") && compoundnbt.contains("y") && compoundnbt.contains("z")) {
            BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
            TileEntity tileentity = this.player.level.getBlockEntity(blockpos);
            if (tileentity != null) {
               CompoundNBT compoundnbt1 = tileentity.save(new CompoundNBT());
               compoundnbt1.remove("x");
               compoundnbt1.remove("y");
               compoundnbt1.remove("z");
               itemstack.addTagElement("BlockEntityTag", compoundnbt1);
            }
         }

         boolean flag1 = p_147344_1_.getSlotNum() >= 1 && p_147344_1_.getSlotNum() <= 45;
         boolean flag2 = itemstack.isEmpty() || itemstack.getDamageValue() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();
         if (flag1 && flag2) {
            if (itemstack.isEmpty()) {
               this.player.inventoryMenu.setItem(p_147344_1_.getSlotNum(), ItemStack.EMPTY);
            } else {
               this.player.inventoryMenu.setItem(p_147344_1_.getSlotNum(), itemstack);
            }

            this.player.inventoryMenu.setSynched(this.player, true);
            this.player.inventoryMenu.broadcastChanges();
         } else if (flag && flag2 && this.dropSpamTickCount < 200) {
            this.dropSpamTickCount += 20;
            this.player.drop(itemstack, true);
         }
      }

   }

   public void handleContainerAck(CConfirmTransactionPacket p_147339_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147339_1_, this, this.player.getLevel());
      int i = this.player.containerMenu.containerId;
      if (i == p_147339_1_.getContainerId() && this.expectedAcks.getOrDefault(i, (short)(p_147339_1_.getUid() + 1)) == p_147339_1_.getUid() && !this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.setSynched(this.player, true);
      }

   }

   public void handleSignUpdate(CUpdateSignPacket p_147343_1_) {
      List<String> list = Stream.of(p_147343_1_.getLines()).map(TextFormatting::stripFormatting).collect(Collectors.toList());
      this.filterTextPacket(list, (p_244547_2_) -> {
         this.updateSignText(p_147343_1_, p_244547_2_);
      });
   }

   private void updateSignText(CUpdateSignPacket p_244542_1_, List<String> p_244542_2_) {
      this.player.resetLastActionTime();
      ServerWorld serverworld = this.player.getLevel();
      BlockPos blockpos = p_244542_1_.getPos();
      if (serverworld.hasChunkAt(blockpos)) {
         BlockState blockstate = serverworld.getBlockState(blockpos);
         TileEntity tileentity = serverworld.getBlockEntity(blockpos);
         if (!(tileentity instanceof SignTileEntity)) {
            return;
         }

         SignTileEntity signtileentity = (SignTileEntity)tileentity;
         if (!signtileentity.isEditable() || signtileentity.getPlayerWhoMayEdit() != this.player) {
            LOGGER.warn("Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
            return;
         }

         for(int i = 0; i < p_244542_2_.size(); ++i) {
            signtileentity.setMessage(i, new StringTextComponent(p_244542_2_.get(i)));
         }

         signtileentity.setChanged();
         serverworld.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
      }

   }

   public void handleKeepAlive(CKeepAlivePacket p_147353_1_) {
      if (this.keepAlivePending && p_147353_1_.getId() == this.keepAliveChallenge) {
         int i = (int)(Util.getMillis() - this.keepAliveTime);
         this.player.latency = (this.player.latency * 3 + i) / 4;
         this.keepAlivePending = false;
      } else if (!this.isSingleplayerOwner()) {
         this.disconnect(new TranslationTextComponent("disconnect.timeout"));
      }

   }

   public void handlePlayerAbilities(CPlayerAbilitiesPacket p_147348_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147348_1_, this, this.player.getLevel());
      this.player.abilities.flying = p_147348_1_.isFlying() && this.player.abilities.mayfly;
   }

   public void handleClientInformation(CClientSettingsPacket p_147352_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147352_1_, this, this.player.getLevel());
      this.player.updateOptions(p_147352_1_);
   }

   public void handleCustomPayload(CCustomPayloadPacket p_147349_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147349_1_, this, this.player.getLevel());
      net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_147349_1_, this.connection);
   }

   public void handleChangeDifficulty(CSetDifficultyPacket p_217263_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217263_1_, this, this.player.getLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficulty(p_217263_1_.getDifficulty(), false);
      }
   }

   public void handleLockDifficulty(CLockDifficultyPacket p_217261_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217261_1_, this, this.player.getLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficultyLocked(p_217261_1_.isLocked());
      }
   }
}
