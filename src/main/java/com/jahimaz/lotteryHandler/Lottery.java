package com.jahimaz.lotteryHandler;

import com.jahimaz.EzLottery;
import com.jahimaz.dataHandler.LotteryDataHandler;
import com.jahimaz.dataHandler.PlayerDataHandler;
import com.jahimaz.economy.Economy;
import com.jahimaz.events.FireworkSpawnEffect;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Lottery {
    EzLottery plugin;

    int lotteryTimerTask, configTimer, lotteryTimer, lotteryNumber, participantsCount, winningTicket;
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    double prizePool;

    //Constructor Class
    public Lottery(EzLottery instance){
        this.plugin = instance;
        this.lotteryNumber = plugin.getConfig().getInt("lottery-number");
        this.participantsCount = 0;
        this.prizePool = 0.0;
        configTimer = plugin.getConfig().getInt("lottery-timer");
        lotteryTimer = configTimer;
        runLottery();
    }

    private void runLottery() {
        lotteryTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
                if(lotteryTimer == configTimer) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "A Lottery has started, Join in by doing " + ChatColor.GREEN + "/lottery join");
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.GOLD + "The Lottery Has Begun!",ChatColor.WHITE + "Do " + ChatColor.GREEN + "/lottery join" + ChatColor.WHITE + "To Join!",1, 5, 1));
                    --lotteryTimer;
                }else if(lotteryTimer == 30){
                    Bukkit.broadcastMessage(ChatColor.GOLD + "30 Seconds remaining before the lottery rolls a winner!");
                    --lotteryTimer;
                }else if(lotteryTimer == 10){
                    Bukkit.broadcastMessage(ChatColor.GOLD + "10 Seconds remaining before the lottery rolls a winner!");
                    --lotteryTimer;
                }else if(lotteryTimer < configTimer) {
                    if(lotteryTimer <= 1){
                        endLottery();
                    }
                    --lotteryTimer;
                }
            }
        }, 0 , 20);
    }

    private void endLottery(){
        Bukkit.getScheduler().cancelTask(lotteryTimerTask);
        if(participantsCount != 0){
            drawWinner();
        }else if(participantsCount == 1) {
            Bukkit.broadcastMessage(ChatColor.RED + "Only one player participated, the money has been given back.");
            Economy.getEconomy().depositPlayer(tickets.get(0).getWinner(), prizePool);
        }else{
            Bukkit.broadcastMessage(ChatColor.RED + "Nobody has participated in the Lottery, hence it is cancelled.");
        }
        plugin.cancelLottery();
    }

    private void drawWinner(){
        Random r = new Random();

        ArrayList<Integer> ticketNumbers = new ArrayList<Integer>();

        for (int i = 0; i < tickets.size(); i++){
            ticketNumbers.add(tickets.get(i).getTicketNumber());
        }

        int low = Collections.min(ticketNumbers);
        int high = Collections.max(ticketNumbers);

        int result = r.nextInt(high-low) + low;
        this.winningTicket = result;

        Player winner = null;

        for (int i = 0; i < tickets.size(); i++){
            if (tickets.get(i).getTicketNumber() == result){
                winner = tickets.get(i).getWinner();
            }
        }
        //Save to Config
        plugin.lotteryConfiguration.set("Lottery-#" + lotteryNumber + ".lottery-number", lotteryNumber);
        plugin.lotteryConfiguration.set("Lottery-#" + lotteryNumber + ".winning-ticket", winningTicket);
        plugin.lotteryConfiguration.set("Lottery-#" + lotteryNumber + ".winning-player", winner.getDisplayName());
        plugin.lotteryConfiguration.set("Lottery-#" + lotteryNumber + ".number-of-participants", participantsCount);
        plugin.lotteryConfiguration.set("Lottery-#" + lotteryNumber + ".prize-pool", prizePool);
        plugin.saveLotteryFiles();

        //Prize Money
        Bukkit.broadcastMessage(ChatColor.GOLD + "The Winning Ticket Is Ticket Number " + ChatColor.GREEN + "#" + result + " Owned By " + ChatColor.WHITE + winner.getDisplayName());
        if(plugin.getConfig().getBoolean("enable-tax")){
            double tax = this.prizePool * LotteryDataHandler.convertPercentageToDecimal(plugin.getConfig().getInt("tax-amount"));
            this.prizePool = prizePool - tax;
            Economy.getEconomy().bankDeposit("ServerBank", tax);
            winner.sendMessage(ChatColor.GOLD + "Congratulations You Won The Lottery! Your Winnings: " + ChatColor.GREEN + "$" + prizePool + ChatColor.DARK_GREEN + " (Taxed: $" + tax + ")" );
        }else{
            winner.sendMessage(ChatColor.GOLD + "Congratulations You Won The Lottery! Your Winnings: " + ChatColor.GREEN + "$" + prizePool);
        }

        Economy.getEconomy().depositPlayer(winner, prizePool);
        FireworkSpawnEffect.createFirework(winner, winner.getLocation(), true, true, "Orange", "Yellow", 2);
        FireworkSpawnEffect.createFirework(winner, winner.getLocation(), true, true, "Orange", "Yellow", 2);
    }

    public void cancelLotteryTimer(){
        Bukkit.getScheduler().cancelTask(lotteryTimerTask);
    }

    //Setters

    public void addParticipant() {
        this.participantsCount += 1;
    }

    public void addTicket(Ticket newTicket){
        tickets.add(newTicket);
    }

    public void addToPool(int purchasedTickets){
        double moneyToPool = purchasedTickets * plugin.getConfig().getDouble("price-per-ticket");
        prizePool += moneyToPool;
    }

    //Getters

    public String getLotteryTimerString() {
        long sec = lotteryTimer % 60;
        long minutes = lotteryTimer % 3600 / 60;
        long hours = lotteryTimer % 86400 / 3600;

        String sSec = String.format("%02d",sec);
        String sMin = String.format("%02d", minutes);
        String sHr = String.format("%02d", hours);

        return sHr + "H : " + sMin + "M : " + sSec + "S";
    }

    public int getLotteryTimer() {
        return lotteryTimer;
    }

    public int getTicketCount(){
        return tickets.size();
    }

    public ArrayList<Ticket> getTickets(){
        return tickets;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public int getWinningTicket() {
        return winningTicket;
    }

    public double getPrizePool() {
        return prizePool;
    }

    public boolean checkPlayer(String playerDisplayName, int maxTickets){
        int numberOfPlayerTickets = PlayerDataHandler.countPlayerTickets(playerDisplayName, tickets);
        if (numberOfPlayerTickets >= maxTickets){
            return true;
        }
        return false;
    }

    public int getPlayerTickets(String playerDisplayName){
        return PlayerDataHandler.countPlayerTickets(playerDisplayName, tickets);
    }

    public Lottery saveLottery(){
        return this;
    }
}
