package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<IDataSerializer<?>> SERIALIZERS = new IntIdentityHashBiMap<>(16);
   public static final IDataSerializer<Byte> BYTE = new IDataSerializer<Byte>() {
      public void write(PacketBuffer p_187160_1_, Byte p_187160_2_) {
         p_187160_1_.writeByte(p_187160_2_);
      }

      public Byte read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readByte();
      }

      public Byte copy(Byte p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Integer> INT = new IDataSerializer<Integer>() {
      public void write(PacketBuffer p_187160_1_, Integer p_187160_2_) {
         p_187160_1_.writeVarInt(p_187160_2_);
      }

      public Integer read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readVarInt();
      }

      public Integer copy(Integer p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Float> FLOAT = new IDataSerializer<Float>() {
      public void write(PacketBuffer p_187160_1_, Float p_187160_2_) {
         p_187160_1_.writeFloat(p_187160_2_);
      }

      public Float read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readFloat();
      }

      public Float copy(Float p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<String> STRING = new IDataSerializer<String>() {
      public void write(PacketBuffer p_187160_1_, String p_187160_2_) {
         p_187160_1_.writeUtf(p_187160_2_);
      }

      public String read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readUtf(32767);
      }

      public String copy(String p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<ITextComponent> COMPONENT = new IDataSerializer<ITextComponent>() {
      public void write(PacketBuffer p_187160_1_, ITextComponent p_187160_2_) {
         p_187160_1_.writeComponent(p_187160_2_);
      }

      public ITextComponent read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readComponent();
      }

      public ITextComponent copy(ITextComponent p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Optional<ITextComponent>> OPTIONAL_COMPONENT = new IDataSerializer<Optional<ITextComponent>>() {
      public void write(PacketBuffer p_187160_1_, Optional<ITextComponent> p_187160_2_) {
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeBoolean(true);
            p_187160_1_.writeComponent(p_187160_2_.get());
         } else {
            p_187160_1_.writeBoolean(false);
         }

      }

      public Optional<ITextComponent> read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBoolean() ? Optional.of(p_187159_1_.readComponent()) : Optional.empty();
      }

      public Optional<ITextComponent> copy(Optional<ITextComponent> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<ItemStack> ITEM_STACK = new IDataSerializer<ItemStack>() {
      public void write(PacketBuffer p_187160_1_, ItemStack p_187160_2_) {
         p_187160_1_.writeItem(p_187160_2_);
      }

      public ItemStack read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readItem();
      }

      public ItemStack copy(ItemStack p_192717_1_) {
         return p_192717_1_.copy();
      }
   };
   public static final IDataSerializer<Optional<BlockState>> BLOCK_STATE = new IDataSerializer<Optional<BlockState>>() {
      public void write(PacketBuffer p_187160_1_, Optional<BlockState> p_187160_2_) {
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeVarInt(Block.getId(p_187160_2_.get()));
         } else {
            p_187160_1_.writeVarInt(0);
         }

      }

      public Optional<BlockState> read(PacketBuffer p_187159_1_) {
         int i = p_187159_1_.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.stateById(i));
      }

      public Optional<BlockState> copy(Optional<BlockState> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Boolean> BOOLEAN = new IDataSerializer<Boolean>() {
      public void write(PacketBuffer p_187160_1_, Boolean p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_);
      }

      public Boolean read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBoolean();
      }

      public Boolean copy(Boolean p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<IParticleData> PARTICLE = new IDataSerializer<IParticleData>() {
      public void write(PacketBuffer p_187160_1_, IParticleData p_187160_2_) {
         p_187160_1_.writeVarInt(Registry.PARTICLE_TYPE.getId(p_187160_2_.getType()));
         p_187160_2_.writeToNetwork(p_187160_1_);
      }

      public IParticleData read(PacketBuffer p_187159_1_) {
         return this.readParticle(p_187159_1_, Registry.PARTICLE_TYPE.byId(p_187159_1_.readVarInt()));
      }

      private <T extends IParticleData> T readParticle(PacketBuffer p_200543_1_, ParticleType<T> p_200543_2_) {
         return p_200543_2_.getDeserializer().fromNetwork(p_200543_2_, p_200543_1_);
      }

      public IParticleData copy(IParticleData p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Rotations> ROTATIONS = new IDataSerializer<Rotations>() {
      public void write(PacketBuffer p_187160_1_, Rotations p_187160_2_) {
         p_187160_1_.writeFloat(p_187160_2_.getX());
         p_187160_1_.writeFloat(p_187160_2_.getY());
         p_187160_1_.writeFloat(p_187160_2_.getZ());
      }

      public Rotations read(PacketBuffer p_187159_1_) {
         return new Rotations(p_187159_1_.readFloat(), p_187159_1_.readFloat(), p_187159_1_.readFloat());
      }

      public Rotations copy(Rotations p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<BlockPos> BLOCK_POS = new IDataSerializer<BlockPos>() {
      public void write(PacketBuffer p_187160_1_, BlockPos p_187160_2_) {
         p_187160_1_.writeBlockPos(p_187160_2_);
      }

      public BlockPos read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBlockPos();
      }

      public BlockPos copy(BlockPos p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>() {
      public void write(PacketBuffer p_187160_1_, Optional<BlockPos> p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_.isPresent());
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeBlockPos(p_187160_2_.get());
         }

      }

      public Optional<BlockPos> read(PacketBuffer p_187159_1_) {
         return !p_187159_1_.readBoolean() ? Optional.empty() : Optional.of(p_187159_1_.readBlockPos());
      }

      public Optional<BlockPos> copy(Optional<BlockPos> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Direction> DIRECTION = new IDataSerializer<Direction>() {
      public void write(PacketBuffer p_187160_1_, Direction p_187160_2_) {
         p_187160_1_.writeEnum(p_187160_2_);
      }

      public Direction read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readEnum(Direction.class);
      }

      public Direction copy(Direction p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Optional<UUID>> OPTIONAL_UUID = new IDataSerializer<Optional<UUID>>() {
      public void write(PacketBuffer p_187160_1_, Optional<UUID> p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_.isPresent());
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeUUID(p_187160_2_.get());
         }

      }

      public Optional<UUID> read(PacketBuffer p_187159_1_) {
         return !p_187159_1_.readBoolean() ? Optional.empty() : Optional.of(p_187159_1_.readUUID());
      }

      public Optional<UUID> copy(Optional<UUID> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<CompoundNBT> COMPOUND_TAG = new IDataSerializer<CompoundNBT>() {
      public void write(PacketBuffer p_187160_1_, CompoundNBT p_187160_2_) {
         p_187160_1_.writeNbt(p_187160_2_);
      }

      public CompoundNBT read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readNbt();
      }

      public CompoundNBT copy(CompoundNBT p_192717_1_) {
         return p_192717_1_.copy();
      }
   };
   public static final IDataSerializer<VillagerData> VILLAGER_DATA = new IDataSerializer<VillagerData>() {
      public void write(PacketBuffer p_187160_1_, VillagerData p_187160_2_) {
         p_187160_1_.writeVarInt(Registry.VILLAGER_TYPE.getId(p_187160_2_.getType()));
         p_187160_1_.writeVarInt(Registry.VILLAGER_PROFESSION.getId(p_187160_2_.getProfession()));
         p_187160_1_.writeVarInt(p_187160_2_.getLevel());
      }

      public VillagerData read(PacketBuffer p_187159_1_) {
         return new VillagerData(Registry.VILLAGER_TYPE.byId(p_187159_1_.readVarInt()), Registry.VILLAGER_PROFESSION.byId(p_187159_1_.readVarInt()), p_187159_1_.readVarInt());
      }

      public VillagerData copy(VillagerData p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new IDataSerializer<OptionalInt>() {
      public void write(PacketBuffer p_187160_1_, OptionalInt p_187160_2_) {
         p_187160_1_.writeVarInt(p_187160_2_.orElse(-1) + 1);
      }

      public OptionalInt read(PacketBuffer p_187159_1_) {
         int i = p_187159_1_.readVarInt();
         return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
      }

      public OptionalInt copy(OptionalInt p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final IDataSerializer<Pose> POSE = new IDataSerializer<Pose>() {
      public void write(PacketBuffer p_187160_1_, Pose p_187160_2_) {
         p_187160_1_.writeEnum(p_187160_2_);
      }

      public Pose read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readEnum(Pose.class);
      }

      public Pose copy(Pose p_192717_1_) {
         return p_192717_1_;
      }
   };

   public static void registerSerializer(IDataSerializer<?> p_187189_0_) {
      if (SERIALIZERS.add(p_187189_0_) >= 256) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
   }

   @Nullable
   public static IDataSerializer<?> getSerializer(int p_187190_0_) {
      return net.minecraftforge.common.ForgeHooks.getSerializer(p_187190_0_, SERIALIZERS);
   }

   public static int getSerializedId(IDataSerializer<?> p_187188_0_) {
      return net.minecraftforge.common.ForgeHooks.getSerializerId(p_187188_0_, SERIALIZERS);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
   }
}
