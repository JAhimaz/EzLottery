package com.jahimaz.dependencies;

import com.jahimaz.EzLottery;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class LotteryPlaceholderExpansion extends PlaceholderExpansion {

    EzLottery plugin;

    public LotteryPlaceholderExpansion(EzLottery plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "lottery";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        String latestLottery;

        if(EzLottery.currentLottery != null){
            latestLottery = "Lottery#" + plugin.lotteryConfiguration.getInt("Lottery#" + (plugin.lotteryConfiguration.getInt("current-lottery") - 1) + ".lottery-number");
        }else{
            latestLottery = "Lottery#" + plugin.lotteryConfiguration.getInt("Lottery#" + (plugin.lotteryConfiguration.getInt("current-lottery")) + ".lottery-number");
        }

        String winningPlayerName = plugin.lotteryConfiguration.getString(latestLottery + ".winning-player");
        int lotteryNumber = plugin.lotteryConfiguration.getInt(latestLottery + ".lottery-number");
        int totalTickets = plugin.lotteryConfiguration.getInt(latestLottery + ".total-number-of-tickets");
        int participantCount = plugin.lotteryConfiguration.getInt(latestLottery + ".number-of-participants");
        double prizePool = plugin.lotteryConfiguration.getDouble(latestLottery + ".prize-pool");
        String lotteryTiming = plugin.lotteryConfiguration.getString(latestLottery + ".lottery-time");

        if(player == null){ return ""; }

        if(identifier.equals("latest_lotterynumber")){ return Integer.toString(lotteryNumber); }

        if(identifier.equals("latest_winner")){ return winningPlayerName; }

        if(identifier.equals("latest_prizepool")){ return Double.toString(prizePool); }

        if(identifier.equals("latest_totaltickets")){ return Integer.toString(totalTickets); }

        if(identifier.equals("latest_participant_count")){ return Integer.toString(participantCount); }

        if(identifier.equals("latest_lottery_time")){ return lotteryTiming; }

        return null;
    }
}
