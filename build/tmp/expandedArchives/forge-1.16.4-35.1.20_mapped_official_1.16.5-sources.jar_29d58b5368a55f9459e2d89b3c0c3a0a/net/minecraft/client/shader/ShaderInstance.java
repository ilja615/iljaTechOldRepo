package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.util.JSONBlendingMode;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderInstance implements IShaderManager, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ShaderDefault DUMMY_UNIFORM = new ShaderDefault();
   private static ShaderInstance lastAppliedEffect;
   private static int lastProgramId = -1;
   private final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> samplerLocations = Lists.newArrayList();
   private final List<ShaderUniform> uniforms = Lists.newArrayList();
   private final List<Integer> uniformLocations = Lists.newArrayList();
   private final Map<String, ShaderUniform> uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private boolean dirty;
   private final JSONBlendingMode blend;
   private final List<Integer> attributes;
   private final List<String> attributeNames;
   private final ShaderLoader vertexProgram;
   private final ShaderLoader fragmentProgram;

   public ShaderInstance(IResourceManager p_i50988_1_, String p_i50988_2_) throws IOException {
      ResourceLocation rl = ResourceLocation.tryParse(p_i50988_2_);
      ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + ".json");
      this.name = p_i50988_2_;
      IResource iresource = null;

      try {
         iresource = p_i50988_1_.getResource(resourcelocation);
         JsonObject jsonobject = JSONUtils.parse(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
         String s = JSONUtils.getAsString(jsonobject, "vertex");
         String s2 = JSONUtils.getAsString(jsonobject, "fragment");
         JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "samplers", (JsonArray)null);
         if (jsonarray != null) {
            int i = 0;

            for(JsonElement jsonelement : jsonarray) {
               try {
                  this.parseSamplerNode(jsonelement);
               } catch (Exception exception2) {
                  JSONException jsonexception1 = JSONException.forException(exception2);
                  jsonexception1.prependJsonKey("samplers[" + i + "]");
                  throw jsonexception1;
               }

               ++i;
            }
         }

         JsonArray jsonarray1 = JSONUtils.getAsJsonArray(jsonobject, "attributes", (JsonArray)null);
         if (jsonarray1 != null) {
            int j = 0;
            this.attributes = Lists.newArrayListWithCapacity(jsonarray1.size());
            this.attributeNames = Lists.newArrayListWithCapacity(jsonarray1.size());

            for(JsonElement jsonelement1 : jsonarray1) {
               try {
                  this.attributeNames.add(JSONUtils.convertToString(jsonelement1, "attribute"));
               } catch (Exception exception1) {
                  JSONException jsonexception2 = JSONException.forException(exception1);
                  jsonexception2.prependJsonKey("attributes[" + j + "]");
                  throw jsonexception2;
               }

               ++j;
            }
         } else {
            this.attributes = null;
            this.attributeNames = null;
         }

         JsonArray jsonarray2 = JSONUtils.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
         if (jsonarray2 != null) {
            int k = 0;

            for(JsonElement jsonelement2 : jsonarray2) {
               try {
                  this.parseUniformNode(jsonelement2);
               } catch (Exception exception) {
                  JSONException jsonexception3 = JSONException.forException(exception);
                  jsonexception3.prependJsonKey("uniforms[" + k + "]");
                  throw jsonexception3;
               }

               ++k;
            }
         }

         this.blend = parseBlendNode(JSONUtils.getAsJsonObject(jsonobject, "blend", (JsonObject)null));
         this.vertexProgram = getOrCreate(p_i50988_1_, ShaderLoader.ShaderType.VERTEX, s);
         this.fragmentProgram = getOrCreate(p_i50988_1_, ShaderLoader.ShaderType.FRAGMENT, s2);
         this.programId = ShaderLinkHelper.createProgram();
         ShaderLinkHelper.linkProgram(this);
         this.updateLocations();
         if (this.attributeNames != null) {
            for(String s3 : this.attributeNames) {
               int l = ShaderUniform.glGetAttribLocation(this.programId, s3);
               this.attributes.add(l);
            }
         }
      } catch (Exception exception3) {
         String s1;
         if (iresource != null) {
            s1 = " (" + iresource.getSourceName() + ")";
         } else {
            s1 = "";
         }

         JSONException jsonexception = JSONException.forException(exception3);
         jsonexception.setFilenameAndFlush(resourcelocation.getPath() + s1);
         throw jsonexception;
      } finally {
         IOUtils.closeQuietly((Closeable)iresource);
      }

      this.markDirty();
   }

   public static ShaderLoader getOrCreate(IResourceManager p_216542_0_, ShaderLoader.ShaderType p_216542_1_, String p_216542_2_) throws IOException {
      ShaderLoader shaderloader = p_216542_1_.getPrograms().get(p_216542_2_);
      if (shaderloader == null) {
         ResourceLocation rl = ResourceLocation.tryParse(p_216542_2_);
         ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + p_216542_1_.getExtension());
         IResource iresource = p_216542_0_.getResource(resourcelocation);

         try {
            shaderloader = ShaderLoader.compileShader(p_216542_1_, p_216542_2_, iresource.getInputStream(), iresource.getSourceName());
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }
      }

      return shaderloader;
   }

   public static JSONBlendingMode parseBlendNode(JsonObject p_216543_0_) {
      if (p_216543_0_ == null) {
         return new JSONBlendingMode();
      } else {
         int i = 32774;
         int j = 1;
         int k = 0;
         int l = 1;
         int i1 = 0;
         boolean flag = true;
         boolean flag1 = false;
         if (JSONUtils.isStringValue(p_216543_0_, "func")) {
            i = JSONBlendingMode.stringToBlendFunc(p_216543_0_.get("func").getAsString());
            if (i != 32774) {
               flag = false;
            }
         }

         if (JSONUtils.isStringValue(p_216543_0_, "srcrgb")) {
            j = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("srcrgb").getAsString());
            if (j != 1) {
               flag = false;
            }
         }

         if (JSONUtils.isStringValue(p_216543_0_, "dstrgb")) {
            k = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("dstrgb").getAsString());
            if (k != 0) {
               flag = false;
            }
         }

         if (JSONUtils.isStringValue(p_216543_0_, "srcalpha")) {
            l = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("srcalpha").getAsString());
            if (l != 1) {
               flag = false;
            }

            flag1 = true;
         }

         if (JSONUtils.isStringValue(p_216543_0_, "dstalpha")) {
            i1 = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("dstalpha").getAsString());
            if (i1 != 0) {
               flag = false;
            }

            flag1 = true;
         }

         if (flag) {
            return new JSONBlendingMode();
         } else {
            return flag1 ? new JSONBlendingMode(j, k, l, i1, i) : new JSONBlendingMode(j, k, i);
         }
      }
   }

   public void close() {
      for(ShaderUniform shaderuniform : this.uniforms) {
         shaderuniform.close();
      }

      ShaderLinkHelper.releaseProgram(this);
   }

   public void clear() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ShaderLinkHelper.glUseProgram(0);
      lastProgramId = -1;
      lastAppliedEffect = null;

      for(int i = 0; i < this.samplerLocations.size(); ++i) {
         if (this.samplerMap.get(this.samplerNames.get(i)) != null) {
            GlStateManager._activeTexture('\u84c0' + i);
            GlStateManager._disableTexture();
            GlStateManager._bindTexture(0);
         }
      }

   }

   public void apply() {
      RenderSystem.assertThread(RenderSystem::isOnGameThread);
      this.dirty = false;
      lastAppliedEffect = this;
      this.blend.apply();
      if (this.programId != lastProgramId) {
         ShaderLinkHelper.glUseProgram(this.programId);
         lastProgramId = this.programId;
      }

      for(int i = 0; i < this.samplerLocations.size(); ++i) {
         String s = this.samplerNames.get(i);
         IntSupplier intsupplier = this.samplerMap.get(s);
         if (intsupplier != null) {
            RenderSystem.activeTexture('\u84c0' + i);
            RenderSystem.enableTexture();
            int j = intsupplier.getAsInt();
            if (j != -1) {
               RenderSystem.bindTexture(j);
               ShaderUniform.uploadInteger(this.samplerLocations.get(i), i);
            }
         }
      }

      for(ShaderUniform shaderuniform : this.uniforms) {
         shaderuniform.upload();
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   @Nullable
   public ShaderUniform getUniform(String p_216539_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return this.uniformMap.get(p_216539_1_);
   }

   public ShaderDefault safeGetUniform(String p_216538_1_) {
      RenderSystem.assertThread(RenderSystem::isOnGameThread);
      ShaderUniform shaderuniform = this.getUniform(p_216538_1_);
      return (ShaderDefault)(shaderuniform == null ? DUMMY_UNIFORM : shaderuniform);
   }

   private void updateLocations() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      IntList intlist = new IntArrayList();

      for(int i = 0; i < this.samplerNames.size(); ++i) {
         String s = this.samplerNames.get(i);
         int j = ShaderUniform.glGetUniformLocation(this.programId, s);
         if (j == -1) {
            LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, s);
            this.samplerMap.remove(s);
            intlist.add(i);
         } else {
            this.samplerLocations.add(j);
         }
      }

      for(int l = intlist.size() - 1; l >= 0; --l) {
         this.samplerNames.remove(intlist.getInt(l));
      }

      for(ShaderUniform shaderuniform : this.uniforms) {
         String s1 = shaderuniform.getName();
         int k = ShaderUniform.glGetUniformLocation(this.programId, s1);
         if (k == -1) {
            LOGGER.warn("Could not find uniform named {} in the specified shader program.", (Object)s1);
         } else {
            this.uniformLocations.add(k);
            shaderuniform.setLocation(k);
            this.uniformMap.put(s1, shaderuniform);
         }
      }

   }

   private void parseSamplerNode(JsonElement p_216541_1_) {
      JsonObject jsonobject = JSONUtils.convertToJsonObject(p_216541_1_, "sampler");
      String s = JSONUtils.getAsString(jsonobject, "name");
      if (!JSONUtils.isStringValue(jsonobject, "file")) {
         this.samplerMap.put(s, (IntSupplier)null);
         this.samplerNames.add(s);
      } else {
         this.samplerNames.add(s);
      }
   }

   public void setSampler(String p_216537_1_, IntSupplier p_216537_2_) {
      if (this.samplerMap.containsKey(p_216537_1_)) {
         this.samplerMap.remove(p_216537_1_);
      }

      this.samplerMap.put(p_216537_1_, p_216537_2_);
      this.markDirty();
   }

   private void parseUniformNode(JsonElement p_216540_1_) throws JSONException {
      JsonObject jsonobject = JSONUtils.convertToJsonObject(p_216540_1_, "uniform");
      String s = JSONUtils.getAsString(jsonobject, "name");
      int i = ShaderUniform.getTypeFromString(JSONUtils.getAsString(jsonobject, "type"));
      int j = JSONUtils.getAsInt(jsonobject, "count");
      float[] afloat = new float[Math.max(j, 16)];
      JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "values");
      if (jsonarray.size() != j && jsonarray.size() > 1) {
         throw new JSONException("Invalid amount of values specified (expected " + j + ", found " + jsonarray.size() + ")");
      } else {
         int k = 0;

         for(JsonElement jsonelement : jsonarray) {
            try {
               afloat[k] = JSONUtils.convertToFloat(jsonelement, "value");
            } catch (Exception exception) {
               JSONException jsonexception = JSONException.forException(exception);
               jsonexception.prependJsonKey("values[" + k + "]");
               throw jsonexception;
            }

            ++k;
         }

         if (j > 1 && jsonarray.size() == 1) {
            while(k < j) {
               afloat[k] = afloat[0];
               ++k;
            }
         }

         int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
         ShaderUniform shaderuniform = new ShaderUniform(s, i + l, j, this);
         if (i <= 3) {
            shaderuniform.setSafe((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
         } else if (i <= 7) {
            shaderuniform.setSafe(afloat[0], afloat[1], afloat[2], afloat[3]);
         } else {
            shaderuniform.set(afloat);
         }

         this.uniforms.add(shaderuniform);
      }
   }

   public ShaderLoader getVertexProgram() {
      return this.vertexProgram;
   }

   public ShaderLoader getFragmentProgram() {
      return this.fragmentProgram;
   }

   public int getId() {
      return this.programId;
   }
}
