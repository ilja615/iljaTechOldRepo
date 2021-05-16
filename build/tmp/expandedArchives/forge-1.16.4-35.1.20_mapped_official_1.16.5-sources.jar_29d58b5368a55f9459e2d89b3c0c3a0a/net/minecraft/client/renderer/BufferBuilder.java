package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder extends DefaultColorVertexBuilder implements IVertexConsumer {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer buffer;
   private final List<BufferBuilder.DrawState> vertexCounts = Lists.newArrayList();
   private int lastRenderedCountIndex = 0;
   private int totalRenderedBytes = 0;
   private int nextElementByte = 0;
   private int totalUploadedBytes = 0;
   private int vertices;
   @Nullable
   private VertexFormatElement currentElement;
   private int elementIndex;
   private int mode;
   private VertexFormat format;
   private boolean fastFormat;
   private boolean fullFormat;
   private boolean building;

   public BufferBuilder(int p_i46275_1_) {
      this.buffer = GLAllocation.createByteBuffer(p_i46275_1_ * 4);
   }

   protected void ensureVertexCapacity() {
      this.ensureCapacity(this.format.getVertexSize());
   }

   private void ensureCapacity(int p_181670_1_) {
      if (this.nextElementByte + p_181670_1_ > this.buffer.capacity()) {
         int i = this.buffer.capacity();
         int j = i + roundUp(p_181670_1_);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
         ByteBuffer bytebuffer = GLAllocation.createByteBuffer(j);
         ((Buffer)this.buffer).position(0);
         bytebuffer.put(this.buffer);
         ((Buffer)bytebuffer).rewind();
         this.buffer = bytebuffer;
      }
   }

   private static int roundUp(int p_216566_0_) {
      int i = 2097152;
      if (p_216566_0_ == 0) {
         return i;
      } else {
         if (p_216566_0_ < 0) {
            i *= -1;
         }

         int j = p_216566_0_ % i;
         return j == 0 ? p_216566_0_ : p_216566_0_ + i - j;
      }
   }

   public void sortQuads(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
      ((Buffer)this.buffer).clear();
      FloatBuffer floatbuffer = this.buffer.asFloatBuffer();
      int i = this.vertices / 4;
      float[] afloat = new float[i];

      for(int j = 0; j < i; ++j) {
         afloat[j] = getQuadDistanceFromPlayer(floatbuffer, p_181674_1_, p_181674_2_, p_181674_3_, this.format.getIntegerSize(), this.totalRenderedBytes / 4 + j * this.format.getVertexSize());
      }

      int[] aint = new int[i];

      for(int k = 0; k < aint.length; aint[k] = k++) {
      }

      IntArrays.mergeSort(aint, (p_227830_1_, p_227830_2_) -> {
         return Floats.compare(afloat[p_227830_2_], afloat[p_227830_1_]);
      });
      BitSet bitset = new BitSet();
      FloatBuffer floatbuffer1 = GLAllocation.createFloatBuffer(this.format.getIntegerSize() * 4);

      for(int l = bitset.nextClearBit(0); l < aint.length; l = bitset.nextClearBit(l + 1)) {
         int i1 = aint[l];
         if (i1 != l) {
            this.limitToVertex(floatbuffer, i1);
            ((Buffer)floatbuffer1).clear();
            floatbuffer1.put(floatbuffer);
            int j1 = i1;

            for(int k1 = aint[i1]; j1 != l; k1 = aint[k1]) {
               this.limitToVertex(floatbuffer, k1);
               FloatBuffer floatbuffer2 = floatbuffer.slice();
               this.limitToVertex(floatbuffer, j1);
               floatbuffer.put(floatbuffer2);
               bitset.set(j1);
               j1 = k1;
            }

            this.limitToVertex(floatbuffer, l);
            ((Buffer)floatbuffer1).flip();
            floatbuffer.put(floatbuffer1);
         }

         bitset.set(l);
      }

   }

   private void limitToVertex(FloatBuffer p_227829_1_, int p_227829_2_) {
      int i = this.format.getIntegerSize() * 4;
      ((Buffer)p_227829_1_).limit(this.totalRenderedBytes / 4 + (p_227829_2_ + 1) * i);
      ((Buffer)p_227829_1_).position(this.totalRenderedBytes / 4 + p_227829_2_ * i);
   }

   public BufferBuilder.State getState() {
      ((Buffer)this.buffer).limit(this.nextElementByte);
      ((Buffer)this.buffer).position(this.totalRenderedBytes);
      ByteBuffer bytebuffer = ByteBuffer.allocate(this.vertices * this.format.getVertexSize());
      bytebuffer.put(this.buffer);
      ((Buffer)this.buffer).clear();
      return new BufferBuilder.State(bytebuffer, this.format);
   }

   private static float getQuadDistanceFromPlayer(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_) {
      float f = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
      float f1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
      float f2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
      float f3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
      float f4 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
      float f5 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
      float f6 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
      float f7 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
      float f8 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
      float f9 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
      float f10 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
      float f11 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
      float f12 = (f + f3 + f6 + f9) * 0.25F - p_181665_1_;
      float f13 = (f1 + f4 + f7 + f10) * 0.25F - p_181665_2_;
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - p_181665_3_;
      return f12 * f12 + f13 * f13 + f14 * f14;
   }

   public void restoreState(BufferBuilder.State p_178993_1_) {
      ((Buffer)p_178993_1_.data).clear();
      int i = p_178993_1_.data.capacity();
      this.ensureCapacity(i);
      ((Buffer)this.buffer).limit(this.buffer.capacity());
      ((Buffer)this.buffer).position(this.totalRenderedBytes);
      this.buffer.put(p_178993_1_.data);
      ((Buffer)this.buffer).clear();
      VertexFormat vertexformat = p_178993_1_.format;
      this.switchFormat(vertexformat);
      this.vertices = i / vertexformat.getVertexSize();
      this.nextElementByte = this.totalRenderedBytes + this.vertices * vertexformat.getVertexSize();
   }

   public void begin(int p_181668_1_, VertexFormat p_181668_2_) {
      if (this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.building = true;
         this.mode = p_181668_1_;
         this.switchFormat(p_181668_2_);
         this.currentElement = p_181668_2_.getElements().get(0);
         this.elementIndex = 0;
         ((Buffer)this.buffer).clear();
      }
   }

   private void switchFormat(VertexFormat p_227828_1_) {
      if (this.format != p_227828_1_) {
         this.format = p_227828_1_;
         boolean flag = p_227828_1_ == DefaultVertexFormats.NEW_ENTITY;
         boolean flag1 = p_227828_1_ == DefaultVertexFormats.BLOCK;
         this.fastFormat = flag || flag1;
         this.fullFormat = flag;
      }
   }

   public void end() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         this.building = false;
         this.vertexCounts.add(new BufferBuilder.DrawState(this.format, this.vertices, this.mode));
         this.totalRenderedBytes += this.vertices * this.format.getVertexSize();
         this.vertices = 0;
         this.currentElement = null;
         this.elementIndex = 0;
      }
   }

   public void putByte(int p_225589_1_, byte p_225589_2_) {
      this.buffer.put(this.nextElementByte + p_225589_1_, p_225589_2_);
   }

   public void putShort(int p_225591_1_, short p_225591_2_) {
      this.buffer.putShort(this.nextElementByte + p_225591_1_, p_225591_2_);
   }

   public void putFloat(int p_225590_1_, float p_225590_2_) {
      this.buffer.putFloat(this.nextElementByte + p_225590_1_, p_225590_2_);
   }

   public void endVertex() {
      if (this.elementIndex != 0) {
         throw new IllegalStateException("Not filled all elements of the vertex");
      } else {
         ++this.vertices;
         this.ensureVertexCapacity();
      }
   }

   public void nextElement() {
      ImmutableList<VertexFormatElement> immutablelist = this.format.getElements();
      this.elementIndex = (this.elementIndex + 1) % immutablelist.size();
      this.nextElementByte += this.currentElement.getByteSize();
      VertexFormatElement vertexformatelement = immutablelist.get(this.elementIndex);
      this.currentElement = vertexformatelement;
      if (vertexformatelement.getUsage() == VertexFormatElement.Usage.PADDING) {
         this.nextElement();
      }

      if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
         IVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
      }

   }

   public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else {
         return IVertexConsumer.super.color(p_225586_1_, p_225586_2_, p_225586_3_, p_225586_4_);
      }
   }

   public void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else if (this.fastFormat) {
         this.putFloat(0, p_225588_1_);
         this.putFloat(4, p_225588_2_);
         this.putFloat(8, p_225588_3_);
         this.putByte(12, (byte)((int)(p_225588_4_ * 255.0F)));
         this.putByte(13, (byte)((int)(p_225588_5_ * 255.0F)));
         this.putByte(14, (byte)((int)(p_225588_6_ * 255.0F)));
         this.putByte(15, (byte)((int)(p_225588_7_ * 255.0F)));
         this.putFloat(16, p_225588_8_);
         this.putFloat(20, p_225588_9_);
         int i;
         if (this.fullFormat) {
            this.putShort(24, (short)(p_225588_10_ & '\uffff'));
            this.putShort(26, (short)(p_225588_10_ >> 16 & '\uffff'));
            i = 28;
         } else {
            i = 24;
         }

         this.putShort(i + 0, (short)(p_225588_11_ & '\uffff'));
         this.putShort(i + 2, (short)(p_225588_11_ >> 16 & '\uffff'));
         this.putByte(i + 4, IVertexConsumer.normalIntValue(p_225588_12_));
         this.putByte(i + 5, IVertexConsumer.normalIntValue(p_225588_13_));
         this.putByte(i + 6, IVertexConsumer.normalIntValue(p_225588_14_));
         this.nextElementByte += i + 8;
         this.endVertex();
      } else {
         super.vertex(p_225588_1_, p_225588_2_, p_225588_3_, p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_, p_225588_8_, p_225588_9_, p_225588_10_, p_225588_11_, p_225588_12_, p_225588_13_, p_225588_14_);
      }
   }

   public Pair<BufferBuilder.DrawState, ByteBuffer> popNextBuffer() {
      BufferBuilder.DrawState bufferbuilder$drawstate = this.vertexCounts.get(this.lastRenderedCountIndex++);
      ((Buffer)this.buffer).position(this.totalUploadedBytes);
      this.totalUploadedBytes += bufferbuilder$drawstate.vertexCount() * bufferbuilder$drawstate.format().getVertexSize();
      ((Buffer)this.buffer).limit(this.totalUploadedBytes);
      if (this.lastRenderedCountIndex == this.vertexCounts.size() && this.vertices == 0) {
         this.clear();
      }

      ByteBuffer bytebuffer = this.buffer.slice();
      bytebuffer.order(this.buffer.order()); // FORGE: Fix incorrect byte order
      ((Buffer)this.buffer).clear();
      return Pair.of(bufferbuilder$drawstate, bytebuffer);
   }

   public void clear() {
      if (this.totalRenderedBytes != this.totalUploadedBytes) {
         LOGGER.warn("Bytes mismatch " + this.totalRenderedBytes + " " + this.totalUploadedBytes);
      }

      this.discard();
   }

   public void discard() {
      this.totalRenderedBytes = 0;
      this.totalUploadedBytes = 0;
      this.nextElementByte = 0;
      this.vertexCounts.clear();
      this.lastRenderedCountIndex = 0;
   }

   public VertexFormatElement currentElement() {
      if (this.currentElement == null) {
         throw new IllegalStateException("BufferBuilder not started");
      } else {
         return this.currentElement;
      }
   }

   public boolean building() {
      return this.building;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class DrawState {
      private final VertexFormat format;
      private final int vertexCount;
      private final int mode;

      private DrawState(VertexFormat p_i225905_1_, int p_i225905_2_, int p_i225905_3_) {
         this.format = p_i225905_1_;
         this.vertexCount = p_i225905_2_;
         this.mode = p_i225905_3_;
      }

      public VertexFormat format() {
         return this.format;
      }

      public int vertexCount() {
         return this.vertexCount;
      }

      public int mode() {
         return this.mode;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class State {
      private final ByteBuffer data;
      private final VertexFormat format;

      private State(ByteBuffer p_i225907_1_, VertexFormat p_i225907_2_) {
         this.data = p_i225907_1_;
         this.format = p_i225907_2_;
      }
   }

   // Forge start
   public void putBulkData(ByteBuffer buffer) {
      ensureCapacity(buffer.limit() + this.format.getVertexSize());
      ((Buffer)this.buffer).position(this.vertices * this.format.getVertexSize());
      this.buffer.put(buffer);
      this.vertices += buffer.limit() / this.format.getVertexSize();
      this.nextElementByte += buffer.limit();
   }

   public VertexFormat getVertexFormat() { return this.format; }
}
