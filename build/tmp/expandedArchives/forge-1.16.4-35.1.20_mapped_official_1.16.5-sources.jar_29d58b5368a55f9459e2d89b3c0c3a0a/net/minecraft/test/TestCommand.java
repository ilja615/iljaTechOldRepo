package net.minecraft.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.data.NBTToSNBTConverter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class TestCommand {
   public static void register(CommandDispatcher<CommandSource> p_229613_0_) {
      p_229613_0_.register(Commands.literal("test").then(Commands.literal("runthis").executes((p_229647_0_) -> {
         return runNearbyTest(p_229647_0_.getSource());
      })).then(Commands.literal("runthese").executes((p_229646_0_) -> {
         return runAllNearbyTests(p_229646_0_.getSource());
      })).then(Commands.literal("runfailed").executes((p_240582_0_) -> {
         return runLastFailedTests(p_240582_0_.getSource(), false, 0, 8);
      }).then(Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes((p_240585_0_) -> {
         return runLastFailedTests(p_240585_0_.getSource(), BoolArgumentType.getBool(p_240585_0_, "onlyRequiredTests"), 0, 8);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_240588_0_) -> {
         return runLastFailedTests(p_240588_0_.getSource(), BoolArgumentType.getBool(p_240588_0_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_240588_0_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_240586_0_) -> {
         return runLastFailedTests(p_240586_0_.getSource(), BoolArgumentType.getBool(p_240586_0_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_240586_0_, "rotationSteps"), IntegerArgumentType.getInteger(p_240586_0_, "testsPerRow"));
      }))))).then(Commands.literal("run").then(Commands.argument("testName", TestArgArgument.testFunctionArgument()).executes((p_229645_0_) -> {
         return runTest(p_229645_0_.getSource(), TestArgArgument.getTestFunction(p_229645_0_, "testName"), 0);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_240584_0_) -> {
         return runTest(p_240584_0_.getSource(), TestArgArgument.getTestFunction(p_240584_0_, "testName"), IntegerArgumentType.getInteger(p_240584_0_, "rotationSteps"));
      })))).then(Commands.literal("runall").executes((p_229644_0_) -> {
         return runAllTests(p_229644_0_.getSource(), 0, 8);
      }).then(Commands.argument("testClassName", TestTypeArgument.testClassName()).executes((p_229643_0_) -> {
         return runAllTestsInClass(p_229643_0_.getSource(), TestTypeArgument.getTestClassName(p_229643_0_, "testClassName"), 0, 8);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_240580_0_) -> {
         return runAllTestsInClass(p_240580_0_.getSource(), TestTypeArgument.getTestClassName(p_240580_0_, "testClassName"), IntegerArgumentType.getInteger(p_240580_0_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_240579_0_) -> {
         return runAllTestsInClass(p_240579_0_.getSource(), TestTypeArgument.getTestClassName(p_240579_0_, "testClassName"), IntegerArgumentType.getInteger(p_240579_0_, "rotationSteps"), IntegerArgumentType.getInteger(p_240579_0_, "testsPerRow"));
      })))).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_240569_0_) -> {
         return runAllTests(p_240569_0_.getSource(), IntegerArgumentType.getInteger(p_240569_0_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_218527_0_) -> {
         return runAllTests(p_218527_0_.getSource(), IntegerArgumentType.getInteger(p_218527_0_, "rotationSteps"), IntegerArgumentType.getInteger(p_218527_0_, "testsPerRow"));
      })))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes((p_229642_0_) -> {
         return exportTestStructure(p_229642_0_.getSource(), StringArgumentType.getString(p_229642_0_, "testName"));
      }))).then(Commands.literal("exportthis").executes((p_240587_0_) -> {
         return exportNearestTestStructure(p_240587_0_.getSource());
      })).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes((p_229641_0_) -> {
         return importTestStructure(p_229641_0_.getSource(), StringArgumentType.getString(p_229641_0_, "testName"));
      }))).then(Commands.literal("pos").executes((p_229640_0_) -> {
         return showPos(p_229640_0_.getSource(), "pos");
      }).then(Commands.argument("var", StringArgumentType.word()).executes((p_229639_0_) -> {
         return showPos(p_229639_0_.getSource(), StringArgumentType.getString(p_229639_0_, "var"));
      }))).then(Commands.literal("create").then(Commands.argument("testName", StringArgumentType.word()).executes((p_229637_0_) -> {
         return createNewStructure(p_229637_0_.getSource(), StringArgumentType.getString(p_229637_0_, "testName"), 5, 5, 5);
      }).then(Commands.argument("width", IntegerArgumentType.integer()).executes((p_229635_0_) -> {
         return createNewStructure(p_229635_0_.getSource(), StringArgumentType.getString(p_229635_0_, "testName"), IntegerArgumentType.getInteger(p_229635_0_, "width"), IntegerArgumentType.getInteger(p_229635_0_, "width"), IntegerArgumentType.getInteger(p_229635_0_, "width"));
      }).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((p_229632_0_) -> {
         return createNewStructure(p_229632_0_.getSource(), StringArgumentType.getString(p_229632_0_, "testName"), IntegerArgumentType.getInteger(p_229632_0_, "width"), IntegerArgumentType.getInteger(p_229632_0_, "height"), IntegerArgumentType.getInteger(p_229632_0_, "depth"));
      })))))).then(Commands.literal("clearall").executes((p_229628_0_) -> {
         return clearAllTests(p_229628_0_.getSource(), 200);
      }).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((p_229614_0_) -> {
         return clearAllTests(p_229614_0_.getSource(), IntegerArgumentType.getInteger(p_229614_0_, "radius"));
      }))));
   }

   private static int createNewStructure(CommandSource p_229618_0_, String p_229618_1_, int p_229618_2_, int p_229618_3_, int p_229618_4_) {
      if (p_229618_2_ <= 48 && p_229618_3_ <= 48 && p_229618_4_ <= 48) {
         ServerWorld serverworld = p_229618_0_.getLevel();
         BlockPos blockpos = new BlockPos(p_229618_0_.getPosition());
         BlockPos blockpos1 = new BlockPos(blockpos.getX(), p_229618_0_.getLevel().getHeightmapPos(Heightmap.Type.WORLD_SURFACE, blockpos).getY(), blockpos.getZ() + 3);
         StructureHelper.createNewEmptyStructureBlock(p_229618_1_.toLowerCase(), blockpos1, new BlockPos(p_229618_2_, p_229618_3_, p_229618_4_), Rotation.NONE, serverworld);

         for(int i = 0; i < p_229618_2_; ++i) {
            for(int j = 0; j < p_229618_4_; ++j) {
               BlockPos blockpos2 = new BlockPos(blockpos1.getX() + i, blockpos1.getY() + 1, blockpos1.getZ() + j);
               Block block = Blocks.POLISHED_ANDESITE;
               BlockStateInput blockstateinput = new BlockStateInput(block.defaultBlockState(), Collections.EMPTY_SET, (CompoundNBT)null);
               blockstateinput.place(serverworld, blockpos2, 2);
            }
         }

         StructureHelper.addCommandBlockAndButtonToStartTest(blockpos1, new BlockPos(1, 0, -1), Rotation.NONE, serverworld);
         return 0;
      } else {
         throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
      }
   }

   private static int showPos(CommandSource p_229617_0_, String p_229617_1_) throws CommandSyntaxException {
      BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_229617_0_.getPlayerOrException().pick(10.0D, 1.0F, false);
      BlockPos blockpos = blockraytraceresult.getBlockPos();
      ServerWorld serverworld = p_229617_0_.getLevel();
      Optional<BlockPos> optional = StructureHelper.findStructureBlockContainingPos(blockpos, 15, serverworld);
      if (!optional.isPresent()) {
         optional = StructureHelper.findStructureBlockContainingPos(blockpos, 200, serverworld);
      }

      if (!optional.isPresent()) {
         p_229617_0_.sendFailure(new StringTextComponent("Can't find a structure block that contains the targeted pos " + blockpos));
         return 0;
      } else {
         StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)serverworld.getBlockEntity(optional.get());
         BlockPos blockpos1 = blockpos.subtract(optional.get());
         String s = blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ();
         String s1 = structureblocktileentity.getStructurePath();
         ITextComponent itextcomponent = (new StringTextComponent(s)).setStyle(Style.EMPTY.withBold(true).withColor(TextFormatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + p_229617_1_ + " = new BlockPos(" + s + ");")));
         p_229617_0_.sendSuccess((new StringTextComponent("Position relative to " + s1 + ": ")).append(itextcomponent), false);
         DebugPacketSender.sendGameTestAddMarker(serverworld, new BlockPos(blockpos), s, -2147418368, 10000);
         return 1;
      }
   }

   private static int runNearbyTest(CommandSource p_229615_0_) {
      BlockPos blockpos = new BlockPos(p_229615_0_.getPosition());
      ServerWorld serverworld = p_229615_0_.getLevel();
      BlockPos blockpos1 = StructureHelper.findNearestStructureBlock(blockpos, 15, serverworld);
      if (blockpos1 == null) {
         say(serverworld, "Couldn't find any structure block within 15 radius", TextFormatting.RED);
         return 0;
      } else {
         TestUtils.clearMarkers(serverworld);
         runTest(serverworld, blockpos1, (TestResultList)null);
         return 1;
      }
   }

   private static int runAllNearbyTests(CommandSource p_229629_0_) {
      BlockPos blockpos = new BlockPos(p_229629_0_.getPosition());
      ServerWorld serverworld = p_229629_0_.getLevel();
      Collection<BlockPos> collection = StructureHelper.findStructureBlocks(blockpos, 200, serverworld);
      if (collection.isEmpty()) {
         say(serverworld, "Couldn't find any structure blocks within 200 block radius", TextFormatting.RED);
         return 1;
      } else {
         TestUtils.clearMarkers(serverworld);
         say(p_229629_0_, "Running " + collection.size() + " tests...");
         TestResultList testresultlist = new TestResultList();
         collection.forEach((p_229626_2_) -> {
            runTest(serverworld, p_229626_2_, testresultlist);
         });
         return 1;
      }
   }

   private static void runTest(ServerWorld p_229623_0_, BlockPos p_229623_1_, @Nullable TestResultList p_229623_2_) {
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229623_0_.getBlockEntity(p_229623_1_);
      String s = structureblocktileentity.getStructurePath();
      TestFunctionInfo testfunctioninfo = TestRegistry.getTestFunction(s);
      TestTracker testtracker = new TestTracker(testfunctioninfo, structureblocktileentity.getRotation(), p_229623_0_);
      if (p_229623_2_ != null) {
         p_229623_2_.addTestToTrack(testtracker);
         testtracker.addListener(new TestCommand.Callback(p_229623_0_, p_229623_2_));
      }

      runTestPreparation(testfunctioninfo, p_229623_0_);
      AxisAlignedBB axisalignedbb = StructureHelper.getStructureBounds(structureblocktileentity);
      BlockPos blockpos = new BlockPos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
      TestUtils.runTest(testtracker, blockpos, TestCollection.singleton);
   }

   private static void showTestSummaryIfAllDone(ServerWorld p_229631_0_, TestResultList p_229631_1_) {
      if (p_229631_1_.isDone()) {
         say(p_229631_0_, "GameTest done! " + p_229631_1_.getTotalCount() + " tests were run", TextFormatting.WHITE);
         if (p_229631_1_.hasFailedRequired()) {
            say(p_229631_0_, "" + p_229631_1_.getFailedRequiredCount() + " required tests failed :(", TextFormatting.RED);
         } else {
            say(p_229631_0_, "All required tests passed :)", TextFormatting.GREEN);
         }

         if (p_229631_1_.hasFailedOptional()) {
            say(p_229631_0_, "" + p_229631_1_.getFailedOptionalCount() + " optional tests failed", TextFormatting.GRAY);
         }
      }

   }

   private static int clearAllTests(CommandSource p_229616_0_, int p_229616_1_) {
      ServerWorld serverworld = p_229616_0_.getLevel();
      TestUtils.clearMarkers(serverworld);
      BlockPos blockpos = new BlockPos(p_229616_0_.getPosition().x, (double)p_229616_0_.getLevel().getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(p_229616_0_.getPosition())).getY(), p_229616_0_.getPosition().z);
      TestUtils.clearAllTests(serverworld, blockpos, TestCollection.singleton, MathHelper.clamp(p_229616_1_, 0, 1024));
      return 1;
   }

   private static int runTest(CommandSource p_229620_0_, TestFunctionInfo p_229620_1_, int p_229620_2_) {
      ServerWorld serverworld = p_229620_0_.getLevel();
      BlockPos blockpos = new BlockPos(p_229620_0_.getPosition());
      int i = p_229620_0_.getLevel().getHeightmapPos(Heightmap.Type.WORLD_SURFACE, blockpos).getY();
      BlockPos blockpos1 = new BlockPos(blockpos.getX(), i, blockpos.getZ() + 3);
      TestUtils.clearMarkers(serverworld);
      runTestPreparation(p_229620_1_, serverworld);
      Rotation rotation = StructureHelper.getRotationForRotationSteps(p_229620_2_);
      TestTracker testtracker = new TestTracker(p_229620_1_, rotation, serverworld);
      TestUtils.runTest(testtracker, blockpos1, TestCollection.singleton);
      return 1;
   }

   private static void runTestPreparation(TestFunctionInfo p_229622_0_, ServerWorld p_229622_1_) {
      Consumer<ServerWorld> consumer = TestRegistry.getBeforeBatchFunction(p_229622_0_.getBatchName());
      if (consumer != null) {
         consumer.accept(p_229622_1_);
      }

   }

   private static int runAllTests(CommandSource p_229633_0_, int p_229633_1_, int p_229633_2_) {
      TestUtils.clearMarkers(p_229633_0_.getLevel());
      Collection<TestFunctionInfo> collection = TestRegistry.getAllTestFunctions();
      say(p_229633_0_, "Running all " + collection.size() + " tests...");
      TestRegistry.forgetFailedTests();
      runTests(p_229633_0_, collection, p_229633_1_, p_229633_2_);
      return 1;
   }

   private static int runAllTestsInClass(CommandSource p_229630_0_, String p_229630_1_, int p_229630_2_, int p_229630_3_) {
      Collection<TestFunctionInfo> collection = TestRegistry.getTestFunctionsForClassName(p_229630_1_);
      TestUtils.clearMarkers(p_229630_0_.getLevel());
      say(p_229630_0_, "Running " + collection.size() + " tests from " + p_229630_1_ + "...");
      TestRegistry.forgetFailedTests();
      runTests(p_229630_0_, collection, p_229630_2_, p_229630_3_);
      return 1;
   }

   private static int runLastFailedTests(CommandSource p_240574_0_, boolean p_240574_1_, int p_240574_2_, int p_240574_3_) {
      Collection<TestFunctionInfo> collection;
      if (p_240574_1_) {
         collection = TestRegistry.getLastFailedTests().stream().filter(TestFunctionInfo::isRequired).collect(Collectors.toList());
      } else {
         collection = TestRegistry.getLastFailedTests();
      }

      if (collection.isEmpty()) {
         say(p_240574_0_, "No failed tests to rerun");
         return 0;
      } else {
         TestUtils.clearMarkers(p_240574_0_.getLevel());
         say(p_240574_0_, "Rerunning " + collection.size() + " failed tests (" + (p_240574_1_ ? "only required tests" : "including optional tests") + ")");
         runTests(p_240574_0_, collection, p_240574_2_, p_240574_3_);
         return 1;
      }
   }

   private static void runTests(CommandSource p_229619_0_, Collection<TestFunctionInfo> p_229619_1_, int p_229619_2_, int p_229619_3_) {
      BlockPos blockpos = new BlockPos(p_229619_0_.getPosition());
      BlockPos blockpos1 = new BlockPos(blockpos.getX(), p_229619_0_.getLevel().getHeightmapPos(Heightmap.Type.WORLD_SURFACE, blockpos).getY(), blockpos.getZ() + 3);
      ServerWorld serverworld = p_229619_0_.getLevel();
      Rotation rotation = StructureHelper.getRotationForRotationSteps(p_229619_2_);
      Collection<TestTracker> collection = TestUtils.runTests(p_229619_1_, blockpos1, rotation, serverworld, TestCollection.singleton, p_229619_3_);
      TestResultList testresultlist = new TestResultList(collection);
      testresultlist.addListener(new TestCommand.Callback(serverworld, testresultlist));
      testresultlist.addFailureListener((p_240576_0_) -> {
         TestRegistry.rememberFailedTest(p_240576_0_.getTestFunction());
      });
   }

   private static void say(CommandSource p_229634_0_, String p_229634_1_) {
      p_229634_0_.sendSuccess(new StringTextComponent(p_229634_1_), false);
   }

   private static int exportNearestTestStructure(CommandSource p_240581_0_) {
      BlockPos blockpos = new BlockPos(p_240581_0_.getPosition());
      ServerWorld serverworld = p_240581_0_.getLevel();
      BlockPos blockpos1 = StructureHelper.findNearestStructureBlock(blockpos, 15, serverworld);
      if (blockpos1 == null) {
         say(serverworld, "Couldn't find any structure block within 15 radius", TextFormatting.RED);
         return 0;
      } else {
         StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)serverworld.getBlockEntity(blockpos1);
         String s = structureblocktileentity.getStructurePath();
         return exportTestStructure(p_240581_0_, s);
      }
   }

   private static int exportTestStructure(CommandSource p_229636_0_, String p_229636_1_) {
      Path path = Paths.get(StructureHelper.testStructuresDir);
      ResourceLocation resourcelocation = new ResourceLocation("minecraft", p_229636_1_);
      Path path1 = p_229636_0_.getLevel().getStructureManager().createPathToStructure(resourcelocation, ".nbt");
      Path path2 = NBTToSNBTConverter.convertStructure(path1, p_229636_1_, path);
      if (path2 == null) {
         say(p_229636_0_, "Failed to export " + path1);
         return 1;
      } else {
         try {
            Files.createDirectories(path2.getParent());
         } catch (IOException ioexception) {
            say(p_229636_0_, "Could not create folder " + path2.getParent());
            ioexception.printStackTrace();
            return 1;
         }

         say(p_229636_0_, "Exported " + p_229636_1_ + " to " + path2.toAbsolutePath());
         return 0;
      }
   }

   private static int importTestStructure(CommandSource p_229638_0_, String p_229638_1_) {
      Path path = Paths.get(StructureHelper.testStructuresDir, p_229638_1_ + ".snbt");
      ResourceLocation resourcelocation = new ResourceLocation("minecraft", p_229638_1_);
      Path path1 = p_229638_0_.getLevel().getStructureManager().createPathToStructure(resourcelocation, ".nbt");

      try {
         BufferedReader bufferedreader = Files.newBufferedReader(path);
         String s = IOUtils.toString((Reader)bufferedreader);
         Files.createDirectories(path1.getParent());

         try (OutputStream outputstream = Files.newOutputStream(path1)) {
            CompressedStreamTools.writeCompressed(JsonToNBT.parseTag(s), outputstream);
         }

         say(p_229638_0_, "Imported to " + path1.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException ioexception) {
         System.err.println("Failed to load structure " + p_229638_1_);
         ioexception.printStackTrace();
         return 1;
      }
   }

   private static void say(ServerWorld p_229624_0_, String p_229624_1_, TextFormatting p_229624_2_) {
      p_229624_0_.getPlayers((p_229627_0_) -> {
         return true;
      }).forEach((p_229621_2_) -> {
         p_229621_2_.sendMessage(new StringTextComponent(p_229624_2_ + p_229624_1_), Util.NIL_UUID);
      });
   }

   static class Callback implements ITestCallback {
      private final ServerWorld level;
      private final TestResultList tracker;

      public Callback(ServerWorld p_i226073_1_, TestResultList p_i226073_2_) {
         this.level = p_i226073_1_;
         this.tracker = p_i226073_2_;
      }

      public void testStructureLoaded(TestTracker p_225644_1_) {
      }

      public void testFailed(TestTracker p_225645_1_) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }
   }
}
