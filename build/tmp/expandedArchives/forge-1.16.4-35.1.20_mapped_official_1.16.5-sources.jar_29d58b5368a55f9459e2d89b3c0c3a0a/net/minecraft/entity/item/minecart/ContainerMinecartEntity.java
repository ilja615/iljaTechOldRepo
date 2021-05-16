package net.minecraft.entity.item.minecart;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class ContainerMinecartEntity extends AbstractMinecartEntity implements IInventory, INamedContainerProvider {
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
   private boolean dropEquipment = true;
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected ContainerMinecartEntity(EntityType<?> p_i48536_1_, World p_i48536_2_) {
      super(p_i48536_1_, p_i48536_2_);
   }

   protected ContainerMinecartEntity(EntityType<?> p_i48537_1_, double p_i48537_2_, double p_i48537_4_, double p_i48537_6_, World p_i48537_8_) {
      super(p_i48537_1_, p_i48537_8_, p_i48537_2_, p_i48537_4_, p_i48537_6_);
   }

   public void destroy(DamageSource p_94095_1_) {
      super.destroy(p_94095_1_);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         InventoryHelper.dropContents(this.level, this, this);
         if (!this.level.isClientSide) {
            Entity entity = p_94095_1_.getDirectEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
               PiglinTasks.angerNearbyPiglins((PlayerEntity)entity, true);
            }
         }
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_70301_1_) {
      this.unpackLootTable((PlayerEntity)null);
      return this.itemStacks.get(p_70301_1_);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      this.unpackLootTable((PlayerEntity)null);
      return ItemStackHelper.removeItem(this.itemStacks, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      this.unpackLootTable((PlayerEntity)null);
      ItemStack itemstack = this.itemStacks.get(p_70304_1_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.itemStacks.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.unpackLootTable((PlayerEntity)null);
      this.itemStacks.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ >= 0 && p_174820_1_ < this.getContainerSize()) {
         this.setItem(p_174820_1_, p_174820_2_);
         return true;
      } else {
         return false;
      }
   }

   public void setChanged() {
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      if (this.removed) {
         return false;
      } else {
         return !(p_70300_1_.distanceToSqr(this) > 64.0D);
      }
   }

   @Nullable
   public Entity changeDimension(ServerWorld p_241206_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      this.dropEquipment = false;
      return super.changeDimension(p_241206_1_, teleporter);
   }

   @Override
   public void remove(boolean keepData) {
      if (!this.level.isClientSide && this.dropEquipment) {
         InventoryHelper.dropContents(this.level, this, this);
      }

      super.remove(keepData);
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.lootTable != null) {
         p_213281_1_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_213281_1_.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ItemStackHelper.saveAllItems(p_213281_1_, this.itemStacks);
      }

   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (p_70037_1_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_70037_1_.getString("LootTable"));
         this.lootTableSeed = p_70037_1_.getLong("LootTableSeed");
      } else {
         ItemStackHelper.loadAllItems(p_70037_1_, this.itemStacks);
      }

   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ActionResultType ret = super.interact(p_184230_1_, p_184230_2_);
      if (ret.consumesAction()) return ret;
      p_184230_1_.openMenu(this);
      if (!p_184230_1_.level.isClientSide) {
         PiglinTasks.angerNearbyPiglins(p_184230_1_, true);
         return ActionResultType.CONSUME;
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   protected void applyNaturalSlowdown() {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - Container.getRedstoneSignalFromContainer(this);
         f += (float)i * 0.001F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.0D, (double)f));
   }

   public void unpackLootTable(@Nullable PlayerEntity p_184288_1_) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);
         if (p_184288_1_ instanceof ServerPlayerEntity) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity)p_184288_1_, this.lootTable);
         }

         this.lootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withParameter(LootParameters.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);
         // Forge: add this entity to loot context, however, currently Vanilla uses 'this' for the player creating the chests. So we take over 'killer_entity' for this.
         lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this);
         if (p_184288_1_ != null) {
            lootcontext$builder.withLuck(p_184288_1_.getLuck()).withParameter(LootParameters.THIS_ENTITY, p_184288_1_);
         }

         loottable.fill(this, lootcontext$builder.create(LootParameterSets.CHEST));
      }

   }

   public void clearContent() {
      this.unpackLootTable((PlayerEntity)null);
      this.itemStacks.clear();
   }

   public void setLootTable(ResourceLocation p_184289_1_, long p_184289_2_) {
      this.lootTable = p_184289_1_;
      this.lootTableSeed = p_184289_2_;
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      if (this.lootTable != null && p_createMenu_3_.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(p_createMenu_2_.player);
         return this.createMenu(p_createMenu_1_, p_createMenu_2_);
      }
   }

   protected abstract Container createMenu(int p_213968_1_, PlayerInventory p_213968_2_);

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   protected void invalidateCaps() {
      super.invalidateCaps();
      itemHandler.invalidate();
   }

   public void dropContentsWhenDead(boolean value) {
      this.dropEquipment = value;
   }
}
