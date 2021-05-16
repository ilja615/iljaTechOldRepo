package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class MissingDimensionFix extends DataFix {
   public MissingDimensionFix(Schema p_i241230_1_, boolean p_i241230_2_) {
      super(p_i241230_1_, p_i241230_2_);
   }

   private static <A> Type<Pair<A, Dynamic<?>>> fields(String p_241312_0_, Type<A> p_241312_1_) {
      return DSL.and(DSL.field(p_241312_0_, p_241312_1_), DSL.remainderType());
   }

   private static <A> Type<Pair<Either<A, Unit>, Dynamic<?>>> optionalFields(String p_241314_0_, Type<A> p_241314_1_) {
      return DSL.and(DSL.optional(DSL.field(p_241314_0_, p_241314_1_)), DSL.remainderType());
   }

   private static <A1, A2> Type<Pair<Either<A1, Unit>, Pair<Either<A2, Unit>, Dynamic<?>>>> optionalFields(String p_241313_0_, Type<A1> p_241313_1_, String p_241313_2_, Type<A2> p_241313_3_) {
      return DSL.and(DSL.optional(DSL.field(p_241313_0_, p_241313_1_)), DSL.optional(DSL.field(p_241313_2_, p_241313_3_)), DSL.remainderType());
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      TaggedChoiceType<String> taggedchoicetype = new TaggedChoiceType<>("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", optionalFields("settings", optionalFields("biome", schema.getType(TypeReferences.BIOME), "layers", DSL.list(optionalFields("block", schema.getType(TypeReferences.BLOCK_NAME))))), "minecraft:noise", optionalFields("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", fields("biome", schema.getType(TypeReferences.BIOME)), "minecraft:multi_noise", DSL.list(fields("biome", schema.getType(TypeReferences.BIOME))), "minecraft:checkerboard", fields("biomes", DSL.list(schema.getType(TypeReferences.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), optionalFields("default_block", schema.getType(TypeReferences.BLOCK_NAME), "default_fluid", schema.getType(TypeReferences.BLOCK_NAME))))));
      CompoundListType<String, ?> compoundlisttype = DSL.compoundList(NamespacedSchema.namespacedString(), fields("generator", taggedchoicetype));
      Type<?> type = DSL.and(compoundlisttype, DSL.remainderType());
      Type<?> type1 = schema.getType(TypeReferences.WORLD_GEN_SETTINGS);
      FieldFinder<?> fieldfinder = new FieldFinder<>("dimensions", type);
      if (!type1.findFieldType("dimensions").equals(type)) {
         throw new IllegalStateException();
      } else {
         OpticFinder<? extends List<? extends Pair<String, ?>>> opticfinder = compoundlisttype.finder();
         return this.fixTypeEverywhereTyped("MissingDimensionFix", type1, (p_241308_4_) -> {
            return p_241308_4_.updateTyped(fieldfinder, (p_241309_4_) -> {
               return p_241309_4_.updateTyped(opticfinder, (p_241310_3_) -> {
                  if (!(p_241310_3_.getValue() instanceof List)) {
                     throw new IllegalStateException("List exptected");
                  } else if (((List)p_241310_3_.getValue()).isEmpty()) {
                     Dynamic<?> dynamic = p_241308_4_.get(DSL.remainderFinder());
                     Dynamic<?> dynamic1 = this.recreateSettings(dynamic);
                     return DataFixUtils.orElse(compoundlisttype.readTyped(dynamic1).result().map(Pair::getFirst), p_241310_3_);
                  } else {
                     return p_241310_3_;
                  }
               });
            });
         });
      }
   }

   private <T> Dynamic<T> recreateSettings(Dynamic<T> p_241311_1_) {
      long i = p_241311_1_.get("seed").asLong(0L);
      return new Dynamic<>(p_241311_1_.getOps(), WorldGenSettings.vanillaLevels(p_241311_1_, i, WorldGenSettings.defaultOverworld(p_241311_1_, i), false));
   }
}
