package com.jahimaz;

import com.jahimaz.commands.LotteryJoin;
import com.jahimaz.dataHandler.LotteryDataHandler;
import com.jahimaz.economy.Economy;
import com.jahimaz.lotteryHandler.Lottery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;


public final class EzLottery extends JavaPlugin {

    public File lotteryConfig;
    public FileConfiguration lotteryConfiguration;
    public static int maxTickets;
    public static Lottery currentLottery;

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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                currentLottery = new Lottery(EzLottery.this);
            }
        }, 10L, (LotteryDataHandler.convertSecondsToTicks(getConfig().getInt("lottery-timer")) + LotteryDataHandler.convertSecondsToTicks(getConfig().getInt("lottery-delay"))));
    }

    public void cancelLottery() {
        currentLottery = null;
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
