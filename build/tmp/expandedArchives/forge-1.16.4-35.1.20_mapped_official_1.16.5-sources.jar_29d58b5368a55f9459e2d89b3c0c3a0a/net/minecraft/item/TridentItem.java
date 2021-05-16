package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TridentItem extends Item implements IVanishable {
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public TridentItem(Item.Properties p_i48788_1_) {
      super(p_i48788_1_);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)-2.9F, AttributeModifier.Operation.ADDITION));
      this.defaultModifiers = builder.build();
   }

   public boolean canAttackBlock(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.SPEAR;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public void releaseUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
      if (p_77615_3_ instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)p_77615_3_;
         int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
         if (i >= 10) {
            int j = EnchantmentHelper.getRiptide(p_77615_1_);
            if (j <= 0 || playerentity.isInWaterOrRain()) {
               if (!p_77615_2_.isClientSide) {
                  p_77615_1_.hurtAndBreak(1, playerentity, (p_220047_1_) -> {
                     p_220047_1_.broadcastBreakEvent(p_77615_3_.getUsedItemHand());
                  });
                  if (j == 0) {
                     TridentEntity tridententity = new TridentEntity(p_77615_2_, playerentity, p_77615_1_);
                     tridententity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                     if (playerentity.abilities.instabuild) {
                        tridententity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                     }

                     p_77615_2_.addFreshEntity(tridententity);
                     p_77615_2_.playSound((PlayerEntity)null, tridententity, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                     if (!playerentity.abilities.instabuild) {
                        playerentity.inventory.removeItem(p_77615_1_);
                     }
                  }
               }

               playerentity.awardStat(Stats.ITEM_USED.get(this));
               if (j > 0) {
                  float f7 = playerentity.yRot;
                  float f = playerentity.xRot;
                  float f1 = -MathHelper.sin(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                  float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F));
                  float f3 = MathHelper.cos(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                  float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                  float f5 = 3.0F * ((1.0F + (float)j) / 4.0F);
                  f1 = f1 * (f5 / f4);
                  f2 = f2 * (f5 / f4);
                  f3 = f3 * (f5 / f4);
                  playerentity.push((double)f1, (double)f2, (double)f3);
                  playerentity.startAutoSpinAttack(20);
                  if (playerentity.isOnGround()) {
                     float f6 = 1.1999999F;
                     playerentity.move(MoverType.SELF, new Vector3d(0.0D, (double)1.1999999F, 0.0D));
                  }

                  SoundEvent soundevent;
                  if (j >= 3) {
                     soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                  } else if (j == 2) {
                     soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                  } else {
                     soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                  }

                  p_77615_2_.playSound((PlayerEntity)null, playerentity, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
               }

            }
         }
      }
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
         return ActionResult.fail(itemstack);
      } else if (EnchantmentHelper.getRiptide(itemstack) > 0 && !p_77659_2_.isInWaterOrRain()) {
         return ActionResult.fail(itemstack);
      } else {
         p_77659_2_.startUsingItem(p_77659_3_);
         return ActionResult.consume(itemstack);
      }
   }

   public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.hurtAndBreak(1, p_77644_3_, (p_220048_0_) -> {
         p_220048_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if ((double)p_179218_3_.getDestroySpeed(p_179218_2_, p_179218_4_) != 0.0D) {
         p_179218_1_.hurtAndBreak(2, p_179218_5_, (p_220046_0_) -> {
            p_220046_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      return p_111205_1_ == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_111205_1_);
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
