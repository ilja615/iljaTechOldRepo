package net.minecraft.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEula {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path file;
   private final boolean agreed;

   public ServerEula(Path p_i50746_1_) {
      this.file = p_i50746_1_;
      this.agreed = SharedConstants.IS_RUNNING_IN_IDE || this.readFile();
   }

   private boolean readFile() {
      try (InputStream inputstream = Files.newInputStream(this.file)) {
         Properties properties = new Properties();
         properties.load(inputstream);
         return Boolean.parseBoolean(properties.getProperty("eula", "false"));
      } catch (Exception exception) {
         LOGGER.warn("Failed to load {}", (Object)this.file);
         this.saveDefaults();
         return false;
      }
   }

   public boolean hasAgreedToEULA() {
      return this.agreed;
   }

   private void saveDefaults() {
      if (!SharedConstants.IS_RUNNING_IN_IDE) {
         try (OutputStream outputstream = Files.newOutputStream(this.file)) {
            Properties properties = new Properties();
            properties.setProperty("eula", "false");
            properties.store(outputstream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
         } catch (Exception exception) {
            LOGGER.warn("Failed to save {}", this.file, exception);
         }

      }
   }
}
