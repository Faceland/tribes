package me.topplethenun.tribes.managers;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpManager {

    private final Map<UUID, Long> tagTime;

    public PvpManager() {
        tagTime = new HashMap<>();
    }

    public long getTime(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return tagTime.containsKey(uuid) ? tagTime.get(uuid) : 0;
    }

    public void setTime(UUID uuid, long time) {
        Preconditions.checkNotNull(uuid);
        tagTime.put(uuid, time);
    }

    public void clearTime(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        tagTime.remove(uuid);
    }

}
