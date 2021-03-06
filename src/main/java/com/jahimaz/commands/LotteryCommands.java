package com.jahimaz.commands;

import com.jahimaz.EzLottery;
import com.jahimaz.dataHandler.JoinLotteryInv;
import com.jahimaz.dataHandler.LotteryDataHandler;
import com.jahimaz.dataHandler.PlayerDataHandler;
import com.jahimaz.lotteryHandler.Ticket;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.jahimaz.EzLottery.*;
import static org.bukkit.Bukkit.getServer;

public class LotteryCommands implements CommandExecutor {

    EzLottery plugin;

    public LotteryCommands(EzLottery instance){
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            /*                  CONSOLE SENT COMMANDS                 */

            if(sender instanceof ConsoleCommandSender){
                //Sending Commands From Console
                if(args.length == 0){
                    getPluginDetails(sender);
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("Reload")){
                        //reloadPlugin();
                    }
                }
                if(args[0].equalsIgnoreCase("time")){
                    if(currentLottery != null){
                        System.out.println(ChatColor.GOLD + "The Lottery Will End In " + LotteryDataHandler.timeHandler(currentLottery.getLotteryTimer()));
                    }else{
                        System.out.println(ChatColor.GOLD + "Next Lottery Will Be In " + LotteryDataHandler.timeHandler(plugin.timer));
                    }
                }

            }

            /*                  PLAYER SENT COMMANDS                 */

