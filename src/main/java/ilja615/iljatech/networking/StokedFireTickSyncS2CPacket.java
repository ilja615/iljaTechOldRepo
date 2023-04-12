package ilja615.iljatech.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StokedFireTickSyncS2CPacket{
    private final int stokedfiretick;

    public StokedFireTickSyncS2CPacket(int sft) {
        this.stokedfiretick = sft;
    }

    public StokedFireTickSyncS2CPacket(FriendlyByteBuf buf) {
        this.stokedfiretick = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(stokedfiretick);
    }

    public boolean handle(StokedFireTickSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT!
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModClientPacketHandler.handlePacket(msg, ctx));
        });
        return true;
    }
}