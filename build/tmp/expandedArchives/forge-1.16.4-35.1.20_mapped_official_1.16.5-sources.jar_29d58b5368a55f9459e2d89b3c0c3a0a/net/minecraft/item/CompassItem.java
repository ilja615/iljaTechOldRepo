package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompassItem extends Item implements IVanishable {
   private static final Logger LOGGER = LogManager.getLogger();

   public CompassItem(Item.Properties p_i48515_1_) {
      super(p_i48515_1_);
   }

   public static boolean isLodestoneCompass(ItemStack p_234670_0_) {
      CompoundNBT compoundnbt = p_234670_0_.getTag();
      return compoundnbt != null && (compoundnbt.contains("LodestoneDimension") || compoundnbt.contains("LodestonePos"));
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return isLodestoneCompass(p_77636_1_) || super.isFoil(p_77636_1_);
   }

   public static Optional<RegistryKey<World>> getLodestoneDimension(CompoundNBT p_234667_0_) {
      return World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, p_234667_0_.get("LodestoneDimension")).result();
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
      if (!p_77663_2_.isClientSide) {
         if (isLodestoneCompass(p_77663_1_)) {
            CompoundNBT compoundnbt = p_77663_1_.getOrCreateTag();
            if (compoundnbt.contains("LodestoneTracked") && !compoundnbt.getBoolean("LodestoneTracked")) {
               return;
            }

            Optional<RegistryKey<World>> optional = getLodestoneDimension(compoundnbt);
            if (optional.isPresent() && optional.get() == p_77663_2_.dimension() && compoundnbt.contains("LodestonePos") && !((ServerWorld)p_77663_2_).getPoiManager().existsAtPosition(PointOfInterestType.LODESTONE, NBTUtil.readBlockPos(compoundnbt.getCompound("LodestonePos")))) {
               compoundnbt.remove("LodestonePos");
            }
         }

      }
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      BlockPos blockpos = p_195939_1_.getClickedPos();
      World world = p_195939_1_.getLevel();
      if (!world.getBlockState(blockpos).is(Blocks.LODESTONE)) {
         return super.useOn(p_195939_1_);
      } else {
         world.playSound((PlayerEntity)null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
         PlayerEntity playerentity = p_195939_1_.getPlayer();
         ItemStack itemstack = p_195939_1_.getItemInHand();
         boolean flag = !playerentity.abilities.instabuild && itemstack.getCount() == 1;
         if (flag) {
            this.addLodestoneTags(world.dimension(), blockpos, itemstack.getOrCreateTag());
         } else {
            ItemStack itemstack1 = new ItemStack(Items.COMPASS, 1);
            CompoundNBT compoundnbt = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundNBT();
            itemstack1.setTag(compoundnbt);
            if (!playerentity.abilities.instabuild) {
               itemstack.shrink(1);
            }

            this.addLodestoneTags(world.dimension(), blockpos, compoundnbt);
            if (!playerentity.inventory.add(itemstack1)) {
               playerentity.drop(itemstack1, false);
            }
         }

         return ActionResultType.sidedSuccess(world.isClientSide);
      }
   }

   private void addLodestoneTags(RegistryKey<World> p_234669_1_, BlockPos p_234669_2_, CompoundNBT p_234669_3_) {
      p_234669_3_.put("LodestonePos", NBTUtil.writeBlockPos(p_234669_2_));
      World.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, p_234669_1_).resultOrPartial(LOGGER::error).ifPresent((p_234668_1_) -> {
         p_234669_3_.put("LodestoneDimension", p_234668_1_);
      });
      p_234669_3_.putBoolean("LodestoneTracked", true);
   }

   public String getDescriptionId(ItemStack p_77667_1_) {
      return isLodestoneCompass(p_77667_1_) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(p_77667_1_);
   }
}
