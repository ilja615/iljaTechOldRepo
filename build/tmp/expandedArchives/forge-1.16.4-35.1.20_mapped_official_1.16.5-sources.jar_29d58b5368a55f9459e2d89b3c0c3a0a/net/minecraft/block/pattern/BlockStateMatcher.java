package net.minecraft.block.pattern;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public class BlockStateMatcher implements Predicate<BlockState> {
   public static final Predicate<BlockState> ANY = (p_201026_0_) -> {
      return true;
   };
   private final StateContainer<Block, BlockState> definition;
   private final Map<Property<?>, Predicate<Object>> properties = Maps.newHashMap();

   private BlockStateMatcher(StateContainer<Block, BlockState> p_i45653_1_) {
      this.definition = p_i45653_1_;
   }

   public static BlockStateMatcher forBlock(Block p_177638_0_) {
      return new BlockStateMatcher(p_177638_0_.getStateDefinition());
   }

   public boolean test(@Nullable BlockState p_test_1_) {
      if (p_test_1_ != null && p_test_1_.getBlock().equals(this.definition.getOwner())) {
         if (this.properties.isEmpty()) {
            return true;
         } else {
            for(Entry<Property<?>, Predicate<Object>> entry : this.properties.entrySet()) {
               if (!this.applies(p_test_1_, entry.getKey(), entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean applies(BlockState p_185927_1_, Property<T> p_185927_2_, Predicate<Object> p_185927_3_) {
      T t = p_185927_1_.getValue(p_185927_2_);
      return p_185927_3_.test(t);
   }

   public <V extends Comparable<V>> BlockStateMatcher where(Property<V> p_201028_1_, Predicate<Object> p_201028_2_) {
      if (!this.definition.getProperties().contains(p_201028_1_)) {
         throw new IllegalArgumentException(this.definition + " cannot support property " + p_201028_1_);
      } else {
         this.properties.put(p_201028_1_, p_201028_2_);
         return this;
      }
   }
}
