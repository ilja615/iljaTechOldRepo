package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Request<T extends Request<T>> {
   protected HttpURLConnection connection;
   private boolean connected;
   protected String url;

   public Request(String p_i51788_1_, int p_i51788_2_, int p_i51788_3_) {
      try {
         this.url = p_i51788_1_;
         Proxy proxy = RealmsClientConfig.getProxy();
         if (proxy != null) {
            this.connection = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection(proxy);
         } else {
            this.connection = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection();
         }

         this.connection.setConnectTimeout(p_i51788_2_);
         this.connection.setReadTimeout(p_i51788_3_);
      } catch (MalformedURLException malformedurlexception) {
         throw new RealmsHttpException(malformedurlexception.getMessage(), malformedurlexception);
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   public void cookie(String p_224962_1_, String p_224962_2_) {
      cookie(this.connection, p_224962_1_, p_224962_2_);
   }

   public static void cookie(HttpURLConnection p_224967_0_, String p_224967_1_, String p_224967_2_) {
      String s = p_224967_0_.getRequestProperty("Cookie");
      if (s == null) {
         p_224967_0_.setRequestProperty("Cookie", p_224967_1_ + "=" + p_224967_2_);
      } else {
         p_224967_0_.setRequestProperty("Cookie", s + ";" + p_224967_1_ + "=" + p_224967_2_);
      }

   }

   public int getRetryAfterHeader() {
      return getRetryAfterHeader(this.connection);
   }

   public static int getRetryAfterHeader(HttpURLConnection p_224964_0_) {
      String s = p_224964_0_.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(s);
      } catch (Exception exception) {
         return 5;
      }
   }

   public int responseCode() {
      try {
         this.connect();
         return this.connection.getResponseCode();
      } catch (Exception exception) {
         throw new RealmsHttpException(exception.getMessage(), exception);
      }
   }

   public String text() {
      try {
         this.connect();
         String s = null;
         if (this.responseCode() >= 400) {
            s = this.read(this.connection.getErrorStream());
         } else {
            s = this.read(this.connection.getInputStream());
         }

         this.dispose();
         return s;
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   private String read(InputStream p_224954_1_) throws IOException {
      if (p_224954_1_ == null) {
         return "";
      } else {
         InputStreamReader inputstreamreader = new InputStreamReader(p_224954_1_, "UTF-8");
         StringBuilder stringbuilder = new StringBuilder();

         for(int i = inputstreamreader.read(); i != -1; i = inputstreamreader.read()) {
            stringbuilder.append((char)i);
         }

         return stringbuilder.toString();
      }
   }

   private void dispose() {
      byte[] abyte = new byte[1024];

      try {
         InputStream inputstream = this.connection.getInputStream();

         while(inputstream.read(abyte) > 0) {
         }

         inputstream.close();
         return;
      } catch (Exception exception) {
         try {
            InputStream inputstream1 = this.connection.getErrorStream();
            if (inputstream1 != null) {
               while(inputstream1.read(abyte) > 0) {
               }

               inputstream1.close();
               return;
            }
         } catch (IOException ioexception) {
            return;
         }
      } finally {
         if (this.connection != null) {
            this.connection.disconnect();
         }

      }

   }

   protected T connect() {
      if (this.connected) {
         return (T)this;
      } else {
         T t = this.doConnect();
         this.connected = true;
         return t;
      }
   }

   protected abstract T doConnect();

   public static Request<?> get(String p_224953_0_) {
      return new Request.Get(p_224953_0_, 5000, 60000);
   }

   public static Request<?> get(String p_224960_0_, int p_224960_1_, int p_224960_2_) {
      return new Request.Get(p_224960_0_, p_224960_1_, p_224960_2_);
   }

   public static Request<?> post(String p_224951_0_, String p_224951_1_) {
      return new Request.Post(p_224951_0_, p_224951_1_, 5000, 60000);
   }

   public static Request<?> post(String p_224959_0_, String p_224959_1_, int p_224959_2_, int p_224959_3_) {
      return new Request.Post(p_224959_0_, p_224959_1_, p_224959_2_, p_224959_3_);
   }

   public static Request<?> delete(String p_224952_0_) {
      return new Request.Delete(p_224952_0_, 5000, 60000);
   }

   public static Request<?> put(String p_224965_0_, String p_224965_1_) {
      return new Request.Put(p_224965_0_, p_224965_1_, 5000, 60000);
   }

   public static Request<?> put(String p_224966_0_, String p_224966_1_, int p_224966_2_, int p_224966_3_) {
      return new Request.Put(p_224966_0_, p_224966_1_, p_224966_2_, p_224966_3_);
   }

   public String getHeader(String p_224956_1_) {
      return getHeader(this.connection, p_224956_1_);
   }

   public static String getHeader(HttpURLConnection p_224961_0_, String p_224961_1_) {
      try {
         return p_224961_0_.getHeaderField(p_224961_1_);
      } catch (Exception exception) {
         return "";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Delete extends Request<Request.Delete> {
      public Delete(String p_i51800_1_, int p_i51800_2_, int p_i51800_3_) {
         super(p_i51800_1_, p_i51800_2_, p_i51800_3_);
      }

      public Request.Delete doConnect() {
         try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Get extends Request<Request.Get> {
      public Get(String p_i51799_1_, int p_i51799_2_, int p_i51799_3_) {
         super(p_i51799_1_, p_i51799_2_, p_i51799_3_);
      }

      public Request.Get doConnect() {
         try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Post extends Request<Request.Post> {
      private final String content;

      public Post(String p_i51798_1_, String p_i51798_2_, int p_i51798_3_, int p_i51798_4_) {
         super(p_i51798_1_, p_i51798_3_, p_i51798_4_);
         this.content = p_i51798_2_;
      }

      public Request.Post doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream outputstream = this.connection.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.content);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Put extends Request<Request.Put> {
      private final String content;

      public Put(String p_i51797_1_, String p_i51797_2_, int p_i51797_3_, int p_i51797_4_) {
         super(p_i51797_1_, p_i51797_3_, p_i51797_4_);
         this.content = p_i51797_2_;
      }

      public Request.Put doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoOutput(true);
            this.connection.setDoInput(true);
            this.connection.setRequestMethod("PUT");
            OutputStream outputstream = this.connection.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.content);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }
}
