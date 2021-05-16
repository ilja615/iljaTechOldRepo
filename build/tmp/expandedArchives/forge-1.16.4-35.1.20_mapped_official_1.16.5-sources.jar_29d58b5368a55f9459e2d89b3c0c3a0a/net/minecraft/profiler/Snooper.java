package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Snooper {
   private final Map<String, Object> fixedData = Maps.newHashMap();
   private final Map<String, Object> dynamicData = Maps.newHashMap();
   private final String token = UUID.randomUUID().toString();
   private final URL url;
   private final ISnooperInfo populator;
   private final Timer timer = new Timer("Snooper Timer", true);
   private final Object lock = new Object();
   private final long startupTime;
   private boolean started;

   public Snooper(String p_i1563_1_, ISnooperInfo p_i1563_2_, long p_i1563_3_) {
      try {
         this.url = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
      } catch (MalformedURLException malformedurlexception) {
         throw new IllegalArgumentException();
      }

      this.populator = p_i1563_2_;
      this.startupTime = p_i1563_3_;
   }

   public void start() {
      if (!this.started) {
      }

   }

   public void prepare() {
      this.setFixedData("memory_total", Runtime.getRuntime().totalMemory());
      this.setFixedData("memory_max", Runtime.getRuntime().maxMemory());
      this.setFixedData("memory_free", Runtime.getRuntime().freeMemory());
      this.setFixedData("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.populator.populateSnooper(this);
   }

   public void setDynamicData(String p_152768_1_, Object p_152768_2_) {
      synchronized(this.lock) {
         this.dynamicData.put(p_152768_1_, p_152768_2_);
      }
   }

   public void setFixedData(String p_152767_1_, Object p_152767_2_) {
      synchronized(this.lock) {
         this.fixedData.put(p_152767_1_, p_152767_2_);
      }
   }

   public boolean isStarted() {
      return this.started;
   }

   public void interrupt() {
      this.timer.cancel();
   }

   @OnlyIn(Dist.CLIENT)
   public String getToken() {
      return this.token;
   }

   public long getStartupTime() {
      return this.startupTime;
   }
}
