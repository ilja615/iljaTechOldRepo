package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerLookPacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private int entity;
   private EntityAnchorArgument.Type fromAnchor;
   private EntityAnchorArgument.Type toAnchor;
   private boolean atEntity;

   public SPlayerLookPacket() {
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48589_1_, double p_i48589_2_, double p_i48589_4_, double p_i48589_6_) {
      this.fromAnchor = p_i48589_1_;
      this.x = p_i48589_2_;
      this.y = p_i48589_4_;
      this.z = p_i48589_6_;
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48590_1_, Entity p_i48590_2_, EntityAnchorArgument.Type p_i48590_3_) {
      this.fromAnchor = p_i48590_1_;
      this.entity = p_i48590_2_.getId();
      this.toAnchor = p_i48590_3_;
      Vector3d vector3d = p_i48590_3_.apply(p_i48590_2_);
      this.x = vector3d.x;
      this.y = vector3d.y;
      this.z = vector3d.z;
      this.atEntity = true;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.fromAnchor = p_148837_1_.readEnum(EntityAnchorArgument.Type.class);
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      if (p_148837_1_.readBoolean()) {
         this.atEntity = true;
         this.entity = p_148837_1_.readVarInt();
         this.toAnchor = p_148837_1_.readEnum(EntityAnchorArgument.Type.class);
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.fromAnchor);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeBoolean(this.atEntity);
      if (this.atEntity) {
         p_148840_1_.writeVarInt(this.entity);
         p_148840_1_.writeEnum(this.toAnchor);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLookAt(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityAnchorArgument.Type getFromAnchor() {
      return this.fromAnchor;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vector3d getPosition(World p_200531_1_) {
      if (this.atEntity) {
         Entity entity = p_200531_1_.getEntity(this.entity);
         return entity == null ? new Vector3d(this.x, this.y, this.z) : this.toAnchor.apply(entity);
      } else {
         return new Vector3d(this.x, this.y, this.z);
      }
   }
}
