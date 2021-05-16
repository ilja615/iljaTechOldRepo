package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<ItemParticleData> DESERIALIZER = new IParticleData.IDeserializer<ItemParticleData>() {
      public ItemParticleData fromCommand(ParticleType<ItemParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         p_197544_2_.expect(' ');
         ItemParser itemparser = (new ItemParser(p_197544_2_, false)).parse();
         ItemStack itemstack = (new ItemInput(itemparser.getItem(), itemparser.getNbt())).createItemStack(1, false);
         return new ItemParticleData(p_197544_1_, itemstack);
      }

      public ItemParticleData fromNetwork(ParticleType<ItemParticleData> p_197543_1_, PacketBuffer p_197543_2_) {
         return new ItemParticleData(p_197543_1_, p_197543_2_.readItem());
      }
   };
   private final ParticleType<ItemParticleData> type;
   private final ItemStack itemStack;

   public static Codec<ItemParticleData> codec(ParticleType<ItemParticleData> p_239809_0_) {
      return ItemStack.CODEC.xmap((p_239810_1_) -> {
         return new ItemParticleData(p_239809_0_, p_239810_1_);
      }, (p_239808_0_) -> {
         return p_239808_0_.itemStack;
      });
   }

   public ItemParticleData(ParticleType<ItemParticleData> p_i47952_1_, ItemStack p_i47952_2_) {
      this.type = p_i47952_1_;
      this.itemStack = p_i47952_2_.copy(); //Forge: Fix stack updating after the fact causing particle changes.
   }

   public void writeToNetwork(PacketBuffer p_197553_1_) {
      p_197553_1_.writeItem(this.itemStack);
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + (new ItemInput(this.itemStack.getItem(), this.itemStack.getTag())).serialize();
   }

   public ParticleType<ItemParticleData> getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return this.itemStack;
   }
}
