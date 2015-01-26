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
package me.topplethenun.tribes.math;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Vec2 is a representation of a {@link org.bukkit.Chunk} without any data other than world, x, and z.
 */
public final class Vec2 {

    private final World world;
    private final int x;
    private final int z;

    private Vec2(World world, int x, int z) {
        Preconditions.checkNotNull(world, "world cannot be null");
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public static Vec2 fromCoordinates(String worldName, int x, int z) {
        Preconditions.checkNotNull(worldName, "worldName cannot be null");
        World w = Bukkit.getWorld(worldName);
        Preconditions.checkNotNull(w, "must be a valid world");
        return new Vec2(w, x, z);
    }

    /**
     * Creates a new Vec2 from a given Chunk.
     * @param c Chunk to create a Vec2
     * @return new Vec2
     */
    public static Vec2 fromChunk(Chunk c) {
        Preconditions.checkNotNull(c);
        return new Vec2(c.getWorld(), c.getX(), c.getZ());
    }

    /**
     * Gets and returns the {@link org.bukkit.World} of this Vec2.
     * @return World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets and returns the X value of this Vec2.
     * @return x value
     */
    public int getX() {
        return x;
    }

    /**
     * Gets and returns the Z value of this Vec2.
     * @return z value
     */
    public int getZ() {
        return z;
    }

    /**
     * Calculates the squared distance between this Vec2 and the given Vec2. Fails if {@code other} is null or does
     * not have the same world.
     * @param other Vec2 to check
     * @return squared distance
     */
    public int distanceSquared(Vec2 other) {
        Preconditions.checkNotNull(other);
        Preconditions.checkState(getWorld().equals(other.getWorld()));
        int xDiff = Math.abs(getX() - other.getX());
        int zDiff = Math.abs(getZ() - other.getZ());
        return (int) Math.ceil(Math.pow(xDiff, 2) + Math.pow(zDiff, 2));
    }

    /**
     * Calculates the distance between this Vec2 and the given Vec2.
     * @param other Vec2 to check
     * @return distance
     */
    public int distance(Vec2 other) {
        return (int) Math.ceil(Math.sqrt(distanceSquared(other)));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec2)) {
            return false;
        }
        Vec2 other = (Vec2) o;
        return getWorld().equals(other.getWorld()) && getX() == other.getX() && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return getWorld().getName() + ":" + getX() + ":" + getZ();
    }

}
