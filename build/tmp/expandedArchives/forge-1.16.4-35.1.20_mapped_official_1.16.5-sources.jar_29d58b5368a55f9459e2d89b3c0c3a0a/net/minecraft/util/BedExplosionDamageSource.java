package net.minecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class BedExplosionDamageSource extends DamageSource {
   protected BedExplosionDamageSource() {
      super("badRespawnPoint");
      this.setScalesWithDifficulty();
      this.setExplosion();
   }

   public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("death.attack.badRespawnPoint.link")).withStyle((p_233545_0_) -> {
         return p_233545_0_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")));
      });
      return new TranslationTextComponent("death.attack.badRespawnPoint.message", p_151519_1_.getDisplayName(), itextcomponent);
   }
}
