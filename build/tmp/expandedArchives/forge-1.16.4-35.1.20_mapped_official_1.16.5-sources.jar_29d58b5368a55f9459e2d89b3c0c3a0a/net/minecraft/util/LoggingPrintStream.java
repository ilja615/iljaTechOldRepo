package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final String name;

   public LoggingPrintStream(String p_i45927_1_, OutputStream p_i45927_2_) {
      super(p_i45927_2_);
      this.name = p_i45927_1_;
   }

   public void println(@Nullable String p_println_1_) {
      this.logLine(p_println_1_);
   }

   public void println(Object p_println_1_) {
      this.logLine(String.valueOf(p_println_1_));
   }

   protected void logLine(@Nullable String p_179882_1_) {
      LOGGER.info("[{}]: {}", this.name, p_179882_1_);
   }
}
