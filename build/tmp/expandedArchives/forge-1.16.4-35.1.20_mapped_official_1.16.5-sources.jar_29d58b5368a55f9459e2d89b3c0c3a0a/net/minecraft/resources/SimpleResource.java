package net.minecraft.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource {
   private final String sourceName;
   private final ResourceLocation location;
   private final InputStream resourceStream;
   private final InputStream metadataStream;
   @OnlyIn(Dist.CLIENT)
   private boolean triedMetadata;
   @OnlyIn(Dist.CLIENT)
   private JsonObject metadata;

   public SimpleResource(String p_i47904_1_, ResourceLocation p_i47904_2_, InputStream p_i47904_3_, @Nullable InputStream p_i47904_4_) {
      this.sourceName = p_i47904_1_;
      this.location = p_i47904_2_;
      this.resourceStream = p_i47904_3_;
      this.metadataStream = p_i47904_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.resourceStream;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasMetadata() {
      return this.metadataStream != null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_199028_1_) {
      if (!this.hasMetadata()) {
         return (T)null;
      } else {
         if (this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            BufferedReader bufferedreader = null;

            try {
               bufferedreader = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
               this.metadata = JSONUtils.parse(bufferedreader);
            } finally {
               IOUtils.closeQuietly((Reader)bufferedreader);
            }
         }

         if (this.metadata == null) {
            return (T)null;
         } else {
            String s = p_199028_1_.getMetadataSectionName();
            return (T)(this.metadata.has(s) ? p_199028_1_.fromJson(JSONUtils.getAsJsonObject(this.metadata, s)) : null);
         }
      }
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource simpleresource = (SimpleResource)p_equals_1_;
         if (this.location != null) {
            if (!this.location.equals(simpleresource.location)) {
               return false;
            }
         } else if (simpleresource.location != null) {
            return false;
         }

         if (this.sourceName != null) {
            if (!this.sourceName.equals(simpleresource.sourceName)) {
               return false;
            }
         } else if (simpleresource.sourceName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int i = this.sourceName != null ? this.sourceName.hashCode() : 0;
      return 31 * i + (this.location != null ? this.location.hashCode() : 0);
   }

   public void close() throws IOException {
      this.resourceStream.close();
      if (this.metadataStream != null) {
         this.metadataStream.close();
      }

   }
}
