package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.util.SoundCategory;

public interface IEquipable {
   boolean isSaddleable();

   void equipSaddle(@Nullable SoundCategory p_230266_1_);

   boolean isSaddled();
}
