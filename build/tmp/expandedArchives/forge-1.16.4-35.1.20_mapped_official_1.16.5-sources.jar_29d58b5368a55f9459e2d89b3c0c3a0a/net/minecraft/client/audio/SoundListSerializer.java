package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
      JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "entry");
      boolean flag = JSONUtils.getAsBoolean(jsonobject, "replace", false);
      String s = JSONUtils.getAsString(jsonobject, "subtitle", (String)null);
      List<Sound> list = this.getSounds(jsonobject);
      return new SoundList(list, flag, s);
   }

   private List<Sound> getSounds(JsonObject p_188733_1_) {
      List<Sound> list = Lists.newArrayList();
      if (p_188733_1_.has("sounds")) {
         JsonArray jsonarray = JSONUtils.getAsJsonArray(p_188733_1_, "sounds");

         for(int i = 0; i < jsonarray.size(); ++i) {
            JsonElement jsonelement = jsonarray.get(i);
            if (JSONUtils.isStringValue(jsonelement)) {
               String s = JSONUtils.convertToString(jsonelement, "sound");
               list.add(new Sound(s, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16));
            } else {
               list.add(this.getSound(JSONUtils.convertToJsonObject(jsonelement, "sound")));
            }
         }
      }

      return list;
   }

   private Sound getSound(JsonObject p_188734_1_) {
      String s = JSONUtils.getAsString(p_188734_1_, "name");
      Sound.Type sound$type = this.getType(p_188734_1_, Sound.Type.FILE);
      float f = JSONUtils.getAsFloat(p_188734_1_, "volume", 1.0F);
      Validate.isTrue(f > 0.0F, "Invalid volume");
      float f1 = JSONUtils.getAsFloat(p_188734_1_, "pitch", 1.0F);
      Validate.isTrue(f1 > 0.0F, "Invalid pitch");
      int i = JSONUtils.getAsInt(p_188734_1_, "weight", 1);
      Validate.isTrue(i > 0, "Invalid weight");
      boolean flag = JSONUtils.getAsBoolean(p_188734_1_, "preload", false);
      boolean flag1 = JSONUtils.getAsBoolean(p_188734_1_, "stream", false);
      int j = JSONUtils.getAsInt(p_188734_1_, "attenuation_distance", 16);
      return new Sound(s, f, f1, i, sound$type, flag1, flag, j);
   }

   private Sound.Type getType(JsonObject p_188732_1_, Sound.Type p_188732_2_) {
      Sound.Type sound$type = p_188732_2_;
      if (p_188732_1_.has("type")) {
         sound$type = Sound.Type.getByName(JSONUtils.getAsString(p_188732_1_, "type"));
         Validate.notNull(sound$type, "Invalid type");
      }

      return sound$type;
   }
}
