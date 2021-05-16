package net.minecraft.network;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketThreadUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   public static <T extends INetHandler> void ensureRunningOnSameThread(IPacket<T> p_218796_0_, T p_218796_1_, ServerWorld p_218796_2_) throws ThreadQuickExitException {
      ensureRunningOnSameThread(p_218796_0_, p_218796_1_, p_218796_2_.getServer());
   }

   public static <T extends INetHandler> void ensureRunningOnSameThread(IPacket<T> p_218797_0_, T p_218797_1_, ThreadTaskExecutor<?> p_218797_2_) throws ThreadQuickExitException {
      if (!p_218797_2_.isSameThread()) {
         p_218797_2_.execute(() -> {
            if (p_218797_1_.getConnection().isConnected()) {
               p_218797_0_.handle(p_218797_1_);
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: " + p_218797_0_);
            }

         });
         throw ThreadQuickExitException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }
}
