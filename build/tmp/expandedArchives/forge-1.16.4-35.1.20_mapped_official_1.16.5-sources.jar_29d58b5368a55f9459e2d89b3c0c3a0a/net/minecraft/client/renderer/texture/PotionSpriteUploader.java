package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PotionSpriteUploader extends SpriteUploader {
   public PotionSpriteUploader(TextureManager p_i50908_1_) {
      super(p_i50908_1_, new ResourceLocation("textures/atlas/mob_effects.png"), "mob_effect");
   }

   protected Stream<ResourceLocation> getResourcesToLoad() {
      return Registry.MOB_EFFECT.keySet().stream();
   }

   public TextureAtlasSprite get(Effect p_215288_1_) {
      return this.getSprite(Registry.MOB_EFFECT.getKey(p_215288_1_));
   }
}
