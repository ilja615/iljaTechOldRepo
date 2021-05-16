package net.minecraft.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackSerializers<C> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TimerCallbackSerializers<MinecraftServer> SERVER_CALLBACKS = (new TimerCallbackSerializers<MinecraftServer>()).register(new TimedFunction.Serializer()).register(new TimedFunctionTag.Serializer());
   private final Map<ResourceLocation, ITimerCallback.Serializer<C, ?>> idToSerializer = Maps.newHashMap();
   private final Map<Class<?>, ITimerCallback.Serializer<C, ?>> classToSerializer = Maps.newHashMap();

   public TimerCallbackSerializers<C> register(ITimerCallback.Serializer<C, ?> p_216340_1_) {
      this.idToSerializer.put(p_216340_1_.getId(), p_216340_1_);
      this.classToSerializer.put(p_216340_1_.getCls(), p_216340_1_);
      return this;
   }

   private <T extends ITimerCallback<C>> ITimerCallback.Serializer<C, T> getSerializer(Class<?> p_216338_1_) {
      return (ITimerCallback.Serializer<C, T>)this.classToSerializer.get(p_216338_1_);
   }

   public <T extends ITimerCallback<C>> CompoundNBT serialize(T p_216339_1_) {
      ITimerCallback.Serializer<C, T> serializer = this.getSerializer(p_216339_1_.getClass());
      CompoundNBT compoundnbt = new CompoundNBT();
      serializer.serialize(compoundnbt, p_216339_1_);
      compoundnbt.putString("Type", serializer.getId().toString());
      return compoundnbt;
   }

   @Nullable
   public ITimerCallback<C> deserialize(CompoundNBT p_216341_1_) {
      ResourceLocation resourcelocation = ResourceLocation.tryParse(p_216341_1_.getString("Type"));
      ITimerCallback.Serializer<C, ?> serializer = this.idToSerializer.get(resourcelocation);
      if (serializer == null) {
         LOGGER.error("Failed to deserialize timer callback: " + p_216341_1_);
         return null;
      } else {
         try {
            return serializer.deserialize(p_216341_1_);
         } catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: " + p_216341_1_, (Throwable)exception);
            return null;
         }
      }
   }
}
