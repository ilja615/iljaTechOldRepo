package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UVTransformationUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EnumMap<Direction, TransformationMatrix> vanillaUvTransformLocalToGlobal = Util.make(Maps.newEnumMap(Direction.class), (p_229382_0_) -> {
      p_229382_0_.put(Direction.SOUTH, TransformationMatrix.identity());
      p_229382_0_.put(Direction.EAST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.WEST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.NORTH, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.UP, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.DOWN, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
   });
   public static final EnumMap<Direction, TransformationMatrix> vanillaUvTransformGlobalToLocal = Util.make(Maps.newEnumMap(Direction.class), (p_229381_0_) -> {
      for(Direction direction : Direction.values()) {
         p_229381_0_.put(direction, vanillaUvTransformLocalToGlobal.get(direction).inverse());
      }

   });

   public static TransformationMatrix blockCenterToCorner(TransformationMatrix p_229379_0_) {
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.5F, 0.5F, 0.5F);
      matrix4f.multiply(p_229379_0_.getMatrix());
      matrix4f.multiply(Matrix4f.createTranslateMatrix(-0.5F, -0.5F, -0.5F));
      return new TransformationMatrix(matrix4f);
   }

   public static TransformationMatrix getUVLockTransform(TransformationMatrix p_229380_0_, Direction p_229380_1_, Supplier<String> p_229380_2_) {
      Direction direction = Direction.rotate(p_229380_0_.getMatrix(), p_229380_1_);
      TransformationMatrix transformationmatrix = p_229380_0_.inverse();
      if (transformationmatrix == null) {
         LOGGER.warn(p_229380_2_.get());
         return new TransformationMatrix((Vector3f)null, (Quaternion)null, new Vector3f(0.0F, 0.0F, 0.0F), (Quaternion)null);
      } else {
         TransformationMatrix transformationmatrix1 = vanillaUvTransformGlobalToLocal.get(p_229380_1_).compose(transformationmatrix).compose(vanillaUvTransformLocalToGlobal.get(direction));
         return blockCenterToCorner(transformationmatrix1);
      }
   }
}
