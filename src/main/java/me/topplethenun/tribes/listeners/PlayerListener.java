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
package me.topplethenun.tribes.listeners;

import me.topplethenun.tribes.TribesPlugin;
import me.topplethenun.tribes.data.Cell;
import me.topplethenun.tribes.data.Tribe;
import me.topplethenun.tribes.math.Vec2;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.nunnerycode.facecore.utilities.MessageUtils;
import org.nunnerycode.kern.shade.google.common.base.Objects;
import org.nunnerycode.kern.shade.google.common.base.Optional;

public class PlayerListener implements Listener {

    private final TribesPlugin plugin;

    public PlayerListener(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to
                .getBlockZ() && from.getWorld().equals(to.getWorld())) {
            return;
        }
        Vec2 toVec = Vec2.fromChunk(to.getChunk());
        Vec2 fromVec = Vec2.fromChunk(from.getChunk());
        Cell toCell = plugin.getCellManager().getCell(toVec).or(new Cell(toVec));
        Cell fromCell = plugin.getCellManager().getCell(fromVec).or(new Cell(fromVec));
        if (Objects.equal(toCell, fromCell) || Objects.equal(toCell.getOwner(), fromCell.getOwner())) {
            return;
        }
        if (toCell.getOwner() == null) {
            MessageUtils.sendMessage(event.getPlayer(), "<gray>Entering unclaimed land");
            return;
        }
        Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribe(toCell.getOwner());
        if (!tribeOptional.isPresent()) {
            return;
        }
        MessageUtils.sendMessage(event.getPlayer(), "<gray>Owner: <white>%owner%", new String[][]{{"%owner%",
                tribeOptional.get().getName()}});
    }

}
