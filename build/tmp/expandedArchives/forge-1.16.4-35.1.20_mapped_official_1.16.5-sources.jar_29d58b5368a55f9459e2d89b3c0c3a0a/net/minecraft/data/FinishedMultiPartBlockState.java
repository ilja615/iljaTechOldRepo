package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public class FinishedMultiPartBlockState implements IFinishedBlockState {
   private final Block block;
   private final List<FinishedMultiPartBlockState.Part> parts = Lists.newArrayList();

   private FinishedMultiPartBlockState(Block p_i232524_1_) {
      this.block = p_i232524_1_;
   }

   public Block getBlock() {
      return this.block;
   }

   public static FinishedMultiPartBlockState multiPart(Block p_240106_0_) {
      return new FinishedMultiPartBlockState(p_240106_0_);
   }

   public FinishedMultiPartBlockState with(List<BlockModelDefinition> p_240112_1_) {
      this.parts.add(new FinishedMultiPartBlockState.Part(p_240112_1_));
      return this;
   }

   public FinishedMultiPartBlockState with(BlockModelDefinition p_240111_1_) {
      return this.with(ImmutableList.of(p_240111_1_));
   }

   public FinishedMultiPartBlockState with(IMultiPartPredicateBuilder p_240109_1_, List<BlockModelDefinition> p_240109_2_) {
      this.parts.add(new FinishedMultiPartBlockState.ConditionalPart(p_240109_1_, p_240109_2_));
      return this;
   }

   public FinishedMultiPartBlockState with(IMultiPartPredicateBuilder p_240110_1_, BlockModelDefinition... p_240110_2_) {
      return this.with(p_240110_1_, ImmutableList.copyOf(p_240110_2_));
   }

   public FinishedMultiPartBlockState with(IMultiPartPredicateBuilder p_240108_1_, BlockModelDefinition p_240108_2_) {
      return this.with(p_240108_1_, ImmutableList.of(p_240108_2_));
   }

   public JsonElement get() {
      StateContainer<Block, BlockState> statecontainer = this.block.getStateDefinition();
      this.parts.forEach((p_240107_1_) -> {
         p_240107_1_.validate(statecontainer);
      });
      JsonArray jsonarray = new JsonArray();
      this.parts.stream().map(FinishedMultiPartBlockState.Part::get).forEach(jsonarray::add);
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("multipart", jsonarray);
      return jsonobject;
   }

   static class ConditionalPart extends FinishedMultiPartBlockState.Part {
      private final IMultiPartPredicateBuilder condition;

      private ConditionalPart(IMultiPartPredicateBuilder p_i232525_1_, List<BlockModelDefinition> p_i232525_2_) {
         super(p_i232525_2_);
         this.condition = p_i232525_1_;
      }

      public void validate(StateContainer<?, ?> p_230525_1_) {
         this.condition.validate(p_230525_1_);
      }

      public void decorate(JsonObject p_230526_1_) {
         p_230526_1_.add("when", this.condition.get());
      }
   }

   static class Part implements Supplier<JsonElement> {
      private final List<BlockModelDefinition> variants;

      private Part(List<BlockModelDefinition> p_i232527_1_) {
         this.variants = p_i232527_1_;
      }

      public void validate(StateContainer<?, ?> p_230525_1_) {
      }

      public void decorate(JsonObject p_230526_1_) {
      }

      public JsonElement get() {
         JsonObject jsonobject = new JsonObject();
         this.decorate(jsonobject);
         jsonobject.add("apply", BlockModelDefinition.convertList(this.variants));
         return jsonobject;
      }
   }
}
