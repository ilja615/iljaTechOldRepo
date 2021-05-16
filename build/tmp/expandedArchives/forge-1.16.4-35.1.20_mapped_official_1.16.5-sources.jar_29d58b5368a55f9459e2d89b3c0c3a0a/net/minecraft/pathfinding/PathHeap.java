package net.minecraft.pathfinding;

public class PathHeap {
   private PathPoint[] heap = new PathPoint[128];
   private int size;

   public PathPoint insert(PathPoint p_75849_1_) {
      if (p_75849_1_.heapIdx >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.size == this.heap.length) {
            PathPoint[] apathpoint = new PathPoint[this.size << 1];
            System.arraycopy(this.heap, 0, apathpoint, 0, this.size);
            this.heap = apathpoint;
         }

         this.heap[this.size] = p_75849_1_;
         p_75849_1_.heapIdx = this.size;
         this.upHeap(this.size++);
         return p_75849_1_;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public PathPoint pop() {
      PathPoint pathpoint = this.heap[0];
      this.heap[0] = this.heap[--this.size];
      this.heap[this.size] = null;
      if (this.size > 0) {
         this.downHeap(0);
      }

      pathpoint.heapIdx = -1;
      return pathpoint;
   }

   public void changeCost(PathPoint p_75850_1_, float p_75850_2_) {
      float f = p_75850_1_.f;
      p_75850_1_.f = p_75850_2_;
      if (p_75850_2_ < f) {
         this.upHeap(p_75850_1_.heapIdx);
      } else {
         this.downHeap(p_75850_1_.heapIdx);
      }

   }

   private void upHeap(int p_75847_1_) {
      PathPoint pathpoint = this.heap[p_75847_1_];

      int i;
      for(float f = pathpoint.f; p_75847_1_ > 0; p_75847_1_ = i) {
         i = p_75847_1_ - 1 >> 1;
         PathPoint pathpoint1 = this.heap[i];
         if (!(f < pathpoint1.f)) {
            break;
         }

         this.heap[p_75847_1_] = pathpoint1;
         pathpoint1.heapIdx = p_75847_1_;
      }

      this.heap[p_75847_1_] = pathpoint;
      pathpoint.heapIdx = p_75847_1_;
   }

   private void downHeap(int p_75846_1_) {
      PathPoint pathpoint = this.heap[p_75846_1_];
      float f = pathpoint.f;

      while(true) {
         int i = 1 + (p_75846_1_ << 1);
         int j = i + 1;
         if (i >= this.size) {
            break;
         }

         PathPoint pathpoint1 = this.heap[i];
         float f1 = pathpoint1.f;
         PathPoint pathpoint2;
         float f2;
         if (j >= this.size) {
            pathpoint2 = null;
            f2 = Float.POSITIVE_INFINITY;
         } else {
            pathpoint2 = this.heap[j];
            f2 = pathpoint2.f;
         }

         if (f1 < f2) {
            if (!(f1 < f)) {
               break;
            }

            this.heap[p_75846_1_] = pathpoint1;
            pathpoint1.heapIdx = p_75846_1_;
            p_75846_1_ = i;
         } else {
            if (!(f2 < f)) {
               break;
            }

            this.heap[p_75846_1_] = pathpoint2;
            pathpoint2.heapIdx = p_75846_1_;
            p_75846_1_ = j;
         }
      }

      this.heap[p_75846_1_] = pathpoint;
      pathpoint.heapIdx = p_75846_1_;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }
}
