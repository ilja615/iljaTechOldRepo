package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public abstract class LockableLootTileEntity extends LockableTileEntity {
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected LockableLootTileEntity(TileEntityType<?> p_i48284_1_) {
      super(p_i48284_1_);
   }

   public static void setLootTable(IBlockReader p_195479_0_, Random p_195479_1_, BlockPos p_195479_2_, ResourceLocation p_195479_3_) {
      TileEntity tileentity = p_195479_0_.getBlockEntity(p_195479_2_);
      if (tileentity instanceof LockableLootTileEntity) {
         ((LockableLootTileEntity)tileentity).setLootTable(p_195479_3_, p_195479_1_.nextLong());
      }

   }

   protected boolean tryLoadLootTable(CompoundNBT p_184283_1_) {
      if (p_184283_1_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_184283_1_.getString("LootTable"));
         this.lootTableSeed = p_184283_1_.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean trySaveLootTable(CompoundNBT p_184282_1_) {
      if (this.lootTable == null) {
         return false;
      } else {
         p_184282_1_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_184282_1_.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void unpackLootTable(@Nullable PlayerEntity p_184281_1_) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);
         if (p_184281_1_ instanceof ServerPlayerEntity) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity)p_184281_1_, this.lootTable);
         }

         this.lootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
         if (p_184281_1_ != null) {
            lootcontext$builder.withLuck(p_184281_1_.getLuck()).withParameter(LootParameters.THIS_ENTITY, p_184281_1_);
         }

         loottable.fill(this, lootcontext$builder.create(LootParameterSets.CHEST));
      }

   }

   public void setLootTable(ResourceLocation p_189404_1_, long p_189404_2_) {
      this.lootTable = p_189404_1_;
      this.lootTableSeed = p_189404_2_;
   }

   public boolean isEmpty() {
      this.unpackLootTable((PlayerEntity)null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getItem(int p_70301_1_) {
      this.unpackLootTable((PlayerEntity)null);
      return this.getItems().get(p_70301_1_);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      this.unpackLootTable((PlayerEntity)null);
      ItemStack itemstack = ItemStackHelper.removeItem(this.getItems(), p_70298_1_, p_70298_2_);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      this.unpackLootTable((PlayerEntity)null);
      return ItemStackHelper.takeItem(this.getItems(), p_70304_1_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.unpackLootTable((PlayerEntity)null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return !(p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
      }
   }

   public void clearContent() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> p_199721_1_);

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return super.canOpen(p_213904_1_) && (this.lootTable == null || !p_213904_1_.isSpectator());
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      if (this.canOpen(p_createMenu_3_)) {
         this.unpackLootTable(p_createMenu_2_.player);
         return this.createMenu(p_createMenu_1_, p_createMenu_2_);
      } else {
         return null;
      }
   }
}
