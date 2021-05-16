package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkRocketItem extends Item {
   public FireworkRocketItem(Item.Properties p_i48498_1_) {
      super(p_i48498_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      if (!world.isClientSide) {
         ItemStack itemstack = p_195939_1_.getItemInHand();
         Vector3d vector3d = p_195939_1_.getClickLocation();
         Direction direction = p_195939_1_.getClickedFace();
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, p_195939_1_.getPlayer(), vector3d.x + (double)direction.getStepX() * 0.15D, vector3d.y + (double)direction.getStepY() * 0.15D, vector3d.z + (double)direction.getStepZ() * 0.15D, itemstack);
         world.addFreshEntity(fireworkrocketentity);
         itemstack.shrink(1);
      }

      return ActionResultType.sidedSuccess(world.isClientSide);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      if (p_77659_2_.isFallFlying()) {
         ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
         if (!p_77659_1_.isClientSide) {
            p_77659_1_.addFreshEntity(new FireworkRocketEntity(p_77659_1_, itemstack, p_77659_2_));
            if (!p_77659_2_.abilities.instabuild) {
               itemstack.shrink(1);
            }
         }

         return ActionResult.sidedSuccess(p_77659_2_.getItemInHand(p_77659_3_), p_77659_1_.isClientSide());
      } else {
         return ActionResult.pass(p_77659_2_.getItemInHand(p_77659_3_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      CompoundNBT compoundnbt = p_77624_1_.getTagElement("Fireworks");
      if (compoundnbt != null) {
         if (compoundnbt.contains("Flight", 99)) {
            p_77624_3_.add((new TranslationTextComponent("item.minecraft.firework_rocket.flight")).append(" ").append(String.valueOf((int)compoundnbt.getByte("Flight"))).withStyle(TextFormatting.GRAY));
         }

         ListNBT listnbt = compoundnbt.getList("Explosions", 10);
         if (!listnbt.isEmpty()) {
            for(int i = 0; i < listnbt.size(); ++i) {
               CompoundNBT compoundnbt1 = listnbt.getCompound(i);
               List<ITextComponent> list = Lists.newArrayList();
               FireworkStarItem.appendHoverText(compoundnbt1, list);
               if (!list.isEmpty()) {
                  for(int j = 1; j < list.size(); ++j) {
                     list.set(j, (new StringTextComponent("  ")).append(list.get(j)).withStyle(TextFormatting.GRAY));
                  }

                  p_77624_3_.addAll(list);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt((p_199796_0_) -> {
         return p_199796_0_.id;
      })).toArray((p_199797_0_) -> {
         return new FireworkRocketItem.Shape[p_199797_0_];
      });
      private final int id;
      private final String name;

      private Shape(int p_i47931_3_, String p_i47931_4_) {
         this.id = p_i47931_3_;
         this.name = p_i47931_4_;
      }

      public int getId() {
         return this.id;
      }

      @OnlyIn(Dist.CLIENT)
      public String getName() {
         return this.name;
      }

      @OnlyIn(Dist.CLIENT)
      public static FireworkRocketItem.Shape byId(int p_196070_0_) {
         return p_196070_0_ >= 0 && p_196070_0_ < BY_ID.length ? BY_ID[p_196070_0_] : SMALL_BALL;
      }
   }
}
