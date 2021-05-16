package net.minecraft.world.gen.feature.template;

import java.util.List;

public class StructureProcessorList {
   private final List<StructureProcessor> list;

   public StructureProcessorList(List<StructureProcessor> p_i242038_1_) {
      this.list = p_i242038_1_;
   }

   public List<StructureProcessor> list() {
      return this.list;
   }

   public String toString() {
      return "ProcessorList[" + this.list + "]";
   }
}
