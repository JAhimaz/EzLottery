package com.jahimaz;

import com.jahimaz.dataHandler.JoinLotteryInv;
import com.jahimaz.lotteryHandler.Lottery;
import com.jahimaz.lotteryHandler.LotteryMechanics;
import com.jahimaz.lotteryHandler.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class EZLottery extends JavaPlugin {

    int maxTickets = 10;
    public static Lottery currentLottery;

    @Override
    public void onEnable() {
        doWelcomeCheck();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        //Save Current Ongoing Lottery
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("lottery")){

            /*                  CONSOLE SENT COMMANDS                 */

            if(sender instanceof ConsoleCommandSender){
                //Sending Commands From Console
                if(args.length == 0){
                    getPluginDetails();
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("Reload")){
                        reloadPlugin();
                    }
                }
            }

            /*                  PLAYER SENT COMMANDS                 */

            if(sender instanceof Player){
                if(args.length == 0){
                    getPluginDetails();
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("Reload")){
                        reloadPlugin();
                    }
                    if(args[0].equalsIgnoreCase("join")){
                        if(currentLottery == null){
                            createLottery();
                        }
                        int playerCurrentTickets = currentLottery.getPlayerTickets(((Player) sender).getDisplayName());
                        JoinLotteryInv lotteryJoin = new JoinLotteryInv(((Player) sender).getDisplayName(), playerCurrentTickets, maxTickets);
                        getServer().getPluginManager().registerEvents(lotteryJoin, this);
                        lotteryJoin.openInventory(((Player) sender).getPlayer());
                        int purchasingTickets = lotteryJoin.getPurchasingTickets();
                    }
                    if(args[0].equalsIgnoreCase("debug")){
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "No Lottery In Progress");
                        }else{
                            ArrayList<Ticket> currentTickets = currentLottery.getTickets();
                            sender.sendMessage(ChatColor.WHITE + "Current Tickets in Lottery = " + currentLottery.getTicketCount());
                            for(int i = 0; i < currentTickets.size(); i++){
                                sender.sendMessage("Ticket #" + currentTickets.get(i).getTicketNumber() + " Owned By: " + currentTickets.get(i).getPlayerName());
                            }
                        }
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                    }
                }
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("join")){
                        if(currentLottery == null){
                            createLottery();
                        }
                        //Do Check To See If Player Is Already Entered
                        if(!(currentLottery.checkPlayer(((Player) sender).getDisplayName(), maxTickets))){
                            try{
                                //Change Max Tickets To Read Config
                                int playerCurrentTickets = currentLottery.getPlayerTickets(((Player) sender).getDisplayName());
                                int noOfTickets = Integer.parseInt(args[1]);

                                if(noOfTickets > 0 && noOfTickets <= maxTickets){
                                    if(noOfTickets <= (maxTickets - playerCurrentTickets)){
                                        //CHECK IF PLAYER CAN AFFORD (VAULT DEPENDENCY)
                                        //Check If first purchase
                                        if(playerCurrentTickets == 0){
                                            currentLottery.addParticipant();

                                        }

                                        //Adding Tickets
                                        for(int i = 1; i <= noOfTickets; i++){
                                            currentLottery.addTicket(new Ticket(((Player) sender).getDisplayName(), currentLottery.getTicketCount() + 1));
                                        }
                                        sender.sendMessage(ChatColor.GREEN + "You have successfully purchased " + noOfTickets + " tickets!");
                                    }else{
                                        sender.sendMessage(ChatColor.RED + "You can't purchase more than " + maxTickets + " tickets!");
                                    }
                                }else{
                                    sender.sendMessage(ChatColor.RED + "Please Enter A Valid Ticket Amount (1 - " + maxTickets + ")");
                                }
                            }catch(NumberFormatException e){
                                sender.sendMessage(ChatColor.RED + args[1] + " Is not a valid ticket number! (1 - " + maxTickets + ")");
                            }
                        }else{
                            sender.sendMessage(ChatColor.RED + "You can't purchase more than " + maxTickets + " tickets!");
                        }

                        if(currentLottery.getParticipantsCount() == 0){
                            cancelLottery();
                        }
                    }
                }
            }
        }

        return false;
    }

    private void createLottery() {
        currentLottery = new Lottery();
    }

    private void cancelLottery() {
        currentLottery = null;
    }

    private void doWelcomeCheck() {
        //Check Configs
        //Get Current Ongoing (If Any)
    }

    private void reloadPlugin() {
        //Reload Plugin
    }

    private void getPluginDetails() {
        //Print Plugin Details
    }
}
