package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class ZombieSplit extends EntityRenameHelper {
   public ZombieSplit(Schema p_i49648_1_, boolean p_i49648_2_) {
      super("EntityZombieSplitFix", p_i49648_1_, p_i49648_2_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_209758_1_, Dynamic<?> p_209758_2_) {
      if (Objects.equals("Zombie", p_209758_1_)) {
         String s = "Zombie";
         int i = p_209758_2_.get("ZombieType").asInt(0);
         switch(i) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            s = "ZombieVillager";
            p_209758_2_ = p_209758_2_.set("Profession", p_209758_2_.createInt(i - 1));
            break;
         case 6:
            s = "Husk";
         }

         p_209758_2_ = p_209758_2_.remove("ZombieType");
         return Pair.of(s, p_209758_2_);
      } else {
         return Pair.of(p_209758_1_, p_209758_2_);
      }
   }
}
