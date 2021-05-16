package net.minecraft.realms;

import com.google.gson.Gson;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PersistenceSerializer {
   private final Gson gson = new Gson();

   public String toJson(IPersistentSerializable p_237694_1_) {
      return this.gson.toJson(p_237694_1_);
   }

   public <T extends IPersistentSerializable> T fromJson(String p_237695_1_, Class<T> p_237695_2_) {
      return this.gson.fromJson(p_237695_1_, p_237695_2_);
   }
}
