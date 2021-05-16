package net.minecraft.client.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IShaderManager {
   int getId();

   void markDirty();

   ShaderLoader getVertexProgram();

   ShaderLoader getFragmentProgram();
}
