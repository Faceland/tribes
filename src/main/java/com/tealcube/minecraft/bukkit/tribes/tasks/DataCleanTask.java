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
package com.tealcube.minecraft.bukkit.tribes.tasks;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class DataCleanTask extends BukkitRunnable {

    private final TribesPlugin plugin;

    public DataCleanTask(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // clearing member's guilds if they don't exist
        for (Member m : plugin.getMemberManager().getMembers()) {
            if (m.getTribe() == null) {
                continue;
            }
            Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribe(m.getTribe());
            if (tribeOptional.isPresent()) {
                continue;
            }
            m.setTribe(null);
        }

        // removing tribes if they don't have any members
        Set<Tribe> tribes = plugin.getTribeManager().getTribes();
        for (Tribe t : tribes) {
            if (t.getName() != null) {
                t.setName(ChatColor.stripColor(TextUtils.color(t.getName())));
            }
            if (t.getMembers().isEmpty() || plugin.getMemberManager().getMembersWithTribe(t.getUniqueId()).isEmpty()) {
                plugin.getTribeManager().removeTribe(t);
            }
        }

        // removing cells if they don't have an owner
        Set<Cell> cells = plugin.getCellManager().getCells();
        for (Cell c : cells) {
            if (c.getOwner() != null) {
                Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribe(c.getOwner());
                if (tribeOptional.isPresent()) {
                    continue;
                }
                c.setOwner(null);
            }
            plugin.getCellManager().placeCell(c.getLocation(), null);
        }
    }

}
