/*
 * This file is part of Tribes, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.tribes.managers;

import com.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;

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

    public Optional<Tribe> getTribe(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return tribeMap.containsKey(uuid) ? Optional.of(tribeMap.get(uuid)) : Optional.<Tribe>absent();
    }

    public Optional<Tribe> getTribeByName(String name) {
        Preconditions.checkNotNull(name);
        for (Tribe tribe : getTribes()) {
            if (name.equals(tribe.getName())) {
                return Optional.of(tribe);
            }
        }
        return Optional.absent();
    }

}
