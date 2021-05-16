package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void main(String[] p_main_0_) {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("nogui");
      OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
      OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
      OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
      OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
      OptionSpec<Void> optionspec6 = optionparser.accepts("safeMode", "Loads level with vanilla datapack only");
      OptionSpec<Void> optionspec7 = optionparser.accepts("help").forHelp();
      OptionSpec<String> optionspec8 = optionparser.accepts("singleplayer").withRequiredArg();
      OptionSpec<String> optionspec9 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".");
      OptionSpec<String> optionspec10 = optionparser.accepts("world").withRequiredArg();
      OptionSpec<Integer> optionspec11 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
      OptionSpec<String> optionspec12 = optionparser.accepts("serverId").withRequiredArg();
      OptionSpec<String> optionspec13 = optionparser.nonOptions();
      optionparser.accepts("allowUpdates").withRequiredArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE); // Forge: allow mod updates to proceed
      optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File(".")); //Forge: Consume this argument, we use it in the launcher, and the client side.

      try {
         OptionSet optionset = optionparser.parse(p_main_0_);
         if (optionset.has(optionspec7)) {
            optionparser.printHelpOn(System.err);
            return;
         }
         Path path1 = Paths.get("eula.txt");
         ServerEula servereula = new ServerEula(path1);

         if (!servereula.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         CrashReport.preload();
         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         if (!optionset.has(optionspec1)) net.minecraftforge.fml.server.ServerModLoader.load(); // Load mods before we load almost anything else anymore. Single spot now. Only loads if they haven't passed the initserver param
         DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();
         Path path = Paths.get("server.properties");
         ServerPropertiesProvider serverpropertiesprovider = new ServerPropertiesProvider(dynamicregistries$impl, path);
         serverpropertiesprovider.forceSave();
         if (optionset.has(optionspec1)) {
            LOGGER.info("Initialized '{}' and '{}'", path.toAbsolutePath(), path1.toAbsolutePath());
            return;
         }

         File file1 = new File(optionset.valueOf(optionspec9));
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(file1, MinecraftServer.USERID_CACHE_FILE.getName()));
         String s = Optional.ofNullable(optionset.valueOf(optionspec10)).orElse(serverpropertiesprovider.getProperties().levelName);
         if (s == null || s.isEmpty() || new File(file1, s).getAbsolutePath().equals(new File(s).getAbsolutePath())) {
            LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! " + s);
            return;
         }
         SaveFormat saveformat = SaveFormat.createDefault(file1.toPath());
         SaveFormat.LevelSave saveformat$levelsave = saveformat.createAccess(s);
         MinecraftServer.convertFromRegionFormatIfNeeded(saveformat$levelsave);
         DatapackCodec datapackcodec = saveformat$levelsave.getDataPacks();
         boolean flag = optionset.has(optionspec6);
         if (flag) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         ResourcePackList resourcepacklist = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(saveformat$levelsave.getLevelPath(FolderName.DATAPACK_DIR).toFile(), IPackNameDecorator.WORLD));
         DatapackCodec datapackcodec1 = MinecraftServer.configurePackRepository(resourcepacklist, datapackcodec == null ? DatapackCodec.DEFAULT : datapackcodec, flag);
         CompletableFuture<DataPackRegistries> completablefuture = DataPackRegistries.loadResources(resourcepacklist.openAllSelected(), Commands.EnvironmentType.DEDICATED, serverpropertiesprovider.getProperties().functionPermissionLevel, Util.backgroundExecutor(), Runnable::run);

         DataPackRegistries datapackregistries;
         try {
            datapackregistries = completablefuture.get();
         } catch (Exception exception) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
            resourcepacklist.close();
            return;
         }

         datapackregistries.updateGlobals();
         WorldSettingsImport<INBT> worldsettingsimport = WorldSettingsImport.create(NBTDynamicOps.INSTANCE, datapackregistries.getResourceManager(), dynamicregistries$impl);
         IServerConfiguration iserverconfiguration = saveformat$levelsave.getDataTag(worldsettingsimport, datapackcodec1);
         if (iserverconfiguration == null) {
            WorldSettings worldsettings;
            DimensionGeneratorSettings dimensiongeneratorsettings;
            if (optionset.has(optionspec2)) {
               worldsettings = MinecraftServer.DEMO_SETTINGS;
               dimensiongeneratorsettings = DimensionGeneratorSettings.demoSettings(dynamicregistries$impl);
            } else {
               ServerProperties serverproperties = serverpropertiesprovider.getProperties();
               worldsettings = new WorldSettings(serverproperties.levelName, serverproperties.gamemode, serverproperties.hardcore, serverproperties.difficulty, false, new GameRules(), datapackcodec1);
               dimensiongeneratorsettings = optionset.has(optionspec3) ? serverproperties.worldGenSettings.withBonusChest() : serverproperties.worldGenSettings;
            }

            // Forge: Deserialize the DimensionGeneratorSettings to ensure modded dims are loaded on first server load (see SimpleRegistryCodec#decode). Vanilla behaviour only loads from the server.properties and deserializes only after the 2nd server load.
            dimensiongeneratorsettings = DimensionGeneratorSettings.CODEC.encodeStart(net.minecraft.util.registry.WorldGenSettingsExport.create(NBTDynamicOps.INSTANCE, dynamicregistries$impl), dimensiongeneratorsettings).flatMap(nbt -> DimensionGeneratorSettings.CODEC.parse(worldsettingsimport, nbt)).getOrThrow(false, errorMsg->{});
            iserverconfiguration = new ServerWorldInfo(worldsettings, dimensiongeneratorsettings, Lifecycle.stable());
         }

         if (optionset.has(optionspec4)) {
            forceUpgrade(saveformat$levelsave, DataFixesManager.getDataFixer(), optionset.has(optionspec5), () -> {
               return true;
            }, iserverconfiguration.worldGenSettings().levels());
         }

         saveformat$levelsave.saveDataTag(dynamicregistries$impl, iserverconfiguration);
         IServerConfiguration iserverconfiguration1 = iserverconfiguration;
         final DedicatedServer dedicatedserver = MinecraftServer.spin((p_240762_16_) -> {
            DedicatedServer dedicatedserver1 = new DedicatedServer(p_240762_16_, dynamicregistries$impl, saveformat$levelsave, resourcepacklist, datapackregistries, iserverconfiguration1, serverpropertiesprovider, DataFixesManager.getDataFixer(), minecraftsessionservice, gameprofilerepository, playerprofilecache, LoggingChunkStatusListener::new);
            dedicatedserver1.setSingleplayerName(optionset.valueOf(optionspec8));
            dedicatedserver1.setPort(optionset.valueOf(optionspec11));
            dedicatedserver1.setDemo(optionset.has(optionspec2));
            dedicatedserver1.setId(optionset.valueOf(optionspec12));
            boolean flag1 = !optionset.has(optionspec) && !optionset.valuesOf(optionspec13).contains("nogui");
            if (flag1 && !GraphicsEnvironment.isHeadless()) {
               dedicatedserver1.showGui();
            }

            return dedicatedserver1;
         });
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.halt(true);
               LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception exception1) {
         LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception1);
      }

   }

   private static void forceUpgrade(SaveFormat.LevelSave p_240761_0_, DataFixer p_240761_1_, boolean p_240761_2_, BooleanSupplier p_240761_3_, ImmutableSet<RegistryKey<World>> p_240761_4_) {
      LOGGER.info("Forcing world upgrade!");
      WorldOptimizer worldoptimizer = new WorldOptimizer(p_240761_0_, p_240761_1_, p_240761_4_, p_240761_2_);
      ITextComponent itextcomponent = null;

      while(!worldoptimizer.isFinished()) {
         ITextComponent itextcomponent1 = worldoptimizer.getStatus();
         if (itextcomponent != itextcomponent1) {
            itextcomponent = itextcomponent1;
            LOGGER.info(worldoptimizer.getStatus().getString());
         }

         int i = worldoptimizer.getTotalChunks();
         if (i > 0) {
            int j = worldoptimizer.getConverted() + worldoptimizer.getSkipped();
            LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
         }

         if (!p_240761_3_.getAsBoolean()) {
            worldoptimizer.cancel();
         } else {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException interruptedexception) {
            }
         }
      }

   }
}
