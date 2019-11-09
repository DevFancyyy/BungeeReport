package de.fancy.bungeereport.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportManager {
    HashMap<ProxiedPlayer, List<List<String>>> playerReports = new HashMap<>();
    HashMap<ProxiedPlayer, ProxiedPlayer> reportAssignments = new HashMap<>();
    HashMap<ProxiedPlayer, List<ProxiedPlayer>> reportedPlayersByPlayer = new HashMap<>();
    List<ProxiedPlayer> onlineStaff = new ArrayList<>();
    List<String> reportReasons = new ArrayList<>();

    public ReportManager() {
        reportReasons.add("HACKING");
        reportReasons.add("TEAMING");
        reportReasons.add("RANDOMKILLING");
        reportReasons.add("BUGUSING");
        reportReasons.add("SKIN");
        reportReasons.add("NAME");
        reportReasons.add("BUILDING");
    }

    public HashMap<ProxiedPlayer, List<List<String>>> getPlayerReports() {
        return this.playerReports;
    }

    public int getPlayerReportSize() {
        return this.playerReports.size();
    }

    public boolean isPlayerReported(ProxiedPlayer target) {
        if (playerReports.containsKey(target)) {
            return true;
        } else {
            return false;
        }
    }

    public List<List<String>> getPlayerReport(ProxiedPlayer target) {
        return this.playerReports.get(target);
    }

    public void createPlayerReport(ProxiedPlayer target, String reason, Date timestamp, ProxiedPlayer reportingPlayer) {
        List<String> infoList = new ArrayList<>();
        infoList.add(reason);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        infoList.add(dateFormat.format(timestamp));
        infoList.add(reportingPlayer.getDisplayName());

        if(isPlayerReported(target)) {
            List<List<String>> playerReportList = getPlayerReport(target);
            playerReportList.add(infoList);
        } else {
            List<List<String>> playerReportList = new ArrayList<>();
            playerReportList.add(infoList);
            playerReports.put(target, playerReportList);
        }

        addPlayerToReportList(reportingPlayer, target);
    }

    public void removePlayerReport(ProxiedPlayer target) {
        playerReports.remove(target);
    }

    public boolean isReportAssigned(ProxiedPlayer target) {
        if(reportAssignments.containsKey(target)) {
            return true;
        } else {
            return false;
        }
    }

    public ProxiedPlayer getAssignedPlayer(ProxiedPlayer staffMember) {
        for(Map.Entry<ProxiedPlayer, ProxiedPlayer> reportedPlayer : reportAssignments.entrySet()) {
            if(reportedPlayer.getValue() == staffMember) {
                return reportedPlayer.getKey();
            }
        }

        return null;
    }

    public ProxiedPlayer getAssignedStaffmember(ProxiedPlayer target) {
        return reportAssignments.get(target);
    }

    public void assignReport(ProxiedPlayer target, ProxiedPlayer staffMember) {
        reportAssignments.put(target, staffMember);
    }

    public void unassignReport(ProxiedPlayer target) {
        reportAssignments.remove(target);
    }

    public int getAssignedReportsSize() {
        return reportAssignments.size();
    }

    public boolean hasStaffmemberAReportAssigned(ProxiedPlayer staffmember) {
        if(reportAssignments.containsValue(staffmember))  {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPlayerReportedTarget(ProxiedPlayer reporter, ProxiedPlayer target) {
        if(reportedPlayersByPlayer.containsKey(reporter)) {
            List<ProxiedPlayer> reportedPlayers = reportedPlayersByPlayer.get(reporter);

            if(reportedPlayers.contains(target)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addPlayerToReportList(ProxiedPlayer reporter, ProxiedPlayer target) {
        if(reportedPlayersByPlayer.containsKey(reporter)) {
            List<ProxiedPlayer> reportedPlayers = reportedPlayersByPlayer.get(reporter);
            reportedPlayers.add(target);
            reportedPlayersByPlayer.replace(reporter, reportedPlayers);
        } else {
            List<ProxiedPlayer> reportedPlayers = new ArrayList<>();
            reportedPlayers.add(target);
            reportedPlayersByPlayer.put(reporter, reportedPlayers);
        }
    }

    public boolean isOnlineStaffMember(ProxiedPlayer player) {
        if(onlineStaff.contains(player)) {
            return true;
        } else {
            return false;
        }
    }

    public List<ProxiedPlayer> getOnlineStaffMember() {
        return onlineStaff;
    }

    public void addToOnlineStaff(ProxiedPlayer player) {
        onlineStaff.add(player);
    }

    public void removeFromOnlineStaff(ProxiedPlayer player) {
        onlineStaff.remove(player);
    }

    public boolean isReportReasonValid(String reason) {
        if(reportReasons.contains(reason)) {
            return true;
        } else {
            return false;
        }
    }

    public List<String> getReportReasons() {
        return reportReasons;
    }

}
