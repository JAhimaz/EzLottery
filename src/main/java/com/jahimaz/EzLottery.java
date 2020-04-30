package com.jahimaz;

import com.jahimaz.commands.LotteryJoin;
import com.jahimaz.dataHandler.LotteryDataHandler;
import com.jahimaz.dataHandler.PlayerDataHandler;
import com.jahimaz.economy.Economy;
import com.jahimaz.lotteryHandler.Lottery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;


public final class EzLottery extends JavaPlugin {

    int lotteryStart;
    public File lotteryConfig;
    public FileConfiguration lotteryConfiguration;
    public static int maxTickets;
    public static Lottery currentLottery;

    int starttimer = 40;
    public int timer = 40;

    @Override
    public void onEnable() {
        if (!Economy.setupEconomy()) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        doWelcomeMessage();
        maxTickets = getConfig().getInt("max-tickets");
        loadConfig();
        loadPlugins();
        if(getConfig().getBoolean("plugin-enabled")){
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
                @Override
                public void run(){
                    startLottery();
                }
            });
        }
    }

    private void doWelcomeMessage() {
        System.out.println("====================================");
        System.out.println(ChatColor.GOLD + "Lottery Plugin Enabled");
        System.out.println("====================================");
    }

    @Override
    public void onDisable() {
        System.out.println("Shutting Down Lottery Plugin");
        saveLotteryFiles();
    }

    private void loadPlugins(){
        this.getCommand("lottery").setExecutor(new LotteryJoin(this));
    }


    public void loadConfig(){

        final FileConfiguration config = this.getConfig();
        getConfig().options().copyDefaults(true);

        lotteryConfig = new File(getDataFolder(), "lottery_data.yml"); // set the file location
        lotteryConfiguration = YamlConfiguration.loadConfiguration(lotteryConfig);

        saveLotteryFiles();
        saveDefaultConfig();
    }

    public void startLottery(){
        lotteryStart = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(timer == starttimer){
                    --timer;
                }else if(timer == 30){
                    Bukkit.broadcastMessage(ChatColor.GOLD + "30 Seconds Till Next Lottery");
                    --timer;
                }else if(timer == 10){
                    Bukkit.broadcastMessage(ChatColor.GOLD + "10 Seconds Till Next Lottery");
                    --timer;
                }else if(timer <= getConfig().getInt("lottery-delay") + getConfig().getInt("lottery-timer")){
                    --timer;
                    if(timer <= 1){
                        currentLottery = new Lottery(EzLottery.this);
                        timer = getConfig().getInt("lottery-delay") + getConfig().getInt("lottery-timer");
                    }
                }
            }
        }, 0L, 20L);
    }

    public void manualCancelLottery(){
        Bukkit.getScheduler().cancelTask(lotteryStart);
        currentLottery = null;
    }

    public void cancelLottery() {
        currentLottery = null;
        Bukkit.broadcastMessage("The Next Lottery Will Be In " + LotteryDataHandler.timeHandler(timer));
    }

    public void saveLotteryFiles() {
        try {
            lotteryConfiguration.save(lotteryConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadPlugin(){
        //Give Warning that current lottery will not save
        //Save lottery
        //Reload Configs
    }
}
