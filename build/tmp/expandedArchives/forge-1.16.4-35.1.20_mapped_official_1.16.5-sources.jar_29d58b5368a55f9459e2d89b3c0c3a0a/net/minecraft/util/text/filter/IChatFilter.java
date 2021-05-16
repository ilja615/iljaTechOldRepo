package net.minecraft.util.text.filter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IChatFilter {
   void join();

   void leave();

   CompletableFuture<Optional<String>> processStreamMessage(String p_244432_1_);

   CompletableFuture<Optional<List<String>>> processMessageBundle(List<String> p_244433_1_);
}
