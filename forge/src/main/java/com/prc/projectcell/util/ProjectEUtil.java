package com.prc.projectcell.util;

import java.util.UUID;
import java.util.WeakHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ProjectEUtil {
   private static final Map<UUID, IKnowledgeProvider> KNOWLEDGE_CACHE = new WeakHashMap<>();
   private static int cacheClears = 0;
   private static final int CACHE_CLEAR_INTERVAL = 100; // Clear cache every 100 calls
   
   @Nullable
   public static ServerPlayer getPlayer(UUID uuid) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server == null ? null : server.getPlayerList().getPlayer(uuid);
   }

   @Nullable
   public static IKnowledgeProvider getKnowledgeProvider(UUID uuid) {
      // Try cache first
      IKnowledgeProvider cached = KNOWLEDGE_CACHE.get(uuid);
      if (cached != null) {
         return cached;
      }
      
      ServerPlayer player = getPlayer(uuid);
      if (player == null) {
         return null;
      }
      
      IKnowledgeProvider provider = getKnowledgeProvider(player);
      if (provider != null) {
         KNOWLEDGE_CACHE.put(uuid, provider);
         
         // Periodically clear cache to avoid memory issues
         if (++cacheClears % CACHE_CLEAR_INTERVAL == 0) {
            KNOWLEDGE_CACHE.clear();
         }
      }
      return provider;
   }

   @Nullable
   public static IKnowledgeProvider getKnowledgeProvider(Player player) {
      return player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).resolve().orElse(null);
   }
}
