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

public class Vec3f extends Vec3 {

    private final float pitch;
    private final float yaw;

    protected Vec3f(World w, int x, int y, int z, float pitch, float yaw) {
        super(w, x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec3f)) return false;
        if (!super.equals(o)) return false;
        Vec3f vec3f = (Vec3f) o;
        return Objects.equal(getPitch(), vec3f.getPitch()) &&
                Objects.equal(getYaw(), vec3f.getYaw());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getPitch(), getYaw());
    }

    @Override
    public String toString() {
        return getWorld().getName() + ":" + getX() + ":" + getY() + ":" + getZ() + ":" + getPitch() + ":" + getYaw();
    }

    public static Vec3f fromCoordinates(String worldName, int x, int y, int z, float pitch, float yaw) {
        Preconditions.checkNotNull(worldName, "worldName cannot be null");
        World w = Bukkit.getWorld(worldName);
        Preconditions.checkNotNull(w, "must be a valid world");
        return new Vec3f(w, x, y, z, pitch, yaw);
    }

    public static Vec3f fromLocation(Location location) {
        Preconditions.checkNotNull(location, "location cannot be null");
        return new Vec3f(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ(), location.getPitch(), location.getYaw());
    }

}
