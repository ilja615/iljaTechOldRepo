package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.ShulkerAABBHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxBlock extends ContainerBlock {
   public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor p_i48334_1_, AbstractBlock.Properties p_i48334_2_) {
      super(p_i48334_2_);
      this.color = p_i48334_1_;
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new ShulkerBoxTileEntity(this.color);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else if (p_225533_4_.isSpectator()) {
         return ActionResultType.CONSUME;
      } else {
         TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
            boolean flag;
            if (shulkerboxtileentity.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
               Direction direction = p_225533_1_.getValue(FACING);
               flag = p_225533_2_.noCollision(ShulkerAABBHelper.openBoundingBox(p_225533_3_, direction));
            } else {
               flag = true;
            }

            if (flag) {
               p_225533_4_.openMenu(shulkerboxtileentity);
               p_225533_4_.awardStat(Stats.OPEN_SHULKER_BOX);
               PiglinTasks.angerNearbyPiglins(p_225533_4_, true);
            }

            return ActionResultType.CONSUME;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getClickedFace());
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      TileEntity tileentity = p_176208_1_.getBlockEntity(p_176208_2_);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         if (!p_176208_1_.isClientSide && p_176208_4_.isCreative() && !shulkerboxtileentity.isEmpty()) {
            ItemStack itemstack = getColoredItemStack(this.getColor());
            CompoundNBT compoundnbt = shulkerboxtileentity.saveToTag(new CompoundNBT());
            if (!compoundnbt.isEmpty()) {
               itemstack.addTagElement("BlockEntityTag", compoundnbt);
            }

            if (shulkerboxtileentity.hasCustomName()) {
               itemstack.setHoverName(shulkerboxtileentity.getCustomName());
            }

            ItemEntity itementity = new ItemEntity(p_176208_1_, (double)p_176208_2_.getX() + 0.5D, (double)p_176208_2_.getY() + 0.5D, (double)p_176208_2_.getZ() + 0.5D, itemstack);
            itementity.setDefaultPickUpDelay();
            p_176208_1_.addFreshEntity(itementity);
         } else {
            shulkerboxtileentity.unpackLootTable(p_176208_4_);
         }
      }

      super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      TileEntity tileentity = p_220076_2_.getOptionalParameter(LootParameters.BLOCK_ENTITY);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         p_220076_2_ = p_220076_2_.withDynamicDrop(CONTENTS, (p_220168_1_, p_220168_2_) -> {
            for(int i = 0; i < shulkerboxtileentity.getContainerSize(); ++i) {
               p_220168_2_.accept(shulkerboxtileentity.getItem(i));
            }

         });
      }

      return super.getDrops(p_220076_1_, p_220076_2_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasCustomHoverName()) {
         TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            ((ShulkerBoxTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
         }
      }

   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         TileEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, p_196243_1_.getBlock());
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
      super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
      CompoundNBT compoundnbt = p_190948_1_.getTagElement("BlockEntityTag");
      if (compoundnbt != null) {
         if (compoundnbt.contains("LootTable", 8)) {
            p_190948_3_.add(new StringTextComponent("???????"));
         }

         if (compoundnbt.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            int i = 0;
            int j = 0;

            for(ItemStack itemstack : nonnulllist) {
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     IFormattableTextComponent iformattabletextcomponent = itemstack.getHoverName().copy();
                     iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                     p_190948_3_.add(iformattabletextcomponent);
                  }
               }
            }

            if (j - i > 0) {
               p_190948_3_.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).withStyle(TextFormatting.ITALIC));
            }
         }
      }

   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      TileEntity tileentity = p_220053_2_.getBlockEntity(p_220053_3_);
      return tileentity instanceof ShulkerBoxTileEntity ? VoxelShapes.create(((ShulkerBoxTileEntity)tileentity).getBoundingBox(p_220053_1_)) : VoxelShapes.block();
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.getRedstoneSignalFromContainer((IInventory)p_180641_2_.getBlockEntity(p_180641_3_));
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      ItemStack itemstack = super.getCloneItemStack(p_185473_1_, p_185473_2_, p_185473_3_);
      ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)p_185473_1_.getBlockEntity(p_185473_2_);
      CompoundNBT compoundnbt = shulkerboxtileentity.saveToTag(new CompoundNBT());
      if (!compoundnbt.isEmpty()) {
         itemstack.addTagElement("BlockEntityTag", compoundnbt);
      }

      return itemstack;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromItem(Item p_190955_0_) {
      return getColorFromBlock(Block.byItem(p_190955_0_));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromBlock(Block p_190954_0_) {
      return p_190954_0_ instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)p_190954_0_).getColor() : null;
   }

   public static Block getBlockByColor(@Nullable DyeColor p_190952_0_) {
      if (p_190952_0_ == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(p_190952_0_) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor p_190953_0_) {
      return new ItemStack(getBlockByColor(p_190953_0_));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }
}
