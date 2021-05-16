package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {
   private static final Ordering<NetworkPlayerInfo> PROFILE_ORDER = Ordering.from((p_210243_0_, p_210243_1_) -> {
      return ComparisonChain.start().compare(p_210243_0_.getProfile().getId(), p_210243_1_.getProfile().getId()).result();
   });
   private static final ITextComponent TELEPORT_TEXT = new TranslationTextComponent("spectatorMenu.teleport");
   private static final ITextComponent TELEPORT_PROMPT = new TranslationTextComponent("spectatorMenu.teleport.prompt");
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToPlayer() {
      this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getOnlinePlayers()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> p_i45493_1_) {
      for(NetworkPlayerInfo networkplayerinfo : PROFILE_ORDER.sortedCopy(p_i45493_1_)) {
         if (networkplayerinfo.getGameMode() != GameType.SPECTATOR) {
            this.items.add(new PlayerMenuObject(networkplayerinfo.getProfile()));
         }
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      p_178661_1_.selectCategory(this);
   }

   public ITextComponent getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_) {
      Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
      AbstractGui.blit(p_230485_1_, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}
