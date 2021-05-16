package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerAddress {
   private final String host;
   private final int port;

   private ServerAddress(String p_i1192_1_, int p_i1192_2_) {
      this.host = p_i1192_1_;
      this.port = p_i1192_2_;
   }

   public String getHost() {
      try {
         return IDN.toASCII(this.host);
      } catch (IllegalArgumentException illegalargumentexception) {
         return "";
      }
   }

   public int getPort() {
      return this.port;
   }

   public static ServerAddress parseString(String p_78860_0_) {
      if (p_78860_0_ == null) {
         return null;
      } else {
         String[] astring = p_78860_0_.split(":");
         if (p_78860_0_.startsWith("[")) {
            int i = p_78860_0_.indexOf("]");
            if (i > 0) {
               String s = p_78860_0_.substring(1, i);
               String s1 = p_78860_0_.substring(i + 1).trim();
               if (s1.startsWith(":") && !s1.isEmpty()) {
                  s1 = s1.substring(1);
                  astring = new String[]{s, s1};
               } else {
                  astring = new String[]{s};
               }
            }
         }

         if (astring.length > 2) {
            astring = new String[]{p_78860_0_};
         }

         String s2 = astring[0];
         int j = astring.length > 1 ? parseInt(astring[1], 25565) : 25565;
         if (j == 25565) {
            Pair<String, Integer> pair = lookupSrv(s2);
            s2 = pair.getFirst();
            j = pair.getSecond();
         }

         return new ServerAddress(s2, j);
      }
   }

   private static Pair<String, Integer> lookupSrv(String p_241677_0_) {
      try {
         String s = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable<String, String> hashtable = new Hashtable<>();
         hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         hashtable.put("java.naming.provider.url", "dns:");
         hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
         DirContext dircontext = new InitialDirContext(hashtable);
         Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + p_241677_0_, new String[]{"SRV"});
         Attribute attribute = attributes.get("srv");
         if (attribute != null) {
            String[] astring = attribute.get().toString().split(" ", 4);
            return Pair.of(astring[3], parseInt(astring[2], 25565));
         }
      } catch (Throwable throwable) {
      }

      return Pair.of(p_241677_0_, 25565);
   }

   private static int parseInt(String p_78862_0_, int p_78862_1_) {
      try {
         return Integer.parseInt(p_78862_0_.trim());
      } catch (Exception exception) {
         return p_78862_1_;
      }
   }
}
