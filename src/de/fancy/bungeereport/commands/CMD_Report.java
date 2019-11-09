package de.fancy.bungeereport.commands;

import de.fancy.bungeereport.Main;
import de.fancy.bungeereport.utils.ReportManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import sun.plugin2.main.server.ProxySupport;

import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CMD_Report extends Command {
    private ReportManager reportManager = Main.getInstance().getReportManager();
    private String noPermission = Main.getInstance().getPrefix() + "§cDu hast keine Rechte dazu!";

    public CMD_Report(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if(args.length == 0) {
            sendHelpMessage(player);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(player);
            } else if(args[0].equalsIgnoreCase("list")) {
                if(player.hasPermission("report.manage")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§7Anzahl der Reports: §4" + reportManager.getPlayerReportSize() + "§7 (§4" + (reportManager.getPlayerReportSize()-reportManager.getAssignedReportsSize()) + "§7 offen)");

                    for(Map.Entry<ProxiedPlayer, List<List<String>>> reports : reportManager.getPlayerReports().entrySet()) {
                        ProxiedPlayer target = reports.getKey();
                        String online = "§4Offline";

                        if(ProxyServer.getInstance().getPlayers().contains(target)) {
                            online = "§aOnline";
                        }

                        if(reportManager.isReportAssigned(reports.getKey())) {
                            TextComponent reportMessage = new TextComponent(Main.getInstance().getPrefix() + target.getDisplayName() + " §7| §4" + reportManager.getPlayerReport(target).size() + " §7Reports §7| §cZugewiesen (" + reportManager.getAssignedStaffmember(target).getDisplayName() + "§c)");
                            reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cDer Report ist bereits zugewiesen").create()));
                            player.sendMessage(reportMessage);
                        } else {
                            TextComponent reportMessage = new TextComponent(Main.getInstance().getPrefix() + target.getDisplayName() + " §7| §4" + args[1].toUpperCase() + " §7| §4" + reportManager.getPlayerReport(target).size() + " §7Reports §7| §aOffen §7| " + online);
                            reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Den Report über " + target.getDisplayName() + " §7annehmen").create()));
                            reportMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report assign " + target.getName()));
                            player.sendMessage(reportMessage);
                        }
                    }
                } else {
                    player.sendMessage(noPermission);
                }
            } else if(args[0].equalsIgnoreCase("toggle")) {
                if(player.hasPermission("report.manage")) {
                    if(reportManager.isOnlineStaffMember(player)) {
                        reportManager.removeFromOnlineStaff(player);
                        player.sendMessage(Main.getInstance().getPrefix() + "§7Du hast dich erfolgreich §causgeloggt§7!");
                    } else {
                        reportManager.addToOnlineStaff(player);
                        player.sendMessage(Main.getInstance().getPrefix() + "§7Du hast dich erfolgreich §aeingeloggt§7!");
                    }
                } else {
                    player.sendMessage(noPermission);
                }
            } else if(args[0].equalsIgnoreCase("info")) {
                if(player.hasPermission("report.manage")) {
                    if(reportManager.hasStaffmemberAReportAssigned(player)) {
                        ProxiedPlayer target = reportManager.getAssignedPlayer(player);

                        List<List<String>> playerReports = reportManager.getPlayerReport(target);
                        String online = "§4Offline";

                        if(ProxyServer.getInstance().getPlayers().contains(target)) {
                            online = "§aOnline";
                        }

                        player.sendMessage(Main.getInstance().getPrefix() + "§4Report-Info §7über " + target.getDisplayName() + "§7:");
                        player.sendMessage(Main.getInstance().getPrefix() + "§7Anzahl der Reports: §4" + playerReports.size());
                        player.sendMessage(Main.getInstance().getPrefix() + "§7Der Spieler ist: " + online);

                        for(List<String> detailedReport : playerReports) {
                            player.sendMessage(Main.getInstance().getPrefix() + "§4" + detailedReport.get(0) + "§7 | §4" + detailedReport.get(1) + "§7 | " + detailedReport.get(2));
                        }
                    } else {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDir ist kein Report zugewiesen!");
                    }
                } else {
                    player.sendMessage(noPermission);
                }
            }
        } else if(args.length == 2) {
            if(!(args[0].equalsIgnoreCase("assign") || args[0].equalsIgnoreCase("unassign") || args[0].equalsIgnoreCase("close") || args[0].equalsIgnoreCase("info"))) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                if(target != null && !reportManager.hasPlayerReportedTarget(player, target) && reportManager.isReportReasonValid(args[1].toUpperCase())) {
                    Date now = new Date();
                    reportManager.createPlayerReport(target, args[1].toUpperCase(), now, player);

                    for(ProxiedPlayer onlineStaffMember : reportManager.getOnlineStaffMember()) {
                        if(reportManager.isReportAssigned(target)) {
                            TextComponent reportMessage = new TextComponent(Main.getInstance().getPrefix() + target.getDisplayName() + " §7| §4" + args[1].toUpperCase() + " §7| §4" + reportManager.getPlayerReport(target).size() + " §7Reports §7| §cZugewiesen (" + reportManager.getAssignedStaffmember(target).getDisplayName() + "§c)");
                            reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cDer Report ist bereits zugewiesen").create()));
                            player.sendMessage(reportMessage);
                        } else {
                            TextComponent reportMessage = new TextComponent(Main.getInstance().getPrefix() + target.getDisplayName() + " §7| §4" + args[1].toUpperCase() + " §7| §4" + reportManager.getPlayerReport(target).size() + " §7Reports §7| §aOffen");
                            reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Den Report über " + target.getDisplayName() + " §7annehmen").create()));
                            reportMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report assign " + target.getName()));
                            player.sendMessage(reportMessage);
                        }
                    }
                } else {
                    if(target == null) {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler ist nicht online!");
                    } else if(reportManager.hasPlayerReportedTarget(player, target)) {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDiesen Spieler hast du schon gemeldet!");
                    } else if(!reportManager.isReportReasonValid(args[1].toUpperCase())) {
                        String validReasons = "§e";

                        for(int i = 0; i < reportManager.getReportReasons().size(); i++) {
                            validReasons = validReasons + reportManager.getReportReasons().get(i) + ", ";
                        }

                        player.sendMessage(Main.getInstance().getPrefix() + "§cDies ist kein gültiger Grund: " + validReasons);
                    }
                }
            } else {
                if(player.hasPermission("report.manage")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if(target != null) {
                        if(args[0].equalsIgnoreCase("assign")) {
                            if(!reportManager.isReportAssigned(target) && !reportManager.hasStaffmemberAReportAssigned(player)) {
                                reportManager.assignReport(target, player);
                                player.sendMessage(Main.getInstance().getPrefix() + "§7Der Report von " + target.getDisplayName() + " §7ist dir nun §azugewiesen§7!");
                            } else {
                                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Report kann dir nicht zugewiesen werden!");
                            }
                        } else if(args[0].equalsIgnoreCase("unassign")) {
                            if(reportManager.isReportAssigned(target) && reportManager.getAssignedStaffmember(target) == player) {
                                reportManager.unassignReport(target);
                                player.sendMessage(Main.getInstance().getPrefix() + "§7Der Report von " + target.getDisplayName() + " §7ist dir nun §cnicht mehr §7zugewiesen!");
                            } else {
                                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Report ist dir nicht zugewiesen!");
                            }
                        } else if(args[0].equalsIgnoreCase("close")) {
                            if(reportManager.isPlayerReported(target)) {
                                if(!reportManager.isReportAssigned(target)) {
                                    reportManager.removePlayerReport(target);
                                    player.sendMessage(Main.getInstance().getPrefix() + "§7Du hast den Report über " + target.getDisplayName() + " §7geschlossen!");
                                } else {
                                    if(reportManager.getAssignedStaffmember(target) == player) {
                                        reportManager.removePlayerReport(target);
                                        player.sendMessage(Main.getInstance().getPrefix() + "§7Du hast den Report über " + target.getDisplayName() + " §7geschlossen!");
                                    } else {
                                        player.sendMessage(Main.getInstance().getPrefix() + "§cDiesen Report kannst du nicht schließen!");
                                    }
                                }
                            } else {
                                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler wurde nicht reported!");
                            }
                        } else if(args[0].equalsIgnoreCase("info")) {
                            if(reportManager.isPlayerReported(target)) {
                                List<List<String>> playerReports = reportManager.getPlayerReport(target);
                                String assigned = "§aOffen";
                                String online = "§4Offline";

                                if(reportManager.isReportAssigned(target)) {
                                    assigned = "§cZugewiesen (" + reportManager.getAssignedStaffmember(target).getDisplayName() + "§c)";
                                }

                                if(ProxyServer.getInstance().getPlayers().contains(target)) {
                                    online = "§aOnline";
                                }

                                player.sendMessage(Main.getInstance().getPrefix() + "§4Report-Info §7über " + target.getDisplayName() + "§7:");
                                player.sendMessage(Main.getInstance().getPrefix() + "§7Anzahl der Reports: §4" + playerReports.size());
                                player.sendMessage(Main.getInstance().getPrefix() + "§7Status des Reports: " + assigned);
                                player.sendMessage(Main.getInstance().getPrefix() + "§7Der Spieler ist: " + online);

                                for(List<String> detailedReport : playerReports) {
                                    player.sendMessage(Main.getInstance().getPrefix() + "§4" + detailedReport.get(0) + "§7 | §4" + detailedReport.get(1) + "§7 | " + detailedReport.get(2));
                                }
                            } else {
                                player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler wurde nicht reported!");
                            }
                        }
                    } else {
                        player.sendMessage(Main.getInstance().getPrefix() + "§cDieser Spieler ist nicht online!");
                    }
                } else {
                    player.sendMessage(noPermission);
                }
            }
        }


    }

    private void sendHelpMessage(ProxiedPlayer player) {
        player.sendMessage(Main.getInstance().getPrefix() + "§7Übersicht über die Befehle des §4Reportsystems§7:");

        if(player.hasPermission("report.manage")) {
            player.sendMessage("§c/report toggle§7: Aus dem Reportsystem ein-/ausloggen");
            player.sendMessage("§c/report list§7: Alle Reports auflisten");
            player.sendMessage("§c/report info§7: Infos zum zugewiesenen Report anzeigen");
            player.sendMessage("§c/report assign <Spieler>§7: Weise dir einen Report zu");
            player.sendMessage("§c/report unassign <Spieler>§7: Gib den Report frei");
            player.sendMessage("§c/report close <Spieler>§7: Schließe den Report");
            player.sendMessage("§c/report info <Spieler>§7: Rufe Infos über einen Report ab");
        } else {
            player.sendMessage("§c/report <Spieler> <Grund>§7: Melde einen Spieler");
        }
    }
}
