package com.gmr.projectcell;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class Config {
   public static final Config.Client CLIENT = new Config.Client();
   public static final Config.Server SERVER = new Config.Server();

   public static void register(ModContainer modContainer) {
      modContainer.registerConfig(Type.CLIENT, CLIENT.spec, "projectcell/client.toml");
      modContainer.registerConfig(Type.SERVER, SERVER.spec, "projectcell/server.toml");
   }

   public static class Server {
      private final Builder builder = new Builder();
      public final ModConfigSpec spec;
      public final BooleanValue limitItemCount = this.builder
         .comment("Limit the maximum item count per type in the EMC storage cell to 2147483647.")
         .define("limitItemCount", true);

      private Server() {
         this.spec = this.builder.build();
      }
   }

   public static class Client {
      private final Builder builder = new Builder();
      public final ModConfigSpec spec;
      public final BooleanValue emcDisplay = this.builder
         .comment("Displays your current EMC and gained EMC/s in the top-left corner.")
         .define("emcDisplay", false);
      public final BooleanValue enableLearnedTooltip = this.builder
         .comment("Shows learned (✓) or unlearned (✗) indicator on item tooltips.")
         .define("enableLearnedTooltip", false);
      public final BooleanValue formatEMC = this.builder
         .comment("Enable EMC value formatting with suffixes (M/B/T...).")
         .define("formatEMC", true);
      public final BooleanValue emcShortNames = this.builder
         .comment("Use short unit names (M/B/T) instead of long names (Million/Billion/Trillion).")
         .define("emcShortNames", false);

      private Client() {
         this.spec = this.builder.build();
      }
   }
}
