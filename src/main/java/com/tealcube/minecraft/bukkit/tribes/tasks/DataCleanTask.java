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
