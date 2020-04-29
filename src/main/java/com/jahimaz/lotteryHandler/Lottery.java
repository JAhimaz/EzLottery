package com.jahimaz.lotteryHandler;

import java.util.ArrayList;
import java.util.List;

public class Lottery {

    int participantsCount;
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    double prizePool;
    String winningPlayer, winningTicket;

    //Constructor Class
    public Lottery(){
        this.participantsCount = 0;
        this.prizePool = 0.0;
    }

    public void addParticipant() {
        this.participantsCount += 1;
    }

    public void addTicket(Ticket newTicket){
        tickets.add(newTicket);
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
        int numberOfPlayerTickets = LotteryMechanics.countPlayerTickets(playerDisplayName, tickets);
        if (numberOfPlayerTickets >= maxTickets){
            return true;
        }
        return false;
    }

    public int getPlayerTickets(String playerDisplayName){
        return LotteryMechanics.countPlayerTickets(playerDisplayName, tickets);
    }

    public Lottery saveLottery(){
        return this;
    }
}
