package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface IDataProvider {
   HashFunction SHA1 = Hashing.sha1();

   void run(DirectoryCache p_200398_1_) throws IOException;

   String getName();

   static void save(Gson p_218426_0_, DirectoryCache p_218426_1_, JsonElement p_218426_2_, Path p_218426_3_) throws IOException {
      String s = p_218426_0_.toJson(p_218426_2_);
      String s1 = SHA1.hashUnencodedChars(s).toString();
      if (!Objects.equals(p_218426_1_.getHash(p_218426_3_), s1) || !Files.exists(p_218426_3_)) {
         Files.createDirectories(p_218426_3_.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(p_218426_3_)) {
            bufferedwriter.write(s);
         }
      }

      p_218426_1_.putNew(p_218426_3_, s1);
   }
}
