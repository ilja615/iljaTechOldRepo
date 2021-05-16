package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FilledMapItem extends AbstractMapItem {
   public FilledMapItem(Item.Properties p_i48482_1_) {
      super(p_i48482_1_);
   }

   public static ItemStack create(World p_195952_0_, int p_195952_1_, int p_195952_2_, byte p_195952_3_, boolean p_195952_4_, boolean p_195952_5_) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      createAndStoreSavedData(itemstack, p_195952_0_, p_195952_1_, p_195952_2_, p_195952_3_, p_195952_4_, p_195952_5_, p_195952_0_.dimension());
      return itemstack;
   }

   @Nullable
   public static MapData getSavedData(ItemStack p_219994_0_, World p_219994_1_) {
      return p_219994_1_.getMapData(makeKey(getMapId(p_219994_0_)));
   }

   @Nullable
   public static MapData getOrCreateSavedData(ItemStack p_195950_0_, World p_195950_1_) {
      // FORGE: Add instance method for mods to override
      Item map = p_195950_0_.getItem();
      if (map instanceof FilledMapItem) {
        return ((FilledMapItem)map).getCustomMapData(p_195950_0_, p_195950_1_);
      }
      return null;
   }

   @Nullable
   protected MapData getCustomMapData(ItemStack p_195950_0_, World p_195950_1_) {
      MapData mapdata = getSavedData(p_195950_0_, p_195950_1_);
      if (mapdata == null && p_195950_1_ instanceof ServerWorld) {
         mapdata = createAndStoreSavedData(p_195950_0_, p_195950_1_, p_195950_1_.getLevelData().getXSpawn(), p_195950_1_.getLevelData().getZSpawn(), 3, false, false, p_195950_1_.dimension());
      }

      return mapdata;
   }

   public static int getMapId(ItemStack p_195949_0_) {
      CompoundNBT compoundnbt = p_195949_0_.getTag();
      return compoundnbt != null && compoundnbt.contains("map", 99) ? compoundnbt.getInt("map") : 0;
   }

   private static MapData createAndStoreSavedData(ItemStack p_195951_0_, World p_195951_1_, int p_195951_2_, int p_195951_3_, int p_195951_4_, boolean p_195951_5_, boolean p_195951_6_, RegistryKey<World> p_195951_7_) {
      int i = p_195951_1_.getFreeMapId();
      MapData mapdata = new MapData(makeKey(i));
      mapdata.setProperties(p_195951_2_, p_195951_3_, p_195951_4_, p_195951_5_, p_195951_6_, p_195951_7_);
      p_195951_1_.setMapData(mapdata);
      p_195951_0_.getOrCreateTag().putInt("map", i);
      return mapdata;
   }

   public static String makeKey(int p_219993_0_) {
      return "map_" + p_219993_0_;
   }

   public void update(World p_77872_1_, Entity p_77872_2_, MapData p_77872_3_) {
      if (p_77872_1_.dimension() == p_77872_3_.dimension && p_77872_2_ instanceof PlayerEntity) {
         int i = 1 << p_77872_3_.scale;
         int j = p_77872_3_.x;
         int k = p_77872_3_.z;
         int l = MathHelper.floor(p_77872_2_.getX() - (double)j) / i + 64;
         int i1 = MathHelper.floor(p_77872_2_.getZ() - (double)k) / i + 64;
         int j1 = 128 / i;
         if (p_77872_1_.dimensionType().hasCeiling()) {
            j1 /= 2;
         }

         MapData.MapInfo mapdata$mapinfo = p_77872_3_.getHoldingPlayer((PlayerEntity)p_77872_2_);
         ++mapdata$mapinfo.step;
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = k1 - l;
                     int j2 = l1 - i1;
                     boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                     int k2 = (j / i + k1 - 64) * i;
                     int l2 = (k / i + l1 - 64) * i;
                     Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
                     Chunk chunk = p_77872_1_.getChunkAt(new BlockPos(k2, 0, l2));
                     if (!chunk.isEmpty()) {
                        ChunkPos chunkpos = chunk.getPos();
                        int i3 = k2 & 15;
                        int j3 = l2 & 15;
                        int k3 = 0;
                        double d1 = 0.0D;
                        if (p_77872_1_.dimensionType().hasCeiling()) {
                           int l3 = k2 + l2 * 231871;
                           l3 = l3 * l3 * 31287121 + l3 * 11;
                           if ((l3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.defaultBlockState().getMapColor(p_77872_1_, BlockPos.ZERO), 10);
                           } else {
                              multiset.add(Blocks.STONE.defaultBlockState().getMapColor(p_77872_1_, BlockPos.ZERO), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
                           BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j4 = 0; j4 < i; ++j4) {
                                 int k4 = chunk.getHeight(Heightmap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                 BlockState blockstate;
                                 if (k4 <= 1) {
                                    blockstate = Blocks.BEDROCK.defaultBlockState();
                                 } else {
                                    do {
                                       --k4;
                                       blockpos$mutable1.set(chunkpos.getMinBlockX() + i4 + i3, k4, chunkpos.getMinBlockZ() + j4 + j3);
                                       blockstate = chunk.getBlockState(blockpos$mutable1);
                                    } while(blockstate.getMapColor(p_77872_1_, blockpos$mutable1) == MaterialColor.NONE && k4 > 0);

                                    if (k4 > 0 && !blockstate.getFluidState().isEmpty()) {
                                       int l4 = k4 - 1;
                                       blockpos$mutable.set(blockpos$mutable1);

                                       BlockState blockstate1;
                                       do {
                                          blockpos$mutable.setY(l4--);
                                          blockstate1 = chunk.getBlockState(blockpos$mutable);
                                          ++k3;
                                       } while(l4 > 0 && !blockstate1.getFluidState().isEmpty());

                                       blockstate = this.getCorrectStateForFluidBlock(p_77872_1_, blockstate, blockpos$mutable1);
                                    }
                                 }

                                 p_77872_3_.checkBanners(p_77872_1_, chunkpos.getMinBlockX() + i4 + i3, chunkpos.getMinBlockZ() + j4 + j3);
                                 d1 += (double)k4 / (double)(i * i);
                                 multiset.add(blockstate.getMapColor(p_77872_1_, blockpos$mutable1));
                              }
                           }
                        }

                        k3 = k3 / (i * i);
                        double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                        int i5 = 1;
                        if (d2 > 0.6D) {
                           i5 = 2;
                        }

                        if (d2 < -0.6D) {
                           i5 = 0;
                        }

                        MaterialColor materialcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.NONE);
                        if (materialcolor == MaterialColor.WATER) {
                           d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           i5 = 1;
                           if (d2 < 0.5D) {
                              i5 = 2;
                           }

                           if (d2 > 0.9D) {
                              i5 = 0;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           byte b0 = p_77872_3_.colors[k1 + l1 * 128];
                           byte b1 = (byte)(materialcolor.id * 4 + i5);
                           if (b0 != b1) {
                              p_77872_3_.colors[k1 + l1 * 128] = b1;
                              p_77872_3_.setDirty(k1, l1);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private BlockState getCorrectStateForFluidBlock(World p_211698_1_, BlockState p_211698_2_, BlockPos p_211698_3_) {
      FluidState fluidstate = p_211698_2_.getFluidState();
      return !fluidstate.isEmpty() && !p_211698_2_.isFaceSturdy(p_211698_1_, p_211698_3_, Direction.UP) ? fluidstate.createLegacyBlock() : p_211698_2_;
   }

   private static boolean isLand(Biome[] p_195954_0_, int p_195954_1_, int p_195954_2_, int p_195954_3_) {
      return p_195954_0_[p_195954_2_ * p_195954_1_ + p_195954_3_ * p_195954_1_ * 128 * p_195954_1_].getDepth() >= 0.0F;
   }

   public static void renderBiomePreviewMap(ServerWorld p_226642_0_, ItemStack p_226642_1_) {
      MapData mapdata = getOrCreateSavedData(p_226642_1_, p_226642_0_);
      if (mapdata != null) {
         if (p_226642_0_.dimension() == mapdata.dimension) {
            int i = 1 << mapdata.scale;
            int j = mapdata.x;
            int k = mapdata.z;
            Biome[] abiome = new Biome[128 * i * 128 * i];

            for(int l = 0; l < 128 * i; ++l) {
               for(int i1 = 0; i1 < 128 * i; ++i1) {
                  abiome[l * 128 * i + i1] = p_226642_0_.getBiome(new BlockPos((j / i - 64) * i + i1, 0, (k / i - 64) * i + l));
               }
            }

            for(int l1 = 0; l1 < 128; ++l1) {
               for(int i2 = 0; i2 < 128; ++i2) {
                  if (l1 > 0 && i2 > 0 && l1 < 127 && i2 < 127) {
                     Biome biome = abiome[l1 * i + i2 * i * 128 * i];
                     int j1 = 8;
                     if (isLand(abiome, i, l1 - 1, i2 - 1)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1 - 1, i2 + 1)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1 - 1, i2)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1 + 1, i2 - 1)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1 + 1, i2 + 1)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1 + 1, i2)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1, i2 - 1)) {
                        --j1;
                     }

                     if (isLand(abiome, i, l1, i2 + 1)) {
                        --j1;
                     }

                     int k1 = 3;
                     MaterialColor materialcolor = MaterialColor.NONE;
                     if (biome.getDepth() < 0.0F) {
                        materialcolor = MaterialColor.COLOR_ORANGE;
                        if (j1 > 7 && i2 % 2 == 0) {
                           k1 = (l1 + (int)(MathHelper.sin((float)i2 + 0.0F) * 7.0F)) / 8 % 5;
                           if (k1 == 3) {
                              k1 = 1;
                           } else if (k1 == 4) {
                              k1 = 0;
                           }
                        } else if (j1 > 7) {
                           materialcolor = MaterialColor.NONE;
                        } else if (j1 > 5) {
                           k1 = 1;
                        } else if (j1 > 3) {
                           k1 = 0;
                        } else if (j1 > 1) {
                           k1 = 0;
                        }
                     } else if (j1 > 0) {
                        materialcolor = MaterialColor.COLOR_BROWN;
                        if (j1 > 3) {
                           k1 = 1;
                        } else {
                           k1 = 3;
                        }
                     }

                     if (materialcolor != MaterialColor.NONE) {
                        mapdata.colors[l1 + i2 * 128] = (byte)(materialcolor.id * 4 + k1);
                        mapdata.setDirty(l1, i2);
                     }
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
      if (!p_77663_2_.isClientSide) {
         MapData mapdata = getOrCreateSavedData(p_77663_1_, p_77663_2_);
         if (mapdata != null) {
            if (p_77663_3_ instanceof PlayerEntity) {
               PlayerEntity playerentity = (PlayerEntity)p_77663_3_;
               mapdata.tickCarriedBy(playerentity, p_77663_1_);
            }

            if (!mapdata.locked && (p_77663_5_ || p_77663_3_ instanceof PlayerEntity && ((PlayerEntity)p_77663_3_).getOffhandItem() == p_77663_1_)) {
               this.update(p_77663_2_, p_77663_3_, mapdata);
            }

         }
      }
   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack p_150911_1_, World p_150911_2_, PlayerEntity p_150911_3_) {
      return getOrCreateSavedData(p_150911_1_, p_150911_2_).getUpdatePacket(p_150911_1_, p_150911_2_, p_150911_3_);
   }

   public void onCraftedBy(ItemStack p_77622_1_, World p_77622_2_, PlayerEntity p_77622_3_) {
      CompoundNBT compoundnbt = p_77622_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("map_scale_direction", 99)) {
         scaleMap(p_77622_1_, p_77622_2_, compoundnbt.getInt("map_scale_direction"));
         compoundnbt.remove("map_scale_direction");
      } else if (compoundnbt != null && compoundnbt.contains("map_to_lock", 1) && compoundnbt.getBoolean("map_to_lock")) {
         lockMap(p_77622_2_, p_77622_1_);
         compoundnbt.remove("map_to_lock");
      }

   }

   protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_) {
      MapData mapdata = getOrCreateSavedData(p_185063_0_, p_185063_1_);
      if (mapdata != null) {
         createAndStoreSavedData(p_185063_0_, p_185063_1_, mapdata.x, mapdata.z, MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4), mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
      }

   }

   public static void lockMap(World p_219992_0_, ItemStack p_219992_1_) {
      MapData mapdata = getOrCreateSavedData(p_219992_1_, p_219992_0_);
      if (mapdata != null) {
         MapData mapdata1 = createAndStoreSavedData(p_219992_1_, p_219992_0_, 0, 0, mapdata.scale, mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
         mapdata1.lockData(mapdata);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      MapData mapdata = p_77624_2_ == null ? null : getOrCreateSavedData(p_77624_1_, p_77624_2_);
      if (mapdata != null && mapdata.locked) {
         p_77624_3_.add((new TranslationTextComponent("filled_map.locked", getMapId(p_77624_1_))).withStyle(TextFormatting.GRAY));
      }

      if (p_77624_4_.isAdvanced()) {
         if (mapdata != null) {
            p_77624_3_.add((new TranslationTextComponent("filled_map.id", getMapId(p_77624_1_))).withStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TranslationTextComponent("filled_map.scale", 1 << mapdata.scale)).withStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TranslationTextComponent("filled_map.level", mapdata.scale, 4)).withStyle(TextFormatting.GRAY));
         } else {
            p_77624_3_.add((new TranslationTextComponent("filled_map.unknown")).withStyle(TextFormatting.GRAY));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(ItemStack p_190907_0_) {
      CompoundNBT compoundnbt = p_190907_0_.getTagElement("display");
      if (compoundnbt != null && compoundnbt.contains("MapColor", 99)) {
         int i = compoundnbt.getInt("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      BlockState blockstate = p_195939_1_.getLevel().getBlockState(p_195939_1_.getClickedPos());
      if (blockstate.is(BlockTags.BANNERS)) {
         if (!p_195939_1_.getLevel().isClientSide) {
            MapData mapdata = getOrCreateSavedData(p_195939_1_.getItemInHand(), p_195939_1_.getLevel());
            mapdata.toggleBanner(p_195939_1_.getLevel(), p_195939_1_.getClickedPos());
         }

         return ActionResultType.sidedSuccess(p_195939_1_.getLevel().isClientSide);
      } else {
         return super.useOn(p_195939_1_);
      }
   }
}
