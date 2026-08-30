package com.gmr.projectcell.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.gmr.projectcell.Config;
import com.gmr.projectcell.util.ProjectEUtil;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.UUID;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EMCMEStorage implements StorageCell {
   private static final long MAX_CELL_COUNT = 2147483647L;
   private static final Map<ItemInfo, AEItemKey> KEY_CACHE = new ConcurrentHashMap();
   private final UUID owner;
   private final boolean nbtFilter;
   private final ISaveProvider host;

   private static long limit() {
      return Config.SERVER.spec.isLoaded() && !(Boolean)Config.SERVER.limitItemCount.get() ? Long.MAX_VALUE : 2147483647L;
   }

   private static final Set<DataComponentType<?>> BLOCKED = Set.of(
      DataComponents.CUSTOM_NAME,
      DataComponents.ITEM_NAME,
      DataComponents.LORE,
      DataComponents.HIDE_TOOLTIP,
      DataComponents.HIDE_ADDITIONAL_TOOLTIP,
      DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
      DataComponents.DAMAGE,
      DataComponents.ENCHANTMENTS
   );

   private static boolean hasBlockedNbt(AEItemKey key) {
      DataComponentPatch patch = key.toStack().getComponentsPatch();
      for (var entry : patch.entrySet()) {
         if (BLOCKED.contains(entry.getKey())) return true;
      }
      return false;
   }

   private static boolean hasNonPersistentNbt(AEItemKey key) {
      ItemInfo info = ItemInfo.fromStack(key.toStack());
      return !info.equals(IEMCProxy.INSTANCE.getPersistentInfo(info));
   }

   private static boolean isBlocked(AEItemKey key) {
      return hasBlockedNbt(key) || hasNonPersistentNbt(key);
   }

   public EMCMEStorage(UUID owner, boolean nbtFilter, ISaveProvider host) {
      this.owner = owner;
      this.nbtFilter = nbtFilter;
      this.host = host;
   }

   public CellState getStatus() {
      return CellState.NOT_EMPTY;
   }

   public double getIdleDrain() {
      return 0.0D;
   }

   public void persist() {
   }

   public boolean canFitInsideCell() {
      return true;
   }

   public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
      if (what instanceof AEItemKey) {
         AEItemKey itemKey = (AEItemKey)what;
         if (IEMCProxy.INSTANCE.hasValue(itemKey.toStack())) {
            return !this.nbtFilter || !isBlocked(itemKey);
         }
      }

      return false;
   }

   public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
      amount = Math.min(amount, limit());
      if (what instanceof AEItemKey) {
         AEItemKey itemKey = (AEItemKey)what;
          if (amount > 0L) {
              ItemStack singleStack = itemKey.toStack(1);
             if (this.nbtFilter && isBlocked(itemKey)) {
                return 0L;
             }
            if (!IEMCProxy.INSTANCE.hasValue(singleStack)) {
               return 0L;
            }

            IKnowledgeProvider provider = ProjectEUtil.getKnowledgeProvider(this.owner);
            if (provider == null) {
               return 0L;
            }

            if (mode == Actionable.MODULATE) {
               long sellValue = IEMCProxy.INSTANCE.getSellValue(singleStack);
               BigInteger totalValue = BigInteger.valueOf(sellValue).multiply(BigInteger.valueOf(amount));
               provider.setEmc(provider.getEmc().add(totalValue));
               ServerPlayer player = ProjectEUtil.getPlayer(this.owner);
               if (player != null) {
                  if (provider.addKnowledge(singleStack)) {
                     provider.syncKnowledgeChange(player, IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(singleStack)), true);
                  }

                  provider.syncEmc(player);
               }
            }

            return amount;
         }
      }

      return 0L;
   }

   public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
      amount = Math.min(amount, limit());
      if (!(what instanceof AEItemKey itemKey) || amount <= 0L) {
         return 0L;
      }

      ItemInfo info = ItemInfo.fromStack(itemKey.toStack());
      ItemInfo persistent = IEMCProxy.INSTANCE.getPersistentInfo(info);

      if (this.nbtFilter && hasBlockedNbt(itemKey)) {
         return 0L;
      }

      if (!info.equals(persistent)) {
         return 0L;
      }

      ItemStack stack = persistent.createStack();
      IKnowledgeProvider provider = ProjectEUtil.getKnowledgeProvider(this.owner);
      if (provider == null || !provider.hasKnowledge(stack)) {
         return 0L;
      }

      long itemValue = IEMCProxy.INSTANCE.getValue(stack);
      if (itemValue <= 0L) {
         return 0L;
      }

      BigInteger playerEmc = provider.getEmc();
      BigInteger actualExtract = playerEmc.divide(BigInteger.valueOf(itemValue)).min(BigInteger.valueOf(amount)).min(BigInteger.valueOf(limit()));
      long extractAmount = actualExtract.longValue();
      if (extractAmount <= 0L) {
         return 0L;
      }

      if (mode == Actionable.MODULATE) {
         BigInteger totalCost = BigInteger.valueOf(itemValue).multiply(BigInteger.valueOf(extractAmount));
         provider.setEmc(playerEmc.subtract(totalCost));
         ServerPlayer player = ProjectEUtil.getPlayer(this.owner);
         if (player != null) {
            provider.syncEmc(player);
         }
      }

      return extractAmount;
   }

   private static boolean isIOPortCaller() {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      for (StackTraceElement e : stack) {
         if (e.getClassName().contains("IOPortBlockEntity")) return true;
      }
      return false;
   }

   public void getAvailableStacks(KeyCounter out) {
      if (this.host != null || isIOPortCaller()) {
         IKnowledgeProvider provider = ProjectEUtil.getKnowledgeProvider(this.owner);
         if (provider != null) {
            BigInteger playerEmc = provider.getEmc();
            long lim = limit();
            Iterator var6 = provider.getKnowledge().iterator();

            while(true) {
               BigInteger count;
               AEItemKey key;
               do {
                  do {
                     ItemInfo info;
                     do {
                        long itemValue;
                        do {
                           if (!var6.hasNext()) {
                              return;
                           }

                           info = (ItemInfo)var6.next();
                           itemValue = IEMCProxy.INSTANCE.getValue(info);
                        } while(itemValue <= 0L);

                        count = playerEmc.divide(BigInteger.valueOf(itemValue));
                     } while(count.compareTo(BigInteger.ZERO) <= 0);

                     key = (AEItemKey)KEY_CACHE.computeIfAbsent(info, (i) -> {
                        return AEItemKey.of(i.createStack());
                     });
                  } while(key == null);
               } while(this.nbtFilter && isBlocked(key));

               out.add(key, count.min(BigInteger.valueOf(lim)).longValue());
            }
         }
      }
   }

   public Component getDescription() {
      return Component.translatable("projectcell.ae2.emc_storage_description");
   }
}
