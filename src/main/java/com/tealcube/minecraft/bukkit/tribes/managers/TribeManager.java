/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.tribes.managers;

import com.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Optional;
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
        tribeMap.put(tribe.getUniqueId(), tribe);
    }

    public void removeTribe(Tribe tribe) {
        Preconditions.checkNotNull(tribe);
        tribeMap.remove(tribe.getUniqueId());
    }

    public void removeTribe(UUID uuid) {
        Preconditions.checkNotNull(uuid);
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
