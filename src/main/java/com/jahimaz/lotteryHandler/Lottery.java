package com.jahimaz.lotteryHandler;

import com.jahimaz.EzLottery;
import com.jahimaz.dataHandler.LotteryDataHandler;
import com.jahimaz.dataHandler.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Lottery {
    EzLottery plugin;

    int lotteryTimerTask;

    int configTimer;
    int lotteryTimer;
    int lotteryNumber;
    int participantsCount;
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    double prizePool;
    String winningPlayer, winningTicket;

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
                if(lotteryTimer == configTimer){
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
        }else{
            Bukkit.broadcastMessage("Nigger");
        }
        EzLottery.cancelLottery();
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
        Player winner = null;

        for (int i = 0; i < tickets.size(); i++){
            if (tickets.get(i).getTicketNumber() == result){
                winner = tickets.get(i).getWinner();
            }
        }

        double tax = this.prizePool * LotteryDataHandler.convertPercentageToDecimal(plugin.getConfig().getInt("tax-amount"));
        this.prizePool = prizePool - tax;

        Bukkit.broadcastMessage(ChatColor.GOLD + "The Winning Ticket Is Ticket Number " + ChatColor.GREEN + "#" + result + " Owned By " + ChatColor.WHITE + winner.getDisplayName());
        EzLottery.getEconomy().depositPlayer(winner, prizePool);
        EzLottery.getEconomy().bankDeposit("ServerBank", tax);
        winner.sendMessage(ChatColor.GOLD + "Congratulations You Won The Lottery! Your Winnings: " + ChatColor.GREEN + "$" + prizePool + ChatColor.DARK_GREEN + " (Taxed: $" + tax + ")" );
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

    public String getWinningTicket() {
        return winningTicket;
    }

    public String getWinningPlayer() {
        return winningPlayer;
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
