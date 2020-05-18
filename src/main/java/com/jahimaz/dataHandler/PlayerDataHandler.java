package com.jahimaz.dataHandler;

import com.jahimaz.lotteryHandler.Ticket;

import java.util.ArrayList;

public class PlayerDataHandler {
    public static int countPlayerTickets(String playerDisplayName, ArrayList<Ticket> tickets){
        ArrayList<Ticket> playerTickets = new ArrayList<Ticket>();
        for(int i = 0; i < tickets.size(); i++){
            if(tickets.get(i).getPlayerName().equalsIgnoreCase(playerDisplayName)){
                playerTickets.add(tickets.get(i));
            }
        }
        return playerTickets.size();
    }

    public static ArrayList<Ticket> getPlayerTickets(String playerDisplayName, ArrayList<Ticket> tickets){
        ArrayList<Ticket> playerTickets = new ArrayList<Ticket>();
        for(int i = 0; i < tickets.size(); i++){
            if(tickets.get(i).getPlayerName().equalsIgnoreCase(playerDisplayName)){
                playerTickets.add(tickets.get(i));
            }
        }
        return playerTickets;
    }

    public static int getPlayerPercentage(int playerTickets, int currentPurchasingTickets, int numberOfTicketsInPool){
        return 0;
    }

    public static int getPlayerPercentage(int playerTickets,int numberOfTicketsInPool){
        return 0;
    }
}
