package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WrittenBookItem extends Item {
   public WrittenBookItem(Item.Properties p_i48454_1_) {
      super(p_i48454_1_);
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundNBT p_77828_0_) {
      if (!WritableBookItem.makeSureTagIsValid(p_77828_0_)) {
         return false;
      } else if (!p_77828_0_.contains("title", 8)) {
         return false;
      } else {
         String s = p_77828_0_.getString("title");
         return s.length() > 32 ? false : p_77828_0_.contains("author", 8);
      }
   }

   public static int getGeneration(ItemStack p_179230_0_) {
      return p_179230_0_.getTag().getInt("generation");
   }

   public static int getPageCount(ItemStack p_220049_0_) {
      CompoundNBT compoundnbt = p_220049_0_.getTag();
      return compoundnbt != null ? compoundnbt.getList("pages", 8).size() : 0;
   }

   public ITextComponent getName(ItemStack p_200295_1_) {
      if (p_200295_1_.hasTag()) {
         CompoundNBT compoundnbt = p_200295_1_.getTag();
         String s = compoundnbt.getString("title");
         if (!StringUtils.isNullOrEmpty(s)) {
            return new StringTextComponent(s);
         }
      }

      return super.getName(p_200295_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (p_77624_1_.hasTag()) {
         CompoundNBT compoundnbt = p_77624_1_.getTag();
         String s = compoundnbt.getString("author");
         if (!StringUtils.isNullOrEmpty(s)) {
            p_77624_3_.add((new TranslationTextComponent("book.byAuthor", s)).withStyle(TextFormatting.GRAY));
         }

         p_77624_3_.add((new TranslationTextComponent("book.generation." + compoundnbt.getInt("generation"))).withStyle(TextFormatting.GRAY));
      }

   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(world, blockpos, blockstate, p_195939_1_.getItemInHand()) ? ActionResultType.sidedSuccess(world.isClientSide) : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      p_77659_2_.openItemGui(itemstack, p_77659_3_);
      p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
   }

   public static boolean resolveBookComponents(ItemStack p_220050_0_, @Nullable CommandSource p_220050_1_, @Nullable PlayerEntity p_220050_2_) {
      CompoundNBT compoundnbt = p_220050_0_.getTag();
      if (compoundnbt != null && !compoundnbt.getBoolean("resolved")) {
         compoundnbt.putBoolean("resolved", true);
         if (!makeSureTagIsValid(compoundnbt)) {
            return false;
         } else {
            ListNBT listnbt = compoundnbt.getList("pages", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);

               ITextComponent itextcomponent;
               try {
                  itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                  itextcomponent = TextComponentUtils.updateForEntity(p_220050_1_, itextcomponent, p_220050_2_, 0);
               } catch (Exception exception) {
                  itextcomponent = new StringTextComponent(s);
               }

               listnbt.set(i, (INBT)StringNBT.valueOf(ITextComponent.Serializer.toJson(itextcomponent)));
            }

            compoundnbt.put("pages", listnbt);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return true;
   }
}
