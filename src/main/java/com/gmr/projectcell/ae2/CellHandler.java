package com.gmr.projectcell.ae2;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.gmr.projectcell.ProjectCell;
import com.gmr.projectcell.item.EMCStorageCell;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CellHandler implements ICellHandler {
   public static final CellHandler INSTANCE = new CellHandler();

   public boolean isCell(ItemStack itemStack) {
      return itemStack.getItem() instanceof EMCStorageCell;
   }

   @Nullable
   public StorageCell getCellInventory(ItemStack itemStack, @Nullable ISaveProvider host) {
      if (!this.isCell(itemStack)) {
         return null;
      } else {
          UUID owner = itemStack.get(ProjectCell.OWNER_UUID);
         if (owner == null) return null;
         boolean nbtFilter = itemStack.getOrDefault(ProjectCell.NBT_FILTER, false);
         return new EMCMEStorage(owner, nbtFilter, host);
      }
   }
}
