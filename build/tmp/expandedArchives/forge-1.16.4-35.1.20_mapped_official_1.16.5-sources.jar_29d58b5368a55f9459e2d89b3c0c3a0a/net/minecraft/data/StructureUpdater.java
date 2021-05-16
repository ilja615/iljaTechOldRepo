package net.minecraft.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureUpdater implements SNBTToNBTConverter.ITransformer {
   private static final Logger LOGGER = LogManager.getLogger();

   public CompoundNBT apply(String p_225371_1_, CompoundNBT p_225371_2_) {
      return p_225371_1_.startsWith("data/minecraft/structures/") ? updateStructure(p_225371_1_, patchVersion(p_225371_2_)) : p_225371_2_;
   }

   private static CompoundNBT patchVersion(CompoundNBT p_225372_0_) {
      if (!p_225372_0_.contains("DataVersion", 99)) {
         p_225372_0_.putInt("DataVersion", 500);
      }

      return p_225372_0_;
   }

   private static CompoundNBT updateStructure(String p_240519_0_, CompoundNBT p_240519_1_) {
      Template template = new Template();
      int i = p_240519_1_.getInt("DataVersion");
      int j = 2532;
      if (i < 2532) {
         LOGGER.warn("SNBT Too old, do not forget to update: " + i + " < " + 2532 + ": " + p_240519_0_);
      }

      CompoundNBT compoundnbt = NBTUtil.update(DataFixesManager.getDataFixer(), DefaultTypeReferences.STRUCTURE, p_240519_1_, i);
      template.load(compoundnbt);
      return template.save(new CompoundNBT());
   }
}
