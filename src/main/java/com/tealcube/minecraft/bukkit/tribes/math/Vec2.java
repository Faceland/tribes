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
package com.tealcube.minecraft.bukkit.tribes.math;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Vec2 is a representation of a {@link org.bukkit.Chunk} without any data other than world, x, and z.
 */
public class Vec2 {

    private final World world;
    private final int x;
    private final int z;

    protected Vec2(World world, int x, int z) {
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

    public Vec2 add(int x, int z) {
        return new Vec2(world, getX() + x, getZ() + z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2)) return false;
        Vec2 vec2 = (Vec2) o;
        return Objects.equal(getX(), vec2.getX()) &&
                Objects.equal(getZ(), vec2.getZ()) &&
                Objects.equal(getWorld(), vec2.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getWorld(), getX(), getZ());
    }

    @Override
    public String toString() {
        return getWorld().getName() + ":" + getX() + ":" + getZ();
    }

}
