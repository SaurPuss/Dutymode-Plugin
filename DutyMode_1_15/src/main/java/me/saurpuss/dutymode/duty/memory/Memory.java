package me.saurpuss.dutymode.duty.memory;

import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Memory {

    private UUID uuid;
    private Snapshot snapshot;
    private DutyGroup group;
    private PermissionAttachment attachment;
    private BukkitTask reminderTask;

    public Memory(UUID uuid, Snapshot snapshot, DutyGroup group, PermissionAttachment attachment,
                  BukkitTask reminderTask) {
        this.uuid = uuid;
        this.snapshot = snapshot;
        this.group = group;
        this.attachment = attachment;
        this.reminderTask = reminderTask;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public DutyGroup getGroup() {
        return group;
    }

    public void setGroup(DutyGroup group) {
        this.group = group;
    }

    public PermissionAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(PermissionAttachment attachment) {
        this.attachment = attachment;
    }

    public BukkitTask getReminderTask() {
        return reminderTask;
    }

    public void setReminderTask(BukkitTask reminderTask) {
        this.reminderTask = reminderTask;
    }
}
