package net.minecraft.resources;

public enum ResourcePackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directory;

   private ResourcePackType(String p_i47913_3_) {
      this.directory = p_i47913_3_;
   }

   public String getDirectory() {
      return this.directory;
   }
}
