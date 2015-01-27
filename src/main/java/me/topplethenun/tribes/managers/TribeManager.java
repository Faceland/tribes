package me.topplethenun.tribes.managers;

import com.google.common.base.Preconditions;
import me.topplethenun.tribes.data.Tribe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TribeManager {

    private final Map<UUID, Tribe> tribeMap;

    public TribeManager() {
        tribeMap = new ConcurrentHashMap<>();
    }

    public void addTribe(Tribe tribe) {
        Preconditions.checkNotNull(tribe);
        Preconditions.checkState(!tribeMap.containsKey(tribe.getUniqueId()));
        tribeMap.put(tribe.getUniqueId(), tribe);
    }

    public void removeTribe(Tribe tribe) {
        Preconditions.checkNotNull(tribe);
        Preconditions.checkState(tribeMap.containsKey(tribe.getUniqueId()));
        tribeMap.remove(tribe.getUniqueId());
    }

    public void removeTribe(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkState(tribeMap.containsKey(uuid));
        tribeMap.remove(uuid);
    }

    public boolean hasTribe(Tribe tribe) {
        Preconditions.checkNotNull(tribe);
        return tribeMap.containsKey(tribe.getUniqueId());
    }

    public boolean hasTribe(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return tribeMap.containsKey(uuid);
    }

    public Set<Tribe> getTribes() {
        return new HashSet<>(tribeMap.values());
    }

    public Tribe getTribe(UUID uuid) {
        Preconditions.checkState(hasTribe(uuid));
        return tribeMap.get(uuid);
    }

}
