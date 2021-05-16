package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSectionSerializer implements IMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSection fromJson(JsonObject p_195812_1_) {
      Set<Language> set = Sets.newHashSet();

      for(Entry<String, JsonElement> entry : p_195812_1_.entrySet()) {
         String s = entry.getKey();
         if (s.length() > 16) {
            throw new JsonParseException("Invalid language->'" + s + "': language code must not be more than " + 16 + " characters long");
         }

         JsonObject jsonobject = JSONUtils.convertToJsonObject(entry.getValue(), "language");
         String s1 = JSONUtils.getAsString(jsonobject, "region");
         String s2 = JSONUtils.getAsString(jsonobject, "name");
         boolean flag = JSONUtils.getAsBoolean(jsonobject, "bidirectional", false);
         if (s1.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->region: empty value");
         }

         if (s2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->name: empty value");
         }

         if (!set.add(new Language(s, s1, s2, flag))) {
            throw new JsonParseException("Duplicate language->'" + s + "' defined");
         }
      }

      return new LanguageMetadataSection(set);
   }

   public String getMetadataSectionName() {
      return "language";
   }
}
