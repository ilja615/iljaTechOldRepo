package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public interface ILocationArgument {
   Vector3d getPosition(CommandSource p_197281_1_);

   Vector2f getRotation(CommandSource p_197282_1_);

   default BlockPos getBlockPos(CommandSource p_197280_1_) {
      return new BlockPos(this.getPosition(p_197280_1_));
   }

   boolean isXRelative();

   boolean isYRelative();

   boolean isZRelative();
}
