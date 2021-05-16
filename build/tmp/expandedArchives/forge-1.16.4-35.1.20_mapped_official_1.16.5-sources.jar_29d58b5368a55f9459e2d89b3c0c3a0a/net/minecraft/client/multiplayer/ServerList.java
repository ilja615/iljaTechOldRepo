package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List<ServerData> serverList = Lists.newArrayList();

   public ServerList(Minecraft p_i1194_1_) {
      this.minecraft = p_i1194_1_;
      this.load();
   }

   public void load() {
      try {
         this.serverList.clear();
         CompoundNBT compoundnbt = CompressedStreamTools.read(new File(this.minecraft.gameDirectory, "servers.dat"));
         if (compoundnbt == null) {
            return;
         }

         ListNBT listnbt = compoundnbt.getList("servers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.serverList.add(ServerData.read(listnbt.getCompound(i)));
         }
      } catch (Exception exception) {
         LOGGER.error("Couldn't load server list", (Throwable)exception);
      }

   }

   public void save() {
      try {
         ListNBT listnbt = new ListNBT();

         for(ServerData serverdata : this.serverList) {
            listnbt.add(serverdata.write());
         }

         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.put("servers", listnbt);
         File file3 = File.createTempFile("servers", ".dat", this.minecraft.gameDirectory);
         CompressedStreamTools.write(compoundnbt, file3);
         File file1 = new File(this.minecraft.gameDirectory, "servers.dat_old");
         File file2 = new File(this.minecraft.gameDirectory, "servers.dat");
         Util.safeReplaceFile(file2, file3, file1);
      } catch (Exception exception) {
         LOGGER.error("Couldn't save server list", (Throwable)exception);
      }

   }

   public ServerData get(int p_78850_1_) {
      return this.serverList.get(p_78850_1_);
   }

   public void remove(ServerData p_217506_1_) {
      this.serverList.remove(p_217506_1_);
   }

   public void add(ServerData p_78849_1_) {
      this.serverList.add(p_78849_1_);
   }

   public int size() {
      return this.serverList.size();
   }

   public void swap(int p_78857_1_, int p_78857_2_) {
      ServerData serverdata = this.get(p_78857_1_);
      this.serverList.set(p_78857_1_, this.get(p_78857_2_));
      this.serverList.set(p_78857_2_, serverdata);
      this.save();
   }

   public void replace(int p_147413_1_, ServerData p_147413_2_) {
      this.serverList.set(p_147413_1_, p_147413_2_);
   }

   public static void saveSingleServer(ServerData p_147414_0_) {
      ServerList serverlist = new ServerList(Minecraft.getInstance());
      serverlist.load();

      for(int i = 0; i < serverlist.size(); ++i) {
         ServerData serverdata = serverlist.get(i);
         if (serverdata.name.equals(p_147414_0_.name) && serverdata.ip.equals(p_147414_0_.ip)) {
            serverlist.replace(i, p_147414_0_);
            break;
         }
      }

      serverlist.save();
   }
}