            if(sender instanceof Player){
                if(args.length == 0){
                    getPluginDetails(sender);
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("Reload") && sender.hasPermission("lottery.reload")){
                        //EzLottery.reloadPlugin();
                    }
                    if(args[0].equalsIgnoreCase("Cancel") && sender.hasPermission("lottery.cancel")){
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "There is no lottery in progress.");
                        }else{
                            plugin.lotteryConfiguration.set("current-lottery", plugin.lotteryConfiguration.getInt("current-lottery") - 1);
                            plugin.saveLotteryFiles();
                            currentLottery.cancelLotteryTimer();
                            plugin.manualCancelLottery();
                            sender.sendMessage(ChatColor.GREEN + "The Lottery has successfully been cancelled!");
                        }
                    }
                    if(args[0].equalsIgnoreCase("tickets")){
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "There is no lottery in progress.");
                        }else{
                            ArrayList<Ticket> tickets = PlayerDataHandler.getPlayerTickets(((Player) sender).getDisplayName(), currentLottery.getTickets());
                            sender.sendMessage(ChatColor.WHITE + "Your Purchased Tickets Numbers: ");
                            for(int i = 0; i < tickets.size(); i++){
                                sender.sendMessage(ChatColor.WHITE + "#" + tickets.get(i).getTicketNumber());
                            }
                            sender.sendMessage(ChatColor.WHITE + "====================================");
                        }
                    }
                    if(args[0].equalsIgnoreCase("Start") && sender.hasPermission("lottery.start")){
                        if(currentLottery != null){
                            sender.sendMessage(ChatColor.RED + "There is already a lottery in progress.");
                        }else{
                            plugin.lotteryConfiguration.set("current-lottery", plugin.lotteryConfiguration.getInt("current-lottery") + 1);
                            plugin.saveLotteryFiles();
                            plugin.startLottery();
                        }
                    }
                    if(args[0].equalsIgnoreCase("time")){
                        if(currentLottery != null){
                            sender.sendMessage(ChatColor.GOLD + "The Lottery Will End In " + LotteryDataHandler.timeHandler(currentLottery.getLotteryTimer()));
                        }else{
                            sender.sendMessage(ChatColor.GOLD + "Next Lottery Will Be In " + LotteryDataHandler.timeHandler(plugin.timer));
                        }
                    }
                    if(args[0].equalsIgnoreCase("Join") || args[0].equalsIgnoreCase("Buy") || args[0].equalsIgnoreCase("Play")){
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "There Is No Active Lottery");
                        }else{
                            int playerCurrentTickets = currentLottery.getPlayerTickets(((Player) sender).getDisplayName());
                            if(playerCurrentTickets >= maxTickets){
                                sender.sendMessage(ChatColor.RED + "You purchased the maximum amount of tickets");
                            }else{
                                JoinLotteryInv lotteryJoin = new JoinLotteryInv(plugin,((Player) sender).getDisplayName(), playerCurrentTickets, maxTickets);
                                getServer().getPluginManager().registerEvents(lotteryJoin, plugin);
                                lotteryJoin.openInventory(((Player) sender).getPlayer());
                            }
                        }

                    }
                    if(args[0].equalsIgnoreCase("debug") && sender.hasPermission("lottery.debug")){
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "No Lottery In Progress");
                        }else{
                            ArrayList<Ticket> currentTickets = currentLottery.getTickets();
                            sender.sendMessage(ChatColor.WHITE + "LOTTERY " + ChatColor.GREEN + "#" + plugin.lotteryConfiguration.get("current-lottery"));
                            sender.sendMessage(ChatColor.WHITE + "Time Remaining: " + ChatColor.GREEN + LotteryDataHandler.timeHandler(currentLottery.getLotteryTimer()));
                            sender.sendMessage(ChatColor.WHITE + "Current Prize Pool in Lottery: " + ChatColor.GREEN + "$" + currentLottery.getPrizePool());
                            sender.sendMessage(ChatColor.WHITE + "Current Tickets in Lottery: " + ChatColor.GREEN + currentLottery.getTicketCount());
                            sender.sendMessage(ChatColor.WHITE + "Current Participants in Lottery: " + ChatColor.GREEN + currentLottery.getParticipantsCount());
                            for(int i = 0; i < currentTickets.size(); i++){
                                sender.sendMessage("Ticket #" + currentTickets.get(i).getTicketNumber() + " Owned By: " + currentTickets.get(i).getPlayerName());
                            }
                        }
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                    }
                }
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("add") && sender.hasPermission("lottery.pool")){
                        try{
                            int increasePool = Integer.parseInt(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "You've Added $" + increasePool + " To The Lottery Pool");
                            currentLottery.addMoneyToPool(increasePool);
                        }catch(NumberFormatException e){
                            sender.sendMessage(ChatColor.RED + args[1] + " Is not a valid amount");
                        }
                    }
                }
            }
        return false;
    }

    private void getPluginDetails(CommandSender sender) {
        if(sender instanceof ConsoleCommandSender){
            System.out.println("=======================================");
            System.out.println(ChatColor.GOLD + "Lottery Version: " + plugin.getDescription().getVersion());
            System.out.println("Do " + ChatColor.GREEN + "/Lottery Join" + ChatColor.GOLD + " To Join An Ongoing Lottery");
            System.out.println(ChatColor.GOLD + "In The Menu, Increase (Left Click) Or Decrease (RightClick) The Nametag To Choose");
            System.out.println(ChatColor.GOLD + "How Many Tickets You Would Like To Purchase, Then Hit Purchase (Emerald Block)");
            System.out.println(ChatColor.RED + "Do Note This Is Considered Gambling (Please Avoid If HARAM / Under 18)");
            System.out.println("=======================================");
        }
        if(sender instanceof Player){
            sender.sendMessage("=======================================");
            sender.sendMessage(ChatColor.GOLD + "Lottery Version: " + plugin.getDescription().getVersion());
            sender.sendMessage("Do " + ChatColor.GREEN + "/Lottery Join" + ChatColor.GOLD + " To Join An Ongoing Lottery");
            sender.sendMessage(ChatColor.GOLD + "In The Menu, Increase (Left Click) Or Decrease (RightClick) The Nametag To Choose");
            sender.sendMessage(ChatColor.GOLD + "How Many Tickets You Would Like To Purchase, Then Hit Purchase (Emerald Block)");
            sender.sendMessage(ChatColor.RED + "Do Note This Is Considered Gambling (Please Avoid If HARAM / Under 18)");
            sender.sendMessage("=======================================");
        }
    }

}
