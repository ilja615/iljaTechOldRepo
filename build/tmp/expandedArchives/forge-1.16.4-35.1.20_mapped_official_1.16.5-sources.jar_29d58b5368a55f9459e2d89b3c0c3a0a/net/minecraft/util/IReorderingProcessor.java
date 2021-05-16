package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
public interface IReorderingProcessor {
   IReorderingProcessor EMPTY = (p_242236_0_) -> {
      return true;
   };

   @OnlyIn(Dist.CLIENT)
   boolean accept(ICharacterConsumer p_accept_1_);

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor codepoint(int p_242233_0_, Style p_242233_1_) {
      return (p_242243_2_) -> {
         return p_242243_2_.accept(0, p_242233_1_, p_242233_0_);
      };
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor forward(String p_242239_0_, Style p_242239_1_) {
      return p_242239_0_.isEmpty() ? EMPTY : (p_242245_2_) -> {
         return TextProcessing.iterate(p_242239_0_, p_242239_1_, p_242245_2_);
      };
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor backward(String p_242246_0_, Style p_242246_1_, Int2IntFunction p_242246_2_) {
      return p_242246_0_.isEmpty() ? EMPTY : (p_242240_3_) -> {
         return TextProcessing.iterateBackwards(p_242246_0_, p_242246_1_, decorateOutput(p_242240_3_, p_242246_2_));
      };
   }

   @OnlyIn(Dist.CLIENT)
   static ICharacterConsumer decorateOutput(ICharacterConsumer p_242237_0_, Int2IntFunction p_242237_1_) {
      return (p_242238_2_, p_242238_3_, p_242238_4_) -> {
         return p_242237_0_.accept(p_242238_2_, p_242238_3_, p_242237_1_.apply(Integer.valueOf(p_242238_4_)));
      };
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor composite(IReorderingProcessor p_242234_0_, IReorderingProcessor p_242234_1_) {
      return fromPair(p_242234_0_, p_242234_1_);
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor composite(List<IReorderingProcessor> p_242241_0_) {
      int i = p_242241_0_.size();
      switch(i) {
      case 0:
         return EMPTY;
      case 1:
         return p_242241_0_.get(0);
      case 2:
         return fromPair(p_242241_0_.get(0), p_242241_0_.get(1));
      default:
         return fromList(ImmutableList.copyOf(p_242241_0_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor fromPair(IReorderingProcessor p_242244_0_, IReorderingProcessor p_242244_1_) {
      return (p_242235_2_) -> {
         return p_242244_0_.accept(p_242235_2_) && p_242244_1_.accept(p_242235_2_);
      };
   }

   @OnlyIn(Dist.CLIENT)
   static IReorderingProcessor fromList(List<IReorderingProcessor> p_242247_0_) {
      return (p_242242_1_) -> {
         for(IReorderingProcessor ireorderingprocessor : p_242247_0_) {
            if (!ireorderingprocessor.accept(p_242242_1_)) {
               return false;
            }
         }

         return true;
      };
   }
}
