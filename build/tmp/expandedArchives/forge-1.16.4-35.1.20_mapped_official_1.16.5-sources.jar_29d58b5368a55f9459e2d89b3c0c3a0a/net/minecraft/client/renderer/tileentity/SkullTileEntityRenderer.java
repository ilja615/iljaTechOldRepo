package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.model.DragonHeadModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkullTileEntityRenderer extends TileEntityRenderer<SkullTileEntity> {
   private static final Map<SkullBlock.ISkullType, GenericHeadModel> MODEL_BY_TYPE = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
      GenericHeadModel genericheadmodel = new GenericHeadModel(0, 0, 64, 32);
      GenericHeadModel genericheadmodel1 = new HumanoidHeadModel();
      DragonHeadModel dragonheadmodel = new DragonHeadModel(0.0F);
      p_209262_0_.put(SkullBlock.Types.SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.WITHER_SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.PLAYER, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.ZOMBIE, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.CREEPER, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.DRAGON, dragonheadmodel);
   });
   private static final Map<SkullBlock.ISkullType, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
      p_209263_0_.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      p_209263_0_.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      p_209263_0_.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      p_209263_0_.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
   });

   public SkullTileEntityRenderer(TileEntityRendererDispatcher p_i226015_1_) {
      super(p_i226015_1_);
   }

   public void render(SkullTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      float f = p_225616_1_.getMouthAnimation(p_225616_2_);
      BlockState blockstate = p_225616_1_.getBlockState();
      boolean flag = blockstate.getBlock() instanceof WallSkullBlock;
      Direction direction = flag ? blockstate.getValue(WallSkullBlock.FACING) : null;
      float f1 = 22.5F * (float)(flag ? (2 + direction.get2DDataValue()) * 4 : blockstate.getValue(SkullBlock.ROTATION));
      renderSkull(direction, f1, ((AbstractSkullBlock)blockstate.getBlock()).getType(), p_225616_1_.getOwnerProfile(), f, p_225616_3_, p_225616_4_, p_225616_5_);
   }

   public static void renderSkull(@Nullable Direction p_228879_0_, float p_228879_1_, SkullBlock.ISkullType p_228879_2_, @Nullable GameProfile p_228879_3_, float p_228879_4_, MatrixStack p_228879_5_, IRenderTypeBuffer p_228879_6_, int p_228879_7_) {
      GenericHeadModel genericheadmodel = MODEL_BY_TYPE.get(p_228879_2_);
      p_228879_5_.pushPose();
      if (p_228879_0_ == null) {
         p_228879_5_.translate(0.5D, 0.0D, 0.5D);
      } else {
         float f = 0.25F;
         p_228879_5_.translate((double)(0.5F - (float)p_228879_0_.getStepX() * 0.25F), 0.25D, (double)(0.5F - (float)p_228879_0_.getStepZ() * 0.25F));
      }

      p_228879_5_.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder ivertexbuilder = p_228879_6_.getBuffer(getRenderType(p_228879_2_, p_228879_3_));
      genericheadmodel.setupAnim(p_228879_4_, p_228879_1_, 0.0F);
      genericheadmodel.renderToBuffer(p_228879_5_, ivertexbuilder, p_228879_7_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_228879_5_.popPose();
   }

   private static RenderType getRenderType(SkullBlock.ISkullType p_228878_0_, @Nullable GameProfile p_228878_1_) {
      ResourceLocation resourcelocation = SKIN_BY_TYPE.get(p_228878_0_);
      if (p_228878_0_ == SkullBlock.Types.PLAYER && p_228878_1_ != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(p_228878_1_);
         return map.containsKey(Type.SKIN) ? RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN)) : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(p_228878_1_)));
      } else {
         return RenderType.entityCutoutNoCullZOffset(resourcelocation);
      }
   }
}
