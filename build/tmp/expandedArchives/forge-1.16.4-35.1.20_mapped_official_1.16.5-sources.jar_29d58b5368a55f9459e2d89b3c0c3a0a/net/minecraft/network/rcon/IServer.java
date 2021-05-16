package net.minecraft.network.rcon;

import net.minecraft.server.dedicated.ServerProperties;

public interface IServer {
   ServerProperties getProperties();

   String getServerIp();

   int getServerPort();

   String getServerName();

   String getServerVersion();

   int getPlayerCount();

   int getMaxPlayers();

   String[] getPlayerNames();

   String getLevelIdName();

   String getPluginNames();

   String runCommand(String p_71252_1_);
}
