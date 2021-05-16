package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityPropertiesPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private final List<SEntityPropertiesPacket.Snapshot> attributes = Lists.newArrayList();

   public SEntityPropertiesPacket() {
   }

   public SEntityPropertiesPacket(int p_i46892_1_, Collection<ModifiableAttributeInstance> p_i46892_2_) {
      this.entityId = p_i46892_1_;

      for(ModifiableAttributeInstance modifiableattributeinstance : p_i46892_2_) {
         this.attributes.add(new SEntityPropertiesPacket.Snapshot(modifiableattributeinstance.getAttribute(), modifiableattributeinstance.getBaseValue(), modifiableattributeinstance.getModifiers()));
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      int i = p_148837_1_.readInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = p_148837_1_.readResourceLocation();
         Attribute attribute = Registry.ATTRIBUTE.get(resourcelocation);
         double d0 = p_148837_1_.readDouble();
         List<AttributeModifier> list = Lists.newArrayList();
         int k = p_148837_1_.readVarInt();

         for(int l = 0; l < k; ++l) {
            UUID uuid = p_148837_1_.readUUID();
            list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", p_148837_1_.readDouble(), AttributeModifier.Operation.fromValue(p_148837_1_.readByte())));
         }

         this.attributes.add(new SEntityPropertiesPacket.Snapshot(attribute, d0, list));
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeInt(this.attributes.size());

      for(SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot : this.attributes) {
         p_148840_1_.writeResourceLocation(Registry.ATTRIBUTE.getKey(sentitypropertiespacket$snapshot.getAttribute()));
         p_148840_1_.writeDouble(sentitypropertiespacket$snapshot.getBase());
         p_148840_1_.writeVarInt(sentitypropertiespacket$snapshot.getModifiers().size());

         for(AttributeModifier attributemodifier : sentitypropertiespacket$snapshot.getModifiers()) {
            p_148840_1_.writeUUID(attributemodifier.getId());
            p_148840_1_.writeDouble(attributemodifier.getAmount());
            p_148840_1_.writeByte(attributemodifier.getOperation().toValue());
         }
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateAttributes(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   public List<SEntityPropertiesPacket.Snapshot> getValues() {
      return this.attributes;
   }

   public class Snapshot {
      private final Attribute attribute;
      private final double base;
      private final Collection<AttributeModifier> modifiers;

      public Snapshot(Attribute p_i232582_2_, double p_i232582_3_, Collection<AttributeModifier> p_i232582_5_) {
         this.attribute = p_i232582_2_;
         this.base = p_i232582_3_;
         this.modifiers = p_i232582_5_;
      }

      public Attribute getAttribute() {
         return this.attribute;
      }

      public double getBase() {
         return this.base;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}
