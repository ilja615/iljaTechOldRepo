package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPattern {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<JigsawPattern> DIRECT_CODEC = RecordCodecBuilder.create((p_236854_0_) -> {
      return p_236854_0_.group(ResourceLocation.CODEC.fieldOf("name").forGetter(JigsawPattern::getName), ResourceLocation.CODEC.fieldOf("fallback").forGetter(JigsawPattern::getFallback), Codec.mapPair(JigsawPiece.CODEC.fieldOf("element"), Codec.INT.fieldOf("weight")).codec().listOf().promotePartial(Util.prefix("Pool element: ", LOGGER::error)).fieldOf("elements").forGetter((p_236857_0_) -> {
         return p_236857_0_.rawTemplates;
      })).apply(p_236854_0_, JigsawPattern::new);
   });
   public static final Codec<Supplier<JigsawPattern>> CODEC = RegistryKeyCodec.create(Registry.TEMPLATE_POOL_REGISTRY, DIRECT_CODEC);
   private final ResourceLocation name;
   private final List<Pair<JigsawPiece, Integer>> rawTemplates;
   private final List<JigsawPiece> templates;
   private final ResourceLocation fallback;
   private int maxSize = Integer.MIN_VALUE;

   public JigsawPattern(ResourceLocation p_i242010_1_, ResourceLocation p_i242010_2_, List<Pair<JigsawPiece, Integer>> p_i242010_3_) {
      this.name = p_i242010_1_;
      this.rawTemplates = p_i242010_3_;
      this.templates = Lists.newArrayList();

      for(Pair<JigsawPiece, Integer> pair : p_i242010_3_) {
         JigsawPiece jigsawpiece = pair.getFirst();

         for(int i = 0; i < pair.getSecond(); ++i) {
            this.templates.add(jigsawpiece);
         }
      }

      this.fallback = p_i242010_2_;
   }

   public JigsawPattern(ResourceLocation p_i51397_1_, ResourceLocation p_i51397_2_, List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> p_i51397_3_, JigsawPattern.PlacementBehaviour p_i51397_4_) {
      this.name = p_i51397_1_;
      this.rawTemplates = Lists.newArrayList();
      this.templates = Lists.newArrayList();

      for(Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer> pair : p_i51397_3_) {
         JigsawPiece jigsawpiece = pair.getFirst().apply(p_i51397_4_);
         this.rawTemplates.add(Pair.of(jigsawpiece, pair.getSecond()));

         for(int i = 0; i < pair.getSecond(); ++i) {
            this.templates.add(jigsawpiece);
         }
      }

      this.fallback = p_i51397_2_;
   }

   public int getMaxSize(TemplateManager p_214945_1_) {
      if (this.maxSize == Integer.MIN_VALUE) {
         this.maxSize = this.templates.stream().mapToInt((p_236856_1_) -> {
            return p_236856_1_.getBoundingBox(p_214945_1_, BlockPos.ZERO, Rotation.NONE).getYSpan();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public JigsawPiece getRandomTemplate(Random p_214944_1_) {
      return this.templates.get(p_214944_1_.nextInt(this.templates.size()));
   }

   public List<JigsawPiece> getShuffledTemplates(Random p_214943_1_) {
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray(new JigsawPiece[0]), p_214943_1_));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   public static enum PlacementBehaviour implements IStringSerializable, net.minecraftforge.common.IExtensibleEnum {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final Codec<JigsawPattern.PlacementBehaviour> CODEC = net.minecraftforge.common.IExtensibleEnum.createCodecForExtensibleEnum(JigsawPattern.PlacementBehaviour::values, JigsawPattern.PlacementBehaviour::byName);
      private static final Map<String, JigsawPattern.PlacementBehaviour> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(JigsawPattern.PlacementBehaviour::getName, (p_214935_0_) -> {
         return p_214935_0_;
      }));
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private PlacementBehaviour(String p_i50487_3_, ImmutableList<StructureProcessor> p_i50487_4_) {
         this.name = p_i50487_3_;
         this.processors = p_i50487_4_;
      }

      public String getName() {
         return this.name;
      }

      public static JigsawPattern.PlacementBehaviour byName(String p_214938_0_) {
         return BY_NAME.get(p_214938_0_);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static PlacementBehaviour create(String enumName, String p_i50487_3_, ImmutableList<StructureProcessor> p_i50487_4_) {
         throw new IllegalStateException("Enum not extended");
      }

      @Override
      @Deprecated
      public void init() {
         BY_NAME.put(getName(), this);
      }
   }
}
