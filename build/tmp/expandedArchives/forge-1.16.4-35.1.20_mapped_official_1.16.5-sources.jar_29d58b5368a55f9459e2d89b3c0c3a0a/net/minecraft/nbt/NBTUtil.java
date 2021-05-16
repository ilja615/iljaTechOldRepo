package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   @Nullable
   public static GameProfile readGameProfile(CompoundNBT p_152459_0_) {
      String s = null;
      UUID uuid = null;
      if (p_152459_0_.contains("Name", 8)) {
         s = p_152459_0_.getString("Name");
      }

      if (p_152459_0_.hasUUID("Id")) {
         uuid = p_152459_0_.getUUID("Id");
      }

      try {
         GameProfile gameprofile = new GameProfile(uuid, s);
         if (p_152459_0_.contains("Properties", 10)) {
            CompoundNBT compoundnbt = p_152459_0_.getCompound("Properties");

            for(String s1 : compoundnbt.getAllKeys()) {
               ListNBT listnbt = compoundnbt.getList(s1, 10);

               for(int i = 0; i < listnbt.size(); ++i) {
                  CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                  String s2 = compoundnbt1.getString("Value");
                  if (compoundnbt1.contains("Signature", 8)) {
                     gameprofile.getProperties().put(s1, new com.mojang.authlib.properties.Property(s1, s2, compoundnbt1.getString("Signature")));
                  } else {
                     gameprofile.getProperties().put(s1, new com.mojang.authlib.properties.Property(s1, s2));
                  }
               }
            }
         }

         return gameprofile;
      } catch (Throwable throwable) {
         return null;
      }
   }

   public static CompoundNBT writeGameProfile(CompoundNBT p_180708_0_, GameProfile p_180708_1_) {
      if (!StringUtils.isNullOrEmpty(p_180708_1_.getName())) {
         p_180708_0_.putString("Name", p_180708_1_.getName());
      }

      if (p_180708_1_.getId() != null) {
         p_180708_0_.putUUID("Id", p_180708_1_.getId());
      }

      if (!p_180708_1_.getProperties().isEmpty()) {
         CompoundNBT compoundnbt = new CompoundNBT();

         for(String s : p_180708_1_.getProperties().keySet()) {
            ListNBT listnbt = new ListNBT();

            for(com.mojang.authlib.properties.Property property : p_180708_1_.getProperties().get(s)) {
               CompoundNBT compoundnbt1 = new CompoundNBT();
               compoundnbt1.putString("Value", property.getValue());
               if (property.hasSignature()) {
                  compoundnbt1.putString("Signature", property.getSignature());
               }

               listnbt.add(compoundnbt1);
            }

            compoundnbt.put(s, listnbt);
         }

         p_180708_0_.put("Properties", compoundnbt);
      }

      return p_180708_0_;
   }

   @VisibleForTesting
   public static boolean compareNbt(@Nullable INBT p_181123_0_, @Nullable INBT p_181123_1_, boolean p_181123_2_) {
      if (p_181123_0_ == p_181123_1_) {
         return true;
      } else if (p_181123_0_ == null) {
         return true;
      } else if (p_181123_1_ == null) {
         return false;
      } else if (!p_181123_0_.getClass().equals(p_181123_1_.getClass())) {
         return false;
      } else if (p_181123_0_ instanceof CompoundNBT) {
         CompoundNBT compoundnbt = (CompoundNBT)p_181123_0_;
         CompoundNBT compoundnbt1 = (CompoundNBT)p_181123_1_;

         for(String s : compoundnbt.getAllKeys()) {
            INBT inbt1 = compoundnbt.get(s);
            if (!compareNbt(inbt1, compoundnbt1.get(s), p_181123_2_)) {
               return false;
            }
         }

         return true;
      } else if (p_181123_0_ instanceof ListNBT && p_181123_2_) {
         ListNBT listnbt = (ListNBT)p_181123_0_;
         ListNBT listnbt1 = (ListNBT)p_181123_1_;
         if (listnbt.isEmpty()) {
            return listnbt1.isEmpty();
         } else {
            for(int i = 0; i < listnbt.size(); ++i) {
               INBT inbt = listnbt.get(i);
               boolean flag = false;

               for(int j = 0; j < listnbt1.size(); ++j) {
                  if (compareNbt(inbt, listnbt1.get(j), p_181123_2_)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return p_181123_0_.equals(p_181123_1_);
      }
   }

   public static IntArrayNBT createUUID(UUID p_240626_0_) {
      return new IntArrayNBT(UUIDCodec.uuidToIntArray(p_240626_0_));
   }

   public static UUID loadUUID(INBT p_186860_0_) {
      if (p_186860_0_.getType() != IntArrayNBT.TYPE) {
         throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayNBT.TYPE.getName() + ", but found " + p_186860_0_.getType().getName() + ".");
      } else {
         int[] aint = ((IntArrayNBT)p_186860_0_).getAsIntArray();
         if (aint.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + aint.length + ".");
         } else {
            return UUIDCodec.uuidFromIntArray(aint);
         }
      }
   }

   public static BlockPos readBlockPos(CompoundNBT p_186861_0_) {
      return new BlockPos(p_186861_0_.getInt("X"), p_186861_0_.getInt("Y"), p_186861_0_.getInt("Z"));
   }

   public static CompoundNBT writeBlockPos(BlockPos p_186859_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putInt("X", p_186859_0_.getX());
      compoundnbt.putInt("Y", p_186859_0_.getY());
      compoundnbt.putInt("Z", p_186859_0_.getZ());
      return compoundnbt;
   }

   public static BlockState readBlockState(CompoundNBT p_190008_0_) {
      if (!p_190008_0_.contains("Name", 8)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         Block block = Registry.BLOCK.get(new ResourceLocation(p_190008_0_.getString("Name")));
         BlockState blockstate = block.defaultBlockState();
         if (p_190008_0_.contains("Properties", 10)) {
            CompoundNBT compoundnbt = p_190008_0_.getCompound("Properties");
            StateContainer<Block, BlockState> statecontainer = block.getStateDefinition();

            for(String s : compoundnbt.getAllKeys()) {
               Property<?> property = statecontainer.getProperty(s);
               if (property != null) {
                  blockstate = setValueHelper(blockstate, property, s, compoundnbt, p_190008_0_);
               }
            }
         }

         return blockstate;
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_193590_0_, Property<T> p_193590_1_, String p_193590_2_, CompoundNBT p_193590_3_, CompoundNBT p_193590_4_) {
      Optional<T> optional = p_193590_1_.getValue(p_193590_3_.getString(p_193590_2_));
      if (optional.isPresent()) {
         return p_193590_0_.setValue(p_193590_1_, optional.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_193590_2_, p_193590_3_.getString(p_193590_2_), p_193590_4_.toString());
         return p_193590_0_;
      }
   }

   public static CompoundNBT writeBlockState(BlockState p_190009_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", Registry.BLOCK.getKey(p_190009_0_.getBlock()).toString());
      ImmutableMap<Property<?>, Comparable<?>> immutablemap = p_190009_0_.getValues();
      if (!immutablemap.isEmpty()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();

         for(Entry<Property<?>, Comparable<?>> entry : immutablemap.entrySet()) {
            Property<?> property = entry.getKey();
            compoundnbt1.putString(property.getName(), getName(property, entry.getValue()));
         }

         compoundnbt.put("Properties", compoundnbt1);
      }

      return compoundnbt;
   }

   private static <T extends Comparable<T>> String getName(Property<T> p_190010_0_, Comparable<?> p_190010_1_) {
      return p_190010_0_.getName((T)p_190010_1_);
   }

   public static CompoundNBT update(DataFixer p_210822_0_, DefaultTypeReferences p_210822_1_, CompoundNBT p_210822_2_, int p_210822_3_) {
      return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, SharedConstants.getCurrentVersion().getWorldVersion());
   }

   public static CompoundNBT update(DataFixer p_210821_0_, DefaultTypeReferences p_210821_1_, CompoundNBT p_210821_2_, int p_210821_3_, int p_210821_4_) {
      return (CompoundNBT)p_210821_0_.update(p_210821_1_.getType(), new Dynamic<>(NBTDynamicOps.INSTANCE, p_210821_2_), p_210821_3_, p_210821_4_).getValue();
   }
}
