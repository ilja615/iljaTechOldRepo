package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CSeenAdvancementsPacket implements IPacket<IServerPlayNetHandler> {
   private CSeenAdvancementsPacket.Action action;
   private ResourceLocation tab;

   public CSeenAdvancementsPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CSeenAdvancementsPacket(CSeenAdvancementsPacket.Action p_i47595_1_, @Nullable ResourceLocation p_i47595_2_) {
      this.action = p_i47595_1_;
      this.tab = p_i47595_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static CSeenAdvancementsPacket openedTab(Advancement p_194163_0_) {
      return new CSeenAdvancementsPacket(CSeenAdvancementsPacket.Action.OPENED_TAB, p_194163_0_.getId());
   }

   @OnlyIn(Dist.CLIENT)
   public static CSeenAdvancementsPacket closedScreen() {
      return new CSeenAdvancementsPacket(CSeenAdvancementsPacket.Action.CLOSED_SCREEN, (ResourceLocation)null);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnum(CSeenAdvancementsPacket.Action.class);
      if (this.action == CSeenAdvancementsPacket.Action.OPENED_TAB) {
         this.tab = p_148837_1_.readResourceLocation();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.action);
      if (this.action == CSeenAdvancementsPacket.Action.OPENED_TAB) {
         p_148840_1_.writeResourceLocation(this.tab);
      }

   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSeenAdvancements(this);
   }

   public CSeenAdvancementsPacket.Action getAction() {
      return this.action;
   }

   public ResourceLocation getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;
   }
}
