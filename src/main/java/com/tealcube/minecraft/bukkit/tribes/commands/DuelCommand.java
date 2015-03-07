package com.tealcube.minecraft.bukkit.tribes.commands;

import ca.wacos.nametagedit.NametagAPI;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.utils.Formatter;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.Question;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand {

    private final TribesPlugin plugin;

    public DuelCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "duel", permissions = "tribes.command.duel", onlyPlayers = true)
    public void duelCommand(final Player sender, @Arg(name = "target") final Player target) {
        if (sender.equals(target)) {
            MessageUtils.sendMessage(sender, "<red>You cannot duel yourself.");
            return;
        }
        final Member senderMember = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(senderMember)) {
            plugin.getMemberManager().addMember(senderMember);
        }
        final Member targetMember = plugin.getMemberManager().getMember(target.getUniqueId()).or(new Member(target.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(targetMember)) {
            plugin.getMemberManager().addMember(targetMember);
        }
        if (senderMember.getPvpState() == Member.PvpState.DUEL || senderMember.getDuelPartner() != null) {
            MessageUtils.sendMessage(sender, "<red>You cannot duel someone if you're already dueling somebody else.");
            return;
        }
        if (targetMember.getPvpState() == Member.PvpState.DUEL || targetMember.getDuelPartner() != null) {
            MessageUtils.sendMessage(sender, "<red>You cannot duel someone if they're already dueling somebody else.");
            return;
        }
        if (sender.getLocation().distance(target.getLocation()) > 50) {
            MessageUtils.sendMessage(target, "<red>You cannot duel someone greater than 50 blocks away.");
            return;
        }
        List<Option> options = new ArrayList<>();
        options.add(new Option("accept", new Runnable() {
            @Override
            public void run() {
                if (senderMember.getPvpState() == Member.PvpState.DUEL || senderMember.getDuelPartner() != null) {
                    MessageUtils.sendMessage(target, "<red>You cannot duel someone if they're already dueling somebody else.");
                    return;
                }
                if (targetMember.getPvpState() == Member.PvpState.DUEL || targetMember.getDuelPartner() != null) {
                    MessageUtils.sendMessage(target, "<red>You cannot duel someone if you're already dueling somebody else.");
                    return;
                }
                if (sender.getLocation().distance(target.getLocation()) > 50) {
                    MessageUtils.sendMessage(target, "<red>You cannot duel someone greater than 50 blocks away.");
                    return;
                }
                senderMember.setPvpState(Member.PvpState.DUEL);
                targetMember.setPvpState(Member.PvpState.DUEL);
                senderMember.setDuelPartner(targetMember.getUniqueId());
                targetMember.setDuelPartner(senderMember.getUniqueId());
                MessageUtils.sendMessage(sender, "<white>%name%<green> accepted your duel request.", new String[][]{{"%name%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<green>You accepted <white>%name%<green>'s duel request.", new String[][]{{"%name%", target.getDisplayName()}});
                plugin.getMemberManager().removeMember(targetMember);
                plugin.getMemberManager().removeMember(senderMember);
                plugin.getMemberManager().addMember(targetMember);
                plugin.getMemberManager().addMember(senderMember);
                NametagAPI.setPrefix(target.getName(), ChatColor.BLUE + String.valueOf('\u2726'));
                NametagAPI.setPrefix(sender.getName(), ChatColor.BLUE + String.valueOf('\u2726'));
                NametagAPI.setSuffix(target.getName(), ChatColor.BLUE + String.valueOf('\u2726'));
                NametagAPI.setSuffix(sender.getName(), ChatColor.BLUE + String.valueOf('\u2726'));
            }
        }));
        options.add(new Option("deny", new Runnable() {
            @Override
            public void run() {
                MessageUtils.sendMessage(sender, "<white>%name%<red> denied your duel request.", new String[][]{{"%name%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<red>You denied <white>%name%<red>'s duel request.", new String[][]{{"%name%", target.getDisplayName()}});
            }
        }));
        Question question = new Question(target.getUniqueId(), TextUtils.color(TextUtils.args(
                "<white>%sender%<gray> would like to duel.",
                new String[][]{{"%sender%", sender.getDisplayName()}})), options);
        plugin.getQPlugin().getQuestionManager().appendQuestion(question);
        List<String> messages = Formatter.format(question);
        for (String m : messages) {
            target.sendMessage(m);
        }
        MessageUtils.sendMessage(sender, "<gray>You sent a duel invite to <white>%player%<gray>!",
                new String[][]{{"%player%", target.getDisplayName()}});
    }

}
