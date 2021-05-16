package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

   public BlockModel generateBlockModel(Function<RenderMaterial, TextureAtlasSprite> p_209579_1_, BlockModel p_209579_2_) {
      Map<String, Either<RenderMaterial, String>> map = Maps.newHashMap();
      List<BlockPart> list = Lists.newArrayList();

      for(int i = 0; i < LAYERS.size(); ++i) {
         String s = LAYERS.get(i);
         if (!p_209579_2_.hasTexture(s)) {
            break;
         }

         RenderMaterial rendermaterial = p_209579_2_.getMaterial(s);
         map.put(s, Either.left(rendermaterial));
         TextureAtlasSprite textureatlassprite = p_209579_1_.apply(rendermaterial);
         list.addAll(this.processFrames(i, s, textureatlassprite));
      }

      map.put("particle", p_209579_2_.hasTexture("particle") ? Either.left(p_209579_2_.getMaterial("particle")) : map.get("layer0"));
      BlockModel blockmodel = new BlockModel((ResourceLocation)null, list, map, false, p_209579_2_.getGuiLight(), p_209579_2_.getTransforms(), p_209579_2_.getOverrides());
      blockmodel.name = p_209579_2_.name;
      blockmodel.customData.copyFrom(p_209579_2_.customData);
      return blockmodel;
   }

   private List<BlockPart> processFrames(int p_178394_1_, String p_178394_2_, TextureAtlasSprite p_178394_3_) {
      Map<Direction, BlockPartFace> map = Maps.newHashMap();
      map.put(Direction.SOUTH, new BlockPartFace((Direction)null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map.put(Direction.NORTH, new BlockPartFace((Direction)null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockPart> list = Lists.newArrayList();
      list.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, (BlockPartRotation)null, true));
      list.addAll(this.createSideElements(p_178394_3_, p_178394_2_, p_178394_1_));
      return list;
   }

   private List<BlockPart> createSideElements(TextureAtlasSprite p_178397_1_, String p_178397_2_, int p_178397_3_) {
      float f = (float)p_178397_1_.getWidth();
      float f1 = (float)p_178397_1_.getHeight();
      List<BlockPart> list = Lists.newArrayList();

      for(ItemModelGenerator.Span itemmodelgenerator$span : this.getSpans(p_178397_1_)) {
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 16.0F / f;
         float f11 = 16.0F / f1;
         float f12 = (float)itemmodelgenerator$span.getMin();
         float f13 = (float)itemmodelgenerator$span.getMax();
         float f14 = (float)itemmodelgenerator$span.getAnchor();
         ItemModelGenerator.SpanFacing itemmodelgenerator$spanfacing = itemmodelgenerator$span.getFacing();
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f8 = f14;
            f3 = f14;
            f5 = f14;
            f9 = f14 + 1.0F;
            break;
         case DOWN:
            f8 = f14;
            f9 = f14 + 1.0F;
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f3 = f14 + 1.0F;
            f5 = f14 + 1.0F;
            break;
         case LEFT:
            f6 = f14;
            f2 = f14;
            f4 = f14;
            f7 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            break;
         case RIGHT:
            f6 = f14;
            f7 = f14 + 1.0F;
            f2 = f14 + 1.0F;
            f4 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
         }

         f2 = f2 * f10;
         f4 = f4 * f10;
         f3 = f3 * f11;
         f5 = f5 * f11;
         f3 = 16.0F - f3;
         f5 = 16.0F - f5;
         f6 = f6 * f10;
         f7 = f7 * f10;
         f8 = f8 * f11;
         f9 = f9 * f11;
         Map<Direction, BlockPartFace> map = Maps.newHashMap();
         map.put(itemmodelgenerator$spanfacing.getDirection(), new BlockPartFace((Direction)null, p_178397_3_, p_178397_2_, new BlockFaceUV(new float[]{f6, f8, f7, f9}, 0)));
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f4, f3, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case DOWN:
            list.add(new BlockPart(new Vector3f(f2, f5, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case LEFT:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f2, f5, 8.5F), map, (BlockPartRotation)null, true));
            break;
         case RIGHT:
            list.add(new BlockPart(new Vector3f(f4, f3, 7.5F), new Vector3f(f4, f5, 8.5F), map, (BlockPartRotation)null, true));
         }
      }

      return list;
   }

   private List<ItemModelGenerator.Span> getSpans(TextureAtlasSprite p_178393_1_) {
      int i = p_178393_1_.getWidth();
      int j = p_178393_1_.getHeight();
      List<ItemModelGenerator.Span> list = Lists.newArrayList();

      for(int k = 0; k < p_178393_1_.getFrameCount(); ++k) {
         for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
               boolean flag = !this.isTransparent(p_178393_1_, k, i1, l, i, j);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, list, p_178393_1_, k, i1, l, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, list, p_178393_1_, k, i1, l, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, list, p_178393_1_, k, i1, l, i, j, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, list, p_178393_1_, k, i1, l, i, j, flag);
            }
         }
      }

      return list;
   }

   private void checkTransition(ItemModelGenerator.SpanFacing p_199338_1_, List<ItemModelGenerator.Span> p_199338_2_, TextureAtlasSprite p_199338_3_, int p_199338_4_, int p_199338_5_, int p_199338_6_, int p_199338_7_, int p_199338_8_, boolean p_199338_9_) {
      boolean flag = this.isTransparent(p_199338_3_, p_199338_4_, p_199338_5_ + p_199338_1_.getXOffset(), p_199338_6_ + p_199338_1_.getYOffset(), p_199338_7_, p_199338_8_) && p_199338_9_;
      if (flag) {
         this.createOrExpandSpan(p_199338_2_, p_199338_1_, p_199338_5_, p_199338_6_);
      }

   }

   private void createOrExpandSpan(List<ItemModelGenerator.Span> p_178395_1_, ItemModelGenerator.SpanFacing p_178395_2_, int p_178395_3_, int p_178395_4_) {
      ItemModelGenerator.Span itemmodelgenerator$span = null;

      for(ItemModelGenerator.Span itemmodelgenerator$span1 : p_178395_1_) {
         if (itemmodelgenerator$span1.getFacing() == p_178395_2_) {
            int i = p_178395_2_.isHorizontal() ? p_178395_4_ : p_178395_3_;
            if (itemmodelgenerator$span1.getAnchor() == i) {
               itemmodelgenerator$span = itemmodelgenerator$span1;
               break;
            }
         }
      }

      int j = p_178395_2_.isHorizontal() ? p_178395_4_ : p_178395_3_;
      int k = p_178395_2_.isHorizontal() ? p_178395_3_ : p_178395_4_;
      if (itemmodelgenerator$span == null) {
         p_178395_1_.add(new ItemModelGenerator.Span(p_178395_2_, k, j));
      } else {
         itemmodelgenerator$span.expand(k);
      }

   }

   private boolean isTransparent(TextureAtlasSprite p_199339_1_, int p_199339_2_, int p_199339_3_, int p_199339_4_, int p_199339_5_, int p_199339_6_) {
      return p_199339_3_ >= 0 && p_199339_4_ >= 0 && p_199339_3_ < p_199339_5_ && p_199339_4_ < p_199339_6_ ? p_199339_1_.isTransparent(p_199339_2_, p_199339_3_, p_199339_4_) : true;
   }

   @OnlyIn(Dist.CLIENT)
   static class Span {
      private final ItemModelGenerator.SpanFacing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing p_i46216_1_, int p_i46216_2_, int p_i46216_3_) {
         this.facing = p_i46216_1_;
         this.min = p_i46216_2_;
         this.max = p_i46216_2_;
         this.anchor = p_i46216_3_;
      }

      public void expand(int p_178382_1_) {
         if (p_178382_1_ < this.min) {
            this.min = p_178382_1_;
         } else if (p_178382_1_ > this.max) {
            this.max = p_178382_1_;
         }

      }

      public ItemModelGenerator.SpanFacing getFacing() {
         return this.facing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum SpanFacing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction direction;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(Direction p_i46215_3_, int p_i46215_4_, int p_i46215_5_) {
         this.direction = p_i46215_3_;
         this.xOffset = p_i46215_4_;
         this.yOffset = p_i46215_5_;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      public int getYOffset() {
         return this.yOffset;
      }

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }
   }
}
