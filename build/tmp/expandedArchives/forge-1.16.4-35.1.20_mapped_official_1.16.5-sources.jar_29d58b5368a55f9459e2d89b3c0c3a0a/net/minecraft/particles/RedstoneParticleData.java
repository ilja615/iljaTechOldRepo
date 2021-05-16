package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneParticleData implements IParticleData {
   public static final RedstoneParticleData REDSTONE = new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F);
   public static final Codec<RedstoneParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> {
      return p_239803_0_.group(Codec.FLOAT.fieldOf("r").forGetter((p_239807_0_) -> {
         return p_239807_0_.r;
      }), Codec.FLOAT.fieldOf("g").forGetter((p_239806_0_) -> {
         return p_239806_0_.g;
      }), Codec.FLOAT.fieldOf("b").forGetter((p_239805_0_) -> {
         return p_239805_0_.b;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_239804_0_) -> {
         return p_239804_0_.scale;
      })).apply(p_239803_0_, RedstoneParticleData::new);
   });
   public static final IParticleData.IDeserializer<RedstoneParticleData> DESERIALIZER = new IParticleData.IDeserializer<RedstoneParticleData>() {
      public RedstoneParticleData fromCommand(ParticleType<RedstoneParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         p_197544_2_.expect(' ');
         float f = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float f1 = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float f2 = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float f3 = (float)p_197544_2_.readDouble();
         return new RedstoneParticleData(f, f1, f2, f3);
      }

      public RedstoneParticleData fromNetwork(ParticleType<RedstoneParticleData> p_197543_1_, PacketBuffer p_197543_2_) {
         return new RedstoneParticleData(p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat());
      }
   };
   private final float r;
   private final float g;
   private final float b;
   private final float scale;

   public RedstoneParticleData(float p_i47950_1_, float p_i47950_2_, float p_i47950_3_, float p_i47950_4_) {
      this.r = p_i47950_1_;
      this.g = p_i47950_2_;
      this.b = p_i47950_3_;
      this.scale = MathHelper.clamp(p_i47950_4_, 0.01F, 4.0F);
   }

   public void writeToNetwork(PacketBuffer p_197553_1_) {
      p_197553_1_.writeFloat(this.r);
      p_197553_1_.writeFloat(this.g);
      p_197553_1_.writeFloat(this.b);
      p_197553_1_.writeFloat(this.scale);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.r, this.g, this.b, this.scale);
   }

   public ParticleType<RedstoneParticleData> getType() {
      return ParticleTypes.DUST;
   }

   @OnlyIn(Dist.CLIENT)
   public float getR() {
      return this.r;
   }

   @OnlyIn(Dist.CLIENT)
   public float getG() {
      return this.g;
   }

   @OnlyIn(Dist.CLIENT)
   public float getB() {
      return this.b;
   }

   @OnlyIn(Dist.CLIENT)
   public float getScale() {
      return this.scale;
   }
}
