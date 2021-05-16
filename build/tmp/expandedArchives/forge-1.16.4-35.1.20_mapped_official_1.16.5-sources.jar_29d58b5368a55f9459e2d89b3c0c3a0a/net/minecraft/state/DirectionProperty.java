package net.minecraft.state;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
   protected DirectionProperty(String p_i45650_1_, Collection<Direction> p_i45650_2_) {
      super(p_i45650_1_, Direction.class, p_i45650_2_);
   }

   public static DirectionProperty create(String p_177712_0_, Predicate<Direction> p_177712_1_) {
      return create(p_177712_0_, Arrays.stream(Direction.values()).filter(p_177712_1_).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String p_196962_0_, Direction... p_196962_1_) {
      return create(p_196962_0_, Lists.newArrayList(p_196962_1_));
   }

   public static DirectionProperty create(String p_177713_0_, Collection<Direction> p_177713_1_) {
      return new DirectionProperty(p_177713_0_, p_177713_1_);
   }
}
