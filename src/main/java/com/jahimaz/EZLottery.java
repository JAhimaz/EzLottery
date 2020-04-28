package com.jahimaz;

import com.jahimaz.dataHandler.JoinLotteryInv;
import com.jahimaz.lotteryHandler.Lottery;
import com.jahimaz.lotteryHandler.LotteryMechanics;
import com.jahimaz.lotteryHandler.Ticket;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class EZLottery extends JavaPlugin {

    //Vault Related
    private static Economy econ = null;


    int maxTickets = getConfig().getInt("max-tickets");
    public static Lottery currentLottery;

    @Override
    public void onEnable() {
        doWelcomeCheck();
        loadConfig();
        loadPlugins();
        if (!setupEconomy() ) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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
                        JoinLotteryInv lotteryJoin = new JoinLotteryInv(this,((Player) sender).getDisplayName(), playerCurrentTickets, maxTickets);
                        getServer().getPluginManager().registerEvents(lotteryJoin, this);
                        lotteryJoin.openInventory(((Player) sender).getPlayer());
                    }
                    if(args[0].equalsIgnoreCase("debug")){
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                        if(currentLottery == null){
                            sender.sendMessage(ChatColor.RED + "No Lottery In Progress");
                        }else{
                            ArrayList<Ticket> currentTickets = currentLottery.getTickets();
                            sender.sendMessage(ChatColor.WHITE + "Current Tickets in Lottery: " + ChatColor.GREEN + currentLottery.getTicketCount());
                            sender.sendMessage(ChatColor.WHITE + "Current Participants in Lottery: " + ChatColor.GREEN + currentLottery.getParticipantsCount());
                            for(int i = 0; i < currentTickets.size(); i++){
                                sender.sendMessage("Ticket #" + currentTickets.get(i).getTicketNumber() + " Owned By: " + currentTickets.get(i).getPlayerName());
                            }
                        }
                        sender.sendMessage(ChatColor.GREEN + "=============== DEBUG =================");
                    }
                }
            }
        }

        return false;
    }

    private void createLottery() {
        currentLottery = new Lottery();
    }

    public static void cancelLottery() {
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

    private void loadPlugins(){

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void loadConfig(){
        final FileConfiguration config = this.getConfig();

        getConfig().options().copyDefaults(true);
        //Placeholders

        saveDefaultConfig();
    }
}
