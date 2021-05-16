package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionItem extends Item {
   public PotionItem(Item.Properties p_i48476_1_) {
      super(p_i48476_1_);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      PlayerEntity playerentity = p_77654_3_ instanceof PlayerEntity ? (PlayerEntity)p_77654_3_ : null;
      if (playerentity instanceof ServerPlayerEntity) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, p_77654_1_);
      }

      if (!p_77654_2_.isClientSide) {
         for(EffectInstance effectinstance : PotionUtils.getMobEffects(p_77654_1_)) {
            if (effectinstance.getEffect().isInstantenous()) {
               effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, p_77654_3_, effectinstance.getAmplifier(), 1.0D);
            } else {
               p_77654_3_.addEffect(new EffectInstance(effectinstance));
            }
         }
      }

      if (playerentity != null) {
         playerentity.awardStat(Stats.ITEM_USED.get(this));
         if (!playerentity.abilities.instabuild) {
            p_77654_1_.shrink(1);
         }
      }

      if (playerentity == null || !playerentity.abilities.instabuild) {
         if (p_77654_1_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (playerentity != null) {
            playerentity.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return p_77654_1_;
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

   public String getDescriptionId(ItemStack p_77667_1_) {
      return PotionUtils.getPotion(p_77667_1_).getName(this.getDescriptionId() + ".effect.");
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 1.0F);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return super.isFoil(p_77636_1_) || !PotionUtils.getMobEffects(p_77636_1_).isEmpty();
   }

   public void fillItemCategory(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.allowdedIn(p_150895_1_)) {
         for(Potion potion : Registry.POTION) {
            if (potion != Potions.EMPTY) {
               p_150895_2_.add(PotionUtils.setPotion(new ItemStack(this), potion));
            }
         }
      }

   }
}
