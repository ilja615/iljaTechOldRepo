package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementTreeNode {
   private final Advancement advancement;
   private final AdvancementTreeNode parent;
   private final AdvancementTreeNode previousSibling;
   private final int childIndex;
   private final List<AdvancementTreeNode> children = Lists.newArrayList();
   private AdvancementTreeNode ancestor;
   private AdvancementTreeNode thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public AdvancementTreeNode(Advancement p_i47466_1_, @Nullable AdvancementTreeNode p_i47466_2_, @Nullable AdvancementTreeNode p_i47466_3_, int p_i47466_4_, int p_i47466_5_) {
      if (p_i47466_1_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.advancement = p_i47466_1_;
         this.parent = p_i47466_2_;
         this.previousSibling = p_i47466_3_;
         this.childIndex = p_i47466_4_;
         this.ancestor = this;
         this.x = p_i47466_5_;
         this.y = -1.0F;
         AdvancementTreeNode advancementtreenode = null;

         for(Advancement advancement : p_i47466_1_.getChildren()) {
            advancementtreenode = this.addChild(advancement, advancementtreenode);
         }

      }
   }

   @Nullable
   private AdvancementTreeNode addChild(Advancement p_192322_1_, @Nullable AdvancementTreeNode p_192322_2_) {
      if (p_192322_1_.getDisplay() != null) {
         p_192322_2_ = new AdvancementTreeNode(p_192322_1_, this, p_192322_2_, this.children.size() + 1, this.x + 1);
         this.children.add(p_192322_2_);
      } else {
         for(Advancement advancement : p_192322_1_.getChildren()) {
            p_192322_2_ = this.addChild(advancement, p_192322_2_);
         }
      }

      return p_192322_2_;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         AdvancementTreeNode advancementtreenode = null;

         for(AdvancementTreeNode advancementtreenode1 : this.children) {
            advancementtreenode1.firstWalk();
            advancementtreenode = advancementtreenode1.apportion(advancementtreenode == null ? advancementtreenode1 : advancementtreenode);
         }

         this.executeShifts();
         float f = ((this.children.get(0)).y + (this.children.get(this.children.size() - 1)).y) / 2.0F;
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
            this.mod = this.y - f;
         } else {
            this.y = f;
         }

      }
   }

   private float secondWalk(float p_192319_1_, int p_192319_2_, float p_192319_3_) {
      this.y += p_192319_1_;
      this.x = p_192319_2_;
      if (this.y < p_192319_3_) {
         p_192319_3_ = this.y;
      }

      for(AdvancementTreeNode advancementtreenode : this.children) {
         p_192319_3_ = advancementtreenode.secondWalk(p_192319_1_ + this.mod, p_192319_2_ + 1, p_192319_3_);
      }

      return p_192319_3_;
   }

   private void thirdWalk(float p_192318_1_) {
      this.y += p_192318_1_;

      for(AdvancementTreeNode advancementtreenode : this.children) {
         advancementtreenode.thirdWalk(p_192318_1_);
      }

   }

   private void executeShifts() {
      float f = 0.0F;
      float f1 = 0.0F;

      for(int i = this.children.size() - 1; i >= 0; --i) {
         AdvancementTreeNode advancementtreenode = this.children.get(i);
         advancementtreenode.y += f;
         advancementtreenode.mod += f;
         f1 += advancementtreenode.change;
         f += advancementtreenode.shift + f1;
      }

   }

   @Nullable
   private AdvancementTreeNode previousOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(0) : null;
      }
   }

   @Nullable
   private AdvancementTreeNode nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
      }
   }

   private AdvancementTreeNode apportion(AdvancementTreeNode p_192324_1_) {
      if (this.previousSibling == null) {
         return p_192324_1_;
      } else {
         AdvancementTreeNode advancementtreenode = this;
         AdvancementTreeNode advancementtreenode1 = this;
         AdvancementTreeNode advancementtreenode2 = this.previousSibling;
         AdvancementTreeNode advancementtreenode3 = this.parent.children.get(0);
         float f = this.mod;
         float f1 = this.mod;
         float f2 = advancementtreenode2.mod;

         float f3;
         for(f3 = advancementtreenode3.mod; advancementtreenode2.nextOrThread() != null && advancementtreenode.previousOrThread() != null; f1 += advancementtreenode1.mod) {
            advancementtreenode2 = advancementtreenode2.nextOrThread();
            advancementtreenode = advancementtreenode.previousOrThread();
            advancementtreenode3 = advancementtreenode3.previousOrThread();
            advancementtreenode1 = advancementtreenode1.nextOrThread();
            advancementtreenode1.ancestor = this;
            float f4 = advancementtreenode2.y + f2 - (advancementtreenode.y + f) + 1.0F;
            if (f4 > 0.0F) {
               advancementtreenode2.getAncestor(this, p_192324_1_).moveSubtree(this, f4);
               f += f4;
               f1 += f4;
            }

            f2 += advancementtreenode2.mod;
            f += advancementtreenode.mod;
            f3 += advancementtreenode3.mod;
         }

         if (advancementtreenode2.nextOrThread() != null && advancementtreenode1.nextOrThread() == null) {
            advancementtreenode1.thread = advancementtreenode2.nextOrThread();
            advancementtreenode1.mod += f2 - f1;
         } else {
            if (advancementtreenode.previousOrThread() != null && advancementtreenode3.previousOrThread() == null) {
               advancementtreenode3.thread = advancementtreenode.previousOrThread();
               advancementtreenode3.mod += f - f3;
            }

            p_192324_1_ = this;
         }

         return p_192324_1_;
      }
   }

   private void moveSubtree(AdvancementTreeNode p_192316_1_, float p_192316_2_) {
      float f = (float)(p_192316_1_.childIndex - this.childIndex);
      if (f != 0.0F) {
         p_192316_1_.change -= p_192316_2_ / f;
         this.change += p_192316_2_ / f;
      }

      p_192316_1_.shift += p_192316_2_;
      p_192316_1_.y += p_192316_2_;
      p_192316_1_.mod += p_192316_2_;
   }

   private AdvancementTreeNode getAncestor(AdvancementTreeNode p_192326_1_, AdvancementTreeNode p_192326_2_) {
      return this.ancestor != null && p_192326_1_.parent.children.contains(this.ancestor) ? this.ancestor : p_192326_2_;
   }

   private void finalizePosition() {
      if (this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setLocation((float)this.x, this.y);
      }

      if (!this.children.isEmpty()) {
         for(AdvancementTreeNode advancementtreenode : this.children) {
            advancementtreenode.finalizePosition();
         }
      }

   }

   public static void run(Advancement p_192323_0_) {
      if (p_192323_0_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         AdvancementTreeNode advancementtreenode = new AdvancementTreeNode(p_192323_0_, (AdvancementTreeNode)null, (AdvancementTreeNode)null, 1, 0);
         advancementtreenode.firstWalk();
         float f = advancementtreenode.secondWalk(0.0F, 0, advancementtreenode.y);
         if (f < 0.0F) {
            advancementtreenode.thirdWalk(-f);
         }

         advancementtreenode.finalizePosition();
      }
   }
}
