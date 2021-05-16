package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class ScheduleDuties {
   private final List<DutyTime> keyframes = Lists.newArrayList();
   private int previousIndex;

   public ScheduleDuties addKeyframe(int p_221394_1_, float p_221394_2_) {
      this.keyframes.add(new DutyTime(p_221394_1_, p_221394_2_));
      this.sortAndDeduplicateKeyframes();
      return this;
   }

   private void sortAndDeduplicateKeyframes() {
      Int2ObjectSortedMap<DutyTime> int2objectsortedmap = new Int2ObjectAVLTreeMap<>();
      this.keyframes.forEach((p_221393_1_) -> {
         DutyTime dutytime = int2objectsortedmap.put(p_221393_1_.getTimeStamp(), p_221393_1_);
      });
      this.keyframes.clear();
      this.keyframes.addAll(int2objectsortedmap.values());
      this.previousIndex = 0;
   }

   public float getValueAt(int p_221392_1_) {
      if (this.keyframes.size() <= 0) {
         return 0.0F;
      } else {
         DutyTime dutytime = this.keyframes.get(this.previousIndex);
         DutyTime dutytime1 = this.keyframes.get(this.keyframes.size() - 1);
         boolean flag = p_221392_1_ < dutytime.getTimeStamp();
         int i = flag ? 0 : this.previousIndex;
         float f = flag ? dutytime1.getValue() : dutytime.getValue();

         for(int j = i; j < this.keyframes.size(); ++j) {
            DutyTime dutytime2 = this.keyframes.get(j);
            if (dutytime2.getTimeStamp() > p_221392_1_) {
               break;
            }

            this.previousIndex = j;
            f = dutytime2.getValue();
         }

         return f;
      }
   }
}
