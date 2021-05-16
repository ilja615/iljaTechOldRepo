package net.minecraft.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionReloader implements IFutureReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PATH_PREFIX_LENGTH = "functions/".length();
   private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private volatile Map<ResourceLocation, FunctionObject> functions = ImmutableMap.of();
   private final TagCollectionReader<FunctionObject> tagsLoader = new TagCollectionReader<>(this::getFunction, "tags/functions", "function");
   private volatile ITagCollection<FunctionObject> tags = ITagCollection.empty();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSource> dispatcher;

   public Optional<FunctionObject> getFunction(ResourceLocation p_240940_1_) {
      return Optional.ofNullable(this.functions.get(p_240940_1_));
   }

   public Map<ResourceLocation, FunctionObject> getFunctions() {
      return this.functions;
   }

   public ITagCollection<FunctionObject> getTags() {
      return this.tags;
   }

   public ITag<FunctionObject> getTag(ResourceLocation p_240943_1_) {
      return this.tags.getTagOrEmpty(p_240943_1_);
   }

   public FunctionReloader(int p_i232596_1_, CommandDispatcher<CommandSource> p_i232596_2_) {
      this.functionCompilationLevel = p_i232596_1_;
      this.dispatcher = p_i232596_2_;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture = this.tagsLoader.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, CompletableFuture<FunctionObject>>> completablefuture1 = CompletableFuture.supplyAsync(() -> {
         return p_215226_2_.listResources("functions", (p_240938_0_) -> {
            return p_240938_0_.endsWith(".mcfunction");
         });
      }, p_215226_5_).thenCompose((p_240933_3_) -> {
         Map<ResourceLocation, CompletableFuture<FunctionObject>> map = Maps.newHashMap();
         CommandSource commandsource = new CommandSource(ICommandSource.NULL, Vector3d.ZERO, Vector2f.ZERO, (ServerWorld)null, this.functionCompilationLevel, "", StringTextComponent.EMPTY, (MinecraftServer)null, (Entity)null);

         for(ResourceLocation resourcelocation : p_240933_3_) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));
            map.put(resourcelocation1, CompletableFuture.supplyAsync(() -> {
               List<String> list = readLines(p_215226_2_, resourcelocation);
               return FunctionObject.fromLines(resourcelocation1, this.dispatcher, commandsource, list);
            }, p_215226_5_));
         }

         CompletableFuture<?>[] completablefuture2 = map.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(completablefuture2).handle((p_240939_1_, p_240939_2_) -> {
            return map;
         });
      });
      return completablefuture.thenCombine(completablefuture1, Pair::of).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_240937_1_) -> {
         Map<ResourceLocation, CompletableFuture<FunctionObject>> map = (Map)p_240937_1_.getSecond();
         Builder<ResourceLocation, FunctionObject> builder = ImmutableMap.builder();
         map.forEach((p_240936_1_, p_240936_2_) -> {
            p_240936_2_.handle((p_240941_2_, p_240941_3_) -> {
               if (p_240941_3_ != null) {
                  LOGGER.error("Failed to load function {}", p_240936_1_, p_240941_3_);
               } else {
                  builder.put(p_240936_1_, p_240941_2_);
               }

               return null;
            }).join();
         });
         this.functions = builder.build();
         this.tags = this.tagsLoader.load((Map)p_240937_1_.getFirst());
      }, p_215226_6_);
   }

   private static List<String> readLines(IResourceManager p_240934_0_, ResourceLocation p_240934_1_) {
      try (IResource iresource = p_240934_0_.getResource(p_240934_1_)) {
         return IOUtils.readLines(iresource.getInputStream(), StandardCharsets.UTF_8);
      } catch (IOException ioexception) {
         throw new CompletionException(ioexception);
      }
   }
}
