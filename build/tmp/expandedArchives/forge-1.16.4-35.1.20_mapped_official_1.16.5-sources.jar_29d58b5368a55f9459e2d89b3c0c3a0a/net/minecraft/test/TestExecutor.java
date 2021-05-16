package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestExecutor {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockPos firstTestNorthWestCorner;
   private final ServerWorld level;
   private final TestCollection testTicker;
   private final int testsPerRow;
   private final List<TestTracker> allTestInfos = Lists.newArrayList();
   private final Map<TestTracker, BlockPos> northWestCorners = Maps.newHashMap();
   private final List<Pair<TestBatch, Collection<TestTracker>>> batches = Lists.newArrayList();
   private TestResultList currentBatchTracker;
   private int currentBatchIndex = 0;
   private BlockPos.Mutable nextTestNorthWestCorner;

   public TestExecutor(Collection<TestBatch> p_i232555_1_, BlockPos p_i232555_2_, Rotation p_i232555_3_, ServerWorld p_i232555_4_, TestCollection p_i232555_5_, int p_i232555_6_) {
      this.nextTestNorthWestCorner = p_i232555_2_.mutable();
      this.firstTestNorthWestCorner = p_i232555_2_;
      this.level = p_i232555_4_;
      this.testTicker = p_i232555_5_;
      this.testsPerRow = p_i232555_6_;
      p_i232555_1_.forEach((p_240539_3_) -> {
         Collection<TestTracker> collection = Lists.newArrayList();

         for(TestFunctionInfo testfunctioninfo : p_240539_3_.getTestFunctions()) {
            TestTracker testtracker = new TestTracker(testfunctioninfo, p_i232555_3_, p_i232555_4_);
            collection.add(testtracker);
            this.allTestInfos.add(testtracker);
         }

         this.batches.add(Pair.of(p_240539_3_, collection));
      });
   }

   public List<TestTracker> getTestInfos() {
      return this.allTestInfos;
   }

   public void start() {
      this.runBatch(0);
   }

   private void runBatch(int p_229477_1_) {
      this.currentBatchIndex = p_229477_1_;
      this.currentBatchTracker = new TestResultList();
      if (p_229477_1_ < this.batches.size()) {
         Pair<TestBatch, Collection<TestTracker>> pair = this.batches.get(this.currentBatchIndex);
         TestBatch testbatch = pair.getFirst();
         Collection<TestTracker> collection = pair.getSecond();
         this.createStructuresForBatch(collection);
         testbatch.runBeforeBatchFunction(this.level);
         String s = testbatch.getName();
         LOGGER.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
         collection.forEach((p_229483_1_) -> {
            this.currentBatchTracker.addTestToTrack(p_229483_1_);
            this.currentBatchTracker.addListener(new ITestCallback() {
               public void testStructureLoaded(TestTracker p_225644_1_) {
               }

               public void testFailed(TestTracker p_225645_1_) {
                  TestExecutor.this.testCompleted(p_225645_1_);
               }
            });
            BlockPos blockpos = this.northWestCorners.get(p_229483_1_);
            TestUtils.runTest(p_229483_1_, blockpos, this.testTicker);
         });
      }
   }

   private void testCompleted(TestTracker p_229479_1_) {
      if (this.currentBatchTracker.isDone()) {
         this.runBatch(this.currentBatchIndex + 1);
      }

   }

   private void createStructuresForBatch(Collection<TestTracker> p_229480_1_) {
      int i = 0;
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.nextTestNorthWestCorner);

      for(TestTracker testtracker : p_229480_1_) {
         BlockPos blockpos = new BlockPos(this.nextTestNorthWestCorner);
         StructureBlockTileEntity structureblocktileentity = StructureHelper.spawnStructure(testtracker.getStructureName(), blockpos, testtracker.getRotation(), 2, this.level, true);
         AxisAlignedBB axisalignedbb1 = StructureHelper.getStructureBounds(structureblocktileentity);
         testtracker.setStructureBlockPos(structureblocktileentity.getBlockPos());
         this.northWestCorners.put(testtracker, new BlockPos(this.nextTestNorthWestCorner));
         axisalignedbb = axisalignedbb.minmax(axisalignedbb1);
         this.nextTestNorthWestCorner.move((int)axisalignedbb1.getXsize() + 5, 0, 0);
         if (i++ % this.testsPerRow == this.testsPerRow - 1) {
            this.nextTestNorthWestCorner.move(0, 0, (int)axisalignedbb.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            axisalignedbb = new AxisAlignedBB(this.nextTestNorthWestCorner);
         }
      }

   }
}
