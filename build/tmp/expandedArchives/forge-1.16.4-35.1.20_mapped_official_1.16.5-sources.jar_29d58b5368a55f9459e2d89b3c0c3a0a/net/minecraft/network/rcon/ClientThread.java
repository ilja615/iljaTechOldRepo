package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientThread extends RConThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean authed;
   private final Socket client;
   private final byte[] buf = new byte[1460];
   private final String rconPassword;
   private final IServer serverInterface;

   ClientThread(IServer p_i50687_1_, String p_i50687_2_, Socket p_i50687_3_) {
      super("RCON Client " + p_i50687_3_.getInetAddress());
      this.serverInterface = p_i50687_1_;
      this.client = p_i50687_3_;

      try {
         this.client.setSoTimeout(0);
      } catch (Exception exception) {
         this.running = false;
      }

      this.rconPassword = p_i50687_2_;
   }

   public void run() {
         try {
         while(true) {
            if (!this.running) {
               return;
            }

            BufferedInputStream bufferedinputstream = new BufferedInputStream(this.client.getInputStream());
            int i = bufferedinputstream.read(this.buf, 0, 1460);
            if (10 <= i) {
               int j = 0;
               int k = RConUtils.intFromByteArray(this.buf, 0, i);
               if (k != i - 4) {
                  return;
               }

               j = j + 4;
               int l = RConUtils.intFromByteArray(this.buf, j, i);
               j = j + 4;
               int i1 = RConUtils.intFromByteArray(this.buf, j);
               j = j + 4;
               switch(i1) {
               case 2:
                  if (this.authed) {
                     String s1 = RConUtils.stringFromByteArray(this.buf, j, i);

                     try {
                        this.sendCmdResponse(l, this.serverInterface.runCommand(s1));
                     } catch (Exception exception) {
                        this.sendCmdResponse(l, "Error executing: " + s1 + " (" + exception.getMessage() + ")");
                     }
                     continue;
                  }

                  this.sendAuthFailure();
                  continue;
               case 3:
                  String s = RConUtils.stringFromByteArray(this.buf, j, i);
                  int j1 = j + s.length();
                  if (!s.isEmpty() && s.equals(this.rconPassword)) {
                     this.authed = true;
                     this.send(l, 2, "");
                     continue;
                  }

                  this.authed = false;
                  this.sendAuthFailure();
                  continue;
               default:
                  this.sendCmdResponse(l, String.format("Unknown request %s", Integer.toHexString(i1)));
                  continue;
               }
            }
            return;
         }
         } catch (IOException ioexception) {
            return;
         } catch (Exception exception1) {
            LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception1);
            return;
         } finally {
            this.closeSocket();
            LOGGER.info("Thread {} shutting down", (Object)this.name);
            this.running = false;
         }
      }

   private void send(int p_72654_1_, int p_72654_2_, String p_72654_3_) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1248);
      DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
      byte[] abyte = p_72654_3_.getBytes(StandardCharsets.UTF_8);
      dataoutputstream.writeInt(Integer.reverseBytes(abyte.length + 10));
      dataoutputstream.writeInt(Integer.reverseBytes(p_72654_1_));
      dataoutputstream.writeInt(Integer.reverseBytes(p_72654_2_));
      dataoutputstream.write(abyte);
      dataoutputstream.write(0);
      dataoutputstream.write(0);
      this.client.getOutputStream().write(bytearrayoutputstream.toByteArray());
   }

   private void sendAuthFailure() throws IOException {
      this.send(-1, 2, "");
   }

   private void sendCmdResponse(int p_72655_1_, String p_72655_2_) throws IOException {
      byte[] whole = p_72655_2_.getBytes(StandardCharsets.UTF_8);
      int i = whole.length;
      int start = 0;
      do {
         int j = 4096 <= i ? 4096 : i;
         this.send(p_72655_1_, 0, new String(java.util.Arrays.copyOfRange(whole, start, j+start), StandardCharsets.UTF_8));
         i -= j;
         start += j;
      } while(0 != i);

   }

   public void stop() {
      this.running = false;
      this.closeSocket();
      super.stop();
   }

   private void closeSocket() {
      try {
         this.client.close();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to close socket", (Throwable)ioexception);
      }

   }
}
