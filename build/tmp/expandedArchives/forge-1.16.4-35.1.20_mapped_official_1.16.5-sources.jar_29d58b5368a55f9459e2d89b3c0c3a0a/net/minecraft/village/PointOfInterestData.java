package net.minecraft.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestData {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Short2ObjectMap<PointOfInterest> records = new Short2ObjectOpenHashMap<>();
   private final Map<PointOfInterestType, Set<PointOfInterest>> byType = Maps.newHashMap();
   private final Runnable setDirty;
   private boolean isValid;

   public static Codec<PointOfInterestData> codec(Runnable p_234158_0_) {
      return RecordCodecBuilder.<PointOfInterestData>create((p_234159_1_) -> {
         return p_234159_1_.group(RecordCodecBuilder.point(p_234158_0_), Codec.BOOL.optionalFieldOf("Valid", Boolean.valueOf(false)).forGetter((p_234162_0_) -> {
            return p_234162_0_.isValid;
         }), PointOfInterest.codec(p_234158_0_).listOf().fieldOf("Records").forGetter((p_234154_0_) -> {
            return ImmutableList.copyOf(p_234154_0_.records.values());
         })).apply(p_234159_1_, PointOfInterestData::new);
      }).orElseGet(Util.prefix("Failed to read POI section: ", LOGGER::error), () -> {
         return new PointOfInterestData(p_234158_0_, false, ImmutableList.of());
      });
   }

   public PointOfInterestData(Runnable p_i50293_1_) {
      this(p_i50293_1_, true, ImmutableList.of());
   }

   private PointOfInterestData(Runnable p_i231555_1_, boolean p_i231555_2_, List<PointOfInterest> p_i231555_3_) {
      this.setDirty = p_i231555_1_;
      this.isValid = p_i231555_2_;
      p_i231555_3_.forEach(this::add);
   }

   public Stream<PointOfInterest> getRecords(Predicate<PointOfInterestType> p_218247_1_, PointOfInterestManager.Status p_218247_2_) {
      return this.byType.entrySet().stream().filter((p_234161_1_) -> {
         return p_218247_1_.test(p_234161_1_.getKey());
      }).flatMap((p_234160_0_) -> {
         return p_234160_0_.getValue().stream();
      }).filter(p_218247_2_.getTest());
   }

   public void add(BlockPos p_218243_1_, PointOfInterestType p_218243_2_) {
      if (this.add(new PointOfInterest(p_218243_1_, p_218243_2_, this.setDirty))) {
         LOGGER.debug("Added POI of type {} @ {}", () -> {
            return p_218243_2_;
         }, () -> {
            return p_218243_1_;
         });
         this.setDirty.run();
      }

   }

   private boolean add(PointOfInterest p_218254_1_) {
      BlockPos blockpos = p_218254_1_.getPos();
      PointOfInterestType pointofinteresttype = p_218254_1_.getPoiType();
      short short1 = SectionPos.sectionRelativePos(blockpos);
      PointOfInterest pointofinterest = this.records.get(short1);
      if (pointofinterest != null) {
         if (pointofinteresttype.equals(pointofinterest.getPoiType())) {
            return false;
         } else {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI data mismatch: already registered at " + blockpos));
         }
      } else {
         this.records.put(short1, p_218254_1_);
         this.byType.computeIfAbsent(pointofinteresttype, (p_234155_0_) -> {
            return Sets.newHashSet();
         }).add(p_218254_1_);
         return true;
      }
   }

   public void remove(BlockPos p_218248_1_) {
      PointOfInterest pointofinterest = this.records.remove(SectionPos.sectionRelativePos(p_218248_1_));
      if (pointofinterest == null) {
         LOGGER.error("POI data mismatch: never registered at " + p_218248_1_);
      } else {
         this.byType.get(pointofinterest.getPoiType()).remove(pointofinterest);
         LOGGER.debug("Removed POI of type {} @ {}", pointofinterest::getPoiType, pointofinterest::getPos);
         this.setDirty.run();
      }
   }

   public boolean release(BlockPos p_218251_1_) {
      PointOfInterest pointofinterest = this.records.get(SectionPos.sectionRelativePos(p_218251_1_));
      if (pointofinterest == null) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI never registered at " + p_218251_1_));
      } else {
         boolean flag = pointofinterest.releaseTicket();
         this.setDirty.run();
         return flag;
      }
   }

   public boolean exists(BlockPos p_218245_1_, Predicate<PointOfInterestType> p_218245_2_) {
      short short1 = SectionPos.sectionRelativePos(p_218245_1_);
      PointOfInterest pointofinterest = this.records.get(short1);
      return pointofinterest != null && p_218245_2_.test(pointofinterest.getPoiType());
   }

   public Optional<PointOfInterestType> getType(BlockPos p_218244_1_) {
      short short1 = SectionPos.sectionRelativePos(p_218244_1_);
      PointOfInterest pointofinterest = this.records.get(short1);
      return pointofinterest != null ? Optional.of(pointofinterest.getPoiType()) : Optional.empty();
   }

   public void refresh(Consumer<BiConsumer<BlockPos, PointOfInterestType>> p_218240_1_) {
      if (!this.isValid) {
         Short2ObjectMap<PointOfInterest> short2objectmap = new Short2ObjectOpenHashMap<>(this.records);
         this.clear();
         p_218240_1_.accept((p_234157_2_, p_234157_3_) -> {
            short short1 = SectionPos.sectionRelativePos(p_234157_2_);
            PointOfInterest pointofinterest = short2objectmap.computeIfAbsent(short1, (p_234156_3_) -> {
               return new PointOfInterest(p_234157_2_, p_234157_3_, this.setDirty);
            });
            this.add(pointofinterest);
         });
         this.isValid = true;
         this.setDirty.run();
      }

   }

   private void clear() {
      this.records.clear();
      this.byType.clear();
   }

   boolean isValid() {
      return this.isValid;
   }
}
