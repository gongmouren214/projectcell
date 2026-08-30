package com.gmr.projectcell.client;

import com.gmr.projectcell.Config;
import com.gmr.projectcell.ProjectCell;
import com.gmr.projectcell.util.EMCFormatUtil;
import java.math.BigInteger;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@EventBusSubscriber(modid = ProjectCell.MODID, bus = Bus.GAME, value = {Dist.CLIENT})
public class EMCDisplay {
   public static final Overlay INSTANCE = new Overlay();
   private static final int PADDING_X = 2;
   private static final int PADDING_Y = 2;
   private static BigInteger emc = BigInteger.ZERO;
   private static final BigInteger[] history = new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
   private static BigInteger lastEMC = BigInteger.ZERO;
   private static int tick = 0;
   private static int repeatedFailures = 0;

   @Nullable
   private static LocalPlayer getPlayer() {
      return Minecraft.getInstance().player;
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      if (!Config.CLIENT.spec.isLoaded() || !Config.CLIENT.emcDisplay.get()) {
         return;
      }
      LocalPlayer player = getPlayer();
      tick++;
      if (player != null && tick >= 20) {
         tick = 0;
         IKnowledgeProvider provider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
         if (provider == null) {
            repeatedFailures++;
            if (repeatedFailures < 10) {
               ProjectCell.LOGGER.warn("Failed to get knowledge provider in EMCDisplay");
            } else if (repeatedFailures == 10) {
               ProjectCell.LOGGER.error("Failed to get knowledge provider 10 times, no longer logging.");
            }
            return;
         }
         repeatedFailures = 0;
         emc = provider.getEmc();
         history[1] = history[0];
         history[0] = emc.subtract(lastEMC);
         lastEMC = emc;
      }
   }

   private static void reset() {
      emc = lastEMC = BigInteger.ZERO;
      tick = 0;
      repeatedFailures = 0;
   }

   @SubscribeEvent
   public static void clientDisconnect(LoggingOut event) {
      reset();
   }

   @SubscribeEvent
   public static void onWorldUnload(Unload event) {
      reset();
   }

   @EventBusSubscriber(modid = ProjectCell.MODID, bus = Bus.MOD, value = {Dist.CLIENT})
   public static class Overlay implements Layer {
      @SubscribeEvent
      public static void onRegisterLayers(RegisterGuiLayersEvent event) {
         event.registerAboveAll(ProjectCell.rl("emc_display"), INSTANCE);
      }

      public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
         if (!Config.CLIENT.spec.isLoaded() || !Config.CLIENT.emcDisplay.get()) {
            return;
         }
         Minecraft mc = Minecraft.getInstance();
         BigInteger avg = history[0].add(history[1]);
         String str = EMCFormatUtil.format(emc);
         if (!avg.equals(BigInteger.ZERO)) {
            String sign = avg.compareTo(BigInteger.ZERO) > 0 ? "§a+" : "§c-";
            str = str + " " + sign + EMCFormatUtil.format(avg.abs()) + "/s";
         }
         String text = "EMC: " + str;
         guiGraphics.drawString(mc.font, text, PADDING_X, PADDING_Y, 0xffffff);
      }
   }
}
