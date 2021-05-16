package com.mojang.realmsclient.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(Logger p_i51787_1_) {
      this.logger = p_i51787_1_;
   }

   public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(p_uncaughtException_2_);
   }
}
