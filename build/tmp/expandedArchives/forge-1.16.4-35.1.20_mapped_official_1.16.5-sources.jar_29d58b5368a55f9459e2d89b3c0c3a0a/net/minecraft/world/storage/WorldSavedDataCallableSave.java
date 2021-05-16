package net.minecraft.world.storage;

public class WorldSavedDataCallableSave implements Runnable {
   private final WorldSavedData savedData;

   public WorldSavedDataCallableSave(WorldSavedData p_i46651_1_) {
      this.savedData = p_i46651_1_;
   }

   public void run() {
      this.savedData.setDirty();
   }
}
