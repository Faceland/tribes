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
package com.tealcube.minecraft.bukkit.tribes.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Objects;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import com.tealcube.minecraft.bukkit.tribes.managers.PvpManager;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.utils.ScoreboardUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TribesPlugin plugin;

    public PlayerListener(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Member member = plugin.getMemberManager().getMember(event.getPlayer().getUniqueId()).or(new Member(event.getPlayer().getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                ScoreboardUtils.setPrefix(event.getPlayer(),
                        (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726') + ChatColor.WHITE);
                ScoreboardUtils.setSuffix(event.getPlayer(),
                        (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726'));
                ScoreboardUtils.updateMightDisplay(member);
            }
        }, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Member member = plugin.getMemberManager().getMember(event.getPlayer().getUniqueId()).or(new Member(event.getPlayer().getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        PvpManager.PvpData data = plugin.getPvpManager().getData(member.getUniqueId());
        member.setPvpState(member.getTribe() != null ? Member.PvpState.ON : Member.PvpState.OFF);
        ScoreboardUtils.setPrefix(event.getPlayer(),
                (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726') + ChatColor.WHITE);
        ScoreboardUtils.setSuffix(event.getPlayer(),
                (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726'));
        if (data.time() != 0 && System.currentTimeMillis() - data.time() < 5000 && data.tagger() != null) {
            Member tagger = plugin.getMemberManager().getMember(data.tagger()).or(new Member(data.tagger()));
            if (!plugin.getMemberManager().hasMember(tagger)) {
                plugin.getMemberManager().addMember(tagger);
            }
            Player p = Bukkit.getPlayer(tagger.getUniqueId());
            ScoreboardUtils.setPrefix(p,
                    (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726') + ChatColor.WHITE);
            ScoreboardUtils.setSuffix(p, (member.getPvpState() == Member.PvpState.ON ? ChatColor.RED : ChatColor.WHITE) + String.valueOf('\u2726'));
            //event.getPlayer().setHealth(0D);
            int scoreChange = (int) (member.getScore() * 0.05);
            tagger.setScore(tagger.getScore() + scoreChange);
            member.setScore(member.getScore() - scoreChange);
            MessageUtils.sendMessage(Bukkit.getPlayer(tagger.getUniqueId()), "<gray>Your score is now <white>%amount%<gray>.",
                    new String[][]{{"%amount%", "" + tagger.getScore()}});
            ScoreboardUtils.updateMightDisplay(tagger);
            ScoreboardUtils.updateMightDisplay(member);
            plugin.getPvpManager().clearTime(member.getUniqueId());
        }
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
            MessageUtils.sendMessage(event.getPlayer(), "<gray>You have left guild territory.");
            return;
        }
        Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribe(toCell.getOwner());
        if (!tribeOptional.isPresent()) {
            return;
        }
        MessageUtils.sendMessage(event.getPlayer(), "<gold>You have entered <white>%owner%<gold>'s territory!", new
            String[][]{{"%owner%",
                tribeOptional.get().getName()}});
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) ||
                !(event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile) event
                        .getDamager()).getShooter() instanceof Player))) {
            return;
        }
        Player damaged = (Player) event.getEntity();
        Player damager = (event.getDamager() instanceof Projectile ? (Player) ((Projectile) event.getDamager()).getShooter()
                : (Player) event.getDamager());
        Member damagedMember = plugin.getMemberManager().getMember(damaged.getUniqueId()).or(new Member(damaged
                .getUniqueId()));
        PvpManager.PvpData oldData = plugin.getPvpManager().getData(damaged.getUniqueId());
        PvpManager.PvpData newData = oldData.withTime(System.currentTimeMillis()).withTagger(damager.getUniqueId());
        plugin.getPvpManager().setData(damaged.getUniqueId(), newData);
        if (!plugin.getMemberManager().hasMember(damagedMember)) {
            plugin.getMemberManager().addMember(damagedMember);
        }
        Member damagerMember = plugin.getMemberManager().getMember(damager.getUniqueId()).or(new Member(damager
                .getUniqueId()));
        if (!plugin.getMemberManager().hasMember(damagerMember)) {
            plugin.getMemberManager().addMember(damagerMember);
        }
        Duelist duelist = DuelistManager.getDuelist(damagerMember.getUniqueId());
        if (duelist.getTarget() != null && duelist.getTarget().equals(damagedMember.getUniqueId())) {
            return;
        }
        if (damagedMember.getPvpState() == Member.PvpState.OFF || damagerMember.getPvpState() == Member.PvpState.OFF) {
            MessageUtils.sendMessage(damager, "<red>You cannot PvP unless both parties are in PvP mode.");
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }
        if (damagedMember.getTribe() != null) {
            if (damagedMember.getTribe().equals(damagerMember.getTribe())) {
                MessageUtils.sendMessage(damager, "<red>You cannot damage a member of your guild!");
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }
            Optional<Cell> cellOptional = plugin.getCellManager().getCell(Vec2.fromChunk(damaged.getLocation().getChunk()));
            if (!cellOptional.isPresent()) {
                return;
            }
            Cell cell = cellOptional.get();
            if (cell.getOwner() == null) {
                return;
            }
            if (damagedMember.getTribe().equals(cell.getOwner())) {
                MessageUtils.sendMessage(damager, "<red>You cannot fight on guild land.");
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }
        }
        if (damagerMember.getTribe() != null) {
            if (damagerMember.getTribe().equals(damagedMember.getTribe())) {
                MessageUtils.sendMessage(damager, "<red>You cannot damage a member of your guild!");
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }
            Optional<Cell> cellOptional = plugin.getCellManager().getCell(Vec2.fromChunk(damager.getLocation().getChunk()));
            if (!cellOptional.isPresent()) {
                return;
            }
            Cell cell = cellOptional.get();
            if (cell.getOwner() == null) {
                return;
            }
            if (damagerMember.getTribe().equals(cell.getOwner())) {
                MessageUtils.sendMessage(damager, "<red>You cannot fight on guild land.");
                event.setCancelled(true);
                event.setDamage(0);
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player damaged = event.getEntity();
        Player damager = event.getEntity().getKiller();
        if (damager == null) {
            return;
        }
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
        int changeScore = damagedMember.getScore() / 10;
        damagedMember.setScore(damagedMember.getScore() - changeScore);
        damagerMember.setScore(damagerMember.getScore() + changeScore);
        plugin.getMemberManager().removeMember(damagedMember);
        plugin.getMemberManager().removeMember(damagerMember);
        plugin.getMemberManager().addMember(damagedMember);
        plugin.getMemberManager().addMember(damagerMember);
        MessageUtils.sendMessage(damaged, "<red>- <white>%amount%<red> Might.",
                new String[][]{{"%amount%", changeScore + ""}});
        MessageUtils.sendMessage(damager, "<green>+ <white>%amount%<green> Might!",
                new String[][]{{"%amount%", changeScore + ""}});
        ScoreboardUtils.updateMightDisplay(damagedMember);
        ScoreboardUtils.updateMightDisplay(damagerMember);
    }

    @EventHandler
    public void onDuelEnd(DuelEndEvent event) {
        if (event.getDuel().isTie()) {
            return;
        }
        Member winner = plugin.getMemberManager().getMember(event.getDuel().getWinner()).or(new Member(event.getDuel().getWinner()));
        Member loser = plugin.getMemberManager().getMember(event.getDuel().getLoser()).or(new Member(event.getDuel().getLoser()));
        int changeScore = loser.getScore() / 20;
        winner.setScore(winner.getScore() + changeScore);
        loser.setScore(loser.getScore() - changeScore);
        if (!plugin.getMemberManager().hasMember(winner)) {
            plugin.getMemberManager().addMember(winner);
        }
        if (!plugin.getMemberManager().hasMember(loser)) {
            plugin.getMemberManager().addMember(loser);
        }

        Player wPlayer = Bukkit.getPlayer(winner.getUniqueId());
        if (wPlayer != null) {
            MessageUtils.sendMessage(wPlayer, "<green>+ <white>%amount%<green> Might!",
                    new String[][]{{"%amount%", changeScore + ""}});
        }
        Player lPlayer = Bukkit.getPlayer(loser.getUniqueId());
        if (lPlayer != null) {
            MessageUtils.sendMessage(lPlayer, "<red>- <white>%amount%<red> Might.",
                    new String[][]{{"%amount%", changeScore + ""}});
        }
        ScoreboardUtils.updateMightDisplay(winner);
        ScoreboardUtils.updateMightDisplay(loser);
    }

}
