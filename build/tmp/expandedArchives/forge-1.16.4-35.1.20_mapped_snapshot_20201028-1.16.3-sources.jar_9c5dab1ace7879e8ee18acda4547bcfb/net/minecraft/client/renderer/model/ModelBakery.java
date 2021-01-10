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
   public static final RenderMaterial LOCATION_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_0"));
   public static final RenderMaterial LOCATION_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_1"));
   public static final RenderMaterial LOCATION_LAVA_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/lava_flow"));
   public static final RenderMaterial LOCATION_WATER_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_flow"));
   public static final RenderMaterial LOCATION_WATER_OVERLAY = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_overlay"));
   public static final RenderMaterial LOCATION_BANNER_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/banner_base"));
   public static final RenderMaterial LOCATION_SHIELD_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base"));
   public static final RenderMaterial LOCATION_SHIELD_NO_PATTERN = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base_nopattern"));
   public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10).mapToObj((destroyStage) -> {
      return new ResourceLocation("block/destroy_stage_" + destroyStage);
   }).collect(Collectors.toList());
   public static final List<ResourceLocation> DESTROY_LOCATIONS = DESTROY_STAGES.stream().map((resourceLocation) -> {
      return new ResourceLocation("textures/" + resourceLocation.getPath() + ".png");
   }).collect(Collectors.toList());
   public static final List<RenderType> DESTROY_RENDER_TYPES = DESTROY_LOCATIONS.stream().map(RenderType::getCrumbling).collect(Collectors.toList());
   protected static final Set<RenderMaterial> LOCATIONS_BUILTIN_TEXTURES = Util.make(Sets.newHashSet(), (materialSet) -> {
      materialSet.add(LOCATION_WATER_FLOW);
      materialSet.add(LOCATION_LAVA_FLOW);
      materialSet.add(LOCATION_WATER_OVERLAY);
      materialSet.add(LOCATION_FIRE_0);
      materialSet.add(LOCATION_FIRE_1);
      materialSet.add(BellTileEntityRenderer.BELL_BODY_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.BASE_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.CAGE_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.WIND_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.VERTICAL_WIND_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.OPEN_EYE_TEXTURE);
      materialSet.add(ConduitTileEntityRenderer.CLOSED_EYE_TEXTURE);
      materialSet.add(EnchantmentTableTileEntityRenderer.TEXTURE_BOOK);
      materialSet.add(LOCATION_BANNER_BASE);
      materialSet.add(LOCATION_SHIELD_BASE);
      materialSet.add(LOCATION_SHIELD_NO_PATTERN);

      for(ResourceLocation resourcelocation : DESTROY_STAGES) {
         materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, resourcelocation));
      }

      materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));
      materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
      materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
      materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
      materialSet.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));
      Atlases.collectAllMaterials(materialSet::add);
   });
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   private static final String MODEL_MISSING_STRING = MODEL_MISSING.toString();
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
   private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
   private static final Splitter SPLITTER_COMMA = Splitter.on(',');
   private static final Splitter EQUALS_SPLITTER = Splitter.on('=').limit(2);
   public static final BlockModel MODEL_GENERATED = Util.make(BlockModel.deserialize("{\"gui_light\": \"front\"}"), (model) -> {
      model.name = "generation marker";
   });
   public static final BlockModel MODEL_ENTITY = Util.make(BlockModel.deserialize("{\"gui_light\": \"side\"}"), (blockModel) -> {
      blockModel.name = "block entity marker";
   });
   private static final StateContainer<Block, BlockState> STATE_CONTAINER_ITEM_FRAME = (new StateContainer.Builder<Block, BlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).func_235882_a_(Block::getDefaultState, BlockState::new);
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private static final Map<ResourceLocation, StateContainer<Block, BlockState>> STATE_CONTAINER_OVERRIDES = ImmutableMap.of(new ResourceLocation("item_frame"), STATE_CONTAINER_ITEM_FRAME);
   protected final IResourceManager resourceManager;
   @Nullable
   private SpriteMap spriteMap;
   private final BlockColors blockColors;
   private final Set<ResourceLocation> unbakedModelLoadingQueue = Sets.newHashSet();
   private final BlockModelDefinition.ContainerHolder containerHolder = new BlockModelDefinition.ContainerHolder();
   private final Map<ResourceLocation, IUnbakedModel> unbakedModels = Maps.newHashMap();
   private final Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> bakedModels = Maps.newHashMap();
   private final Map<ResourceLocation, IUnbakedModel> topUnbakedModels = Maps.newHashMap();
   private final Map<ResourceLocation, IBakedModel> topBakedModels = Maps.newHashMap();
   private Map<ResourceLocation, Pair<AtlasTexture, AtlasTexture.SheetData>> sheetData;
   private int counterModelId = 1;
   private final Object2IntMap<BlockState> stateModelIds = Util.make(new Object2IntOpenHashMap<>(), (state) -> {
      state.defaultReturnValue(-1);
   });

   public ModelBakery(IResourceManager resourceManagerIn, BlockColors blockColorsIn, IProfiler profilerIn, int maxMipmapLevel) {
      this(resourceManagerIn, blockColorsIn, true);
      processLoading(profilerIn, maxMipmapLevel);
   }

   protected ModelBakery(IResourceManager resourceManagerIn, BlockColors blockColorsIn, boolean vanillaBakery) {
      this.resourceManager = resourceManagerIn;
      this.blockColors = blockColorsIn;
   }

   protected void processLoading(IProfiler profilerIn, int maxMipmapLevel) {
      net.minecraftforge.client.model.ModelLoaderRegistry.onModelLoadingStart();
      profilerIn.startSection("missing_model");

      try {
         this.unbakedModels.put(MODEL_MISSING, this.loadModel(MODEL_MISSING));
         this.loadTopModel(MODEL_MISSING);
      } catch (IOException ioexception) {
         LOGGER.error("Error loading missing model, should never happen :(", (Throwable)ioexception);
         throw new RuntimeException(ioexception);
      }

      profilerIn.endStartSection("static_definitions");
      STATE_CONTAINER_OVERRIDES.forEach((resourceLocation, container) -> {
         container.getValidStates().forEach((state) -> {
            this.loadTopModel(BlockModelShapes.getModelLocation(resourceLocation, state));
         });
      });
      profilerIn.endStartSection("blocks");

      for(Block block : Registry.BLOCK) {
         block.getStateContainer().getValidStates().forEach((state) -> {
            this.loadTopModel(BlockModelShapes.getModelLocation(state));
         });
      }

      profilerIn.endStartSection("items");

      for(ResourceLocation resourcelocation : Registry.ITEM.keySet()) {
         this.loadTopModel(new ModelResourceLocation(resourcelocation, "inventory"));
      }

      profilerIn.endStartSection("special");
      this.loadTopModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      for (ResourceLocation rl : getSpecialModels()) {
         addModelToCache(rl);
      }
      profilerIn.endStartSection("textures");
      Set<Pair<String, String>> set = Sets.newLinkedHashSet();
      Set<RenderMaterial> set1 = this.topUnbakedModels.values().stream().flatMap((unbaked) -> {
         return unbaked.getTextures(this::getUnbakedModel, set).stream();
      }).collect(Collectors.toSet());
      set1.addAll(LOCATIONS_BUILTIN_TEXTURES);
      net.minecraftforge.client.ForgeHooksClient.gatherFluidTextures(set1);
      set.stream().filter((stringPair) -> {
         return !stringPair.getSecond().equals(MODEL_MISSING_STRING);
      }).forEach((textureReferenceErrors) -> {
         LOGGER.warn("Unable to resolve texture reference: {} in {}", textureReferenceErrors.getFirst(), textureReferenceErrors.getSecond());
      });
      Map<ResourceLocation, List<RenderMaterial>> map = set1.stream().collect(Collectors.groupingBy(RenderMaterial::getAtlasLocation));
      profilerIn.endStartSection("stitching");
      this.sheetData = Maps.newHashMap();

      for(Entry<ResourceLocation, List<RenderMaterial>> entry : map.entrySet()) {
         AtlasTexture atlastexture = new AtlasTexture(entry.getKey());
         AtlasTexture.SheetData atlastexture$sheetdata = atlastexture.stitch(this.resourceManager, entry.getValue().stream().map(RenderMaterial::getTextureLocation), profilerIn, maxMipmapLevel);
         this.sheetData.put(entry.getKey(), Pair.of(atlastexture, atlastexture$sheetdata));
      }

      profilerIn.endSection();
   }

   public SpriteMap uploadTextures(TextureManager resourceManagerIn, IProfiler profilerIn) {
      profilerIn.startSection("atlas");

      for(Pair<AtlasTexture, AtlasTexture.SheetData> pair : this.sheetData.values()) {
         AtlasTexture atlastexture = pair.getFirst();
         AtlasTexture.SheetData atlastexture$sheetdata = pair.getSecond();
         atlastexture.upload(atlastexture$sheetdata);
         resourceManagerIn.loadTexture(atlastexture.getTextureLocation(), atlastexture);
         resourceManagerIn.bindTexture(atlastexture.getTextureLocation());
         atlastexture.setBlurMipmap(atlastexture$sheetdata);
      }

      this.spriteMap = new SpriteMap(this.sheetData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
      profilerIn.endStartSection("baking");
      this.topUnbakedModels.keySet().forEach((resourceLocation) -> {
         IBakedModel ibakedmodel = null;

         try {
            ibakedmodel = this.bake(resourceLocation, ModelRotation.X0_Y0);
         } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, exception);
         }

         if (ibakedmodel != null) {
            this.topBakedModels.put(resourceLocation, ibakedmodel);
         }

      });
      profilerIn.endSection();
      return this.spriteMap;
   }

   private static Predicate<BlockState> parseVariantKey(StateContainer<Block, BlockState> containerIn, String variantIn) {
      Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

      for(String s : SPLITTER_COMMA.split(variantIn)) {
         Iterator<String> iterator = EQUALS_SPLITTER.split(s).iterator();
         if (iterator.hasNext()) {
            String s1 = iterator.next();
            Property<?> property = containerIn.getProperty(s1);
            if (property != null && iterator.hasNext()) {
               String s2 = iterator.next();
               Comparable<?> comparable = parseValue(property, s2);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + property.getAllowedValues());
               }

               map.put(property, comparable);
            } else if (!s1.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
            }
         }
      }

      Block block = containerIn.getOwner();
      return (state) -> {
         if (state != null && block == state.getBlock()) {
            for(Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
               if (!Objects.equals(state.get(entry.getKey()), entry.getValue())) {
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
   static <T extends Comparable<T>> T parseValue(Property<T> property, String value) {
      return property.parseValue(value).orElse((T)null);
   }

   public IUnbakedModel getUnbakedModel(ResourceLocation modelLocation) {
      if (this.unbakedModels.containsKey(modelLocation)) {
         return this.unbakedModels.get(modelLocation);
      } else if (this.unbakedModelLoadingQueue.contains(modelLocation)) {
         throw new IllegalStateException("Circular reference while loading " + modelLocation);
      } else {
         this.unbakedModelLoadingQueue.add(modelLocation);
         IUnbakedModel iunbakedmodel = this.unbakedModels.get(MODEL_MISSING);

         while(!this.unbakedModelLoadingQueue.isEmpty()) {
            ResourceLocation resourcelocation = this.unbakedModelLoadingQueue.iterator().next();

            try {
               if (!this.unbakedModels.containsKey(resourcelocation)) {
                  this.loadBlockstate(resourcelocation);
               }
            } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
               LOGGER.warn(modelbakery$blockstatedefinitionexception.getMessage());
               this.unbakedModels.put(resourcelocation, iunbakedmodel);
            } catch (Exception exception) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, modelLocation, exception);
               this.unbakedModels.put(resourcelocation, iunbakedmodel);
            } finally {
               this.unbakedModelLoadingQueue.remove(resourcelocation);
            }
         }

         return this.unbakedModels.getOrDefault(modelLocation, iunbakedmodel);
      }
   }

   private void loadBlockstate(ResourceLocation blockstateLocation) throws Exception {
      if (!(blockstateLocation instanceof ModelResourceLocation)) {
         this.putModel(blockstateLocation, this.loadModel(blockstateLocation));
      } else {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)blockstateLocation;
         if (Objects.equals(modelresourcelocation.getVariant(), "inventory")) {
            ResourceLocation resourcelocation2 = new ResourceLocation(blockstateLocation.getNamespace(), "item/" + blockstateLocation.getPath());
            BlockModel blockmodel = this.loadModel(resourcelocation2);
            this.putModel(modelresourcelocation, blockmodel);
            this.unbakedModels.put(resourcelocation2, blockmodel);
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(blockstateLocation.getNamespace(), blockstateLocation.getPath());
            StateContainer<Block, BlockState> statecontainer = Optional.ofNullable(STATE_CONTAINER_OVERRIDES.get(resourcelocation)).orElseGet(() -> {
               return Registry.BLOCK.getOrDefault(resourcelocation).getStateContainer();
            });
            this.containerHolder.setStateContainer(statecontainer);
            List<Property<?>> list = ImmutableList.copyOf(this.blockColors.getColorProperties(statecontainer.getOwner()));
            ImmutableList<BlockState> immutablelist = statecontainer.getValidStates();
            Map<ModelResourceLocation, BlockState> map = Maps.newHashMap();
            immutablelist.forEach((state) -> {
               BlockState blockstate = map.put(BlockModelShapes.getModelLocation(resourcelocation, state), state);
            });
            Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map1 = Maps.newHashMap();
            ResourceLocation resourcelocation1 = new ResourceLocation(blockstateLocation.getNamespace(), "blockstates/" + blockstateLocation.getPath() + ".json");
            IUnbakedModel iunbakedmodel = this.unbakedModels.get(MODEL_MISSING);
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = new ModelBakery.ModelListWrapper(ImmutableList.of(iunbakedmodel), ImmutableList.of());
            Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair = Pair.of(iunbakedmodel, () -> {
               return modelbakery$modellistwrapper;
            });

            try {
               List<Pair<String, BlockModelDefinition>> list1;
               try {
                  list1 = this.resourceManager.getAllResources(resourcelocation1).stream().map((resource) -> {
                     try (InputStream inputstream = resource.getInputStream()) {
                        return Pair.of(resource.getPackName(), BlockModelDefinition.fromJson(this.containerHolder, new InputStreamReader(inputstream, StandardCharsets.UTF_8)));
                     } catch (Exception exception1) {
                        throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getLocation(), resource.getPackName(), exception1.getMessage()));
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
                  if (blockmodeldefinition.hasMultipartData()) {
                     multipart = blockmodeldefinition.getMultipartData();
                     immutablelist.forEach((state) -> {
                        Pair pair2 = map2.put(state, Pair.of(multipart, () -> {
                           return ModelBakery.ModelListWrapper.makeWrapper(state, multipart, list);
                        }));
                     });
                  } else {
                     multipart = null;
                  }

                  blockmodeldefinition.getVariants().forEach((name, variantList) -> {
                     try {
                        immutablelist.stream().filter(parseVariantKey(statecontainer, name)).forEach((state) -> {
                           Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map2.put(state, Pair.of(variantList, () -> {
                              return ModelBakery.ModelListWrapper.makeWrapper(state, variantList, list);
                           }));
                           if (pair2 != null && pair2.getFirst() != multipart) {
                              map2.put(state, pair);
                              throw new RuntimeException("Overlapping definition with: " + (String)blockmodeldefinition.getVariants().entrySet().stream().filter((nameToVariantList) -> {
                                 return nameToVariantList.getValue() == pair2.getFirst();
                              }).findFirst().get().getKey());
                           }
                        });
                     } catch (Exception exception1) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair1.getFirst(), name, exception1.getMessage());
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
               map.forEach((resourceLocation, state) -> {
                  Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map1.get(state);
                  if (pair2 == null) {
                     LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, resourceLocation);
                     pair2 = pair;
                  }

                  this.putModel(resourceLocation, pair2.getFirst());

                  try {
                     ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = pair2.getSecond().get();
                     lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (listWrapper) -> {
                        return Sets.newIdentityHashSet();
                     }).add(state);
                  } catch (Exception exception1) {
                     LOGGER.warn("Exception evaluating model definition: '{}'", resourceLocation, exception1);
                  }

               });
               lvt_20_1_.forEach((listWrapper, stateSet) -> {
                  Iterator<BlockState> iterator = stateSet.iterator();

                  while(iterator.hasNext()) {
                     BlockState blockstate = iterator.next();
                     if (blockstate.getRenderType() != BlockRenderType.MODEL) {
                        iterator.remove();
                        this.stateModelIds.put(blockstate, 0);
                     }
                  }

                  if (stateSet.size() > 1) {
                     this.registerModelIds(stateSet);
                  }

               });
            }
         }
      }
   }

   private void putModel(ResourceLocation locationIn, IUnbakedModel modelIn) {
      this.unbakedModels.put(locationIn, modelIn);
      this.unbakedModelLoadingQueue.addAll(modelIn.getDependencies());
   }

   // Same as loadTopModel but without restricting to MRL's
   private void addModelToCache(ResourceLocation locationIn) {
      IUnbakedModel iunbakedmodel = this.getUnbakedModel(locationIn);
      this.unbakedModels.put(locationIn, iunbakedmodel);
      this.topUnbakedModels.put(locationIn, iunbakedmodel);
   }

   private void loadTopModel(ModelResourceLocation locationIn) {
      IUnbakedModel iunbakedmodel = this.getUnbakedModel(locationIn);
      this.unbakedModels.put(locationIn, iunbakedmodel);
      this.topUnbakedModels.put(locationIn, iunbakedmodel);
   }

   private void registerModelIds(Iterable<BlockState> blockStatesIn) {
      int i = this.counterModelId++;
      blockStatesIn.forEach((state) -> {
         this.stateModelIds.put(state, i);
      });
   }

   @Nullable
   @Deprecated
   public IBakedModel bake(ResourceLocation locationIn, IModelTransform transformIn) {
      return getBakedModel(locationIn, transformIn, this.spriteMap::getSprite);
   }

   @Nullable
   public IBakedModel getBakedModel(ResourceLocation locationIn, IModelTransform transformIn, java.util.function.Function<RenderMaterial, net.minecraft.client.renderer.texture.TextureAtlasSprite> textureGetter) {
      Triple<ResourceLocation, TransformationMatrix, Boolean> triple = Triple.of(locationIn, transformIn.getRotation(), transformIn.isUvLock());
      if (this.bakedModels.containsKey(triple)) {
         return this.bakedModels.get(triple);
      } else if (this.spriteMap == null) {
         throw new IllegalStateException("bake called too early");
      } else {
         IUnbakedModel iunbakedmodel = this.getUnbakedModel(locationIn);
         if (iunbakedmodel instanceof BlockModel) {
            BlockModel blockmodel = (BlockModel)iunbakedmodel;
            if (blockmodel.getRootModel() == MODEL_GENERATED) {
               return ITEM_MODEL_GENERATOR.makeItemModel(textureGetter, blockmodel).bakeModel(this, blockmodel, this.spriteMap::getSprite, transformIn, locationIn, false);
            }
         }

         IBakedModel ibakedmodel = iunbakedmodel.bakeModel(this, textureGetter, transformIn, locationIn);
         this.bakedModels.put(triple, ibakedmodel);
         return ibakedmodel;
      }
   }

   protected BlockModel loadModel(ResourceLocation location) throws IOException {
      Reader reader = null;
      IResource iresource = null;

      BlockModel blockmodel;
      try {
         String s = location.getPath();
         if (!"builtin/generated".equals(s)) {
            if ("builtin/entity".equals(s)) {
               return MODEL_ENTITY;
            }

            if (s.startsWith("builtin/")) {
               String s2 = s.substring("builtin/".length());
               String s1 = BUILT_IN_MODELS.get(s2);
               if (s1 == null) {
                  throw new FileNotFoundException(location.toString());
               }

               reader = new StringReader(s1);
            } else {
               iresource = this.resourceManager.getResource(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json"));
               reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
            }

            blockmodel = BlockModel.deserialize(reader);
            blockmodel.name = location.toString();
            return blockmodel;
         }

         blockmodel = MODEL_GENERATED;
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly((Closeable)iresource);
      }

      return blockmodel;
   }

   public Map<ResourceLocation, IBakedModel> getTopBakedModels() {
      return this.topBakedModels;
   }

   public Object2IntMap<BlockState> getStateModelIds() {
      return this.stateModelIds;
   }

   public Set<ResourceLocation> getSpecialModels() {
      return java.util.Collections.emptySet();
   }

   @OnlyIn(Dist.CLIENT)
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String message) {
         super(message);
      }
   }

   public SpriteMap getSpriteMap() {
      return this.spriteMap;
   }

   @OnlyIn(Dist.CLIENT)
   static class ModelListWrapper {
      private final List<IUnbakedModel> models;
      private final List<Object> colorValues;

      public ModelListWrapper(List<IUnbakedModel> modelsIn, List<Object> colorValuesIn) {
         this.models = modelsIn;
         this.colorValues = colorValuesIn;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof ModelBakery.ModelListWrapper)) {
            return false;
         } else {
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = (ModelBakery.ModelListWrapper)p_equals_1_;
            return Objects.equals(this.models, modelbakery$modellistwrapper.models) && Objects.equals(this.colorValues, modelbakery$modellistwrapper.colorValues);
         }
      }

      public int hashCode() {
         return 31 * this.models.hashCode() + this.colorValues.hashCode();
      }

      public static ModelBakery.ModelListWrapper makeWrapper(BlockState blockStateIn, Multipart multipartIn, Collection<Property<?>> propertiesIn) {
         StateContainer<Block, BlockState> statecontainer = blockStateIn.getBlock().getStateContainer();
         List<IUnbakedModel> list = multipartIn.getSelectors().stream().filter((selector) -> {
            return selector.getPredicate(statecontainer).test(blockStateIn);
         }).map(Selector::getVariantList).collect(ImmutableList.toImmutableList());
         List<Object> list1 = getColorValues(blockStateIn, propertiesIn);
         return new ModelBakery.ModelListWrapper(list, list1);
      }

      public static ModelBakery.ModelListWrapper makeWrapper(BlockState blockStateIn, IUnbakedModel modelIn, Collection<Property<?>> propertiesIn) {
         List<Object> list = getColorValues(blockStateIn, propertiesIn);
         return new ModelBakery.ModelListWrapper(ImmutableList.of(modelIn), list);
      }

      private static List<Object> getColorValues(BlockState blockStateIn, Collection<Property<?>> propertiesIn) {
         return propertiesIn.stream().map(blockStateIn::get).collect(ImmutableList.toImmutableList());
      }
   }
}
