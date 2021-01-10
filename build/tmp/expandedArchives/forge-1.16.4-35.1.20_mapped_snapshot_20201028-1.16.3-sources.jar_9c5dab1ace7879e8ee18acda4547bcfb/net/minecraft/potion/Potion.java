package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Potion extends net.minecraftforge.registries.ForgeRegistryEntry<Potion> {
   private final net.minecraftforge.common.util.ReverseTagWrapper<Potion> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, () -> net.minecraft.tags.TagCollectionManager.getManager().getCustomTypeCollection(net.minecraftforge.registries.ForgeRegistries.POTION_TYPES));
   private final String baseName;
   private final ImmutableList<EffectInstance> effects;

   public static Potion getPotionTypeForName(String name) {
      return Registry.POTION.getOrDefault(ResourceLocation.tryCreate(name));
   }

   public Potion(EffectInstance... effectsIn) {
      this((String)null, effectsIn);
   }

   public Potion(@Nullable String baseNameIn, EffectInstance... effectsIn) {
      this.baseName = baseNameIn;
      this.effects = ImmutableList.copyOf(effectsIn);
   }

   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public boolean isIn(net.minecraft.tags.ITag<Potion> tag) {
      return tag.contains(this);
   }

   /**
    * Gets the name of this PotionType with a prefix (such as "Splash" or "Lingering") prepended
    */
   public String getNamePrefixed(String prefix) {
      return prefix + (this.baseName == null ? Registry.POTION.getKey(this).getPath() : this.baseName);
   }

   public List<EffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffect() {
      if (!this.effects.isEmpty()) {
         for(EffectInstance effectinstance : this.effects) {
            if (effectinstance.getPotion().isInstant()) {
               return true;
            }
         }
      }

      return false;
   }
}
