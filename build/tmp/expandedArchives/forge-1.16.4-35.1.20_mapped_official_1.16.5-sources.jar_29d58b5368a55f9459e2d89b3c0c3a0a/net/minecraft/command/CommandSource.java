package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CommandSource implements ISuggestionProvider {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(new TranslationTextComponent("permissions.requires.player"));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(new TranslationTextComponent("permissions.requires.entity"));
   private final ICommandSource source;
   private final Vector3d worldPosition;
   private final ServerWorld level;
   private final int permissionLevel;
   private final String textName;
   private final ITextComponent displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   private final ResultConsumer<CommandSource> consumer;
   private final EntityAnchorArgument.Type anchor;
   private final Vector2f rotation;

   public CommandSource(ICommandSource p_i49552_1_, Vector3d p_i49552_2_, Vector2f p_i49552_3_, ServerWorld p_i49552_4_, int p_i49552_5_, String p_i49552_6_, ITextComponent p_i49552_7_, MinecraftServer p_i49552_8_, @Nullable Entity p_i49552_9_) {
      this(p_i49552_1_, p_i49552_2_, p_i49552_3_, p_i49552_4_, p_i49552_5_, p_i49552_6_, p_i49552_7_, p_i49552_8_, p_i49552_9_, false, (p_197032_0_, p_197032_1_, p_197032_2_) -> {
      }, EntityAnchorArgument.Type.FEET);
   }

   protected CommandSource(ICommandSource p_i49553_1_, Vector3d p_i49553_2_, Vector2f p_i49553_3_, ServerWorld p_i49553_4_, int p_i49553_5_, String p_i49553_6_, ITextComponent p_i49553_7_, MinecraftServer p_i49553_8_, @Nullable Entity p_i49553_9_, boolean p_i49553_10_, ResultConsumer<CommandSource> p_i49553_11_, EntityAnchorArgument.Type p_i49553_12_) {
      this.source = p_i49553_1_;
      this.worldPosition = p_i49553_2_;
      this.level = p_i49553_4_;
      this.silent = p_i49553_10_;
      this.entity = p_i49553_9_;
      this.permissionLevel = p_i49553_5_;
      this.textName = p_i49553_6_;
      this.displayName = p_i49553_7_;
      this.server = p_i49553_8_;
      this.consumer = p_i49553_11_;
      this.anchor = p_i49553_12_;
      this.rotation = p_i49553_3_;
   }

   public CommandSource withEntity(Entity p_197024_1_) {
      return this.entity == p_197024_1_ ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, p_197024_1_.getName().getString(), p_197024_1_.getDisplayName(), this.server, p_197024_1_, this.silent, this.consumer, this.anchor);
   }

   public CommandSource withPosition(Vector3d p_201009_1_) {
      return this.worldPosition.equals(p_201009_1_) ? this : new CommandSource(this.source, p_201009_1_, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSource withRotation(Vector2f p_201007_1_) {
      return this.rotation.equals(p_201007_1_) ? this : new CommandSource(this.source, this.worldPosition, p_201007_1_, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSource withCallback(ResultConsumer<CommandSource> p_197029_1_) {
      return this.consumer.equals(p_197029_1_) ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, p_197029_1_, this.anchor);
   }

   public CommandSource withCallback(ResultConsumer<CommandSource> p_209550_1_, BinaryOperator<ResultConsumer<CommandSource>> p_209550_2_) {
      ResultConsumer<CommandSource> resultconsumer = p_209550_2_.apply(this.consumer, p_209550_1_);
      return this.withCallback(resultconsumer);
   }

   public CommandSource withSuppressedOutput() {
      return this.silent ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor);
   }

   public CommandSource withPermission(int p_197033_1_) {
      return p_197033_1_ == this.permissionLevel ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, p_197033_1_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSource withMaximumPermission(int p_197026_1_) {
      return p_197026_1_ <= this.permissionLevel ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, p_197026_1_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSource withAnchor(EntityAnchorArgument.Type p_201010_1_) {
      return p_201010_1_ == this.anchor ? this : new CommandSource(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, p_201010_1_);
   }

   public CommandSource withLevel(ServerWorld p_201003_1_) {
      if (p_201003_1_ == this.level) {
         return this;
      } else {
         double d0 = DimensionType.getTeleportationScale(this.level.dimensionType(), p_201003_1_.dimensionType());
         Vector3d vector3d = new Vector3d(this.worldPosition.x * d0, this.worldPosition.y, this.worldPosition.z * d0);
         return new CommandSource(this.source, vector3d, this.rotation, p_201003_1_, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
      }
   }

   public CommandSource facing(Entity p_201006_1_, EntityAnchorArgument.Type p_201006_2_) throws CommandSyntaxException {
      return this.facing(p_201006_2_.apply(p_201006_1_));
   }

   public CommandSource facing(Vector3d p_201005_1_) throws CommandSyntaxException {
      Vector3d vector3d = this.anchor.apply(this);
      double d0 = p_201005_1_.x - vector3d.x;
      double d1 = p_201005_1_.y - vector3d.y;
      double d2 = p_201005_1_.z - vector3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withRotation(new Vector2f(f, f1));
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   public boolean hasPermission(int p_197034_1_) {
      return this.permissionLevel >= p_197034_1_;
   }

   public Vector3d getPosition() {
      return this.worldPosition;
   }

   public ServerWorld getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if (this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayerEntity getPlayerOrException() throws CommandSyntaxException {
      if (!(this.entity instanceof ServerPlayerEntity)) {
         throw ERROR_NOT_PLAYER.create();
      } else {
         return (ServerPlayerEntity)this.entity;
      }
   }

   public Vector2f getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Type getAnchor() {
      return this.anchor;
   }

   public void sendSuccess(ITextComponent p_197030_1_, boolean p_197030_2_) {
      if (this.source.acceptsSuccess() && !this.silent) {
         this.source.sendMessage(p_197030_1_, Util.NIL_UUID);
      }

      if (p_197030_2_ && this.source.shouldInformAdmins() && !this.silent) {
         this.broadcastToAdmins(p_197030_1_);
      }

   }

   private void broadcastToAdmins(ITextComponent p_197020_1_) {
      ITextComponent itextcomponent = (new TranslationTextComponent("chat.type.admin", this.getDisplayName(), p_197020_1_)).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
            if (serverplayerentity != this.source && this.server.getPlayerList().isOp(serverplayerentity.getGameProfile())) {
               serverplayerentity.sendMessage(itextcomponent, Util.NIL_UUID);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendMessage(itextcomponent, Util.NIL_UUID);
      }

   }

   public void sendFailure(ITextComponent p_197021_1_) {
      if (this.source.acceptsFailure() && !this.silent) {
         this.source.sendMessage((new StringTextComponent("")).append(p_197021_1_).withStyle(TextFormatting.RED), Util.NIL_UUID);
      }

   }

   public void onCommandComplete(CommandContext<CommandSource> p_197038_1_, boolean p_197038_2_, int p_197038_3_) {
      if (this.consumer != null) {
         this.consumer.onCommandComplete(p_197038_1_, p_197038_2_, p_197038_3_);
      }

   }

   public Collection<String> getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection<String> getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getAvailableSoundEvents() {
      return Registry.SOUND_EVENT.keySet();
   }

   public Stream<ResourceLocation> getRecipeNames() {
      return this.server.getRecipeManager().getRecipeIds();
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<ISuggestionProvider> p_197009_1_, SuggestionsBuilder p_197009_2_) {
      return null;
   }

   public Set<RegistryKey<World>> levels() {
      return this.server.levelKeys();
   }

   public DynamicRegistries registryAccess() {
      return this.server.registryAccess();
   }
}
