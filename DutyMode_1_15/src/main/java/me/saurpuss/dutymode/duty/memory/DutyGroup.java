package me.saurpuss.dutymode.duty.memory;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DutyGroup {

    public static class Builder {
        private String groupName;
        private boolean broadcast, inventory, op, dropItems;
        private GameMode gameMode;
        private long reminder;
        private ItemStack[] items;
        private Map<String, Boolean> temporaryPermissions;
        private List<String> consoleCommandsOn, consoleCommandsOff;

        public Builder(String groupName) {
            this.groupName = groupName;
        }

        public Builder withBroadcast(boolean broadcast) {
            this.broadcast = broadcast;

            return this;
        }

        public Builder withReminder(long reminder) {
            this.reminder = reminder;

            return this;
        }

        public Builder withInventory(boolean inventory) {
            this.inventory = inventory;

            return this;
        }

        public Builder withItems(ItemStack[] items) {
            this.items = items;

            return this;
        }

        public Builder withOp(boolean op) {
            this.op = op;

            return this;
        }

        public Builder withGameMode(GameMode gameMode) {
            this.gameMode = gameMode;

            return this;
        }

        public Builder withDropItems(boolean dropItems) {
            this.dropItems = dropItems;

            return this;
        }

        public Builder withTemporaryPermissions(List<String> permissions) {
            Map<String, Boolean> temporaryPermissions = new HashMap<>();
            permissions.forEach(perm -> temporaryPermissions.put(perm, true));
            this.temporaryPermissions = temporaryPermissions;

            return this;
        }

        public Builder withConsoleCommandsOn(List<String> consoleCommandsOn) {
            this.consoleCommandsOn = consoleCommandsOn;

            return this;
        }

        public Builder withConsoleCommandsOff(List<String> consoleCommandsOff) {
            this.consoleCommandsOff = consoleCommandsOff;

            return this;
        }

        public DutyGroup build() {
            DutyGroup group = new DutyGroup();
            group.groupName = this.groupName;
            group.broadcast = this.broadcast;
            group.reminder = this.reminder;
            group.op = this.op;
            group.inventory = this.inventory;
            group.items = this.items;
            group.gameMode = this.gameMode;
            group.dropItems = this.dropItems;
            group.temporaryPermissions = this.temporaryPermissions;
            group.consoleCommandsOn = this.consoleCommandsOn;
            group.consoleCommandsOff = this.consoleCommandsOff;

            return group;
        }
    }

    private String groupName;
    private boolean broadcast, inventory, op, dropItems;
    private GameMode gameMode;
    private long reminder;
    private ItemStack[] items;
    private Map<String, Boolean> temporaryPermissions;
    private List<String> consoleCommandsOn, consoleCommandsOff;

    private DutyGroup() {}

    public String getGroupName() {
        return groupName;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public boolean isOp() {
        return op;
    }

    public boolean isInventory() {
        return inventory;
    }

    public ItemStack[] getItems() { return items; }

    public boolean isDropItems() {
        return dropItems;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public long getReminder() {
        return reminder;
    }

    public Map<String, Boolean> getTemporaryPermissions() {
        return temporaryPermissions;
    }

    public List<String> getConsoleCommandsOn() {
        return consoleCommandsOn;
    }

    public List<String> getConsoleCommandsOff() {
        return consoleCommandsOff;
    }


    // TODO activate duty state on player
}

