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
package com.tealcube.minecraft.bukkit.tribes.data;

import com.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.math.Vec3;
import com.tealcube.minecraft.bukkit.tribes.math.Vec3f;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Tribe {

    private final UUID uniqueId;
    private UUID owner;
    private String name;
    private Map<UUID, Rank> memberRankMap;
    private Map<Vec2, Cell> claimedLandMap;
    private boolean validated;
    private Level level;
    private Vec3f home;

    public Tribe(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.memberRankMap = new ConcurrentHashMap<>();
        this.claimedLandMap = new ConcurrentHashMap<>();
        this.level = Level.TINY;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Rank getRank(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid cannot be null");
        return memberRankMap.containsKey(uuid) ? memberRankMap.get(uuid) : Rank.GUEST;
    }

    public void setRank(UUID uuid, Rank rank) {
        Preconditions.checkNotNull(uuid, "uuid cannot be null");
        Preconditions.checkNotNull(rank, "rank cannot be null");
        if (rank == Rank.GUEST) {
            memberRankMap.remove(uuid);
        } else {
            memberRankMap.put(uuid, rank);
        }
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(memberRankMap.keySet());
    }

    public Set<Cell> getOwnedCells() {
        return new HashSet<>(claimedLandMap.values());
    }

    public boolean ownsCell(Vec2 vec) {
        Preconditions.checkNotNull(vec, "vec cannot be null");
        return claimedLandMap.containsKey(vec) && getUniqueId().equals(claimedLandMap.get(vec).getOwner());
    }

    public void claimCell(Cell cell) {
        Preconditions.checkNotNull(cell, "cell cannot be null");
        cell.setOwner(getUniqueId());
        claimedLandMap.put(cell.getLocation(), cell);
    }

    public void unclaimCell(Cell cell) {
        Preconditions.checkNotNull(cell, "cell cannot be null");
        cell.setOwner(null);
        claimedLandMap.remove(cell.getLocation());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Vec3f getHome() {
        return home;
    }

    public void setHome(Vec3f home) {
        this.home = home;
    }

    public enum Permission {
        BREAK,
        INTERACT,
        KICK,
        INVITE,
        KICK_IMMUNE,
        PROMOTE
    }

    public enum Rank {
        LEADER(Permission.values()),
        CAPTAIN(Permission.INTERACT, Permission.INVITE, Permission.BREAK, Permission.KICK, Permission.KICK_IMMUNE, Permission.PROMOTE),
        OFFICER(Permission.INTERACT, Permission.INVITE, Permission.BREAK, Permission.KICK),
        MEMBER(Permission.INTERACT, Permission.INVITE),
        RECRUIT(Permission.INTERACT),
        GUEST();

        private final List<Permission> permissions;

        Rank(Permission... permissions) {
            this.permissions = Arrays.asList(permissions);
        }

        public List<Permission> getPermissions() {
            return permissions;
        }

        public static Rank fromString(String s) {
            for (Rank rank : values()) {
                if (rank.name().equals(s)) {
                    return rank;
                }
            }
            return GUEST;
        }
    }

    public enum Level {
        TINY(4, 4, 0),
        SMALL(8, 8, 60000),
        MEDIUM(14, 14, 120000),
        LARGE(22, 22, 240000),
        HUGE(32, 32, 480000);

        private final int chunks;
        private final int members;
        private final double price;

        Level(int chunks, int members, double price) {
            this.chunks = chunks;
            this.members = members;
            this.price = price;
        }

        public int getChunks() {
            return chunks;
        }

        public int getMembers() {
            return members;
        }

        public double getPrice() {
            return price;
        }
    }

}
