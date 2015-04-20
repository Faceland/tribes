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

import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DataSaveTask extends BukkitRunnable {

    private final TribesPlugin plugin;

    public DataSaveTask(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getDataStorage().saveTribes(plugin.getTribeManager().getTribes());
        plugin.getDataStorage().saveCells(plugin.getCellManager().getCells());
        plugin.getDataStorage().saveMembers(plugin.getMemberManager().getMembers());
    }

}