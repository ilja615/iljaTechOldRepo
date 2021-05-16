package net.minecraft.command;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class TimedFunction implements ITimerCallback<MinecraftServer> {
   private final ResourceLocation functionId;

   public TimedFunction(ResourceLocation p_i51190_1_) {
      this.functionId = p_i51190_1_;
   }

   public void handle(MinecraftServer p_212869_1_, TimerCallbackManager<MinecraftServer> p_212869_2_, long p_212869_3_) {
      FunctionManager functionmanager = p_212869_1_.getFunctions();
      functionmanager.get(this.functionId).ifPresent((p_216316_1_) -> {
         functionmanager.execute(p_216316_1_, functionmanager.getGameLoopSender());
      });
   }

   public static class Serializer extends ITimerCallback.Serializer<MinecraftServer, TimedFunction> {
      public Serializer() {
         super(new ResourceLocation("function"), TimedFunction.class);
      }

      public void serialize(CompoundNBT p_212847_1_, TimedFunction p_212847_2_) {
         p_212847_1_.putString("Name", p_212847_2_.functionId.toString());
      }

      public TimedFunction deserialize(CompoundNBT p_212846_1_) {
         ResourceLocation resourcelocation = new ResourceLocation(p_212846_1_.getString("Name"));
         return new TimedFunction(resourcelocation);
      }
   }
}
