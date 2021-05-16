package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
   public static final RenderMaterial FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
   public static final RenderMaterial FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
   public static final RenderMaterial LAVA_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
   public static final RenderMaterial WATER_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
   public static final RenderMaterial WATER_OVERLAY = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
   public static final RenderMaterial BANNER_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/banner_base"));
   public static final RenderMaterial SHIELD_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base"));
   public static final RenderMaterial NO_PATTERN_SHIELD = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base_nopattern"));
   public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10).mapToObj((p_229323_0_) -> {
      return new ResourceLocation("block/destroy_stage_" + p_229323_0_);
   }).collect(Collectors.toList());
   public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream().map((p_229351_0_) -> {
      return new ResourceLocation("textures/" + p_229351_0_.getPath() + ".png");
   }).collect(Collectors.toList());
   public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
   protected static final Set<RenderMaterial> UNREFERENCED_TEXTURES = Util.make(Sets.newHashSet(), (p_229337_0_) -> {
      p_229337_0_.add(WATER_FLOW);
      p_229337_0_.add(LAVA_FLOW);
      p_229337_0_.add(WATER_OVERLAY);
      p_229337_0_.add(FIRE_0);
      p_229337_0_.add(FIRE_1);
      p_229337_0_.add(BellTileEntityRenderer.BELL_RESOURCE_LOCATION);
      p_229337_0_.add(ConduitTileEntityRenderer.SHELL_TEXTURE);
      p_229337_0_.add(ConduitTileEntityRenderer.ACTIVE_SHELL_TEXTURE);
      p_229337_0_.add(ConduitTileEntityRenderer.WIND_TEXTURE);
      p_229337_0_.add(ConduitTileEntityRenderer.VERTICAL_WIND_TEXTURE);
      p_229337_0_.add(ConduitTileEntityRenderer.OPEN_EYE_TEXTURE);
      p_229337_0_.add(ConduitTileEntityRenderer.CLOSED_EYE_TEXTURE);
      p_229337_0_.add(EnchantmentTableTileEntityRenderer.BOOK_LOCATION);
      p_229337_0_.add(BANNER_BASE);
      p_229337_0_.add(SHIELD_BASE);
      p_229337_0_.add(NO_PATTERN_SHIELD);

      for(ResourceLocation resourcelocation : DESTROY_STAGES) {
         p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, resourcelocation));
      }

      p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));
      p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
      p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
      p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
      p_229337_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));
      Atlases.getAllMaterials(p_229337_0_::add);
   });
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
   private static final String MISSING_MODEL_LOCATION_STRING = MISSING_MODEL_LOCATION.toString();
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
   private static final Map<String, String> BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);
   public static final BlockModel GENERATION_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), (p_229347_0_) -> {
      p_229347_0_.name = "generation marker";
   });
   public static final BlockModel BLOCK_ENTITY_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), (p_229332_0_) -> {
      p_229332_0_.name = "block entity marker";
   });
   private static final StateContainer<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = (new StateContainer.Builder<Block, BlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private static final Map<ResourceLocation, StateContainer<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION);
   protected final IResourceManager resourceManager;
   @Nullable
   private SpriteMap atlasSet;
   private final BlockColors blockColors;
   private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
   private final BlockModelDefinition.ContainerHolder context = new BlockModelDefinition.ContainerHolder();
   private final Map<ResourceLocation, IUnbakedModel> unbakedCache = Maps.newHashMap();
   private final Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> bakedCache = Maps.newHashMap();
   private final Map<ResourceLocation, IUnbakedModel> topLevelModels = Maps.newHashMap();
   private final Map<ResourceLocation, IBakedModel> bakedTopLevelModels = Maps.newHashMap();
   private Map<ResourceLocation, Pair<AtlasTexture, AtlasTexture.SheetData>> atlasPreparations;
   private int nextModelGroup = 1;
   private final Object2IntMap<BlockState> modelGroups = Util.make(new Object2IntOpenHashMap<>(), (p_229336_0_) -> {
      p_229336_0_.defaultReturnValue(-1);
   });

   public ModelBakery(IResourceManager p_i226056_1_, BlockColors p_i226056_2_, IProfiler p_i226056_3_, int p_i226056_4_) {
      this(p_i226056_1_, p_i226056_2_, true);
      processLoading(p_i226056_3_, p_i226056_4_);
   }

   protected ModelBakery(IResourceManager p_i226056_1_, BlockColors p_i226056_2_, boolean vanillaBakery) {
      this.resourceManager = p_i226056_1_;
      this.blockColors = p_i226056_2_;
   }

   protected void processLoading(IProfiler p_i226056_3_, int p_i226056_4_) {
      net.minecraftforge.client.model.ModelLoaderRegistry.onModelLoadingStart();
      p_i226056_3_.push("missing_model");

      try {
         this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
         this.loadTopLevel(MISSING_MODEL_LOCATION);
      } catch (IOException ioexception) {
         LOGGER.error("Error loading missing model, should never happen :(", (Throwable)ioexception);
         throw new RuntimeException(ioexception);
      }

      p_i226056_3_.popPush("static_definitions");
      STATIC_DEFINITIONS.forEach((p_229344_1_, p_229344_2_) -> {
         p_229344_2_.getPossibleStates().forEach((p_229343_2_) -> {
            this.loadTopLevel(BlockModelShapes.stateToModelLocation(p_229344_1_, p_229343_2_));
         });
      });
      p_i226056_3_.popPush("blocks");

      for(Block block : Registry.BLOCK) {
         block.getStateDefinition().getPossibleStates().forEach((p_229326_1_) -> {
            this.loadTopLevel(BlockModelShapes.stateToModelLocation(p_229326_1_));
         });
      }

      p_i226056_3_.popPush("items");

      for(ResourceLocation resourcelocation : Registry.ITEM.keySet()) {
         this.loadTopLevel(new ModelResourceLocation(resourcelocation, "inventory"));
      }

      p_i226056_3_.popPush("special");
      this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      for (ResourceLocation rl : getSpecialModels()) {
         addModelToCache(rl);
      }
      p_i226056_3_.popPush("textures");
      Set<Pair<String, String>> set = Sets.newLinkedHashSet();
      Set<RenderMaterial> set1 = this.topLevelModels.values().stream().flatMap((p_229342_2_) -> {
         return p_229342_2_.getMaterials(this::getModel, set).stream();
      }).collect(Collectors.toSet());
      set1.addAll(UNREFERENCED_TEXTURES);
      net.minecraftforge.client.ForgeHooksClient.gatherFluidTextures(set1);
      set.stream().filter((p_229346_0_) -> {
         return !p_229346_0_.getSecond().equals(MISSING_MODEL_LOCATION_STRING);
      }).forEach((p_229330_0_) -> {
         LOGGER.warn("Unable to resolve texture reference: {} in {}", p_229330_0_.getFirst(), p_229330_0_.getSecond());
      });
      Map<ResourceLocation, List<RenderMaterial>> map = set1.stream().collect(Collectors.groupingBy(RenderMaterial::atlasLocation));
      p_i226056_3_.popPush("stitching");
      this.atlasPreparations = Maps.newHashMap();

      for(Entry<ResourceLocation, List<RenderMaterial>> entry : map.entrySet()) {
         AtlasTexture atlastexture = new AtlasTexture(entry.getKey());
         AtlasTexture.SheetData atlastexture$sheetdata = atlastexture.prepareToStitch(this.resourceManager, entry.getValue().stream().map(RenderMaterial::texture), p_i226056_3_, p_i226056_4_);
         this.atlasPreparations.put(entry.getKey(), Pair.of(atlastexture, atlastexture$sheetdata));
      }

      p_i226056_3_.pop();
   }

   public SpriteMap uploadTextures(TextureManager p_229333_1_, IProfiler p_229333_2_) {
      p_229333_2_.push("atlas");

      for(Pair<AtlasTexture, AtlasTexture.SheetData> pair : this.atlasPreparations.values()) {
         AtlasTexture atlastexture = pair.getFirst();
         AtlasTexture.SheetData atlastexture$sheetdata = pair.getSecond();
         atlastexture.reload(atlastexture$sheetdata);
         p_229333_1_.register(atlastexture.location(), atlastexture);
         p_229333_1_.bind(atlastexture.location());
         atlastexture.updateFilter(atlastexture$sheetdata);
      }

      this.atlasSet = new SpriteMap(this.atlasPreparations.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
      p_229333_2_.popPush("baking");
      this.topLevelModels.keySet().forEach((p_229350_1_) -> {
         IBakedModel ibakedmodel = null;

         try {
            ibakedmodel = this.bake(p_229350_1_, ModelRotation.X0_Y0);
         } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.warn("Unable to bake model: '{}': {}", p_229350_1_, exception);
         }

         if (ibakedmodel != null) {
            this.bakedTopLevelModels.put(p_229350_1_, ibakedmodel);
         }

      });
      p_229333_2_.pop();
      return this.atlasSet;
   }

   private static Predicate<BlockState> predicate(StateContainer<Block, BlockState> p_209605_0_, String p_209605_1_) {
      Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

      for(String s : COMMA_SPLITTER.split(p_209605_1_)) {
         Iterator<String> iterator = EQUAL_SPLITTER.split(s).iterator();
         if (iterator.hasNext()) {
            String s1 = iterator.next();
            Property<?> property = p_209605_0_.getProperty(s1);
            if (property != null && iterator.hasNext()) {
               String s2 = iterator.next();
               Comparable<?> comparable = getValueHelper(property, s2);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + property.getPossibleValues());
               }

               map.put(property, comparable);
            } else if (!s1.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
            }
         }
      }

      Block block = p_209605_0_.getOwner();
      return (p_229325_2_) -> {
         if (p_229325_2_ != null && block == p_229325_2_.getBlock()) {
            for(Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
               if (!Objects.equals(p_229325_2_.getValue(entry.getKey()), entry.getValue())) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      };
   }

   @Nullable
   static <T extends Comparable<T>> T getValueHelper(Property<T> p_209592_0_, String p_209592_1_) {
      return p_209592_0_.getValue(p_209592_1_).orElse((T)null);
   }

   public IUnbakedModel getModel(ResourceLocation p_209597_1_) {
      if (this.unbakedCache.containsKey(p_209597_1_)) {
         return this.unbakedCache.get(p_209597_1_);
      } else if (this.loadingStack.contains(p_209597_1_)) {
         throw new IllegalStateException("Circular reference while loading " + p_209597_1_);
      } else {
         this.loadingStack.add(p_209597_1_);
         IUnbakedModel iunbakedmodel = this.unbakedCache.get(MISSING_MODEL_LOCATION);

         while(!this.loadingStack.isEmpty()) {
            ResourceLocation resourcelocation = this.loadingStack.iterator().next();

            try {
               if (!this.unbakedCache.containsKey(resourcelocation)) {
                  this.loadModel(resourcelocation);
               }
            } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
               LOGGER.warn(modelbakery$blockstatedefinitionexception.getMessage());
               this.unbakedCache.put(resourcelocation, iunbakedmodel);
            } catch (Exception exception) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, p_209597_1_, exception);
               this.unbakedCache.put(resourcelocation, iunbakedmodel);
            } finally {
               this.loadingStack.remove(resourcelocation);
            }
         }

         return this.unbakedCache.getOrDefault(p_209597_1_, iunbakedmodel);
      }
   }

   private void loadModel(ResourceLocation p_209598_1_) throws Exception {
      if (!(p_209598_1_ instanceof ModelResourceLocation)) {
         this.cacheAndQueueDependencies(p_209598_1_, this.loadBlockModel(p_209598_1_));
      } else {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_209598_1_;
         if (Objects.equals(modelresourcelocation.getVariant(), "inventory")) {
            ResourceLocation resourcelocation2 = new ResourceLocation(p_209598_1_.getNamespace(), "item/" + p_209598_1_.getPath());
            BlockModel blockmodel = this.loadBlockModel(resourcelocation2);
            this.cacheAndQueueDependencies(modelresourcelocation, blockmodel);
            this.unbakedCache.put(resourcelocation2, blockmodel);
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(p_209598_1_.getNamespace(), p_209598_1_.getPath());
            StateContainer<Block, BlockState> statecontainer = Optional.ofNullable(STATIC_DEFINITIONS.get(resourcelocation)).orElseGet(() -> {
               return Registry.BLOCK.get(resourcelocation).getStateDefinition();
            });
            this.context.setDefinition(statecontainer);
            List<Property<?>> list = ImmutableList.copyOf(this.blockColors.getColoringProperties(statecontainer.getOwner()));
            ImmutableList<BlockState> immutablelist = statecontainer.getPossibleStates();
            Map<ModelResourceLocation, BlockState> map = Maps.newHashMap();
            immutablelist.forEach((p_229340_2_) -> {
               BlockState blockstate = map.put(BlockModelShapes.stateToModelLocation(resourcelocation, p_229340_2_), p_229340_2_);
            });
            Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map1 = Maps.newHashMap();
            ResourceLocation resourcelocation1 = new ResourceLocation(p_209598_1_.getNamespace(), "blockstates/" + p_209598_1_.getPath() + ".json");
            IUnbakedModel iunbakedmodel = this.unbakedCache.get(MISSING_MODEL_LOCATION);
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = new ModelBakery.ModelListWrapper(ImmutableList.of(iunbakedmodel), ImmutableList.of());
            Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair = Pair.of(iunbakedmodel, () -> {
               return modelbakery$modellistwrapper;
            });

            try {
               List<Pair<String, BlockModelDefinition>> list1;
               try {
                  list1 = this.resourceManager.getResources(resourcelocation1).stream().map((p_229345_1_) -> {
                     try (InputStream inputstream = p_229345_1_.getInputStream()) {
                        return Pair.of(p_229345_1_.getSourceName(), BlockModelDefinition.fromStream(this.context, new InputStreamReader(inputstream, StandardCharsets.UTF_8)));
                     } catch (Exception exception1) {
                        throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", p_229345_1_.getLocation(), p_229345_1_.getSourceName(), exception1.getMessage()));
                     }
                  }).collect(Collectors.toList());
               } catch (IOException ioexception) {
                  LOGGER.warn("Exception loading blockstate definition: {}: {}", resourcelocation1, ioexception);
                  return;
               }

               for(Pair<String, BlockModelDefinition> pair1 : list1) {
                  BlockModelDefinition blockmodeldefinition = pair1.getSecond();
                  Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map2 = Maps.newIdentityHashMap();
                  Multipart multipart;
                  if (blockmodeldefinition.isMultiPart()) {
                     multipart = blockmodeldefinition.getMultiPart();
                     immutablelist.forEach((p_229339_3_) -> {
                        Pair pair2 = map2.put(p_229339_3_, Pair.of(multipart, () -> {
                           return ModelBakery.ModelListWrapper.create(p_229339_3_, multipart, list);
                        }));
                     });
                  } else {
                     multipart = null;
                  }

                  blockmodeldefinition.getVariants().forEach((p_229329_9_, p_229329_10_) -> {
                     try {
                        immutablelist.stream().filter(predicate(statecontainer, p_229329_9_)).forEach((p_229338_6_) -> {
                           Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map2.put(p_229338_6_, Pair.of(p_229329_10_, () -> {
                              return ModelBakery.ModelListWrapper.create(p_229338_6_, p_229329_10_, list);
                           }));
                           if (pair2 != null && pair2.getFirst() != multipart) {
                              map2.put(p_229338_6_, pair);
                              throw new RuntimeException("Overlapping definition with: " + (String)blockmodeldefinition.getVariants().entrySet().stream().filter((p_229331_1_) -> {
                                 return p_229331_1_.getValue() == pair2.getFirst();
                              }).findFirst().get().getKey());
                           }
                        });
                     } catch (Exception exception1) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair1.getFirst(), p_229329_9_, exception1.getMessage());
                     }

                  });
                  map1.putAll(map2);
               }

            } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
               throw modelbakery$blockstatedefinitionexception;
            } catch (Exception exception) {
               throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourcelocation1, exception));
            } finally {
               HashMap<ModelBakery.ModelListWrapper, Set<BlockState>> lvt_20_1_ = Maps.newHashMap();
               map.forEach((p_229341_5_, p_229341_6_) -> {
                  Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map1.get(p_229341_6_);
                  if (pair2 == null) {
                     LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_229341_5_);
                     pair2 = pair;
                  }

                  this.cacheAndQueueDependencies(p_229341_5_, pair2.getFirst());

                  try {
                     ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = pair2.getSecond().get();
                     lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (p_229334_0_) -> {
                        return Sets.newIdentityHashSet();
                     }).add(p_229341_6_);
                  } catch (Exception exception1) {
                     LOGGER.warn("Exception evaluating model definition: '{}'", p_229341_5_, exception1);
                  }

               });
               lvt_20_1_.forEach((p_229335_1_, p_229335_2_) -> {
                  Iterator<BlockState> iterator = p_229335_2_.iterator();

                  while(iterator.hasNext()) {
                     BlockState blockstate = iterator.next();
                     if (blockstate.getRenderShape() != BlockRenderType.MODEL) {
                        iterator.remove();
                        this.modelGroups.put(blockstate, 0);
                     }
                  }

                  if (p_229335_2_.size() > 1) {
                     this.registerModelGroup(p_229335_2_);
                  }

               });
            }
         }
      }
   }

   private void cacheAndQueueDependencies(ResourceLocation p_209593_1_, IUnbakedModel p_209593_2_) {
      this.unbakedCache.put(p_209593_1_, p_209593_2_);
      this.loadingStack.addAll(p_209593_2_.getDependencies());
   }

   // Same as loadTopModel but without restricting to MRL's
   private void addModelToCache(ResourceLocation p_217843_1_) {
      IUnbakedModel iunbakedmodel = this.getModel(p_217843_1_);
      this.unbakedCache.put(p_217843_1_, iunbakedmodel);
      this.topLevelModels.put(p_217843_1_, iunbakedmodel);
   }

   private void loadTopLevel(ModelResourceLocation p_217843_1_) {
      IUnbakedModel iunbakedmodel = this.getModel(p_217843_1_);
      this.unbakedCache.put(p_217843_1_, iunbakedmodel);
      this.topLevelModels.put(p_217843_1_, iunbakedmodel);
   }

   private void registerModelGroup(Iterable<BlockState> p_225352_1_) {
      int i = this.nextModelGroup++;
      p_225352_1_.forEach((p_229324_2_) -> {
         this.modelGroups.put(p_229324_2_, i);
      });
   }

   @Nullable
   @Deprecated
   public IBakedModel bake(ResourceLocation p_217845_1_, IModelTransform p_217845_2_) {
      return getBakedModel(p_217845_1_, p_217845_2_, this.atlasSet::getSprite);
   }

   @Nullable
   public IBakedModel getBakedModel(ResourceLocation p_217845_1_, IModelTransform p_217845_2_, java.util.function.Function<RenderMaterial, net.minecraft.client.renderer.texture.TextureAtlasSprite> textureGetter) {
      Triple<ResourceLocation, TransformationMatrix, Boolean> triple = Triple.of(p_217845_1_, p_217845_2_.getRotation(), p_217845_2_.isUvLocked());
      if (this.bakedCache.containsKey(triple)) {
         return this.bakedCache.get(triple);
      } else if (this.atlasSet == null) {
         throw new IllegalStateException("bake called too early");
      } else {
         IUnbakedModel iunbakedmodel = this.getModel(p_217845_1_);
         if (iunbakedmodel instanceof BlockModel) {
            BlockModel blockmodel = (BlockModel)iunbakedmodel;
            if (blockmodel.getRootModel() == GENERATION_MARKER) {
               return ITEM_MODEL_GENERATOR.generateBlockModel(textureGetter, blockmodel).bake(this, blockmodel, this.atlasSet::getSprite, p_217845_2_, p_217845_1_, false);
            }
         }

         IBakedModel ibakedmodel = iunbakedmodel.bake(this, textureGetter, p_217845_2_, p_217845_1_);
         this.bakedCache.put(triple, ibakedmodel);
         return ibakedmodel;
      }
   }

   protected BlockModel loadBlockModel(ResourceLocation p_177594_1_) throws IOException {
      Reader reader = null;
      IResource iresource = null;

      BlockModel blockmodel;
      try {
         String s = p_177594_1_.getPath();
         if (!"builtin/generated".equals(s)) {
            if ("builtin/entity".equals(s)) {
               return BLOCK_ENTITY_MARKER;
            }

            if (s.startsWith("builtin/")) {
               String s2 = s.substring("builtin/".length());
               String s1 = BUILTIN_MODELS.get(s2);
               if (s1 == null) {
                  throw new FileNotFoundException(p_177594_1_.toString());
               }

               reader = new StringReader(s1);
            } else {
               iresource = this.resourceManager.getResource(new ResourceLocation(p_177594_1_.getNamespace(), "models/" + p_177594_1_.getPath() + ".json"));
               reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
            }

            blockmodel = BlockModel.fromStream(reader);
            blockmodel.name = p_177594_1_.toString();
            return blockmodel;
         }

         blockmodel = GENERATION_MARKER;
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly((Closeable)iresource);
      }

      return blockmodel;
   }

   public Map<ResourceLocation, IBakedModel> getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
   }

   public Set<ResourceLocation> getSpecialModels() {
      return java.util.Collections.emptySet();
   }

   @OnlyIn(Dist.CLIENT)
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String p_i49526_1_) {
         super(p_i49526_1_);
      }
   }

   public SpriteMap getSpriteMap() {
      return this.atlasSet;
   }

   @OnlyIn(Dist.CLIENT)
   static class ModelListWrapper {
      private final List<IUnbakedModel> models;
      private final List<Object> coloringValues;

      public ModelListWrapper(List<IUnbakedModel> p_i51613_1_, List<Object> p_i51613_2_) {
         this.models = p_i51613_1_;
         this.coloringValues = p_i51613_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof ModelBakery.ModelListWrapper)) {
            return false;
         } else {
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = (ModelBakery.ModelListWrapper)p_equals_1_;
            return Objects.equals(this.models, modelbakery$modellistwrapper.models) && Objects.equals(this.coloringValues, modelbakery$modellistwrapper.coloringValues);
         }
      }

      public int hashCode() {
         return 31 * this.models.hashCode() + this.coloringValues.hashCode();
      }

      public static ModelBakery.ModelListWrapper create(BlockState p_225335_0_, Multipart p_225335_1_, Collection<Property<?>> p_225335_2_) {
         StateContainer<Block, BlockState> statecontainer = p_225335_0_.getBlock().getStateDefinition();
         List<IUnbakedModel> list = p_225335_1_.getSelectors().stream().filter((p_225338_2_) -> {
            return p_225338_2_.getPredicate(statecontainer).test(p_225335_0_);
         }).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
         List<Object> list1 = getColoringValues(p_225335_0_, p_225335_2_);
         return new ModelBakery.ModelListWrapper(list, list1);
      }

      public static ModelBakery.ModelListWrapper create(BlockState p_225336_0_, IUnbakedModel p_225336_1_, Collection<Property<?>> p_225336_2_) {
         List<Object> list = getColoringValues(p_225336_0_, p_225336_2_);
         return new ModelBakery.ModelListWrapper(ImmutableList.of(p_225336_1_), list);
      }

      private static List<Object> getColoringValues(BlockState p_225337_0_, Collection<Property<?>> p_225337_1_) {
         return p_225337_1_.stream().map(p_225337_0_::getValue).collect(ImmutableList.toImmutableList());
      }
   }
}
