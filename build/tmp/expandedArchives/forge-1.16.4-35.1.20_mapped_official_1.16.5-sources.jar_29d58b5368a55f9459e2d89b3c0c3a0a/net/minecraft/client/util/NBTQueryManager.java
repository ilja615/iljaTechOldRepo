package net.minecraft.client.util;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NBTQueryManager {
   private final ClientPlayNetHandler connection;
   private int transactionId = -1;
   @Nullable
   private Consumer<CompoundNBT> callback;

   public NBTQueryManager(ClientPlayNetHandler p_i49773_1_) {
      this.connection = p_i49773_1_;
   }

   public boolean handleResponse(int p_211548_1_, @Nullable CompoundNBT p_211548_2_) {
      if (this.transactionId == p_211548_1_ && this.callback != null) {
         this.callback.accept(p_211548_2_);
         this.callback = null;
         return true;
      } else {
         return false;
      }
   }

   private int startTransaction(Consumer<CompoundNBT> p_211546_1_) {
      this.callback = p_211546_1_;
      return ++this.transactionId;
   }

   public void queryEntityTag(int p_211549_1_, Consumer<CompoundNBT> p_211549_2_) {
      int i = this.startTransaction(p_211549_2_);
      this.connection.send(new CQueryEntityNBTPacket(i, p_211549_1_));
   }

   public void queryBlockEntityTag(BlockPos p_211547_1_, Consumer<CompoundNBT> p_211547_2_) {
      int i = this.startTransaction(p_211547_2_);
      this.connection.send(new CQueryTileEntityNBTPacket(i, p_211547_1_));
   }
}
