package net.minecraft.data;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class TexturedModel {
   public static final TexturedModel.ISupplier CUBE = createDefault(ModelTextures::cube, StockModelShapes.CUBE_ALL);
   public static final TexturedModel.ISupplier CUBE_MIRRORED = createDefault(ModelTextures::cube, StockModelShapes.CUBE_MIRRORED_ALL);
   public static final TexturedModel.ISupplier COLUMN = createDefault(ModelTextures::column, StockModelShapes.CUBE_COLUMN);
   public static final TexturedModel.ISupplier COLUMN_HORIZONTAL = createDefault(ModelTextures::column, StockModelShapes.CUBE_COLUMN_HORIZONTAL);
   public static final TexturedModel.ISupplier CUBE_TOP_BOTTOM = createDefault(ModelTextures::cubeBottomTop, StockModelShapes.CUBE_BOTTOM_TOP);
   public static final TexturedModel.ISupplier CUBE_TOP = createDefault(ModelTextures::cubeTop, StockModelShapes.CUBE_TOP);
   public static final TexturedModel.ISupplier ORIENTABLE_ONLY_TOP = createDefault(ModelTextures::orientableCubeOnlyTop, StockModelShapes.CUBE_ORIENTABLE);
   public static final TexturedModel.ISupplier ORIENTABLE = createDefault(ModelTextures::orientableCube, StockModelShapes.CUBE_ORIENTABLE_TOP_BOTTOM);
   public static final TexturedModel.ISupplier CARPET = createDefault(ModelTextures::wool, StockModelShapes.CARPET);
   public static final TexturedModel.ISupplier GLAZED_TERRACOTTA = createDefault(ModelTextures::pattern, StockModelShapes.GLAZED_TERRACOTTA);
   public static final TexturedModel.ISupplier CORAL_FAN = createDefault(ModelTextures::fan, StockModelShapes.CORAL_FAN);
   public static final TexturedModel.ISupplier PARTICLE_ONLY = createDefault(ModelTextures::particle, StockModelShapes.PARTICLE_ONLY);
   public static final TexturedModel.ISupplier ANVIL = createDefault(ModelTextures::top, StockModelShapes.ANVIL);
   public static final TexturedModel.ISupplier LEAVES = createDefault(ModelTextures::cube, StockModelShapes.LEAVES);
   public static final TexturedModel.ISupplier LANTERN = createDefault(ModelTextures::lantern, StockModelShapes.LANTERN);
   public static final TexturedModel.ISupplier HANGING_LANTERN = createDefault(ModelTextures::lantern, StockModelShapes.HANGING_LANTERN);
   public static final TexturedModel.ISupplier SEAGRASS = createDefault(ModelTextures::defaultTexture, StockModelShapes.SEAGRASS);
   public static final TexturedModel.ISupplier COLUMN_ALT = createDefault(ModelTextures::logColumn, StockModelShapes.CUBE_COLUMN);
   public static final TexturedModel.ISupplier COLUMN_HORIZONTAL_ALT = createDefault(ModelTextures::logColumn, StockModelShapes.CUBE_COLUMN_HORIZONTAL);
   public static final TexturedModel.ISupplier TOP_BOTTOM_WITH_WALL = createDefault(ModelTextures::cubeBottomTopWithWall, StockModelShapes.CUBE_BOTTOM_TOP);
   public static final TexturedModel.ISupplier COLUMN_WITH_WALL = createDefault(ModelTextures::columnWithWall, StockModelShapes.CUBE_COLUMN);
   private final ModelTextures mapping;
   private final ModelsUtil template;

   private TexturedModel(ModelTextures p_i232548_1_, ModelsUtil p_i232548_2_) {
      this.mapping = p_i232548_1_;
      this.template = p_i232548_2_;
   }

   public ModelsUtil getTemplate() {
      return this.template;
   }

   public ModelTextures getMapping() {
      return this.mapping;
   }

   public TexturedModel updateTextures(Consumer<ModelTextures> p_240460_1_) {
      p_240460_1_.accept(this.mapping);
      return this;
   }

   public ResourceLocation create(Block p_240459_1_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240459_2_) {
      return this.template.create(p_240459_1_, this.mapping, p_240459_2_);
   }

   public ResourceLocation createWithSuffix(Block p_240458_1_, String p_240458_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240458_3_) {
      return this.template.createWithSuffix(p_240458_1_, p_240458_2_, this.mapping, p_240458_3_);
   }

   private static TexturedModel.ISupplier createDefault(Function<Block, ModelTextures> p_240461_0_, ModelsUtil p_240461_1_) {
      return (p_240462_2_) -> {
         return new TexturedModel(p_240461_0_.apply(p_240462_2_), p_240461_1_);
      };
   }

   public static TexturedModel createAllSame(ResourceLocation p_240463_0_) {
      return new TexturedModel(ModelTextures.cube(p_240463_0_), StockModelShapes.CUBE_ALL);
   }

   @FunctionalInterface
   public interface ISupplier {
      TexturedModel get(Block p_get_1_);

      default ResourceLocation create(Block p_240466_1_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240466_2_) {
         return this.get(p_240466_1_).create(p_240466_1_, p_240466_2_);
      }

      default ResourceLocation createWithSuffix(Block p_240465_1_, String p_240465_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240465_3_) {
         return this.get(p_240465_1_).createWithSuffix(p_240465_1_, p_240465_2_, p_240465_3_);
      }

      default TexturedModel.ISupplier updateTexture(Consumer<ModelTextures> p_240467_1_) {
         return (p_240468_2_) -> {
            return this.get(p_240468_2_).updateTextures(p_240467_1_);
         };
      }
   }
}
