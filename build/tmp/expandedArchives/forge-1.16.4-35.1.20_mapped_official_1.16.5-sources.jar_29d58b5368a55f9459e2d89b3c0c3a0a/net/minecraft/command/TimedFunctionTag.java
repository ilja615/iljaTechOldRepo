package net.minecraft.command;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class TimedFunctionTag implements ITimerCallback<MinecraftServer> {
   private final ResourceLocation tagId;

   public TimedFunctionTag(ResourceLocation p_i51189_1_) {
      this.tagId = p_i51189_1_;
   }

   public void handle(MinecraftServer p_212869_1_, TimerCallbackManager<MinecraftServer> p_212869_2_, long p_212869_3_) {
      FunctionManager functionmanager = p_212869_1_.getFunctions();
      ITag<FunctionObject> itag = functionmanager.getTag(this.tagId);

      for(FunctionObject functionobject : itag.getValues()) {
         functionmanager.execute(functionobject, functionmanager.getGameLoopSender());
      }

   }

   public static class Serializer extends ITimerCallback.Serializer<MinecraftServer, TimedFunctionTag> {
      public Serializer() {
         super(new ResourceLocation("function_tag"), TimedFunctionTag.class);
      }

      public void serialize(CompoundNBT p_212847_1_, TimedFunctionTag p_212847_2_) {
         p_212847_1_.putString("Name", p_212847_2_.tagId.toString());
      }

      public TimedFunctionTag deserialize(CompoundNBT p_212846_1_) {
         ResourceLocation resourcelocation = new ResourceLocation(p_212846_1_.getString("Name"));
         return new TimedFunctionTag(resourcelocation);
      }
   }
}
