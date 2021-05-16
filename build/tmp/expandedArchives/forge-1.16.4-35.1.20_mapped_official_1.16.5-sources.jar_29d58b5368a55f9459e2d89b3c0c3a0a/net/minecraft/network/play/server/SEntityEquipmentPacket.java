package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityEquipmentPacket implements IPacket<IClientPlayNetHandler> {
   private int entity;
   private final List<Pair<EquipmentSlotType, ItemStack>> slots;

   public SEntityEquipmentPacket() {
      this.slots = Lists.newArrayList();
   }

   public SEntityEquipmentPacket(int p_i241270_1_, List<Pair<EquipmentSlotType, ItemStack>> p_i241270_2_) {
      this.entity = p_i241270_1_;
      this.slots = p_i241270_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entity = p_148837_1_.readVarInt();
      EquipmentSlotType[] aequipmentslottype = EquipmentSlotType.values();

      int i;
      do {
         i = p_148837_1_.readByte();
         EquipmentSlotType equipmentslottype = aequipmentslottype[i & 127];
         ItemStack itemstack = p_148837_1_.readItem();
         this.slots.add(Pair.of(equipmentslottype, itemstack));
      } while((i & -128) != 0);

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entity);
      int i = this.slots.size();

      for(int j = 0; j < i; ++j) {
         Pair<EquipmentSlotType, ItemStack> pair = this.slots.get(j);
         EquipmentSlotType equipmentslottype = pair.getFirst();
         boolean flag = j != i - 1;
         int k = equipmentslottype.ordinal();
         p_148840_1_.writeByte(flag ? k | -128 : k);
         p_148840_1_.writeItem(pair.getSecond());
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetEquipment(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntity() {
      return this.entity;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Pair<EquipmentSlotType, ItemStack>> getSlots() {
      return this.slots;
   }
}
