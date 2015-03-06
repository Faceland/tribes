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

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Objects;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import me.topplethenun.tribes.TribesPlugin;
import me.topplethenun.tribes.data.Cell;
import me.topplethenun.tribes.data.Member;
import me.topplethenun.tribes.data.Tribe;
import me.topplethenun.tribes.math.Vec2;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Member damagedMember = plugin.getMemberManager().getMember(damaged.getUniqueId()).or(new Member(damaged
                .getUniqueId()));
        if (!plugin.getMemberManager().hasMember(damagedMember)) {
            plugin.getMemberManager().addMember(damagedMember);
        }
        Member damagerMember = plugin.getMemberManager().getMember(damager.getUniqueId()).or(new Member(damager
                .getUniqueId()));
        if (!plugin.getMemberManager().hasMember(damagerMember)) {
            plugin.getMemberManager().addMember(damagerMember);
        }
        if (damagedMember.getTribe() == null || damagerMember.getTribe() == null) {
            return;
        }
        if (damagedMember.getTribe().equals(damagerMember.getTribe())) {
            MessageUtils.sendMessage(damager, "<red>You cannot damage a member of your tribe!");
            event.setCancelled(true);
            event.setDamage(0);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        Player damaged = event.getEntity();
        Player damager = event.getEntity().getKiller();
        Member damagedMember = plugin.getMemberManager().getMember(damaged.getUniqueId()).or(new Member(damaged
                .getUniqueId()));
        if (!plugin.getMemberManager().hasMember(damagedMember)) {
            plugin.getMemberManager().addMember(damagedMember);
        }
        Member damagerMember = plugin.getMemberManager().getMember(damager.getUniqueId()).or(new Member(damager
                .getUniqueId()));
        if (!plugin.getMemberManager().hasMember(damagerMember)) {
            plugin.getMemberManager().addMember(damagerMember);
        }
        int score = damagedMember.getScore() / 10;
        damagedMember.setScore(damagedMember.getScore() - score);
        damagerMember.setScore(damagerMember.getScore() + score);
        plugin.getMemberManager().removeMember(damagedMember);
        plugin.getMemberManager().removeMember(damagerMember);
        plugin.getMemberManager().addMember(damagedMember);
        plugin.getMemberManager().addMember(damagerMember);
        MessageUtils.sendMessage(damaged, "<red>You lost <white>%amount%<red> score for dying.",
                new String[][]{{"%amount%", score + ""}});
        MessageUtils.sendMessage(damager, "<green>You gained <white>%amount%<green> score for a successful kill.",
                new String[][]{{"%amount%", score + ""}});
    }

}
