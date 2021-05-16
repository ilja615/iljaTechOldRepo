package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LootCommand {
   public static final SuggestionProvider<CommandSource> SUGGEST_LOOT_TABLE = (p_218873_0_, p_218873_1_) -> {
      LootTableManager loottablemanager = p_218873_0_.getSource().getServer().getLootTables();
      return ISuggestionProvider.suggestResource(loottablemanager.getIds(), p_218873_1_);
   };
   private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType((p_218896_0_) -> {
      return new TranslationTextComponent("commands.drop.no_held_items", p_218896_0_);
   });
   private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType((p_218889_0_) -> {
      return new TranslationTextComponent("commands.drop.no_loot_table", p_218889_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_218886_0_) {
      p_218886_0_.register(addTargets(Commands.literal("loot").requires((p_218903_0_) -> {
         return p_218903_0_.hasPermission(2);
      }), (p_218880_0_, p_218880_1_) -> {
         return p_218880_0_.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218899_1_) -> {
            return dropFishingLoot(p_218899_1_, ResourceLocationArgument.getId(p_218899_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218899_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         }).then(Commands.argument("tool", ItemArgument.item()).executes((p_218874_1_) -> {
            return dropFishingLoot(p_218874_1_, ResourceLocationArgument.getId(p_218874_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218874_1_, "pos"), ItemArgument.getItem(p_218874_1_, "tool").createItemStack(1, false), p_218880_1_);
         })).then(Commands.literal("mainhand").executes((p_218892_1_) -> {
            return dropFishingLoot(p_218892_1_, ResourceLocationArgument.getId(p_218892_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218892_1_, "pos"), getSourceHandItem(p_218892_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         })).then(Commands.literal("offhand").executes((p_218898_1_) -> {
            return dropFishingLoot(p_218898_1_, ResourceLocationArgument.getId(p_218898_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218898_1_, "pos"), getSourceHandItem(p_218898_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes((p_218861_1_) -> {
            return dropChestLoot(p_218861_1_, ResourceLocationArgument.getId(p_218861_1_, "loot_table"), p_218880_1_);
         }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((p_218891_1_) -> {
            return dropKillLoot(p_218891_1_, EntityArgument.getEntity(p_218891_1_, "target"), p_218880_1_);
         }))).then(Commands.literal("mine").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218897_1_) -> {
            return dropBlockLoot(p_218897_1_, BlockPosArgument.getLoadedBlockPos(p_218897_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         }).then(Commands.argument("tool", ItemArgument.item()).executes((p_218878_1_) -> {
            return dropBlockLoot(p_218878_1_, BlockPosArgument.getLoadedBlockPos(p_218878_1_, "pos"), ItemArgument.getItem(p_218878_1_, "tool").createItemStack(1, false), p_218880_1_);
         })).then(Commands.literal("mainhand").executes((p_218895_1_) -> {
            return dropBlockLoot(p_218895_1_, BlockPosArgument.getLoadedBlockPos(p_218895_1_, "pos"), getSourceHandItem(p_218895_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         })).then(Commands.literal("offhand").executes((p_218888_1_) -> {
            return dropBlockLoot(p_218888_1_, BlockPosArgument.getLoadedBlockPos(p_218888_1_, "pos"), getSourceHandItem(p_218888_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))));
      }));
   }

   private static <T extends ArgumentBuilder<CommandSource, T>> T addTargets(T p_218868_0_, LootCommand.ISourceArgumentBuilder p_218868_1_) {
      return p_218868_0_.then(Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218866_0_, p_218866_1_, p_218866_2_) -> {
         return entityReplace(EntityArgument.getEntities(p_218866_0_, "entities"), SlotArgument.getSlot(p_218866_0_, "slot"), p_218866_1_.size(), p_218866_1_, p_218866_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218884_0_, p_218884_1_, p_218884_2_) -> {
         return entityReplace(EntityArgument.getEntities(p_218884_0_, "entities"), SlotArgument.getSlot(p_218884_0_, "slot"), IntegerArgumentType.getInteger(p_218884_0_, "count"), p_218884_1_, p_218884_2_);
      }))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218864_0_, p_218864_1_, p_218864_2_) -> {
         return blockReplace(p_218864_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218864_0_, "targetPos"), SlotArgument.getSlot(p_218864_0_, "slot"), p_218864_1_.size(), p_218864_1_, p_218864_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218870_0_, p_218870_1_, p_218870_2_) -> {
         return blockReplace(p_218870_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218870_0_, "targetPos"), IntegerArgumentType.getInteger(p_218870_0_, "slot"), IntegerArgumentType.getInteger(p_218870_0_, "count"), p_218870_1_, p_218870_2_);
      })))))).then(Commands.literal("insert").then(p_218868_1_.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (p_218885_0_, p_218885_1_, p_218885_2_) -> {
         return blockDistribute(p_218885_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218885_0_, "targetPos"), p_218885_1_, p_218885_2_);
      }))).then(Commands.literal("give").then(p_218868_1_.construct(Commands.argument("players", EntityArgument.players()), (p_218867_0_, p_218867_1_, p_218867_2_) -> {
         return playerGive(EntityArgument.getPlayers(p_218867_0_, "players"), p_218867_1_, p_218867_2_);
      }))).then(Commands.literal("spawn").then(p_218868_1_.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (p_218877_0_, p_218877_1_, p_218877_2_) -> {
         return dropInWorld(p_218877_0_.getSource(), Vec3Argument.getVec3(p_218877_0_, "targetPos"), p_218877_1_, p_218877_2_);
      })));
   }

   private static IInventory getContainer(CommandSource p_218862_0_, BlockPos p_218862_1_) throws CommandSyntaxException {
      TileEntity tileentity = p_218862_0_.getLevel().getBlockEntity(p_218862_1_);
      if (!(tileentity instanceof IInventory)) {
         throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
      } else {
         return (IInventory)tileentity;
      }
   }

   private static int blockDistribute(CommandSource p_218900_0_, BlockPos p_218900_1_, List<ItemStack> p_218900_2_, LootCommand.ISuccessListener p_218900_3_) throws CommandSyntaxException {
      IInventory iinventory = getContainer(p_218900_0_, p_218900_1_);
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218900_2_.size());

      for(ItemStack itemstack : p_218900_2_) {
         if (distributeToContainer(iinventory, itemstack.copy())) {
            iinventory.setChanged();
            list.add(itemstack);
         }
      }

      p_218900_3_.accept(list);
      return list.size();
   }

   private static boolean distributeToContainer(IInventory p_218890_0_, ItemStack p_218890_1_) {
      boolean flag = false;

      for(int i = 0; i < p_218890_0_.getContainerSize() && !p_218890_1_.isEmpty(); ++i) {
         ItemStack itemstack = p_218890_0_.getItem(i);
         if (p_218890_0_.canPlaceItem(i, p_218890_1_)) {
            if (itemstack.isEmpty()) {
               p_218890_0_.setItem(i, p_218890_1_);
               flag = true;
               break;
            }

            if (canMergeItems(itemstack, p_218890_1_)) {
               int j = p_218890_1_.getMaxStackSize() - itemstack.getCount();
               int k = Math.min(p_218890_1_.getCount(), j);
               p_218890_1_.shrink(k);
               itemstack.grow(k);
               flag = true;
            }
         }
      }

      return flag;
   }

   private static int blockReplace(CommandSource p_218894_0_, BlockPos p_218894_1_, int p_218894_2_, int p_218894_3_, List<ItemStack> p_218894_4_, LootCommand.ISuccessListener p_218894_5_) throws CommandSyntaxException {
      IInventory iinventory = getContainer(p_218894_0_, p_218894_1_);
      int i = iinventory.getContainerSize();
      if (p_218894_2_ >= 0 && p_218894_2_ < i) {
         List<ItemStack> list = Lists.newArrayListWithCapacity(p_218894_4_.size());

         for(int j = 0; j < p_218894_3_; ++j) {
            int k = p_218894_2_ + j;
            ItemStack itemstack = j < p_218894_4_.size() ? p_218894_4_.get(j) : ItemStack.EMPTY;
            if (iinventory.canPlaceItem(k, itemstack)) {
               iinventory.setItem(k, itemstack);
               list.add(itemstack);
            }
         }

         p_218894_5_.accept(list);
         return list.size();
      } else {
         throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create(p_218894_2_);
      }
   }

   private static boolean canMergeItems(ItemStack p_218883_0_, ItemStack p_218883_1_) {
      return p_218883_0_.getItem() == p_218883_1_.getItem() && p_218883_0_.getDamageValue() == p_218883_1_.getDamageValue() && p_218883_0_.getCount() <= p_218883_0_.getMaxStackSize() && Objects.equals(p_218883_0_.getTag(), p_218883_1_.getTag());
   }

   private static int playerGive(Collection<ServerPlayerEntity> p_218859_0_, List<ItemStack> p_218859_1_, LootCommand.ISuccessListener p_218859_2_) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218859_1_.size());

      for(ItemStack itemstack : p_218859_1_) {
         for(ServerPlayerEntity serverplayerentity : p_218859_0_) {
            if (serverplayerentity.inventory.add(itemstack.copy())) {
               list.add(itemstack);
            }
         }
      }

      p_218859_2_.accept(list);
      return list.size();
   }

   private static void setSlots(Entity p_218901_0_, List<ItemStack> p_218901_1_, int p_218901_2_, int p_218901_3_, List<ItemStack> p_218901_4_) {
      for(int i = 0; i < p_218901_3_; ++i) {
         ItemStack itemstack = i < p_218901_1_.size() ? p_218901_1_.get(i) : ItemStack.EMPTY;
         if (p_218901_0_.setSlot(p_218901_2_ + i, itemstack.copy())) {
            p_218901_4_.add(itemstack);
         }
      }

   }

   private static int entityReplace(Collection<? extends Entity> p_218865_0_, int p_218865_1_, int p_218865_2_, List<ItemStack> p_218865_3_, LootCommand.ISuccessListener p_218865_4_) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218865_3_.size());

      for(Entity entity : p_218865_0_) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            serverplayerentity.inventoryMenu.broadcastChanges();
            setSlots(entity, p_218865_3_, p_218865_1_, p_218865_2_, list);
            serverplayerentity.inventoryMenu.broadcastChanges();
         } else {
            setSlots(entity, p_218865_3_, p_218865_1_, p_218865_2_, list);
         }
      }

      p_218865_4_.accept(list);
      return list.size();
   }

   private static int dropInWorld(CommandSource p_218881_0_, Vector3d p_218881_1_, List<ItemStack> p_218881_2_, LootCommand.ISuccessListener p_218881_3_) throws CommandSyntaxException {
      ServerWorld serverworld = p_218881_0_.getLevel();
      p_218881_2_.forEach((p_218882_2_) -> {
         ItemEntity itementity = new ItemEntity(serverworld, p_218881_1_.x, p_218881_1_.y, p_218881_1_.z, p_218882_2_.copy());
         itementity.setDefaultPickUpDelay();
         serverworld.addFreshEntity(itementity);
      });
      p_218881_3_.accept(p_218881_2_);
      return p_218881_2_.size();
   }

   private static void callback(CommandSource p_218875_0_, List<ItemStack> p_218875_1_) {
      if (p_218875_1_.size() == 1) {
         ItemStack itemstack = p_218875_1_.get(0);
         p_218875_0_.sendSuccess(new TranslationTextComponent("commands.drop.success.single", itemstack.getCount(), itemstack.getDisplayName()), false);
      } else {
         p_218875_0_.sendSuccess(new TranslationTextComponent("commands.drop.success.multiple", p_218875_1_.size()), false);
      }

   }

   private static void callback(CommandSource p_218860_0_, List<ItemStack> p_218860_1_, ResourceLocation p_218860_2_) {
      if (p_218860_1_.size() == 1) {
         ItemStack itemstack = p_218860_1_.get(0);
         p_218860_0_.sendSuccess(new TranslationTextComponent("commands.drop.success.single_with_table", itemstack.getCount(), itemstack.getDisplayName(), p_218860_2_), false);
      } else {
         p_218860_0_.sendSuccess(new TranslationTextComponent("commands.drop.success.multiple_with_table", p_218860_1_.size(), p_218860_2_), false);
      }

   }

   private static ItemStack getSourceHandItem(CommandSource p_218872_0_, EquipmentSlotType p_218872_1_) throws CommandSyntaxException {
      Entity entity = p_218872_0_.getEntityOrException();
      if (entity instanceof LivingEntity) {
         return ((LivingEntity)entity).getItemBySlot(p_218872_1_);
      } else {
         throw ERROR_NO_HELD_ITEMS.create(entity.getDisplayName());
      }
   }

   private static int dropBlockLoot(CommandContext<CommandSource> p_218879_0_, BlockPos p_218879_1_, ItemStack p_218879_2_, LootCommand.ITargetHandler p_218879_3_) throws CommandSyntaxException {
      CommandSource commandsource = p_218879_0_.getSource();
      ServerWorld serverworld = commandsource.getLevel();
      BlockState blockstate = serverworld.getBlockState(p_218879_1_);
      TileEntity tileentity = serverworld.getBlockEntity(p_218879_1_);
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverworld)).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(p_218879_1_)).withParameter(LootParameters.BLOCK_STATE, blockstate).withOptionalParameter(LootParameters.BLOCK_ENTITY, tileentity).withOptionalParameter(LootParameters.THIS_ENTITY, commandsource.getEntity()).withParameter(LootParameters.TOOL, p_218879_2_);
      List<ItemStack> list = blockstate.getDrops(lootcontext$builder);
      return p_218879_3_.accept(p_218879_0_, list, (p_218893_2_) -> {
         callback(commandsource, p_218893_2_, blockstate.getBlock().getLootTable());
      });
   }

   private static int dropKillLoot(CommandContext<CommandSource> p_218869_0_, Entity p_218869_1_, LootCommand.ITargetHandler p_218869_2_) throws CommandSyntaxException {
      if (!(p_218869_1_ instanceof LivingEntity)) {
         throw ERROR_NO_LOOT_TABLE.create(p_218869_1_.getDisplayName());
      } else {
         ResourceLocation resourcelocation = ((LivingEntity)p_218869_1_).getLootTable();
         CommandSource commandsource = p_218869_0_.getSource();
         LootContext.Builder lootcontext$builder = new LootContext.Builder(commandsource.getLevel());
         Entity entity = commandsource.getEntity();
         if (entity instanceof PlayerEntity) {
            lootcontext$builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity);
         }

         lootcontext$builder.withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
         lootcontext$builder.withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, entity);
         lootcontext$builder.withOptionalParameter(LootParameters.KILLER_ENTITY, entity);
         lootcontext$builder.withParameter(LootParameters.THIS_ENTITY, p_218869_1_);
         lootcontext$builder.withParameter(LootParameters.ORIGIN, commandsource.getPosition());
         LootTable loottable = commandsource.getServer().getLootTables().get(resourcelocation);
         List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootParameterSets.ENTITY));
         return p_218869_2_.accept(p_218869_0_, list, (p_218863_2_) -> {
            callback(commandsource, p_218863_2_, resourcelocation);
         });
      }
   }

   private static int dropChestLoot(CommandContext<CommandSource> p_218887_0_, ResourceLocation p_218887_1_, LootCommand.ITargetHandler p_218887_2_) throws CommandSyntaxException {
      CommandSource commandsource = p_218887_0_.getSource();
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(commandsource.getLevel())).withOptionalParameter(LootParameters.THIS_ENTITY, commandsource.getEntity()).withParameter(LootParameters.ORIGIN, commandsource.getPosition());
      return drop(p_218887_0_, p_218887_1_, lootcontext$builder.create(LootParameterSets.CHEST), p_218887_2_);
   }

   private static int dropFishingLoot(CommandContext<CommandSource> p_218876_0_, ResourceLocation p_218876_1_, BlockPos p_218876_2_, ItemStack p_218876_3_, LootCommand.ITargetHandler p_218876_4_) throws CommandSyntaxException {
      CommandSource commandsource = p_218876_0_.getSource();
      LootContext lootcontext = (new LootContext.Builder(commandsource.getLevel())).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(p_218876_2_)).withParameter(LootParameters.TOOL, p_218876_3_).withOptionalParameter(LootParameters.THIS_ENTITY, commandsource.getEntity()).create(LootParameterSets.FISHING);
      return drop(p_218876_0_, p_218876_1_, lootcontext, p_218876_4_);
   }

   private static int drop(CommandContext<CommandSource> p_218871_0_, ResourceLocation p_218871_1_, LootContext p_218871_2_, LootCommand.ITargetHandler p_218871_3_) throws CommandSyntaxException {
      CommandSource commandsource = p_218871_0_.getSource();
      LootTable loottable = commandsource.getServer().getLootTables().get(p_218871_1_);
      List<ItemStack> list = loottable.getRandomItems(p_218871_2_);
      return p_218871_3_.accept(p_218871_0_, list, (p_218902_1_) -> {
         callback(commandsource, p_218902_1_);
      });
   }

   @FunctionalInterface
   interface ISourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> construct(ArgumentBuilder<CommandSource, ?> p_construct_1_, LootCommand.ITargetHandler p_construct_2_);
   }

   @FunctionalInterface
   interface ISuccessListener {
      void accept(List<ItemStack> p_accept_1_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface ITargetHandler {
      int accept(CommandContext<CommandSource> p_accept_1_, List<ItemStack> p_accept_2_, LootCommand.ISuccessListener p_accept_3_) throws CommandSyntaxException;
   }
}
