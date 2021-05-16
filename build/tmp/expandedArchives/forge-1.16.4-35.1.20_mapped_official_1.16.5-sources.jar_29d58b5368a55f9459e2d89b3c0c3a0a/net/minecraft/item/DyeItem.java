package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
   private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
   private final DyeColor dyeColor;

   public DyeItem(DyeColor p_i48510_1_, Item.Properties p_i48510_2_) {
      super(p_i48510_2_);
      this.dyeColor = p_i48510_1_;
      ITEM_BY_COLOR.put(p_i48510_1_, this);
   }

   public ActionResultType interactLivingEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_ instanceof SheepEntity) {
         SheepEntity sheepentity = (SheepEntity)p_111207_3_;
         if (sheepentity.isAlive() && !sheepentity.isSheared() && sheepentity.getColor() != this.dyeColor) {
            if (!p_111207_2_.level.isClientSide) {
               sheepentity.setColor(this.dyeColor);
               p_111207_1_.shrink(1);
            }

            return ActionResultType.sidedSuccess(p_111207_2_.level.isClientSide);
         }
      }

      return ActionResultType.PASS;
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static DyeItem byColor(DyeColor p_195961_0_) {
      return ITEM_BY_COLOR.get(p_195961_0_);
   }
}
