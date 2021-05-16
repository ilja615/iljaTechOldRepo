package com.mojang.blaze3d;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.IRenderCall;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Empty3i {
   private final List<ConcurrentLinkedQueue<IRenderCall>> renderCalls = ImmutableList.of(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
   private volatile int recordingBuffer;
   private volatile int processedBuffer;
   private volatile int renderingBuffer;

   public Empty3i() {
      this.recordingBuffer = this.processedBuffer = this.renderingBuffer + 1;
   }
}
