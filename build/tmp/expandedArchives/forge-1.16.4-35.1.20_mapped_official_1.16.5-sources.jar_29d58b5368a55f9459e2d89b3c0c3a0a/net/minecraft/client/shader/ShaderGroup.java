package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderGroup implements AutoCloseable {
   private final Framebuffer screenTarget;
   private final IResourceManager resourceManager;
   private final String name;
   private final List<Shader> passes = Lists.newArrayList();
   private final Map<String, Framebuffer> customRenderTargets = Maps.newHashMap();
   private final List<Framebuffer> fullSizedTargets = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;
   private int screenWidth;
   private int screenHeight;
   private float time;
   private float lastStamp;

   public ShaderGroup(TextureManager p_i1050_1_, IResourceManager p_i1050_2_, Framebuffer p_i1050_3_, ResourceLocation p_i1050_4_) throws IOException, JsonSyntaxException {
      this.resourceManager = p_i1050_2_;
      this.screenTarget = p_i1050_3_;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.screenWidth = p_i1050_3_.viewWidth;
      this.screenHeight = p_i1050_3_.viewHeight;
      this.name = p_i1050_4_.toString();
      this.updateOrthoMatrix();
      this.load(p_i1050_1_, p_i1050_4_);
   }

   private void load(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws IOException, JsonSyntaxException {
      IResource iresource = null;

      try {
         iresource = this.resourceManager.getResource(p_152765_2_);
         JsonObject jsonobject = JSONUtils.parse(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
         if (JSONUtils.isArrayNode(jsonobject, "targets")) {
            JsonArray jsonarray = jsonobject.getAsJsonArray("targets");
            int i = 0;

            for(JsonElement jsonelement : jsonarray) {
               try {
                  this.parseTargetNode(jsonelement);
               } catch (Exception exception1) {
                  JSONException jsonexception1 = JSONException.forException(exception1);
                  jsonexception1.prependJsonKey("targets[" + i + "]");
                  throw jsonexception1;
               }

               ++i;
            }
         }

         if (JSONUtils.isArrayNode(jsonobject, "passes")) {
            JsonArray jsonarray1 = jsonobject.getAsJsonArray("passes");
            int j = 0;

            for(JsonElement jsonelement1 : jsonarray1) {
               try {
                  this.parsePassNode(p_152765_1_, jsonelement1);
               } catch (Exception exception) {
                  JSONException jsonexception2 = JSONException.forException(exception);
                  jsonexception2.prependJsonKey("passes[" + j + "]");
                  throw jsonexception2;
               }

               ++j;
            }
         }
      } catch (Exception exception2) {
         String s;
         if (iresource != null) {
            s = " (" + iresource.getSourceName() + ")";
         } else {
            s = "";
         }

         JSONException jsonexception = JSONException.forException(exception2);
         jsonexception.setFilenameAndFlush(p_152765_2_.getPath() + s);
         throw jsonexception;
      } finally {
         IOUtils.closeQuietly((Closeable)iresource);
      }

   }

   private void parseTargetNode(JsonElement p_148027_1_) throws JSONException {
      if (JSONUtils.isStringValue(p_148027_1_)) {
         this.addTempTarget(p_148027_1_.getAsString(), this.screenWidth, this.screenHeight);
      } else {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_148027_1_, "target");
         String s = JSONUtils.getAsString(jsonobject, "name");
         int i = JSONUtils.getAsInt(jsonobject, "width", this.screenWidth);
         int j = JSONUtils.getAsInt(jsonobject, "height", this.screenHeight);
         if (this.customRenderTargets.containsKey(s)) {
            throw new JSONException(s + " is already defined");
         }

         this.addTempTarget(s, i, j);
      }

   }

   private void parsePassNode(TextureManager p_152764_1_, JsonElement p_152764_2_) throws IOException {
      JsonObject jsonobject = JSONUtils.convertToJsonObject(p_152764_2_, "pass");
      String s = JSONUtils.getAsString(jsonobject, "name");
      String s1 = JSONUtils.getAsString(jsonobject, "intarget");
      String s2 = JSONUtils.getAsString(jsonobject, "outtarget");
      Framebuffer framebuffer = this.getRenderTarget(s1);
      Framebuffer framebuffer1 = this.getRenderTarget(s2);
      if (framebuffer == null) {
         throw new JSONException("Input target '" + s1 + "' does not exist");
      } else if (framebuffer1 == null) {
         throw new JSONException("Output target '" + s2 + "' does not exist");
      } else {
         Shader shader = this.addPass(s, framebuffer, framebuffer1);
         JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "auxtargets", (JsonArray)null);
         if (jsonarray != null) {
            int i = 0;

            for(JsonElement jsonelement : jsonarray) {
               try {
                  JsonObject jsonobject1 = JSONUtils.convertToJsonObject(jsonelement, "auxtarget");
                  String s5 = JSONUtils.getAsString(jsonobject1, "name");
                  String s3 = JSONUtils.getAsString(jsonobject1, "id");
                  boolean flag;
                  String s4;
                  if (s3.endsWith(":depth")) {
                     flag = true;
                     s4 = s3.substring(0, s3.lastIndexOf(58));
                  } else {
                     flag = false;
                     s4 = s3;
                  }

                  Framebuffer framebuffer2 = this.getRenderTarget(s4);
                  if (framebuffer2 == null) {
                     if (flag) {
                        throw new JSONException("Render target '" + s4 + "' can't be used as depth buffer");
                     }

                     ResourceLocation rl = ResourceLocation.tryParse(s4);
                     ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "textures/effect/" + rl.getPath() + ".png");
                     IResource iresource = null;

                     try {
                        iresource = this.resourceManager.getResource(resourcelocation);
                     } catch (FileNotFoundException filenotfoundexception) {
                        throw new JSONException("Render target or texture '" + s4 + "' does not exist");
                     } finally {
                        IOUtils.closeQuietly((Closeable)iresource);
                     }

                     p_152764_1_.bind(resourcelocation);
                     Texture lvt_22_2_ = p_152764_1_.getTexture(resourcelocation);
                     int lvt_23_1_ = JSONUtils.getAsInt(jsonobject1, "width");
                     int lvt_24_1_ = JSONUtils.getAsInt(jsonobject1, "height");
                     boolean flag1 = JSONUtils.getAsBoolean(jsonobject1, "bilinear");
                     if (flag1) {
                        RenderSystem.texParameter(3553, 10241, 9729);
                        RenderSystem.texParameter(3553, 10240, 9729);
                     } else {
                        RenderSystem.texParameter(3553, 10241, 9728);
                        RenderSystem.texParameter(3553, 10240, 9728);
                     }

                     shader.addAuxAsset(s5, lvt_22_2_::getId, lvt_23_1_, lvt_24_1_);
                  } else if (flag) {
                     shader.addAuxAsset(s5, framebuffer2::getDepthTextureId, framebuffer2.width, framebuffer2.height);
                  } else {
                     shader.addAuxAsset(s5, framebuffer2::getColorTextureId, framebuffer2.width, framebuffer2.height);
                  }
               } catch (Exception exception1) {
                  JSONException jsonexception = JSONException.forException(exception1);
                  jsonexception.prependJsonKey("auxtargets[" + i + "]");
                  throw jsonexception;
               }

               ++i;
            }
         }

         JsonArray jsonarray1 = JSONUtils.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
         if (jsonarray1 != null) {
            int l = 0;

            for(JsonElement jsonelement1 : jsonarray1) {
               try {
                  this.parseUniformNode(jsonelement1);
               } catch (Exception exception) {
                  JSONException jsonexception1 = JSONException.forException(exception);
                  jsonexception1.prependJsonKey("uniforms[" + l + "]");
                  throw jsonexception1;
               }

               ++l;
            }
         }

      }
   }

   private void parseUniformNode(JsonElement p_148028_1_) throws JSONException {
      JsonObject jsonobject = JSONUtils.convertToJsonObject(p_148028_1_, "uniform");
      String s = JSONUtils.getAsString(jsonobject, "name");
      ShaderUniform shaderuniform = this.passes.get(this.passes.size() - 1).getEffect().getUniform(s);
      if (shaderuniform == null) {
         throw new JSONException("Uniform '" + s + "' does not exist");
      } else {
         float[] afloat = new float[4];
         int i = 0;

         for(JsonElement jsonelement : JSONUtils.getAsJsonArray(jsonobject, "values")) {
            try {
               afloat[i] = JSONUtils.convertToFloat(jsonelement, "value");
            } catch (Exception exception) {
               JSONException jsonexception = JSONException.forException(exception);
               jsonexception.prependJsonKey("values[" + i + "]");
               throw jsonexception;
            }

            ++i;
         }

         switch(i) {
         case 0:
         default:
            break;
         case 1:
            shaderuniform.set(afloat[0]);
            break;
         case 2:
            shaderuniform.set(afloat[0], afloat[1]);
            break;
         case 3:
            shaderuniform.set(afloat[0], afloat[1], afloat[2]);
            break;
         case 4:
            shaderuniform.set(afloat[0], afloat[1], afloat[2], afloat[3]);
         }

      }
   }

   public Framebuffer getTempTarget(String p_177066_1_) {
      return this.customRenderTargets.get(p_177066_1_);
   }

   public void addTempTarget(String p_148020_1_, int p_148020_2_, int p_148020_3_) {
      Framebuffer framebuffer = new Framebuffer(p_148020_2_, p_148020_3_, true, Minecraft.ON_OSX);
      framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.customRenderTargets.put(p_148020_1_, framebuffer);
      if (p_148020_2_ == this.screenWidth && p_148020_3_ == this.screenHeight) {
         this.fullSizedTargets.add(framebuffer);
      }

   }

   public void close() {
      for(Framebuffer framebuffer : this.customRenderTargets.values()) {
         framebuffer.destroyBuffers();
      }

      for(Shader shader : this.passes) {
         shader.close();
      }

      this.passes.clear();
   }

   public Shader addPass(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws IOException {
      Shader shader = new Shader(this.resourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
      this.passes.add(this.passes.size(), shader);
      return shader;
   }

   private void updateOrthoMatrix() {
      this.shaderOrthoMatrix = Matrix4f.orthographic((float)this.screenTarget.width, (float)this.screenTarget.height, 0.1F, 1000.0F);
   }

   public void resize(int p_148026_1_, int p_148026_2_) {
      this.screenWidth = this.screenTarget.width;
      this.screenHeight = this.screenTarget.height;
      this.updateOrthoMatrix();

      for(Shader shader : this.passes) {
         shader.setOrthoMatrix(this.shaderOrthoMatrix);
      }

      for(Framebuffer framebuffer : this.fullSizedTargets) {
         framebuffer.resize(p_148026_1_, p_148026_2_, Minecraft.ON_OSX);
      }

   }

   public void process(float p_148018_1_) {
      if (p_148018_1_ < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += p_148018_1_;
      } else {
         this.time += p_148018_1_ - this.lastStamp;
      }

      for(this.lastStamp = p_148018_1_; this.time > 20.0F; this.time -= 20.0F) {
      }

      for(Shader shader : this.passes) {
         shader.process(this.time / 20.0F);
      }

   }

   public final String getName() {
      return this.name;
   }

   private Framebuffer getRenderTarget(String p_148017_1_) {
      if (p_148017_1_ == null) {
         return null;
      } else {
         return p_148017_1_.equals("minecraft:main") ? this.screenTarget : this.customRenderTargets.get(p_148017_1_);
      }
   }
}
