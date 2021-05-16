package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMerchantOffersPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private MerchantOffers offers;
   private int villagerLevel;
   private int villagerXp;
   private boolean showProgress;
   private boolean canRestock;

   public SMerchantOffersPacket() {
   }

   public SMerchantOffersPacket(int p_i51539_1_, MerchantOffers p_i51539_2_, int p_i51539_3_, int p_i51539_4_, boolean p_i51539_5_, boolean p_i51539_6_) {
      this.containerId = p_i51539_1_;
      this.offers = p_i51539_2_;
      this.villagerLevel = p_i51539_3_;
      this.villagerXp = p_i51539_4_;
      this.showProgress = p_i51539_5_;
      this.canRestock = p_i51539_6_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readVarInt();
      this.offers = MerchantOffers.createFromStream(p_148837_1_);
      this.villagerLevel = p_148837_1_.readVarInt();
      this.villagerXp = p_148837_1_.readVarInt();
      this.showProgress = p_148837_1_.readBoolean();
      this.canRestock = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.containerId);
      this.offers.writeToStream(p_148840_1_);
      p_148840_1_.writeVarInt(this.villagerLevel);
      p_148840_1_.writeVarInt(this.villagerXp);
      p_148840_1_.writeBoolean(this.showProgress);
      p_148840_1_.writeBoolean(this.canRestock);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMerchantOffers(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public MerchantOffers getOffers() {
      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVillagerLevel() {
      return this.villagerLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVillagerXp() {
      return this.villagerXp;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showProgress() {
      return this.showProgress;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRestock() {
      return this.canRestock;
   }
}
