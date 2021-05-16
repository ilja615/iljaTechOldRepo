package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPartFace {
   public final Direction cullForDirection;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV uv;

   public BlockPartFace(@Nullable Direction p_i46230_1_, int p_i46230_2_, String p_i46230_3_, BlockFaceUV p_i46230_4_) {
      this.cullForDirection = p_i46230_1_;
      this.tintIndex = p_i46230_2_;
      this.texture = p_i46230_3_;
      this.uv = p_i46230_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockPartFace> {
      public BlockPartFace deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Direction direction = this.getCullFacing(jsonobject);
         int i = this.getTintIndex(jsonobject);
         String s = this.getTexture(jsonobject);
         BlockFaceUV blockfaceuv = p_deserialize_3_.deserialize(jsonobject, BlockFaceUV.class);
         return new BlockPartFace(direction, i, s, blockfaceuv);
      }

      protected int getTintIndex(JsonObject p_178337_1_) {
         return JSONUtils.getAsInt(p_178337_1_, "tintindex", -1);
      }

      private String getTexture(JsonObject p_178340_1_) {
         return JSONUtils.getAsString(p_178340_1_, "texture");
      }

      @Nullable
      private Direction getCullFacing(JsonObject p_178339_1_) {
         String s = JSONUtils.getAsString(p_178339_1_, "cullface", "");
         return Direction.byName(s);
      }
   }
}
