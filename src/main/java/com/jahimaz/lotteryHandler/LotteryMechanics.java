package com.jahimaz.lotteryHandler;

import java.util.ArrayList;

public class LotteryMechanics {

    public static int countPlayerTickets(String playerDisplayName, ArrayList<Ticket> tickets){
        ArrayList<Ticket> playerTickets = new ArrayList<Ticket>();
        for(int i = 0; i < tickets.size(); i++){
            if(tickets.get(i).playerName.equalsIgnoreCase(playerDisplayName)){
                playerTickets.add(tickets.get(i));
            }
        }

        return playerTickets.size();
    }

    public static boolean maxTicketCheck(int purchaseAmount, int currentTickets, int maxTickets){
        if(purchaseAmount <= (maxTickets - currentTickets)){
            return true;
        }
        return false;
    }
}
