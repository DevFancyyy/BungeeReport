package de.fancy.bungeereport.listener;

import de.fancy.bungeereport.Main;
import de.fancy.bungeereport.utils.ReportManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerConnectionListener implements Listener {
    private ReportManager reportManager = Main.getInstance().getReportManager();

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(player.hasPermission("report.manage")) {
            player.sendMessage(Main.getInstance().getPrefix() + "§7Status des Reportsystems: §4" + reportManager.getPlayerReportSize() + "§7 Reports (§4" + (reportManager.getPlayerReportSize()-reportManager.getAssignedReportsSize()) +
                    "§7 offen)");
            reportManager.addToOnlineStaff(player);
        }

        if(reportManager.isPlayerReported(player)) {
            for(ProxiedPlayer staffMember : reportManager.getOnlineStaffMember()) {
                staffMember.sendMessage(Main.getInstance().getPrefix() + "§7Der gemeldete Spieler " + player.getDisplayName() + " §7ist nun wieder §aonline§7!");
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(reportManager.isPlayerReported(player)) {
            for(ProxiedPlayer staffMember : reportManager.getOnlineStaffMember()) {
                staffMember.sendMessage(Main.getInstance().getPrefix() + "§7Der gemeldete Spieler " + player.getDisplayName() + " §7ist nun §coffline§7!");
            }

            reportManager.unassignReport(player);
        }

        if(reportManager.getOnlineStaffMember().contains(player)) {
            reportManager.removeFromOnlineStaff(player);
        }
    }
}
