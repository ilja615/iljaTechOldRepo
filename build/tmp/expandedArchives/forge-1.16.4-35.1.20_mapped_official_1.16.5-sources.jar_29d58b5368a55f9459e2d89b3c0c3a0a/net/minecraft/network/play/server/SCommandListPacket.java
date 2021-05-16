package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SCommandListPacket implements IPacket<IClientPlayNetHandler> {
   private RootCommandNode<ISuggestionProvider> root;

   public SCommandListPacket() {
   }

   public SCommandListPacket(RootCommandNode<ISuggestionProvider> p_i47940_1_) {
      this.root = p_i47940_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      SCommandListPacket.Entry[] ascommandlistpacket$entry = new SCommandListPacket.Entry[p_148837_1_.readVarInt()];

      for(int i = 0; i < ascommandlistpacket$entry.length; ++i) {
         ascommandlistpacket$entry[i] = readNode(p_148837_1_);
      }

      resolveEntries(ascommandlistpacket$entry);
      this.root = (RootCommandNode)ascommandlistpacket$entry[p_148837_1_.readVarInt()].node;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      Object2IntMap<CommandNode<ISuggestionProvider>> object2intmap = enumerateNodes(this.root);
      CommandNode<ISuggestionProvider>[] commandnode = getNodesInIdOrder(object2intmap);
      p_148840_1_.writeVarInt(commandnode.length);

      for(CommandNode<ISuggestionProvider> commandnode1 : commandnode) {
         writeNode(p_148840_1_, commandnode1, object2intmap);
      }

      p_148840_1_.writeVarInt(object2intmap.get(this.root));
   }

   private static void resolveEntries(SCommandListPacket.Entry[] p_244294_0_) {
      List<SCommandListPacket.Entry> list = Lists.newArrayList(p_244294_0_);

      while(!list.isEmpty()) {
         boolean flag = list.removeIf((p_244295_1_) -> {
            return p_244295_1_.build(p_244294_0_);
         });
         if (!flag) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

   }

   private static Object2IntMap<CommandNode<ISuggestionProvider>> enumerateNodes(RootCommandNode<ISuggestionProvider> p_244292_0_) {
      Object2IntMap<CommandNode<ISuggestionProvider>> object2intmap = new Object2IntOpenHashMap<>();
      Queue<CommandNode<ISuggestionProvider>> queue = Queues.newArrayDeque();
      queue.add(p_244292_0_);

      CommandNode<ISuggestionProvider> commandnode;
      while((commandnode = queue.poll()) != null) {
         if (!object2intmap.containsKey(commandnode)) {
            int i = object2intmap.size();
            object2intmap.put(commandnode, i);
            queue.addAll(commandnode.getChildren());
            if (commandnode.getRedirect() != null) {
               queue.add(commandnode.getRedirect());
            }
         }
      }

      return object2intmap;
   }

   private static CommandNode<ISuggestionProvider>[] getNodesInIdOrder(Object2IntMap<CommandNode<ISuggestionProvider>> p_244293_0_) {
      CommandNode<ISuggestionProvider>[] commandnode = new CommandNode[p_244293_0_.size()];

      for(Object2IntMap.Entry<CommandNode<ISuggestionProvider>> entry : Object2IntMaps.fastIterable(p_244293_0_)) {
         commandnode[entry.getIntValue()] = entry.getKey();
      }

      return commandnode;
   }

   private static SCommandListPacket.Entry readNode(PacketBuffer p_197692_0_) {
      byte b0 = p_197692_0_.readByte();
      int[] aint = p_197692_0_.readVarIntArray();
      int i = (b0 & 8) != 0 ? p_197692_0_.readVarInt() : 0;
      ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = createBuilder(p_197692_0_, b0);
      return new SCommandListPacket.Entry(argumentbuilder, b0, i, aint);
   }

   @Nullable
   private static ArgumentBuilder<ISuggestionProvider, ?> createBuilder(PacketBuffer p_197695_0_, byte p_197695_1_) {
      int i = p_197695_1_ & 3;
      if (i == 2) {
         String s = p_197695_0_.readUtf(32767);
         ArgumentType<?> argumenttype = ArgumentTypes.deserialize(p_197695_0_);
         if (argumenttype == null) {
            return null;
         } else {
            RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);
            if ((p_197695_1_ & 16) != 0) {
               requiredargumentbuilder.suggests(SuggestionProviders.getProvider(p_197695_0_.readResourceLocation()));
            }

            return requiredargumentbuilder;
         }
      } else {
         return i == 1 ? LiteralArgumentBuilder.literal(p_197695_0_.readUtf(32767)) : null;
      }
   }

   private static void writeNode(PacketBuffer p_197696_0_, CommandNode<ISuggestionProvider> p_197696_1_, Map<CommandNode<ISuggestionProvider>, Integer> p_197696_2_) {
      byte b0 = 0;
      if (p_197696_1_.getRedirect() != null) {
         b0 = (byte)(b0 | 8);
      }

      if (p_197696_1_.getCommand() != null) {
         b0 = (byte)(b0 | 4);
      }

      if (p_197696_1_ instanceof RootCommandNode) {
         b0 = (byte)(b0 | 0);
      } else if (p_197696_1_ instanceof ArgumentCommandNode) {
         b0 = (byte)(b0 | 2);
         if (((ArgumentCommandNode)p_197696_1_).getCustomSuggestions() != null) {
            b0 = (byte)(b0 | 16);
         }
      } else {
         if (!(p_197696_1_ instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + p_197696_1_);
         }

         b0 = (byte)(b0 | 1);
      }

      p_197696_0_.writeByte(b0);
      p_197696_0_.writeVarInt(p_197696_1_.getChildren().size());

      for(CommandNode<ISuggestionProvider> commandnode : p_197696_1_.getChildren()) {
         p_197696_0_.writeVarInt(p_197696_2_.get(commandnode));
      }

      if (p_197696_1_.getRedirect() != null) {
         p_197696_0_.writeVarInt(p_197696_2_.get(p_197696_1_.getRedirect()));
      }

      if (p_197696_1_ instanceof ArgumentCommandNode) {
         ArgumentCommandNode<ISuggestionProvider, ?> argumentcommandnode = (ArgumentCommandNode)p_197696_1_;
         p_197696_0_.writeUtf(argumentcommandnode.getName());
         ArgumentTypes.serialize(p_197696_0_, argumentcommandnode.getType());
         if (argumentcommandnode.getCustomSuggestions() != null) {
            p_197696_0_.writeResourceLocation(SuggestionProviders.getName(argumentcommandnode.getCustomSuggestions()));
         }
      } else if (p_197696_1_ instanceof LiteralCommandNode) {
         p_197696_0_.writeUtf(((LiteralCommandNode)p_197696_1_).getLiteral());
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCommands(this);
   }

   public RootCommandNode<ISuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<ISuggestionProvider, ?> builder;
      private final byte flags;
      private final int redirect;
      private final int[] children;
      @Nullable
      private CommandNode<ISuggestionProvider> node;

      private Entry(@Nullable ArgumentBuilder<ISuggestionProvider, ?> p_i48139_1_, byte p_i48139_2_, int p_i48139_3_, int[] p_i48139_4_) {
         this.builder = p_i48139_1_;
         this.flags = p_i48139_2_;
         this.redirect = p_i48139_3_;
         this.children = p_i48139_4_;
      }

      public boolean build(SCommandListPacket.Entry[] p_197723_1_) {
         if (this.node == null) {
            if (this.builder == null) {
               this.node = new RootCommandNode<>();
            } else {
               if ((this.flags & 8) != 0) {
                  if (p_197723_1_[this.redirect].node == null) {
                     return false;
                  }

                  this.builder.redirect(p_197723_1_[this.redirect].node);
               }

               if ((this.flags & 4) != 0) {
                  this.builder.executes((p_197724_0_) -> {
                     return 0;
                  });
               }

               this.node = this.builder.build();
            }
         }

         for(int i : this.children) {
            if (p_197723_1_[i].node == null) {
               return false;
            }
         }

         for(int j : this.children) {
            CommandNode<ISuggestionProvider> commandnode = p_197723_1_[j].node;
            if (!(commandnode instanceof RootCommandNode)) {
               this.node.addChild(commandnode);
            }
         }

         return true;
      }
   }
}
