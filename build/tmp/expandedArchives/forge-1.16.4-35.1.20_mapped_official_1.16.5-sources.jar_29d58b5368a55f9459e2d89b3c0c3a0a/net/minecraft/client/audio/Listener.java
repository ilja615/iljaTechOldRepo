package net.minecraft.client.audio;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class Listener {
   private float gain = 1.0F;
   private Vector3d position = Vector3d.ZERO;

   public void setListenerPosition(Vector3d p_216465_1_) {
      this.position = p_216465_1_;
      AL10.alListener3f(4100, (float)p_216465_1_.x, (float)p_216465_1_.y, (float)p_216465_1_.z);
   }

   public Vector3d getListenerPosition() {
      return this.position;
   }

   public void setListenerOrientation(Vector3f p_227580_1_, Vector3f p_227580_2_) {
      AL10.alListenerfv(4111, new float[]{p_227580_1_.x(), p_227580_1_.y(), p_227580_1_.z(), p_227580_2_.x(), p_227580_2_.y(), p_227580_2_.z()});
   }

   public void setGain(float p_216466_1_) {
      AL10.alListenerf(4106, p_216466_1_);
      this.gain = p_216466_1_;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vector3d.ZERO);
      this.setListenerOrientation(Vector3f.ZN, Vector3f.YP);
   }
}
