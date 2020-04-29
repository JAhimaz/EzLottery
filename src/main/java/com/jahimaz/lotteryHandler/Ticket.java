package com.jahimaz.lotteryHandler;

import org.bukkit.entity.Player;

public class Ticket {
    int ticketNumber;
    String playerName;
    Player player;

    public Ticket(Player player, int ticketNumber){
        this.ticketNumber = ticketNumber;
        this.player = player;
        this.playerName = player.getDisplayName();

    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getWinner(){
        return player;
    }
}
