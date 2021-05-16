package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStructureReader {
   @Nullable
   StructureStart<?> getStartForFeature(Structure<?> p_230342_1_);

   void setStartForFeature(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_);

   LongSet getReferencesForFeature(Structure<?> p_230346_1_);

   void addReferenceForFeature(Structure<?> p_230343_1_, long p_230343_2_);

   Map<Structure<?>, LongSet> getAllReferences();

   void setAllReferences(Map<Structure<?>, LongSet> p_201606_1_);
}
