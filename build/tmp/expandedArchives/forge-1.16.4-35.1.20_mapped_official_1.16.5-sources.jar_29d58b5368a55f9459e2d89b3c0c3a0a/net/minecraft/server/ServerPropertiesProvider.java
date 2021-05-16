package net.minecraft.server;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.registry.DynamicRegistries;

public class ServerPropertiesProvider {
   private final Path source;
   private ServerProperties properties;

   public ServerPropertiesProvider(DynamicRegistries p_i242100_1_, Path p_i242100_2_) {
      this.source = p_i242100_2_;
      this.properties = ServerProperties.fromFile(p_i242100_1_, p_i242100_2_);
   }

   public ServerProperties getProperties() {
      return this.properties;
   }

   public void forceSave() {
      this.properties.store(this.source);
   }

   public ServerPropertiesProvider update(UnaryOperator<ServerProperties> p_219033_1_) {
      (this.properties = p_219033_1_.apply(this.properties)).store(this.source);
      return this;
   }
}
