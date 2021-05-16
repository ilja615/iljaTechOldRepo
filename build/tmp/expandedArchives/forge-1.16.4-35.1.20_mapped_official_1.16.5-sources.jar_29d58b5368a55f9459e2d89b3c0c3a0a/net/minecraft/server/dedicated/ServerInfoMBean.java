package net.minecraft.server.dedicated;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerInfoMBean implements DynamicMBean {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final MBeanInfo mBeanInfo;
   private final Map<String, ServerInfoMBean.Attribute> attributeDescriptionByName = Stream.of(new ServerInfoMBean.Attribute("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class), new ServerInfoMBean.Attribute("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap((p_233492_0_) -> {
      return p_233492_0_.name;
   }, Function.identity()));

   private ServerInfoMBean(MinecraftServer p_i231479_1_) {
      this.server = p_i231479_1_;
      MBeanAttributeInfo[] ambeanattributeinfo = this.attributeDescriptionByName.values().stream().map((p_233489_0_) -> {
         return p_233489_0_.asMBeanAttributeInfo();
      }).toArray((p_233487_0_) -> {
         return new MBeanAttributeInfo[p_233487_0_];
      });
      this.mBeanInfo = new MBeanInfo(ServerInfoMBean.class.getSimpleName(), "metrics for dedicated server", ambeanattributeinfo, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])null, new MBeanNotificationInfo[0]);
   }

   public static void registerJmxMonitoring(MinecraftServer p_233490_0_) {
      try {
         ManagementFactory.getPlatformMBeanServer().registerMBean(new ServerInfoMBean(p_233490_0_), new ObjectName("net.minecraft.server:type=Server"));
      } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException malformedobjectnameexception) {
         LOGGER.warn("Failed to initialise server as JMX bean", (Throwable)malformedobjectnameexception);
      }

   }

   private float getAverageTickTime() {
      return this.server.getAverageTickTime();
   }

   private long[] getTickTimes() {
      return this.server.tickTimes;
   }

   @Nullable
   public Object getAttribute(String p_getAttribute_1_) {
      ServerInfoMBean.Attribute serverinfombean$attribute = this.attributeDescriptionByName.get(p_getAttribute_1_);
      return serverinfombean$attribute == null ? null : serverinfombean$attribute.getter.get();
   }

   public void setAttribute(javax.management.Attribute p_setAttribute_1_) {
   }

   public AttributeList getAttributes(String[] p_getAttributes_1_) {
      List<javax.management.Attribute> list = Arrays.stream(p_getAttributes_1_).map(this.attributeDescriptionByName::get).filter(Objects::nonNull).map((p_233488_0_) -> {
         return new javax.management.Attribute(p_233488_0_.name, p_233488_0_.getter.get());
      }).collect(Collectors.toList());
      return new AttributeList(list);
   }

   public AttributeList setAttributes(AttributeList p_setAttributes_1_) {
      return new AttributeList();
   }

   @Nullable
   public Object invoke(String p_invoke_1_, Object[] p_invoke_2_, String[] p_invoke_3_) {
      return null;
   }

   public MBeanInfo getMBeanInfo() {
      return this.mBeanInfo;
   }

   static final class Attribute {
      private final String name;
      private final Supplier<Object> getter;
      private final String description;
      private final Class<?> type;

      private Attribute(String p_i231480_1_, Supplier<Object> p_i231480_2_, String p_i231480_3_, Class<?> p_i231480_4_) {
         this.name = p_i231480_1_;
         this.getter = p_i231480_2_;
         this.description = p_i231480_3_;
         this.type = p_i231480_4_;
      }

      private MBeanAttributeInfo asMBeanAttributeInfo() {
         return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
      }
   }
}
