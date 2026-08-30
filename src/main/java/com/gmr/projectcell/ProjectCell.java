package com.gmr.projectcell;

import appeng.api.storage.StorageCells;
import com.gmr.projectcell.ae2.CellHandler;
import com.gmr.projectcell.item.EMCStorageCell;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import org.slf4j.Logger;

@Mod(ProjectCell.MODID)
public class ProjectCell {
   public static final String MODID = "projectcell";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final Items ITEMS = DeferredRegister.createItems(MODID);
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
   public static final DeferredItem<EMCStorageCell> EMC_STORAGE_CELL = ITEMS.register("emc_storage_cell", () -> new EMCStorageCell(new Properties()));
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER_UUID = DATA_COMPONENT_TYPES.register(
      "owner_uuid", () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build()
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NBT_FILTER = DATA_COMPONENT_TYPES.register(
      "nbt_filter", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );

   public ProjectCell(IEventBus modEventBus, ModContainer modContainer) {
      Config.register(modContainer);
      modEventBus.addListener(this::commonSetup);
      ITEMS.register(modEventBus);
      DATA_COMPONENT_TYPES.register(modEventBus);
      modEventBus.addListener(this::addCreative);
   }

   public static ResourceLocation rl(String path) {
      return ResourceLocation.fromNamespaceAndPath(MODID, path);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> StorageCells.addCellHandler(CellHandler.INSTANCE));
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
         event.accept(EMC_STORAGE_CELL);
      }
   }
}
