package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateJigsawBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private ResourceLocation name;
   private ResourceLocation target;
   private ResourceLocation pool;
   private String finalState;
   private JigsawTileEntity.OrientationType joint;

   public CUpdateJigsawBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateJigsawBlockPacket(BlockPos p_i232584_1_, ResourceLocation p_i232584_2_, ResourceLocation p_i232584_3_, ResourceLocation p_i232584_4_, String p_i232584_5_, JigsawTileEntity.OrientationType p_i232584_6_) {
      this.pos = p_i232584_1_;
      this.name = p_i232584_2_;
      this.target = p_i232584_3_;
      this.pool = p_i232584_4_;
      this.finalState = p_i232584_5_;
      this.joint = p_i232584_6_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.name = p_148837_1_.readResourceLocation();
      this.target = p_148837_1_.readResourceLocation();
      this.pool = p_148837_1_.readResourceLocation();
      this.finalState = p_148837_1_.readUtf(32767);
      this.joint = JigsawTileEntity.OrientationType.byName(p_148837_1_.readUtf(32767)).orElse(JigsawTileEntity.OrientationType.ALIGNED);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeResourceLocation(this.name);
      p_148840_1_.writeResourceLocation(this.target);
      p_148840_1_.writeResourceLocation(this.pool);
      p_148840_1_.writeUtf(this.finalState);
      p_148840_1_.writeUtf(this.joint.getSerializedName());
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawTileEntity.OrientationType getJoint() {
      return this.joint;
   }
}
