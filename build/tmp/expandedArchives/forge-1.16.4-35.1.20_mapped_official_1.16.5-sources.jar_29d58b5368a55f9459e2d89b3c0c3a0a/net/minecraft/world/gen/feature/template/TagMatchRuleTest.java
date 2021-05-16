package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;

public class TagMatchRuleTest extends RuleTest {
   public static final Codec<TagMatchRuleTest> CODEC = ITag.codec(() -> {
      return TagCollectionManager.getInstance().getBlocks();
   }).fieldOf("tag").xmap(TagMatchRuleTest::new, (p_237162_0_) -> {
      return p_237162_0_.tag;
   }).codec();
   private final ITag<Block> tag;

   public TagMatchRuleTest(ITag<Block> p_i51318_1_) {
      this.tag = p_i51318_1_;
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_.is(this.tag);
   }

   protected IRuleTestType<?> getType() {
      return IRuleTestType.TAG_TEST;
   }
}
