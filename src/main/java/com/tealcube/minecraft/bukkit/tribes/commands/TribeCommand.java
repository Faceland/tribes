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

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.text.WordUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Wildcard;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.math.Vec3;
import com.tealcube.minecraft.bukkit.tribes.math.Vec3f;
import com.tealcube.minecraft.bukkit.tribes.utils.Formatter;
import com.tealcube.minecraft.bukkit.tribes.utils.ScoreboardUtils;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.Question;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class TribeCommand {

    private final TribesPlugin plugin;

    public TribeCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "guild", description = "base guild command", onlyPlayers = false,
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
        MessageUtils.sendMessage(player, "<green><====||====|<white> Guild Status <green>|====||====>");
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<white>You are not a member of a guild.");
        } else {
            Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
            MessageUtils.sendMessage(player, "<aqua>%rank% of %tribe%", new String[][]{
                    {"%rank%", WordUtils.capitalizeFully(member.getRank().name())},
                    {"%tribe%", tribe.isValidated() ? tribe.getName() : "a non validated guild"}
            });
            int cap = tribe.getLevel().getChunks();
            int numOfCells = plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId()).size();
            MessageUtils.sendMessage(player, "<gray>Claimed <white>%amount%<gray>/<white>%cap%<gray> " +
                    "chunks", new String[][]{{"%amount%", numOfCells + ""}, {"%cap%", cap + ""}});
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
            List<String> onlineMembers = new ArrayList<>();
            for (UUID uuid : tribe.getMembers()) {
                if (Bukkit.getPlayer(uuid).isOnline()) {
                    onlineMembers.add(Bukkit.getPlayer(uuid).getDisplayName());
                }
            }
            MessageUtils.sendMessage(player, "<green>Online Members: <white>%members%", new String[][]{{"%members%", onlineMembers.toString().replace("[", "").replace("]", "")}});
        }
        MessageUtils.sendMessage(player, "<green><====||====| <white>Might: %score% <green>|====||====>", new
            String[][]{{"%score%", member.getScore() + ""}});
    }

    @Command(identifier = "guild create", onlyPlayers = false, permissions = "tribes.command.create")
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
            double price = 30000;
            double balance = plugin.getEconomy().getBalance(player);
            if (balance < price) {
                MessageUtils.sendMessage(player, "<red>You don't have enough Bits! You need <white>%currency%<red>.",
                                         new String[][]{{"%currency%", plugin.getEconomy().format(price)}});
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, price);
        }
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() != null) {
            MessageUtils.sendMessage(sender, "<red>You cannot create a guild for someone if they're already in one.");
            MessageUtils.sendMessage(player, "<red>You are already in a guild!");
            return;
        }
        Tribe tribe = new Tribe(UUID.randomUUID());
        tribe.setRank(member.getUniqueId(), Tribe.Rank.LEADER);
        tribe.setOwner(member.getUniqueId());
        tribe.setHome(Vec3f.fromLocation(player.getLocation()));
        tribe.setLevel(Tribe.Level.TINY);
        member.setTribe(tribe.getUniqueId());
        member.setRank(Tribe.Rank.LEADER);
        member.setPvpState(Member.PvpState.ON);
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
        plugin.getTribeManager().addTribe(tribe);
        ScoreboardUtils.updateMightDisplay(member);
        ScoreboardUtils.setPrefix(player, ChatColor.RED + String.valueOf('\u2726') + ChatColor.WHITE);
        ScoreboardUtils.setSuffix(player, ChatColor.RED + String.valueOf('\u2726'));
        MessageUtils.sendMessage(player, "<green>You created a guild! Now the next step is naming it!");
        MessageUtils.sendMessage(player, "<green>Use <white>/guild<green> to check your guild's status");
        MessageUtils.sendMessage(player, "<green>Name your guild with <white>/guild name <name><green>!");
    }

    @Command(identifier = "guild claim", onlyPlayers = true, permissions = "tribes.command.claim")
    public void claimSubcommand(Player player) {
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<red>You cannot claim if you're not in a guild.");
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        Vec2 vec2 = Vec2.fromChunk(chunk);
        Cell cell = plugin.getCellManager().getCell(vec2).or(new Cell(vec2));
        if (cell.getOwner() != null) {
            MessageUtils.sendMessage(player, "<red>This chunk has already been claimed.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (!tribe.isValidated()) {
            MessageUtils.sendMessage(player, "<red>You must validate your guild with <white>/guild validate<red> "
                                             + "first.");
            return;
        }
        int cap = tribe.getLevel().getChunks();
        int numOfCells = plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId()).size();
        if (numOfCells >= cap) {
            MessageUtils.sendMessage(player, "<red>You have reached the maximum number of claims for your guild size.");
            return;
        }
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(player, "<red>Only guild leaders can claim land.");
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
                MessageUtils.sendMessage(player, "<red>The chunk you're claiming must be adjacent to an existing "
                                                 + "claim.");
                return;
            }
        }
        cell.setOwner(tribe.getUniqueId());
        plugin.getCellManager().placeCell(vec2, cell);
        MessageUtils.sendMessage(player, "<green>You claimed this chunk for your guild!");
    }

    @Command(identifier = "guild unclaim", onlyPlayers = true, permissions = "tribes.command.claim")
    public void unclaimSubcommand(Player player) {
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<red>You cannot unclaim if you're not in a guild.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (!tribe.isValidated()) {
            MessageUtils.sendMessage(player, "<red>You must validate your guild with <white>/guild validate<red> "
                    + "first.");
            return;
        }
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(player, "<red>Only guild leaders can unclaim land.");
            return;
        }
        for (Cell cell : plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId())) {
            cell.setOwner(null);
            plugin.getCellManager().placeCell(cell.getLocation(), cell);
        }
        MessageUtils.sendMessage(player, "<green>You unclaimed all of your guilds' land!");
    }

    @Command(identifier = "guild validate", onlyPlayers = false, permissions = "tribes.command.validate")
    public void validateSubcommand(CommandSender sender, @Arg(name = "guild", def = "") String tribeName) {
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
                    MessageUtils.sendMessage(sender, "<red>You are not in a guild. You have nothing to validate.");
                    return;
                }
                tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
            }
        } else {
            Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribeByName(tribeName);
            if (!tribeOptional.isPresent()) {
                MessageUtils.sendMessage(sender, "<red>What are you trying to validate..!?");
                return;
            }
            tribe = tribeOptional.get();
        }
        if (tribe.isValidated()) {
            MessageUtils.sendMessage(sender, "<red>This guild is already validated.");
            return;
        }
        if (tribe.getName() == null || tribe.getName().equals("")) {
            MessageUtils.sendMessage(sender, "<red>You must name your guild with<white> /guild name <name><red> "
                                             + "before you validate it!");
            return;
        }
        tribe.setValidated(true);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You validated the guild <white>%tribe%<green>!",
                new String[][]{{"%tribe%", tribe.getName()}});
        MessageUtils.sendMessage(sender, "<green>You can now start inviting players with <white>/guild invite "
                + "<player><green>!");
        MessageUtils.sendMessage(sender, "<green>Use <white>/guild claim<green> to claim a chunk as your territory!");
    }

    @Command(identifier = "guild name", onlyPlayers = true, permissions = "tribes.command.name")
    public void nameSubcommand(Player sender, @Wildcard @Arg(name = "name") String name) {
        Member member = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You are not in a guild. What exactly are you trying to name...?");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(sender, "<red>You must be the leader of your guild in order to name it.");
            return;
        }
        if (tribe.isValidated()) {
            MessageUtils.sendMessage(sender, "<red>You cannot rename your guild once it has been validated.");
            return;
        }
        String checkName = name.length() > 16 ? name.substring(0, 15) : name;
        if (checkName.isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>You must name your guild.");
            return;
        }
        if (plugin.getTribeManager().getTribeByName(checkName).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>A guild with this name already exists.");
            return;
        }
        tribe.setName(checkName);
        plugin.getTribeManager().removeTribe(tribe);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You have named your guild <white>%tribe%<green>!",
                                 new String[][]{{"%tribe%", tribe.getName()}});
        MessageUtils.sendMessage(sender, "<green>Use <white>/guild name <name><green> to rename your guild.");
        MessageUtils.sendMessage(sender, "<green>Use <white>/guild validate<green> to confirm the name and finish your "
                                         + "guild!");
    }

    @Command(identifier = "guild invite", onlyPlayers = true, permissions = "tribes.command.invite")
    public void inviteSubcommand(final Player sender, @Arg(name = "target") final Player target) {
        Member senderMember =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(senderMember)) {
            plugin.getMemberManager().addMember(senderMember);
        }
        final Member targetMember =
                plugin.getMemberManager().getMember(target.getUniqueId()).or(new Member(target.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(targetMember)) {
            plugin.getMemberManager().addMember(targetMember);
        }
        if (senderMember.getTribe() == null || !plugin.getTribeManager().getTribe(senderMember.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Dude, you're not even in a guild what are you doing.");
            return;
        }
        final Tribe tribe = plugin.getTribeManager().getTribe(senderMember.getTribe()).get();
        if (tribe.getMembers().size() >= tribe.getLevel().getMembers()) {
            MessageUtils.sendMessage(sender, "<red>You have already reached your member limit.");
            return;
        }
        if (targetMember.getTribe() != null && plugin.getTribeManager().getTribe(targetMember.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>This player is already in a guild!");
            return;
        }
        if (!senderMember.getRank().getPermissions().contains(Tribe.Permission.INVITE)) {
            MessageUtils.sendMessage(sender, "<red>You are not a high enough rank to invite others to the guild.");
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
                MessageUtils.sendMessage(sender, "<white>%player%<green> joined your guild!",
                        new String[][]{{"%player%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<green>You joined <white>%tribe%<green>!",
                        new String[][]{{"%tribe%", tribe.getName()}});
                ScoreboardUtils.setPrefix(target, ChatColor.RED + String.valueOf('\u2726') + ChatColor.WHITE);
                ScoreboardUtils.setSuffix(target, ChatColor.RED + String.valueOf('\u2726'));
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
                new String[][]{{"%tribe%", tribe.getName()}, {"%player%", sender.getDisplayName()}})), options);
        plugin.getQPlugin().getQuestionManager().appendQuestion(question);
        List<String> messages = Formatter.format(question);
        for (String m : messages) {
            target.sendMessage(m);
        }
        MessageUtils.sendMessage(sender, "<green>You sent an invite to <white>%player%<green>!",
                new String[][]{{"%player%", target.getDisplayName()}});
    }

    @Command(identifier = "guild leave", onlyPlayers = true, permissions = "tribes.command.leave")
    public void leaveSubcommand(Player sender) {
        Member member =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You can't leave a guild if you're not in one, bruh.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (plugin.getMemberManager().getMembersWithTribe(tribe.getUniqueId()).size() > 1 && member.getUniqueId()
                .equals(tribe.getOwner())) {
            MessageUtils.sendMessage(sender, "<red>You must kick all players from your guild before leaving yourself.");
            return;
        }
        member.setTribe(null);
        member.setRank(Tribe.Rank.GUEST);
        tribe.setRank(member.getUniqueId(), Tribe.Rank.GUEST);
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
        plugin.getTribeManager().removeTribe(tribe);
        if (plugin.getMemberManager().getMembersWithTribe(tribe.getUniqueId()).size() == 0) {
            for (Cell cell : plugin.getCellManager().getCellsWithOwner(tribe.getUniqueId())) {
                cell.setOwner(null);
                plugin.getCellManager().placeCell(cell.getLocation(), cell);
            }
        } else {
            plugin.getTribeManager().addTribe(tribe);
        }
        MessageUtils.sendMessage(sender, "<green>You left your guild.");
    }

    @Command(identifier = "guild top", onlyPlayers = false, permissions = "tribes.command.top")
    public void topSubcommand(CommandSender sender) {
        List<Member> topMembers = plugin.getDataStorage().loadMembers();
        MessageUtils.sendMessage(sender, "<green><====||====| <white>PvP Rankings <green>|====||====>");
        for (int i = 0; i < Math.min(10, topMembers.size()); i++) {
            Member m = topMembers.get(i);
            MessageUtils.sendMessage(sender, "<gray>%num%. <white>%player%<gray> : <white>%score%<gray> Might",
                    new String[][]{{"%num%", (i + 1) + ""}, {"%player%", Bukkit.getOfflinePlayer(m.getUniqueId())
                            .getName()}, {"%score%", m.getScore() + ""}});
        }
    }

    @Command(identifier = "guild kick", onlyPlayers = true, permissions = "tribes.command.kick")
    public void banishSubcommand(Player sender, @Arg(name = "target") String name) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        Member member =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You can't kick from a guild if you're not in one.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (!member.getRank().getPermissions().contains(Tribe.Permission.KICK)) {
            MessageUtils.sendMessage(sender, "<red>You don't have permission to kick.");
            return;
        }
        Member targetMember = plugin.getMemberManager().getMember(target.getUniqueId()).or(new Member(target.getUniqueId()));
        if (!member.getTribe().equals(targetMember.getUniqueId())) {
            MessageUtils.sendMessage(sender, "<red>You can't kick someone who isn't in your guild..");
            return;
        }
        if (targetMember.getRank().getPermissions().contains(Tribe.Permission.KICK_IMMUNE)) {
            MessageUtils.sendMessage(sender, "<red>You cannot kick that member.");
            return;
        }
        targetMember.setTribe(null);
        tribe.setRank(targetMember.getUniqueId(), Tribe.Rank.GUEST);
        MessageUtils.sendMessage(target.getPlayer(), "<red>You have been kicked from your guild.");
        MessageUtils.sendMessage(sender, "<green>You kicked <white>%target%<green> from your guild.", new
            String[][]{{"%target%", target.getPlayer().getDisplayName()}});
    }

    @Command(identifier = "guild upgrade", onlyPlayers = true, permissions = "tribes.command.upgrade")
    public void upgradeSubcommand(Player sender) {
        Member member = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You need to be the leader of a guild to upgrade it. Also you "
                                             + "should probably be IN a guild.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(sender, "<red>Only guild leaders can upgrade the guild.");
            return;
        }
        if (tribe.getLevel().ordinal() == Tribe.Level.values().length - 1) {
            MessageUtils.sendMessage(sender, "<red>You cannot upgrade your guild any further.");
            return;
        }
        double price = Tribe.Level.values()[tribe.getLevel().ordinal() + 1].getPrice();
        double balance = plugin.getEconomy().getBalance(sender);
        if (balance < price) {
            MessageUtils.sendMessage(sender, "<red>You don't have enough bits. You need <white>%currency%<red>.", new String[][]{{"%currency%", plugin.getEconomy().format(price)}});
            return;
        }
        plugin.getEconomy().withdrawPlayer(sender, price);
        tribe.setLevel(Tribe.Level.values()[tribe.getLevel().ordinal() + 1]);
        MessageUtils.sendMessage(sender, "<green>You have upgraded your guild!");
    }

    @Command(identifier = "guild home", onlyPlayers = true, permissions = "tribes.command.home")
    public void homeSubcommand(Player sender) {
        Member member =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Where exactly are you trying to return to?");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        Vec3f home = tribe.getHome();
        sender.teleport(new Location(home.getWorld(), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch()),
                PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Command(identifier = "guild sethome", onlyPlayers = true, permissions = "tribes.command.home")
    public void setHomeSubcommand(Player sender) {
        Member member =
                plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Guild home set. Except it wasn't. Because you're not even in one.");
            return;
        }
        Tribe tribe = plugin.getTribeManager().getTribe(member.getTribe()).get();
        if (member.getRank() != Tribe.Rank.LEADER || tribe.getRank(member.getUniqueId()) != Tribe.Rank.LEADER) {
            MessageUtils.sendMessage(sender, "<red>You must be the leader of your guild in order to set its home, bruh.");
            return;
        }
        Vec3f location = Vec3f.fromLocation(sender.getLocation());
        tribe.setHome(location);
        plugin.getTribeManager().addTribe(tribe);
        MessageUtils.sendMessage(sender, "<green>You successfully set your guild's home.");
    }

}
