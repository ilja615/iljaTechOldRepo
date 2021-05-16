package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0100 extends Schema {
   public V0100(int p_i49611_1_, Schema p_i49611_2_) {
      super(p_i49611_1_, p_i49611_2_);
   }

   protected static TypeTemplate equipment(Schema p_206605_0_) {
      return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(p_206605_0_)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(p_206605_0_)));
   }

   protected static void registerMob(Schema p_206611_0_, Map<String, Supplier<TypeTemplate>> p_206611_1_, String p_206611_2_) {
      p_206611_0_.register(p_206611_1_, p_206611_2_, () -> {
         return equipment(p_206611_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerMob(p_registerEntities_1_, map, "ArmorStand");
      registerMob(p_registerEntities_1_, map, "Creeper");
      registerMob(p_registerEntities_1_, map, "Skeleton");
      registerMob(p_registerEntities_1_, map, "Spider");
      registerMob(p_registerEntities_1_, map, "Giant");
      registerMob(p_registerEntities_1_, map, "Zombie");
      registerMob(p_registerEntities_1_, map, "Slime");
      registerMob(p_registerEntities_1_, map, "Ghast");
      registerMob(p_registerEntities_1_, map, "PigZombie");
      p_registerEntities_1_.register(map, "Enderman", (p_206609_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerMob(p_registerEntities_1_, map, "CaveSpider");
      registerMob(p_registerEntities_1_, map, "Silverfish");
      registerMob(p_registerEntities_1_, map, "Blaze");
      registerMob(p_registerEntities_1_, map, "LavaSlime");
      registerMob(p_registerEntities_1_, map, "EnderDragon");
      registerMob(p_registerEntities_1_, map, "WitherBoss");
      registerMob(p_registerEntities_1_, map, "Bat");
      registerMob(p_registerEntities_1_, map, "Witch");
      registerMob(p_registerEntities_1_, map, "Endermite");
      registerMob(p_registerEntities_1_, map, "Guardian");
      registerMob(p_registerEntities_1_, map, "Pig");
      registerMob(p_registerEntities_1_, map, "Sheep");
      registerMob(p_registerEntities_1_, map, "Cow");
      registerMob(p_registerEntities_1_, map, "Chicken");
      registerMob(p_registerEntities_1_, map, "Squid");
      registerMob(p_registerEntities_1_, map, "Wolf");
      registerMob(p_registerEntities_1_, map, "MushroomCow");
      registerMob(p_registerEntities_1_, map, "SnowMan");
      registerMob(p_registerEntities_1_, map, "Ozelot");
      registerMob(p_registerEntities_1_, map, "VillagerGolem");
      p_registerEntities_1_.register(map, "EntityHorse", (p_206612_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerMob(p_registerEntities_1_, map, "Rabbit");
      p_registerEntities_1_.register(map, "Villager", (p_206608_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), equipment(p_registerEntities_1_));
      });
      registerMob(p_registerEntities_1_, map, "Shulker");
      p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
      p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
   }
}
