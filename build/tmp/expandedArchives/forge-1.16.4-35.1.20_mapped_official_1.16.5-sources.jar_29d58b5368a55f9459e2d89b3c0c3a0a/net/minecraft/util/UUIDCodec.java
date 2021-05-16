package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;

public final class UUIDCodec {
   public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap((p_239778_0_) -> {
      return Util.fixedSize(p_239778_0_, 4).map(UUIDCodec::uuidFromIntArray);
   }, (p_239780_0_) -> {
      return Arrays.stream(uuidToIntArray(p_239780_0_));
   });

   public static UUID uuidFromIntArray(int[] p_239779_0_) {
      return new UUID((long)p_239779_0_[0] << 32 | (long)p_239779_0_[1] & 4294967295L, (long)p_239779_0_[2] << 32 | (long)p_239779_0_[3] & 4294967295L);
   }

   public static int[] uuidToIntArray(UUID p_239777_0_) {
      long i = p_239777_0_.getMostSignificantBits();
      long j = p_239777_0_.getLeastSignificantBits();
      return leastMostToIntArray(i, j);
   }

   private static int[] leastMostToIntArray(long p_239776_0_, long p_239776_2_) {
      return new int[]{(int)(p_239776_0_ >> 32), (int)p_239776_0_, (int)(p_239776_2_ >> 32), (int)p_239776_2_};
   }
}
