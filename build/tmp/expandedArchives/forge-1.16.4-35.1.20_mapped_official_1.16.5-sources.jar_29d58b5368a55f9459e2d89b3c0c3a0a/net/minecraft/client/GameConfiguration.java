package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation user;
   public final ScreenSize display;
   public final GameConfiguration.FolderInformation location;
   public final GameConfiguration.GameInformation game;
   public final GameConfiguration.ServerInformation server;

   public GameConfiguration(GameConfiguration.UserInformation p_i51071_1_, ScreenSize p_i51071_2_, GameConfiguration.FolderInformation p_i51071_3_, GameConfiguration.GameInformation p_i51071_4_, GameConfiguration.ServerInformation p_i51071_5_) {
      this.user = p_i51071_1_;
      this.display = p_i51071_2_;
      this.location = p_i51071_3_;
      this.game = p_i51071_4_;
      this.server = p_i51071_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      @Nullable
      public final String assetIndex;

      public FolderInformation(File p_i45489_1_, File p_i45489_2_, File p_i45489_3_, @Nullable String p_i45489_4_) {
         this.gameDirectory = p_i45489_1_;
         this.resourcePackDirectory = p_i45489_2_;
         this.assetDirectory = p_i45489_3_;
         this.assetIndex = p_i45489_4_;
      }

      public ResourceIndex getAssetIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetDirectory) : new ResourceIndex(this.assetDirectory, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;

      public GameInformation(boolean p_i232334_1_, String p_i232334_2_, String p_i232334_3_, boolean p_i232334_4_, boolean p_i232334_5_) {
         this.demo = p_i232334_1_;
         this.launchVersion = p_i232334_2_;
         this.versionType = p_i232334_3_;
         this.disableMultiplayer = p_i232334_4_;
         this.disableChat = p_i232334_5_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      @Nullable
      public final String hostname;
      public final int port;

      public ServerInformation(@Nullable String p_i45487_1_, int p_i45487_2_) {
         this.hostname = p_i45487_1_;
         this.port = p_i45487_2_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session user;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session p_i46375_1_, PropertyMap p_i46375_2_, PropertyMap p_i46375_3_, Proxy p_i46375_4_) {
         this.user = p_i46375_1_;
         this.userProperties = p_i46375_2_;
         this.profileProperties = p_i46375_3_;
         this.proxy = p_i46375_4_;
      }
   }
}
