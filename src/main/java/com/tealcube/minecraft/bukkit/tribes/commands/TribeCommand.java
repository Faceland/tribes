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
package com.tealcube.minecraft.bukkit.tribes.commands;

import ca.wacos.nametagedit.NametagAPI;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.text.WordUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Wildcard;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.utils.Formatter;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.Question;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
    public void nameSubcommand(Player sender, @Wildcard @Arg(name = "name") String name) {
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

    @Command(identifier = "tribe invite", onlyPlayers = true, permissions = "tribes.command.invite")
    public void inviteSubcommand(final Player sender, @Arg(name = "target") final Player target) {
        Member senderMember =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(senderMember)) {
            plugin.getMemberManager().addMember(senderMember);
        }
        final Member targetMember =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(targetMember)) {
            plugin.getMemberManager().addMember(targetMember);
        }
        if (senderMember.getTribe() == null || !plugin.getTribeManager().getTribe(senderMember.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You cannot invite to your tribe if you are not part of a tribe.");
            return;
        }
        final Tribe tribe = plugin.getTribeManager().getTribe(senderMember.getTribe()).get();
        if (targetMember.getTribe() != null && plugin.getTribeManager().getTribe(targetMember.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You cannot invite someone if they're already part of a tribe.");
            return;
        }
        if (!senderMember.getRank().getPermissions().contains(Tribe.Permission.INVITE)) {
            MessageUtils.sendMessage(sender, "<red>You don't have permission in your tribe to invite someone.");
            return;
        }
        List<Option> options = new ArrayList<>();
        options.add(new Option("accept", new Runnable() {
            @Override
            public void run() {
                tribe.setRank(targetMember.getUniqueId(), Tribe.Rank.GUEST);
                plugin.getTribeManager().removeTribe(tribe);
                plugin.getTribeManager().addTribe(tribe);
                targetMember.setTribe(tribe.getUniqueId());
                targetMember.setPvpState(Member.PvpState.ON);
                plugin.getMemberManager().removeMember(targetMember);
                plugin.getMemberManager().addMember(targetMember);
                MessageUtils.sendMessage(sender, "<white>%player%<green> joined your tribe!",
                        new String[][]{{"%player%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<green>You joined <white>%tribe%<green>!",
                        new String[][]{{"%tribe%", tribe.getName()}});
                NametagAPI.setPrefix(target.getName(), ChatColor.RED + String.valueOf('\u2726'));
                NametagAPI.setSuffix(target.getName(), ChatColor.RED + String.valueOf('\u2726'));
            }
        }, "Accept the invitation"));
        options.add(new Option("deny", new Runnable() {
            @Override
            public void run() {
                MessageUtils.sendMessage(sender, "<white>%player%<red> rejected your invitation.",
                        new String[][]{{"%player%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<red>You rejected <white>%player%<red>'s invitation.",
                        new String[][]{{"%player%", sender.getDisplayName()}});
            }
        }, "Reject the invitation"));
        Question question = new Question(target.getUniqueId(), TextUtils.color(TextUtils.args(
                "<green>You have been invited to join <white>%tribe%<green> by <white>%player%<green>!",
                new String[][]{{}})), options);
        plugin.getQPlugin().getQuestionManager().appendQuestion(question);
        List<String> messages = Formatter.format(question);
        for (String m : messages) {
            target.sendMessage(m);
        }
        MessageUtils.sendMessage(sender, "<green>You sent an invite to <white>%player%<green>!",
                new String[][]{{"%player%", target.getDisplayName()}});
    }

    @Command(identifier = "tribe leave", onlyPlayers = true, permissions = "tribes.command.leave")
    public void leaveSubcommand(Player sender) {
        Member member =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You can't leave a tribe if you're not in one.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (plugin.getMemberManager().getMembersWithTribe(tribe.getUniqueId()).size() > 1 && member.getUniqueId()
                .equals(tribe.getOwner())) {
            MessageUtils.sendMessage(sender, "<red>You cannot leave your tribe unless you are the last member.");
            return;
        }
        member.setTribe(null);
        member.setRank(Tribe.Rank.GUEST);
        tribe.setRank(member.getUniqueId(), Tribe.Rank.GUEST);
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
        plugin.getTribeManager().removeTribe(tribe);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You left your tribe.");
    }

    @Command(identifier = "tribe top", onlyPlayers = false, permissions = "tribes.command.top")
    public void topSubcommand(CommandSender sender) {
        List<Member> topMembers = plugin.getDataStorage().loadMembers();
        MessageUtils.sendMessage(sender, "<green><====||==== PvP Rankings ====||====>");
        for (int i = 0; i < Math.min(10, topMembers.size()); i++) {
            Member m = topMembers.get(i);
            MessageUtils.sendMessage(sender, "<gray>%num%. <white>%player%<gray> : <white>%%score%<gray> points",
                    new String[][]{{"%num%", (i + 1) + ""}, {"%player%", Bukkit.getOfflinePlayer(m.getUniqueId())
                            .getName()}, {"%score%", m.getScore() + ""}});
        }
    }


}
