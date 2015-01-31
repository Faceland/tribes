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

import java.util.UUID;

public class TribeCommand {

    private final TribesPlugin plugin;

    public TribeCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "tribe", description = "base tribe command", onlyPlayers = false)
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

    @Command(identifier = "tribe create", onlyPlayers = false)
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

    @Command(identifier = "tribe claim", onlyPlayers = true)
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
        cell.setOwner(tribe.getOwner());
        plugin.getCellManager().placeCell(vec2, cell);
        MessageUtils.sendMessage(player, "<green>You claimed this cell for your tribe!");
    }

}
