package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Properties p_i48513_1_) {
      super(p_i48513_1_);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return true;
   }

   public boolean canAttackBlock(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      if (!p_195938_2_.isClientSide) {
         this.handleInteraction(p_195938_4_, p_195938_1_, p_195938_2_, p_195938_3_, false, p_195938_4_.getItemInHand(Hand.MAIN_HAND));
      }

      return false;
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      PlayerEntity playerentity = p_195939_1_.getPlayer();
      World world = p_195939_1_.getLevel();
      if (!world.isClientSide && playerentity != null) {
         BlockPos blockpos = p_195939_1_.getClickedPos();
         this.handleInteraction(playerentity, world.getBlockState(blockpos), world, blockpos, true, p_195939_1_.getItemInHand());
      }

      return ActionResultType.sidedSuccess(world.isClientSide);
   }

   private void handleInteraction(PlayerEntity p_195958_1_, BlockState p_195958_2_, IWorld p_195958_3_, BlockPos p_195958_4_, boolean p_195958_5_, ItemStack p_195958_6_) {
      if (p_195958_1_.canUseGameMasterBlocks()) {
         Block block = p_195958_2_.getBlock();
         StateContainer<Block, BlockState> statecontainer = block.getStateDefinition();
         Collection<Property<?>> collection = statecontainer.getProperties();
         String s = Registry.BLOCK.getKey(block).toString();
         if (collection.isEmpty()) {
            message(p_195958_1_, new TranslationTextComponent(this.getDescriptionId() + ".empty", s));
         } else {
            CompoundNBT compoundnbt = p_195958_6_.getOrCreateTagElement("DebugProperty");
            String s1 = compoundnbt.getString(s);
            Property<?> property = statecontainer.getProperty(s1);
            if (p_195958_5_) {
               if (property == null) {
                  property = collection.iterator().next();
               }

               BlockState blockstate = cycleState(p_195958_2_, property, p_195958_1_.isSecondaryUseActive());
               p_195958_3_.setBlock(p_195958_4_, blockstate, 18);
               message(p_195958_1_, new TranslationTextComponent(this.getDescriptionId() + ".update", property.getName(), getNameHelper(blockstate, property)));
            } else {
               property = getRelative(collection, property, p_195958_1_.isSecondaryUseActive());
               String s2 = property.getName();
               compoundnbt.putString(s, s2);
               message(p_195958_1_, new TranslationTextComponent(this.getDescriptionId() + ".select", s2, getNameHelper(p_195958_2_, property)));
            }

         }
      }
   }

   private static <T extends Comparable<T>> BlockState cycleState(BlockState p_195960_0_, Property<T> p_195960_1_, boolean p_195960_2_) {
      return p_195960_0_.setValue(p_195960_1_, getRelative(p_195960_1_.getPossibleValues(), p_195960_0_.getValue(p_195960_1_), p_195960_2_));
   }

   private static <T> T getRelative(Iterable<T> p_195959_0_, @Nullable T p_195959_1_, boolean p_195959_2_) {
      return (T)(p_195959_2_ ? Util.findPreviousInIterable(p_195959_0_, p_195959_1_) : Util.findNextInIterable(p_195959_0_, p_195959_1_));
   }

   private static void message(PlayerEntity p_195956_0_, ITextComponent p_195956_1_) {
      ((ServerPlayerEntity)p_195956_0_).sendMessage(p_195956_1_, ChatType.GAME_INFO, Util.NIL_UUID);
   }

   private static <T extends Comparable<T>> String getNameHelper(BlockState p_195957_0_, Property<T> p_195957_1_) {
      return p_195957_1_.getName(p_195957_0_.getValue(p_195957_1_));
   }
}
