package net.minecraft.test;

import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLogger implements ITestLogger {
   private static final Logger LOGGER = LogManager.getLogger();

   public void onTestFailed(TestTracker p_225646_1_) {
      if (p_225646_1_.isRequired()) {
         LOGGER.error(p_225646_1_.getTestName() + " failed! " + Util.describeError(p_225646_1_.getError()));
      } else {
         LOGGER.warn("(optional) " + p_225646_1_.getTestName() + " failed. " + Util.describeError(p_225646_1_.getError()));
      }

   }
}
