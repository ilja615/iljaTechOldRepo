package net.minecraft.item;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class Foods {
   public static final Food APPLE = (new Food.Builder()).nutrition(4).saturationMod(0.3F).build();
   public static final Food BAKED_POTATO = (new Food.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final Food BEEF = (new Food.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final Food BEETROOT = (new Food.Builder()).nutrition(1).saturationMod(0.6F).build();
   public static final Food BEETROOT_SOUP = stew(6);
   public static final Food BREAD = (new Food.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final Food CARROT = (new Food.Builder()).nutrition(3).saturationMod(0.6F).build();
   public static final Food CHICKEN = (new Food.Builder()).nutrition(2).saturationMod(0.3F).effect(new EffectInstance(Effects.HUNGER, 600, 0), 0.3F).meat().build();
   public static final Food CHORUS_FRUIT = (new Food.Builder()).nutrition(4).saturationMod(0.3F).alwaysEat().build();
   public static final Food COD = (new Food.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final Food COOKED_BEEF = (new Food.Builder()).nutrition(8).saturationMod(0.8F).meat().build();
   public static final Food COOKED_CHICKEN = (new Food.Builder()).nutrition(6).saturationMod(0.6F).meat().build();
   public static final Food COOKED_COD = (new Food.Builder()).nutrition(5).saturationMod(0.6F).build();
   public static final Food COOKED_MUTTON = (new Food.Builder()).nutrition(6).saturationMod(0.8F).meat().build();
   public static final Food COOKED_PORKCHOP = (new Food.Builder()).nutrition(8).saturationMod(0.8F).meat().build();
   public static final Food COOKED_RABBIT = (new Food.Builder()).nutrition(5).saturationMod(0.6F).meat().build();
   public static final Food COOKED_SALMON = (new Food.Builder()).nutrition(6).saturationMod(0.8F).build();
   public static final Food COOKIE = (new Food.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final Food DRIED_KELP = (new Food.Builder()).nutrition(1).saturationMod(0.3F).fast().build();
   public static final Food ENCHANTED_GOLDEN_APPLE = (new Food.Builder()).nutrition(4).saturationMod(1.2F).effect(new EffectInstance(Effects.REGENERATION, 400, 1), 1.0F).effect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.ABSORPTION, 2400, 3), 1.0F).alwaysEat().build();
   public static final Food GOLDEN_APPLE = (new Food.Builder()).nutrition(4).saturationMod(1.2F).effect(new EffectInstance(Effects.REGENERATION, 100, 1), 1.0F).effect(new EffectInstance(Effects.ABSORPTION, 2400, 0), 1.0F).alwaysEat().build();
   public static final Food GOLDEN_CARROT = (new Food.Builder()).nutrition(6).saturationMod(1.2F).build();
   public static final Food HONEY_BOTTLE = (new Food.Builder()).nutrition(6).saturationMod(0.1F).build();
   public static final Food MELON_SLICE = (new Food.Builder()).nutrition(2).saturationMod(0.3F).build();
   public static final Food MUSHROOM_STEW = stew(6);
   public static final Food MUTTON = (new Food.Builder()).nutrition(2).saturationMod(0.3F).meat().build();
   public static final Food POISONOUS_POTATO = (new Food.Builder()).nutrition(2).saturationMod(0.3F).effect(new EffectInstance(Effects.POISON, 100, 0), 0.6F).build();
   public static final Food PORKCHOP = (new Food.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final Food POTATO = (new Food.Builder()).nutrition(1).saturationMod(0.3F).build();
   public static final Food PUFFERFISH = (new Food.Builder()).nutrition(1).saturationMod(0.1F).effect(new EffectInstance(Effects.POISON, 1200, 3), 1.0F).effect(new EffectInstance(Effects.HUNGER, 300, 2), 1.0F).effect(new EffectInstance(Effects.CONFUSION, 300, 0), 1.0F).build();
   public static final Food PUMPKIN_PIE = (new Food.Builder()).nutrition(8).saturationMod(0.3F).build();
   public static final Food RABBIT = (new Food.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
   public static final Food RABBIT_STEW = stew(10);
   public static final Food ROTTEN_FLESH = (new Food.Builder()).nutrition(4).saturationMod(0.1F).effect(new EffectInstance(Effects.HUNGER, 600, 0), 0.8F).meat().build();
   public static final Food SALMON = (new Food.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final Food SPIDER_EYE = (new Food.Builder()).nutrition(2).saturationMod(0.8F).effect(new EffectInstance(Effects.POISON, 100, 0), 1.0F).build();
   public static final Food SUSPICIOUS_STEW = stew(6);
   public static final Food SWEET_BERRIES = (new Food.Builder()).nutrition(2).saturationMod(0.1F).build();
   public static final Food TROPICAL_FISH = (new Food.Builder()).nutrition(1).saturationMod(0.1F).build();

   private static Food stew(int p_221412_0_) {
      return (new Food.Builder()).nutrition(p_221412_0_).saturationMod(0.6F).build();
   }
}
