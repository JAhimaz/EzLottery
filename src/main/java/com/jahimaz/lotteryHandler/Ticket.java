package com.jahimaz.lotteryHandler;

public class Ticket {
    int ticketNumber;
    String playerName;

    public Ticket(String playerName, int ticketNumber){
        this.ticketNumber = ticketNumber;
        this.playerName = playerName;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public String getPlayerName() {
        return playerName;
    }
}
