package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;

public interface IStructureProcessorType<P extends StructureProcessor> {
   IStructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
   IStructureProcessorType<IntegrityProcessor> BLOCK_ROT = register("block_rot", IntegrityProcessor.CODEC);
   IStructureProcessorType<GravityStructureProcessor> GRAVITY = register("gravity", GravityStructureProcessor.CODEC);
   IStructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
   IStructureProcessorType<RuleStructureProcessor> RULE = register("rule", RuleStructureProcessor.CODEC);
   IStructureProcessorType<NopProcessor> NOP = register("nop", NopProcessor.CODEC);
   IStructureProcessorType<BlockMosinessProcessor> BLOCK_AGE = register("block_age", BlockMosinessProcessor.CODEC);
   IStructureProcessorType<BlackStoneReplacementProcessor> BLACKSTONE_REPLACE = register("blackstone_replace", BlackStoneReplacementProcessor.CODEC);
   IStructureProcessorType<LavaSubmergingProcessor> LAVA_SUBMERGED_BLOCK = register("lava_submerged_block", LavaSubmergingProcessor.CODEC);
   Codec<StructureProcessor> SINGLE_CODEC = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, IStructureProcessorType::codec);
   Codec<StructureProcessorList> LIST_OBJECT_CODEC = SINGLE_CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::list);
   Codec<StructureProcessorList> DIRECT_CODEC = Codec.either(LIST_OBJECT_CODEC.fieldOf("processors").codec(), LIST_OBJECT_CODEC).xmap((p_242923_0_) -> {
      return p_242923_0_.map((p_242926_0_) -> {
         return p_242926_0_;
      }, (p_242925_0_) -> {
         return p_242925_0_;
      });
   }, Either::left);
   Codec<Supplier<StructureProcessorList>> LIST_CODEC = RegistryKeyCodec.create(Registry.PROCESSOR_LIST_REGISTRY, DIRECT_CODEC);

   Codec<P> codec();

   static <P extends StructureProcessor> IStructureProcessorType<P> register(String p_237139_0_, Codec<P> p_237139_1_) {
      return Registry.register(Registry.STRUCTURE_PROCESSOR, p_237139_0_, () -> {
         return p_237139_1_;
      });
   }
}
