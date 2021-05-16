package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StringUtils;
import net.minecraft.world.storage.FolderName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File OLD_IPBANLIST = new File("banned-ips.txt");
   public static final File OLD_USERBANLIST = new File("banned-players.txt");
   public static final File OLD_OPLIST = new File("ops.txt");
   public static final File OLD_WHITELIST = new File("white-list.txt");

   static List<String> readOldListFormat(File p_152721_0_, Map<String, String[]> p_152721_1_) throws IOException {
      List<String> list = Files.readLines(p_152721_0_, StandardCharsets.UTF_8);

      for(String s : list) {
         s = s.trim();
         if (!s.startsWith("#") && s.length() >= 1) {
            String[] astring = s.split("\\|");
            p_152721_1_.put(astring[0].toLowerCase(Locale.ROOT), astring);
         }
      }

      return list;
   }

   private static void lookupPlayers(MinecraftServer p_152717_0_, Collection<String> p_152717_1_, ProfileLookupCallback p_152717_2_) {
      String[] astring = p_152717_1_.stream().filter((p_201150_0_) -> {
         return !StringUtils.isNullOrEmpty(p_201150_0_);
      }).toArray((p_201149_0_) -> {
         return new String[p_201149_0_];
      });
      if (p_152717_0_.usesAuthentication()) {
         p_152717_0_.getProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, p_152717_2_);
      } else {
         for(String s : astring) {
            UUID uuid = PlayerEntity.createPlayerUUID(new GameProfile((UUID)null, s));
            GameProfile gameprofile = new GameProfile(uuid, s);
            p_152717_2_.onProfileLookupSucceeded(gameprofile);
         }
      }

   }

   public static boolean convertUserBanlist(final MinecraftServer p_152724_0_) {
      final BanList banlist = new BanList(PlayerList.USERBANLIST_FILE);
      if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         if (banlist.getFile().exists()) {
            try {
               banlist.load();
            } catch (IOException ioexception1) {
               LOGGER.warn("Could not load existing file {}", banlist.getFile().getName(), ioexception1);
            }
         }

         try {
            final Map<String, String[]> map = Maps.newHashMap();
            readOldListFormat(OLD_USERBANLIST, map);
            ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152724_0_.getProfileCache().add(p_onProfileLookupSucceeded_1_);
                  String[] astring = map.get(p_onProfileLookupSucceeded_1_.getName().toLowerCase(Locale.ROOT));
                  if (astring == null) {
                     PreYggdrasilConverter.LOGGER.warn("Could not convert user banlist entry for {}", (Object)p_onProfileLookupSucceeded_1_.getName());
                     throw new PreYggdrasilConverter.ConversionError("Profile not in the conversionlist");
                  } else {
                     Date date = astring.length > 1 ? PreYggdrasilConverter.parseDate(astring[1], (Date)null) : null;
                     String s = astring.length > 2 ? astring[2] : null;
                     Date date1 = astring.length > 3 ? PreYggdrasilConverter.parseDate(astring[3], (Date)null) : null;
                     String s1 = astring.length > 4 ? astring[4] : null;
                     banlist.add(new ProfileBanEntry(p_onProfileLookupSucceeded_1_, date, s, date1, s1));
                  }
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user banlist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupPlayers(p_152724_0_, map.keySet(), profilelookupcallback);
            banlist.save();
            renameOldFile(OLD_USERBANLIST);
            return true;
         } catch (IOException ioexception) {
            LOGGER.warn("Could not read old user banlist to convert it!", (Throwable)ioexception);
            return false;
         } catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertIpBanlist(MinecraftServer p_152722_0_) {
      IPBanList ipbanlist = new IPBanList(PlayerList.IPBANLIST_FILE);
      if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         if (ipbanlist.getFile().exists()) {
            try {
               ipbanlist.load();
            } catch (IOException ioexception1) {
               LOGGER.warn("Could not load existing file {}", ipbanlist.getFile().getName(), ioexception1);
            }
         }

         try {
            Map<String, String[]> map = Maps.newHashMap();
            readOldListFormat(OLD_IPBANLIST, map);

            for(String s : map.keySet()) {
               String[] astring = map.get(s);
               Date date = astring.length > 1 ? parseDate(astring[1], (Date)null) : null;
               String s1 = astring.length > 2 ? astring[2] : null;
               Date date1 = astring.length > 3 ? parseDate(astring[3], (Date)null) : null;
               String s2 = astring.length > 4 ? astring[4] : null;
               ipbanlist.add(new IPBanEntry(s, date, s1, date1, s2));
            }

            ipbanlist.save();
            renameOldFile(OLD_IPBANLIST);
            return true;
         } catch (IOException ioexception) {
            LOGGER.warn("Could not parse old ip banlist to convert it!", (Throwable)ioexception);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertOpsList(final MinecraftServer p_152718_0_) {
      final OpList oplist = new OpList(PlayerList.OPLIST_FILE);
      if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         if (oplist.getFile().exists()) {
            try {
               oplist.load();
            } catch (IOException ioexception1) {
               LOGGER.warn("Could not load existing file {}", oplist.getFile().getName(), ioexception1);
            }
         }

         try {
            List<String> list = Files.readLines(OLD_OPLIST, StandardCharsets.UTF_8);
            ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152718_0_.getProfileCache().add(p_onProfileLookupSucceeded_1_);
                  oplist.add(new OpEntry(p_onProfileLookupSucceeded_1_, p_152718_0_.getOperatorUserPermissionLevel(), false));
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup oplist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupPlayers(p_152718_0_, list, profilelookupcallback);
            oplist.save();
            renameOldFile(OLD_OPLIST);
            return true;
         } catch (IOException ioexception) {
            LOGGER.warn("Could not read old oplist to convert it!", (Throwable)ioexception);
            return false;
         } catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertWhiteList(final MinecraftServer p_152710_0_) {
      final WhiteList whitelist = new WhiteList(PlayerList.WHITELIST_FILE);
      if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         if (whitelist.getFile().exists()) {
            try {
               whitelist.load();
            } catch (IOException ioexception1) {
               LOGGER.warn("Could not load existing file {}", whitelist.getFile().getName(), ioexception1);
            }
         }

         try {
            List<String> list = Files.readLines(OLD_WHITELIST, StandardCharsets.UTF_8);
            ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152710_0_.getProfileCache().add(p_onProfileLookupSucceeded_1_);
                  whitelist.add(new WhitelistEntry(p_onProfileLookupSucceeded_1_));
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupPlayers(p_152710_0_, list, profilelookupcallback);
            whitelist.save();
            renameOldFile(OLD_WHITELIST);
            return true;
         } catch (IOException ioexception) {
            LOGGER.warn("Could not read old whitelist to convert it!", (Throwable)ioexception);
            return false;
         } catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
            return false;
         }
      } else {
         return true;
      }
   }

   @Nullable
   public static UUID convertMobOwnerIfNecessary(final MinecraftServer p_187473_0_, String p_187473_1_) {
      if (!StringUtils.isNullOrEmpty(p_187473_1_) && p_187473_1_.length() <= 16) {
         GameProfile gameprofile = p_187473_0_.getProfileCache().get(p_187473_1_);
         if (gameprofile != null && gameprofile.getId() != null) {
            return gameprofile.getId();
         } else if (!p_187473_0_.isSingleplayer() && p_187473_0_.usesAuthentication()) {
            final List<GameProfile> list = Lists.newArrayList();
            ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_187473_0_.getProfileCache().add(p_onProfileLookupSucceeded_1_);
                  list.add(p_onProfileLookupSucceeded_1_);
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
               }
            };
            lookupPlayers(p_187473_0_, Lists.newArrayList(p_187473_1_), profilelookupcallback);
            return !list.isEmpty() && list.get(0).getId() != null ? list.get(0).getId() : null;
         } else {
            return PlayerEntity.createPlayerUUID(new GameProfile((UUID)null, p_187473_1_));
         }
      } else {
         try {
            return UUID.fromString(p_187473_1_);
         } catch (IllegalArgumentException illegalargumentexception) {
            return null;
         }
      }
   }

   public static boolean convertPlayers(final DedicatedServer p_152723_0_) {
      final File file1 = getWorldPlayersDirectory(p_152723_0_);
      final File file2 = new File(file1.getParentFile(), "playerdata");
      final File file3 = new File(file1.getParentFile(), "unknownplayers");
      if (file1.exists() && file1.isDirectory()) {
         File[] afile = file1.listFiles();
         List<String> list = Lists.newArrayList();

         for(File file4 : afile) {
            String s = file4.getName();
            if (s.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String s1 = s.substring(0, s.length() - ".dat".length());
               if (!s1.isEmpty()) {
                  list.add(s1);
               }
            }
         }

         try {
            final String[] astring = list.toArray(new String[list.size()]);
            ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152723_0_.getProfileCache().add(p_onProfileLookupSucceeded_1_);
                  UUID uuid = p_onProfileLookupSucceeded_1_.getId();
                  if (uuid == null) {
                     throw new PreYggdrasilConverter.ConversionError("Missing UUID for user profile " + p_onProfileLookupSucceeded_1_.getName());
                  } else {
                     this.movePlayerFile(file2, this.getFileNameForProfile(p_onProfileLookupSucceeded_1_), uuid.toString());
                  }
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user uuid for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException) {
                     String s2 = this.getFileNameForProfile(p_onProfileLookupFailed_1_);
                     this.movePlayerFile(file3, s2, s2);
                  } else {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }

               private void movePlayerFile(File p_152743_1_, String p_152743_2_, String p_152743_3_) {
                  File file5 = new File(file1, p_152743_2_ + ".dat");
                  File file6 = new File(p_152743_1_, p_152743_3_ + ".dat");
                  PreYggdrasilConverter.ensureDirectoryExists(p_152743_1_);
                  if (!file5.renameTo(file6)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not convert file for " + p_152743_2_);
                  }
               }

               private String getFileNameForProfile(GameProfile p_152744_1_) {
                  String s2 = null;

                  for(String s3 : astring) {
                     if (s3 != null && s3.equalsIgnoreCase(p_152744_1_.getName())) {
                        s2 = s3;
                        break;
                     }
                  }

                  if (s2 == null) {
                     throw new PreYggdrasilConverter.ConversionError("Could not find the filename for " + p_152744_1_.getName() + " anymore");
                  } else {
                     return s2;
                  }
               }
            };
            lookupPlayers(p_152723_0_, Lists.newArrayList(astring), profilelookupcallback);
            return true;
         } catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
            LOGGER.error("Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
            return false;
         }
      } else {
         return true;
      }
   }

   private static void ensureDirectoryExists(File p_152711_0_) {
      if (p_152711_0_.exists()) {
         if (!p_152711_0_.isDirectory()) {
            throw new PreYggdrasilConverter.ConversionError("Can't create directory " + p_152711_0_.getName() + " in world save directory.");
         }
      } else if (!p_152711_0_.mkdirs()) {
         throw new PreYggdrasilConverter.ConversionError("Can't create directory " + p_152711_0_.getName() + " in world save directory.");
      }
   }

   public static boolean serverReadyAfterUserconversion(MinecraftServer p_219587_0_) {
      boolean flag = areOldUserlistsRemoved();
      return flag && areOldPlayersConverted(p_219587_0_);
   }

   private static boolean areOldUserlistsRemoved() {
      boolean flag = false;
      if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         flag = true;
      }

      boolean flag1 = false;
      if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         flag1 = true;
      }

      boolean flag2 = false;
      if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         flag2 = true;
      }

      boolean flag3 = false;
      if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         flag3 = true;
      }

      if (!flag && !flag1 && !flag2 && !flag3) {
         return true;
      } else {
         LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         LOGGER.warn("** please remove the following files and restart the server:");
         if (flag) {
            LOGGER.warn("* {}", (Object)OLD_USERBANLIST.getName());
         }

         if (flag1) {
            LOGGER.warn("* {}", (Object)OLD_IPBANLIST.getName());
         }

         if (flag2) {
            LOGGER.warn("* {}", (Object)OLD_OPLIST.getName());
         }

         if (flag3) {
            LOGGER.warn("* {}", (Object)OLD_WHITELIST.getName());
         }

         return false;
      }
   }

   private static boolean areOldPlayersConverted(MinecraftServer p_219589_0_) {
      File file1 = getWorldPlayersDirectory(p_219589_0_);
      if (!file1.exists() || !file1.isDirectory() || file1.list().length <= 0 && file1.delete()) {
         return true;
      } else {
         LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", (Object)file1.getPath());
         return false;
      }
   }

   private static File getWorldPlayersDirectory(MinecraftServer p_219585_0_) {
      return p_219585_0_.getWorldPath(FolderName.PLAYER_OLD_DATA_DIR).toFile();
   }

   private static void renameOldFile(File p_152727_0_) {
      File file1 = new File(p_152727_0_.getName() + ".converted");
      p_152727_0_.renameTo(file1);
   }

   private static Date parseDate(String p_152713_0_, Date p_152713_1_) {
      Date date;
      try {
         date = BanEntry.DATE_FORMAT.parse(p_152713_0_);
      } catch (ParseException parseexception) {
         date = p_152713_1_;
      }

      return date;
   }

   static class ConversionError extends RuntimeException {
      private ConversionError(String p_i1206_1_, Throwable p_i1206_2_) {
         super(p_i1206_1_, p_i1206_2_);
      }

      private ConversionError(String p_i1207_1_) {
         super(p_i1207_1_);
      }
   }
}
