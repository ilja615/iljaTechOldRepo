package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class RuleEntry {
   public static final Codec<RuleEntry> CODEC = RecordCodecBuilder.create((p_237111_0_) -> {
      return p_237111_0_.group(RuleTest.CODEC.fieldOf("input_predicate").forGetter((p_237116_0_) -> {
         return p_237116_0_.inputPredicate;
      }), RuleTest.CODEC.fieldOf("location_predicate").forGetter((p_237115_0_) -> {
         return p_237115_0_.locPredicate;
      }), PosRuleTest.CODEC.optionalFieldOf("position_predicate", AlwaysTrueTest.INSTANCE).forGetter((p_237114_0_) -> {
         return p_237114_0_.posPredicate;
      }), BlockState.CODEC.fieldOf("output_state").forGetter((p_237113_0_) -> {
         return p_237113_0_.outputState;
      }), CompoundNBT.CODEC.optionalFieldOf("output_nbt").forGetter((p_237112_0_) -> {
         return Optional.ofNullable(p_237112_0_.outputTag);
      })).apply(p_237111_0_, RuleEntry::new);
   });
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final PosRuleTest posPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundNBT outputTag;

   public RuleEntry(RuleTest p_i51326_1_, RuleTest p_i51326_2_, BlockState p_i51326_3_) {
      this(p_i51326_1_, p_i51326_2_, AlwaysTrueTest.INSTANCE, p_i51326_3_, Optional.empty());
   }

   public RuleEntry(RuleTest p_i232117_1_, RuleTest p_i232117_2_, PosRuleTest p_i232117_3_, BlockState p_i232117_4_) {
      this(p_i232117_1_, p_i232117_2_, p_i232117_3_, p_i232117_4_, Optional.empty());
   }

   public RuleEntry(RuleTest p_i232118_1_, RuleTest p_i232118_2_, PosRuleTest p_i232118_3_, BlockState p_i232118_4_, Optional<CompoundNBT> p_i232118_5_) {
      this.inputPredicate = p_i232118_1_;
      this.locPredicate = p_i232118_2_;
      this.posPredicate = p_i232118_3_;
      this.outputState = p_i232118_4_;
      this.outputTag = p_i232118_5_.orElse((CompoundNBT)null);
   }

   public boolean test(BlockState p_237110_1_, BlockState p_237110_2_, BlockPos p_237110_3_, BlockPos p_237110_4_, BlockPos p_237110_5_, Random p_237110_6_) {
      return this.inputPredicate.test(p_237110_1_, p_237110_6_) && this.locPredicate.test(p_237110_2_, p_237110_6_) && this.posPredicate.test(p_237110_3_, p_237110_4_, p_237110_5_, p_237110_6_);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundNBT getOutputTag() {
      return this.outputTag;
   }
}
