package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
   public SuspiciousStewItem(Item.Properties p_i50035_1_) {
      super(p_i50035_1_);
   }

   public static void saveMobEffect(ItemStack p_220037_0_, Effect p_220037_1_, int p_220037_2_) {
      CompoundNBT compoundnbt = p_220037_0_.getOrCreateTag();
      ListNBT listnbt = compoundnbt.getList("Effects", 9);
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt1.putByte("EffectId", (byte)Effect.getId(p_220037_1_));
      compoundnbt1.putInt("EffectDuration", p_220037_2_);
      listnbt.add(compoundnbt1);
      compoundnbt.put("Effects", listnbt);
   }

   public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      ItemStack itemstack = super.finishUsingItem(p_77654_1_, p_77654_2_, p_77654_3_);
      CompoundNBT compoundnbt = p_77654_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("Effects", 9)) {
         ListNBT listnbt = compoundnbt.getList("Effects", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            int j = 160;
            CompoundNBT compoundnbt1 = listnbt.getCompound(i);
            if (compoundnbt1.contains("EffectDuration", 3)) {
               j = compoundnbt1.getInt("EffectDuration");
            }

            Effect effect = Effect.byId(compoundnbt1.getByte("EffectId"));
            if (effect != null) {
               p_77654_3_.addEffect(new EffectInstance(effect, j));
            }
         }
      }

      return p_77654_3_ instanceof PlayerEntity && ((PlayerEntity)p_77654_3_).abilities.instabuild ? itemstack : new ItemStack(Items.BOWL);
   }
}
