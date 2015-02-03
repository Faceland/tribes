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
package me.topplethenun.tribes.commands;

import me.topplethenun.tribes.TribesPlugin;
import me.topplethenun.tribes.data.Cell;
import me.topplethenun.tribes.data.Member;
import me.topplethenun.tribes.data.Tribe;
import me.topplethenun.tribes.math.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nunnerycode.facecore.utilities.MessageUtils;
import org.nunnerycode.kern.apache.commons.lang3.text.WordUtils;
import org.nunnerycode.kern.methodcommand.Arg;
import org.nunnerycode.kern.methodcommand.Command;
import org.nunnerycode.kern.shade.google.common.base.Optional;

import java.util.UUID;

public class TribeCommand {

    private final TribesPlugin plugin;

    public TribeCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "tribe", description = "base tribe command", onlyPlayers = false,
            permissions = "tribes.command")
    public void baseCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "<red>Only players can execute the base command.");
            return;
        }
        Player player = (Player) sender;
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        MessageUtils.sendMessage(player, "<green><====||====||====||====>");
        MessageUtils.sendMessage(player, "<white>%player%", new String[][]{{"%player%", player.getDisplayName()}});
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<red>Not a member of any Tribe");
        } else {
            Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
            MessageUtils.sendMessage(player, "<white>%rank%<dark green> of <white>%tribe%", new String[][]{
                    {"%rank%", WordUtils.capitalizeFully(member.getRank().name())},
                    {"%tribe%", !tribe.isValidated() ? tribe.getName() : "a nonvalidated tribe"}
            });
            int cap = plugin.getSettings().getInt("config.cells-per-member", 1) * plugin.getMemberManager()
                    .getMembersWithTribe(tribe.getUniqueId()).size();
            int numOfCells = plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId()).size();
            MessageUtils.sendMessage(player, "<gray>Your tribe has claimed <white>%amount%<gray>/<white>%cap%<gray> " +
                    "cells");
            for (Tribe.Permission permission : Tribe.Permission.values()) {
                if (permission == Tribe.Permission.KICK_IMMUNE || !tribe.isValidated()) {
                    continue;
                }
                if (member.getRank().getPermissions().contains(permission)) {
                    MessageUtils.sendMessage(player, "<gray>You <green>CAN<gray> " + permission.name().toLowerCase());
                } else {
                    MessageUtils.sendMessage(player, "<gray>You <red>CAN'T<gray> " + permission.name().toLowerCase());
                }
            }
        }
        MessageUtils.sendMessage(player, "<dark green>Score: <white>%score%", new String[][]{{"%score%", member
                .getScore() + ""}});
        MessageUtils.sendMessage(player, "<green><====||====||====||====>");
    }

    @Command(identifier = "tribe create", onlyPlayers = false, permissions = "tribes.command.create")
    public void createByConsoleSubcommand(CommandSender sender, @Arg(name = "player", def = "") String playerName) {
        Player player;
        if (playerName.equals("")) {
            if (!(sender instanceof Player)) {
                MessageUtils
                        .sendMessage(sender, "<red>Must be a player to use this command without specifying a player.");
                return;
            } else {
                player = (Player) sender;
            }
        } else {
            player = Bukkit.getPlayer(playerName);
            if (player == null) {
                MessageUtils.sendMessage(sender, "<red>That player is not online right now.");
                return;
            }
        }
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() != null) {
            MessageUtils.sendMessage(sender, "<red>You cannot create a tribe for someone if they're already in one.");
            MessageUtils.sendMessage(player, "<red>A tribe cannot be created for you if you're already in one.");
            return;
        }
        Tribe tribe = new Tribe(UUID.randomUUID());
        tribe.setRank(member.getUniqueId(), Tribe.Rank.LEADER);
        tribe.setOwner(member.getUniqueId());
        member.setTribe(tribe.getUniqueId());
        member.setRank(Tribe.Rank.LEADER);
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You created a tribe!");
        MessageUtils.sendMessage(player, "<green>You are now the leader of a tribe!");
    }

    @Command(identifier = "tribe claim", onlyPlayers = true, permissions = "tribes.command.claim")
    public void claimSubcommand(Player player) {
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<red>You cannot claim if you are not part of a tribe.");
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        Vec2 vec2 = Vec2.fromChunk(chunk);
        Cell cell = plugin.getCellManager().getCell(vec2).or(new Cell(vec2));
        if (cell.getOwner() != null) {
            MessageUtils.sendMessage(player, "<red>You cannot claim a cell if it's already claimed.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (!tribe.isValidated()) {
            MessageUtils.sendMessage(player, "<red>You cannot claim if your tribe is not validated.");
            return;
        }
        int cap = plugin.getSettings().getInt("config.cells-per-member", 1) * plugin.getMemberManager()
                .getMembersWithTribe(tribe.getUniqueId()).size();
        int numOfCells = plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId()).size();
        if (numOfCells >= cap) {
            MessageUtils.sendMessage(player, "<red>You cannot claim another cell for your tribe.");
            return;
        }
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(player, "<red>You must be the leader of your tribe in order to claim.");
            return;
        }
        if (numOfCells > 0) {
            Cell northCell = plugin.getCellManager().getCell(vec2.add(1, 0)).or(new Cell(vec2.add(1, 0)));
            Cell westCell = plugin.getCellManager().getCell(vec2.add(0, 1)).or(new Cell(vec2.add(0, 1)));
            Cell southCell = plugin.getCellManager().getCell(vec2.add(-1, 0)).or(new Cell(vec2.add(-1, 0)));
            Cell eastCell = plugin.getCellManager().getCell(vec2.add(0, -1)).or(new Cell(vec2.add(0, -1)));
            if (!tribe.getUniqueId().equals(northCell.getOwner()) && !tribe.getUniqueId().equals(westCell.getOwner())
                    && !tribe.getUniqueId().equals(southCell.getOwner()) && !tribe.getUniqueId().equals(eastCell
                    .getOwner())) {
                MessageUtils.sendMessage(player, "<red>The cell you're claiming must be adjacent to an existing claim" +
                        ".");
                return;
            }
        }
        cell.setOwner(tribe.getUniqueId());
        plugin.getCellManager().placeCell(vec2, cell);
        MessageUtils.sendMessage(player, "<green>You claimed this cell for your tribe!");
    }

    @Command(identifier = "tribe validate", onlyPlayers = false, permissions = "tribes.command.validate")
    public void validateSubcommand(CommandSender sender, @Arg(name = "tribe", def = "") String tribeName) {
        Tribe tribe;
        if (tribeName.equals("")) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendMessage(sender, "<red>You may not validate your own tribe unless you are a player.");
                return;
            } else {
                Member member = plugin.getMemberManager().getMember(((Player) sender).getUniqueId()).or(new Member
                        (((Player) sender).getUniqueId()));
                if (!plugin.getMemberManager().hasMember(member)) {
                    plugin.getMemberManager().addMember(member);
                }
                if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
                    MessageUtils.sendMessage(sender, "<red>You cannot validate if you are not part of a tribe.");
                    return;
                }
                tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
            }
        } else {
            Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribeByName(tribeName);
            if (!tribeOptional.isPresent()) {
                MessageUtils.sendMessage(sender, "<red>You may not validate a nonexistent tribe.");
                return;
            }
            tribe = tribeOptional.get();
        }
        if (tribe.isValidated()) {
            MessageUtils.sendMessage(sender, "<red>That tribe is already validated.");
            return;
        }
        if (tribe.getName() == null || tribe.getName().equals("")) {
            MessageUtils.sendMessage(sender, "<red>You may not validate a tribe without a name.");
            return;
        }
        tribe.setValidated(true);
        plugin.getTribeManager().removeTribe(tribe);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You validated the tribe <white>%tribe%<green>!",
                new String[][]{{"%tribe%", tribe.getName()}});
    }

    @Command(identifier = "tribe name", onlyPlayers = true, permissions = "tribes.command.name")
    public void nameSubcommand(Player sender, @Arg(name = "name") String name) {
        Member member = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You cannot name your tribe if you are not part of a tribe.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(sender, "<red>You must be the leader of your tribe in order to name.");
            return;
        }
        String checkName = name.length() > 16 ? name.substring(0, 15) : name;
        if (plugin.getTribeManager().getTribeByName(checkName).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>That name has already been taken.");
            return;
        }
        tribe.setName(checkName);
        plugin.getTribeManager().removeTribe(tribe);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You have named your tribe <white>%tribe%<green>!",
                new String[][]{{"%tribe%", tribe.getName()}});
    }

}
