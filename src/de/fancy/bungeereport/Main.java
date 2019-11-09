package de.fancy.bungeereport;

import de.fancy.bungeereport.commands.CMD_Report;
import de.fancy.bungeereport.listener.PlayerConnectionListener;
import de.fancy.bungeereport.utils.ReportManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class Main extends Plugin {
    public static Main instance;

    public String prefix = "§7[§4Report§7] §7";
    public ReportManager reportManager = new ReportManager();
    public HashMap<ProxiedPlayer, String> languageMap = new HashMap<>();

    public YamlConfiguration languageConfig;
    public File languageFile;

    @Override
    public void onEnable() {
        instance = this;

        System.out.println("[BungeeReport] Das Plugin wurde erfolgreich gestartet!");

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CMD_Report("report"));
    }

    public void onDisable() {
        System.out.println("[BungeeReport] Das Plugin wurde erfolgreich beendet!");
    }

    public static Main getInstance() {
        return instance;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public ReportManager getReportManager() {
        return this.reportManager;
    }
}
