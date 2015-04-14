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
package com.tealcube.minecraft.bukkit.tribes.math;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Vec3 extends Vec2 {

    private final int y;

    private Vec3(World world, int x, int y, int z) {
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
