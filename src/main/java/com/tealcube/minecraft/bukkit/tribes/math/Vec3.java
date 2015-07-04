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
import org.bukkit.Location;
import org.bukkit.World;

public class Vec3 extends Vec2 {

    private final int y;

    protected Vec3(World world, int x, int y, int z) {
        super(world, x, z);
        this.y = y;
    }

    public static Vec3 fromCoordinates(String worldName, int x, int y, int z) {
        Preconditions.checkNotNull(worldName, "worldName cannot be null");
        World w = Bukkit.getWorld(worldName);
        Preconditions.checkNotNull(w, "must be a valid world");
        return new Vec3(w, x, y, z);
    }

    public static Vec3 fromLocation(Location location) {
        Preconditions.checkNotNull(location, "location cannot be null");
        return new Vec3(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec3)) return false;
        if (!super.equals(o)) return false;
        Vec3 vec3 = (Vec3) o;
        return Objects.equal(getY(), vec3.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getY());
    }

    @Override
    public int distanceSquared(Vec2 other) {
        Preconditions.checkNotNull(other);
        Preconditions.checkState(getWorld().equals(other.getWorld()));
        if (other instanceof Vec3) {
            int xDiff = Math.abs(getX() - other.getX());
            int yDiff = Math.abs(getY() - ((Vec3) other).getY());
            int zDiff = Math.abs(getZ() - other.getZ());
            return (int) Math.ceil(Math.pow(xDiff, 2) + Math.pow(yDiff, 2) + Math.pow(zDiff, 2));
        }
        return super.distanceSquared(other);
    }

    @Override
    public Vec2 add(int x, int z) {
        return super.add(x, z);
    }

    @Override
    public String toString() {
        return getWorld().getName() + ":" + getX() + ":" + getY() + ":" + getZ();
    }
}
