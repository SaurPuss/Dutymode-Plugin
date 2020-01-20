package me.saurpuss.dutymode.duty.memory;

import me.saurpuss.dutymode.DutyMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DutyItemFactory {

    private DutyMode plugin;

    public DutyItemFactory(DutyMode plugin) {
        this.plugin = plugin;
    }

    public ItemStack itemFromDutyGroup(String itemString) {
        // TODO

        return new ItemStack(Material.AIR, 1);
    }
}
