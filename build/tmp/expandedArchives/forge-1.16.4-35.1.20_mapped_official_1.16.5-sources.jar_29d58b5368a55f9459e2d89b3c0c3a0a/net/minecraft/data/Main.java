package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {
   public static void main(String[] p_main_0_) throws IOException {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
      OptionSpec<Void> optionspec1 = optionparser.accepts("server", "Include server generators");
      OptionSpec<Void> optionspec2 = optionparser.accepts("client", "Include client generators");
      OptionSpec<Void> optionspec3 = optionparser.accepts("dev", "Include development tools");
      OptionSpec<Void> optionspec4 = optionparser.accepts("reports", "Include data reports");
      OptionSpec<Void> optionspec5 = optionparser.accepts("validate", "Validate inputs");
      OptionSpec<Void> optionspec6 = optionparser.accepts("all", "Include all generators");
      OptionSpec<String> optionspec7 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
      OptionSpec<String> optionspec8 = optionparser.accepts("input", "Input folder").withRequiredArg();
      OptionSpec<String> existing = optionparser.accepts("existing", "Existing resource packs that generated resources can reference").withRequiredArg();
      OptionSpec<String> existingMod = optionparser.accepts("existing-mod", "Existing mods that generated resources can reference the resource packs of").withRequiredArg();
      OptionSpec<java.io.File> gameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(java.io.File.class).defaultsTo(new java.io.File(".")).required(); //Need by modlauncher, so lets just eat it
      OptionSpec<String> mod = optionparser.accepts("mod", "A modid to dump").withRequiredArg().withValuesSeparatedBy(",");
      OptionSpec<Void> flat = optionparser.accepts("flat", "Do not append modid prefix to output directory when generating for multiple mods");
      OptionSet optionset = optionparser.parse(p_main_0_);
      if (!optionset.has(optionspec) && optionset.hasOptions() && !(optionset.specs().size() == 1 && optionset.has(gameDir))) {
         Path path = Paths.get(optionspec7.value(optionset));
         boolean flag = optionset.has(optionspec6);
         boolean flag1 = flag || optionset.has(optionspec2);
         boolean flag2 = flag || optionset.has(optionspec1);
         boolean flag3 = flag || optionset.has(optionspec3);
         boolean flag4 = flag || optionset.has(optionspec4);
         boolean flag5 = flag || optionset.has(optionspec5);
         Collection<Path> inputs = optionset.valuesOf(optionspec8).stream().map(Paths::get).collect(Collectors.toList());
         Collection<Path> existingPacks = optionset.valuesOf(existing).stream().map(Paths::get).collect(Collectors.toList());
         java.util.Set<String> existingMods = new java.util.HashSet<>(optionset.valuesOf(existingMod));
         java.util.Set<String> mods = new java.util.HashSet<>(optionset.valuesOf(mod));
         boolean isFlat = mods.isEmpty() || optionset.has(flat);
         net.minecraftforge.fml.DatagenModLoader.begin(mods, path, inputs, existingPacks, existingMods, flag2, flag1, flag3, flag4, flag5, isFlat);
         if (mods.contains("minecraft") || mods.isEmpty())
            createStandardGenerator(isFlat ? path : path.resolve("minecraft"), inputs, flag1, flag2, flag3, flag4, flag5).run();
      } else {
         optionparser.printHelpOn(System.out);
      }
   }

   public static DataGenerator createStandardGenerator(Path p_200264_0_, Collection<Path> p_200264_1_, boolean p_200264_2_, boolean p_200264_3_, boolean p_200264_4_, boolean p_200264_5_, boolean p_200264_6_) {
      DataGenerator datagenerator = new DataGenerator(p_200264_0_, p_200264_1_);
      if (p_200264_2_ || p_200264_3_) {
         datagenerator.addProvider((new SNBTToNBTConverter(datagenerator)).addFilter(new StructureUpdater()));
      }

      if (p_200264_2_) {
         datagenerator.addProvider(new BlockStateProvider(datagenerator));
      }

      if (p_200264_3_) {
         datagenerator.addProvider(new FluidTagsProvider(datagenerator));
         BlockTagsProvider blocktagsprovider = new BlockTagsProvider(datagenerator);
         datagenerator.addProvider(blocktagsprovider);
         datagenerator.addProvider(new ItemTagsProvider(datagenerator, blocktagsprovider));
         datagenerator.addProvider(new EntityTypeTagsProvider(datagenerator));
         datagenerator.addProvider(new RecipeProvider(datagenerator));
         datagenerator.addProvider(new AdvancementProvider(datagenerator));
         datagenerator.addProvider(new LootTableProvider(datagenerator));
      }

      if (p_200264_4_) {
         datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
      }

      if (p_200264_5_) {
         datagenerator.addProvider(new BlockListReport(datagenerator));
         datagenerator.addProvider(new RegistryDumpReport(datagenerator));
         datagenerator.addProvider(new CommandsReport(datagenerator));
         datagenerator.addProvider(new BiomeProvider(datagenerator));
      }

      return datagenerator;
   }
}
