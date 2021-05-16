package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
   public MilkBucketItem(Item.Properties p_i48481_1_) {
      super(p_i48481_1_);
   }

   public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      if (!p_77654_2_.isClientSide) p_77654_3_.curePotionEffects(p_77654_1_); // FORGE - move up so stack.shrink does not turn stack into air

      if (p_77654_3_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_77654_3_;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, p_77654_1_);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }

      if (p_77654_3_ instanceof PlayerEntity && !((PlayerEntity)p_77654_3_).abilities.instabuild) {
         p_77654_1_.shrink(1);
      }

      return p_77654_1_.isEmpty() ? new ItemStack(Items.BUCKET) : p_77654_1_;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      return DrinkHelper.useDrink(p_77659_1_, p_77659_2_, p_77659_3_);
   }

   @Override
   public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @javax.annotation.Nullable net.minecraft.nbt.CompoundNBT nbt) {
      return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
   }
}
