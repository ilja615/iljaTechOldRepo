package net.minecraft.world.storage;

public class FolderName {
   public static final FolderName PLAYER_ADVANCEMENTS_DIR = new FolderName("advancements");
   public static final FolderName PLAYER_STATS_DIR = new FolderName("stats");
   public static final FolderName PLAYER_DATA_DIR = new FolderName("playerdata");
   public static final FolderName PLAYER_OLD_DATA_DIR = new FolderName("players");
   public static final FolderName LEVEL_DATA_FILE = new FolderName("level.dat");
   public static final FolderName GENERATED_DIR = new FolderName("generated");
   public static final FolderName DATAPACK_DIR = new FolderName("datapacks");
   public static final FolderName MAP_RESOURCE_FILE = new FolderName("resources.zip");
   public static final FolderName ROOT = new FolderName(".");
   private final String id;

   public FolderName(String p_i232151_1_) {
      this.id = p_i232151_1_;
   }

   public String getId() {
      return this.id;
   }

   public String toString() {
      return "/" + this.id;
   }
}
