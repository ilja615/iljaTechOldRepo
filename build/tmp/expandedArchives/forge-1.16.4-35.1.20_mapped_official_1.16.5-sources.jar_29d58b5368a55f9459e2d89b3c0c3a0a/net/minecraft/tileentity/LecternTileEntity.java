package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LecternTileEntity extends TileEntity implements IClearable, INamedContainerProvider {
   private final IInventory bookAccess = new IInventory() {
      public int getContainerSize() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternTileEntity.this.book.isEmpty();
      }

      public ItemStack getItem(int p_70301_1_) {
         return p_70301_1_ == 0 ? LecternTileEntity.this.book : ItemStack.EMPTY;
      }

      public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
         if (p_70298_1_ == 0) {
            ItemStack itemstack = LecternTileEntity.this.book.split(p_70298_2_);
            if (LecternTileEntity.this.book.isEmpty()) {
               LecternTileEntity.this.onBookItemRemove();
            }

            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public ItemStack removeItemNoUpdate(int p_70304_1_) {
         if (p_70304_1_ == 0) {
            ItemStack itemstack = LecternTileEntity.this.book;
            LecternTileEntity.this.book = ItemStack.EMPTY;
            LecternTileEntity.this.onBookItemRemove();
            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      }

      public int getMaxStackSize() {
         return 1;
      }

      public void setChanged() {
         LecternTileEntity.this.setChanged();
      }

      public boolean stillValid(PlayerEntity p_70300_1_) {
         if (LecternTileEntity.this.level.getBlockEntity(LecternTileEntity.this.worldPosition) != LecternTileEntity.this) {
            return false;
         } else {
            return p_70300_1_.distanceToSqr((double)LecternTileEntity.this.worldPosition.getX() + 0.5D, (double)LecternTileEntity.this.worldPosition.getY() + 0.5D, (double)LecternTileEntity.this.worldPosition.getZ() + 0.5D) > 64.0D ? false : LecternTileEntity.this.hasBook();
         }
      }

      public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
         return false;
      }

      public void clearContent() {
      }
   };
   private final IIntArray dataAccess = new IIntArray() {
      public int get(int p_221476_1_) {
         return p_221476_1_ == 0 ? LecternTileEntity.this.page : 0;
      }

      public void set(int p_221477_1_, int p_221477_2_) {
         if (p_221477_1_ == 0) {
            LecternTileEntity.this.setPage(p_221477_2_);
         }

      }

      public int getCount() {
         return 1;
      }
   };
   private ItemStack book = ItemStack.EMPTY;
   private int page;
   private int pageCount;

   public LecternTileEntity() {
      super(TileEntityType.LECTERN);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean hasBook() {
      Item item = this.book.getItem();
      return item == Items.WRITABLE_BOOK || item == Items.WRITTEN_BOOK;
   }

   public void setBook(ItemStack p_214045_1_) {
      this.setBook(p_214045_1_, (PlayerEntity)null);
   }

   private void onBookItemRemove() {
      this.page = 0;
      this.pageCount = 0;
      LecternBlock.resetBookState(this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
   }

   public void setBook(ItemStack p_214040_1_, @Nullable PlayerEntity p_214040_2_) {
      this.book = this.resolveBook(p_214040_1_, p_214040_2_);
      this.page = 0;
      this.pageCount = WrittenBookItem.getPageCount(this.book);
      this.setChanged();
   }

   private void setPage(int p_214035_1_) {
      int i = MathHelper.clamp(p_214035_1_, 0, this.pageCount - 1);
      if (i != this.page) {
         this.page = i;
         this.setChanged();
         LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public int getPage() {
      return this.page;
   }

   public int getRedstoneSignal() {
      float f = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0F) : 1.0F;
      return MathHelper.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
   }

   private ItemStack resolveBook(ItemStack p_214047_1_, @Nullable PlayerEntity p_214047_2_) {
      if (this.level instanceof ServerWorld && p_214047_1_.getItem() == Items.WRITTEN_BOOK) {
         WrittenBookItem.resolveBookComponents(p_214047_1_, this.createCommandSourceStack(p_214047_2_), p_214047_2_);
      }

      return p_214047_1_;
   }

   private CommandSource createCommandSourceStack(@Nullable PlayerEntity p_214039_1_) {
      String s;
      ITextComponent itextcomponent;
      if (p_214039_1_ == null) {
         s = "Lectern";
         itextcomponent = new StringTextComponent("Lectern");
      } else {
         s = p_214039_1_.getName().getString();
         itextcomponent = p_214039_1_.getDisplayName();
      }

      Vector3d vector3d = Vector3d.atCenterOf(this.worldPosition);
      return new CommandSource(ICommandSource.NULL, vector3d, Vector2f.ZERO, (ServerWorld)this.level, 2, s, itextcomponent, this.level.getServer(), p_214039_1_);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      if (p_230337_2_.contains("Book", 10)) {
         this.book = this.resolveBook(ItemStack.of(p_230337_2_.getCompound("Book")), (PlayerEntity)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pageCount = WrittenBookItem.getPageCount(this.book);
      this.page = MathHelper.clamp(p_230337_2_.getInt("Page"), 0, this.pageCount - 1);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (!this.getBook().isEmpty()) {
         p_189515_1_.put("Book", this.getBook().save(new CompoundNBT()));
         p_189515_1_.putInt("Page", this.page);
      }

      return p_189515_1_;
   }

   public void clearContent() {
      this.setBook(ItemStack.EMPTY);
   }

   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return new LecternContainer(p_createMenu_1_, this.bookAccess, this.dataAccess);
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("container.lectern");
   }
}
