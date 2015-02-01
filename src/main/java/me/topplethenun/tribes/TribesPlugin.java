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
package me.topplethenun.tribes;

import me.topplethenun.tribes.commands.TribeCommand;
import me.topplethenun.tribes.data.Cell;
import me.topplethenun.tribes.data.Member;
import me.topplethenun.tribes.data.Tribe;
import me.topplethenun.tribes.listeners.PlayerListener;
import me.topplethenun.tribes.managers.CellManager;
import me.topplethenun.tribes.managers.MemberManager;
import me.topplethenun.tribes.managers.TribeManager;
import me.topplethenun.tribes.storage.DataStorage;
import me.topplethenun.tribes.storage.MySQLDataStorage;
import me.topplethenun.tribes.storage.SqliteDataStorage;
import org.bukkit.event.HandlerList;
import org.nunnerycode.facecore.configuration.MasterConfiguration;
import org.nunnerycode.facecore.configuration.VersionedSmartConfiguration;
import org.nunnerycode.facecore.configuration.VersionedSmartYamlConfiguration;
import org.nunnerycode.facecore.logging.PluginLogger;
import org.nunnerycode.facecore.plugin.FacePlugin;
import org.nunnerycode.kern.methodcommand.CommandHandler;
import org.nunnerycode.kern.shade.google.common.base.Optional;

import java.io.File;

public class TribesPlugin extends FacePlugin {

    private static TribesPlugin INSTANCE;
    private DataStorage dataStorage;
    private CellManager cellManager;
    private TribeManager tribeManager;
    private MemberManager memberManager;
    private PluginLogger debugPrinter;
    private MasterConfiguration settings;

    @Override
    public void enable() {
        INSTANCE = this;
        debugPrinter = new PluginLogger(this);
        debug("Enabling v" + getDescription().getVersion());

        VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            debug("Updating config.yml");
        }
        VersionedSmartYamlConfiguration dbYAML = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "db.yml"), getResource("db.yml"),
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (dbYAML.update()) {
            debug("Updating db.yml");
        }

        settings = MasterConfiguration.loadFromFiles(configYAML, dbYAML);

        if (settings.getString("db.type").equals("mysql")) {
            dataStorage = new MySQLDataStorage(this);
        } else {
            dataStorage = new SqliteDataStorage(this);
        }
        dataStorage.initialize();

        cellManager = new CellManager();
        memberManager = new MemberManager();
        tribeManager = new TribeManager();

        loadData();

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new TribeCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                debug("saving and loading data");
                saveData();
                loadData();
            }
        }, 20L * 600, 20L * 600);
    }

    private void loadData() {
        for (Cell cell : dataStorage.loadCells()) {
            cellManager.placeCell(cell.getLocation(), cell);
        }
        for (Member member : dataStorage.loadMembers()) {
            if (memberManager.hasMember(member)) {
                memberManager.removeMember(member);
            }
            memberManager.addMember(member);
        }
        for (Tribe tribe : dataStorage.loadTribes()) {
            if (tribeManager.hasTribe(tribe)) {
                tribeManager.removeTribe(tribe);
            }
            tribeManager.addTribe(tribe);
        }
        debug("cells loaded: " + cellManager.getCells().size(),
                "members loaded: " + memberManager.getMembers().size(),
                "tribes loaded: " + tribeManager.getTribes().size());
        for (Member member : memberManager.getMembers()) {
            if (member.getTribe() == null) {
                continue;
            }
            Optional<Tribe> tribeOptional = tribeManager.getTribe(member.getTribe());
            if (tribeOptional.isPresent()) {
                Tribe tribe = tribeOptional.get();
                tribeOptional.get().setRank(member.getUniqueId(), member.getRank());
                getTribeManager().removeTribe(tribe);
                getTribeManager().addTribe(tribe);
            } else {
                member.setRank(Tribe.Rank.GUEST);
                member.setTribe(null);
            }
        }
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        saveData();
        dataStorage.shutdown();
    }

    private void saveData() {
        dataStorage.saveCells(cellManager.getCells());
        dataStorage.saveMembers(memberManager.getMembers());
        dataStorage.saveTribes(tribeManager.getTribes());
    }

    public static TribesPlugin getInstance() {
        return INSTANCE;
    }

    public void debug(String... messages) {
        for (String message : messages) {
            debugPrinter.log(message);
        }
    }

    public CellManager getCellManager() {
        return cellManager;
    }

    public TribeManager getTribeManager() {
        return tribeManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    public MasterConfiguration getSettings() {
        return settings;
    }
}
