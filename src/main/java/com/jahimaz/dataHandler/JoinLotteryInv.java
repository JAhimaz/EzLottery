package com.jahimaz.dataHandler;

import com.jahimaz.EzLottery;
import com.jahimaz.lotteryHandler.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JoinLotteryInv implements InventoryHolder, Listener {
    private final Inventory inv;

    private boolean purchaseMode;

    private EzLottery plugin;

    int purchasingTickets = 1, currentTickets, maxTickets;

    ItemStack ticketCounter;

    public JoinLotteryInv(EzLottery instance, String name, int currentTickets, int maxTickets) {
        this.plugin = instance;

        System.out.println("Current Tickets: " + currentTickets);
        this.currentTickets = currentTickets;
        this.maxTickets = maxTickets;

        inv = Bukkit.createInventory(this, 45, ChatColor.GOLD + "" + ChatColor.BOLD + "Purchase Lottery Tickets");
        firstSetup();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    // Nice little method to create a gui item with a custom name, and description

    protected ItemStack createGuiItem(final Material material, final String dpName, final String name, final int size, boolean enchanted) {
        final ItemStack item = new ItemStack(material, size);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(dpName);
        meta.setLocalizedName(name);
        if(enchanted){
            item.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }
    // You can open the inventory with this

    public void openInventory(final HumanEntity ent) {
        inv.setItem(23, ticketCounter);
        ent.openInventory(inv);
    }
    // Check for clicks on items

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        if (e.getClick().equals(ClickType.NUMBER_KEY)){
            e.setCancelled(true);
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("TicketCounter")){
            if(e.isLeftClick() && ticketCounter.getAmount() < (maxTickets - currentTickets)) {
                ticketCounter.setAmount(ticketCounter.getAmount() + 1);
            }else if(e.isLeftClick() && ticketCounter.getAmount() >= (maxTickets - currentTickets)){
                e.getWhoClicked().sendMessage(ChatColor.RED + "Maximum " + maxTickets + " Tickets");
            }

            if(e.isRightClick() && ticketCounter.getAmount() > 1){
                ticketCounter.setAmount(ticketCounter.getAmount() - 1);
            }else if(e.isRightClick() && ticketCounter.getAmount() == 1){
                e.getWhoClicked().sendMessage(ChatColor.RED + "Minimum 1 Ticket");
            }

            this.purchasingTickets = ticketCounter.getAmount();
            ItemMeta updateName = ticketCounter.getItemMeta();
            updateName.setDisplayName("Tickets For Purchase: " + purchasingTickets + " | " + ChatColor.GREEN + "Price: $" + (plugin.getConfig().getDouble("price-per-ticket") * purchasingTickets));
            ticketCounter.setItemMeta(updateName);
            openInventory(e.getWhoClicked());
        }

        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("purchase")){
            if(EzLottery.currentLottery == null){
                e.getWhoClicked().sendMessage(ChatColor.RED + "The Current Lottery Has Already Ended");
                e.getWhoClicked().closeInventory();
            }
            if(EzLottery.getEconomy().getBalance((Player) e.getWhoClicked()) >= (purchasingTickets * plugin.getConfig().getDouble("price-per-ticket"))){
                EzLottery.getEconomy().withdrawPlayer((Player) e.getWhoClicked(),purchasingTickets * plugin.getConfig().getDouble("price-per-ticket"));
                if(currentTickets == 0){
                    EzLottery.currentLottery.addParticipant();
                }
                //Successful Purchase
                for(int i = 1; i <= purchasingTickets; i++){
                    EzLottery.currentLottery.addTicket(new Ticket((Player) e.getWhoClicked(), EzLottery.currentLottery.getTicketCount() + 1));
                }
                EzLottery.currentLottery.addToPool(purchasingTickets);
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "You Have Purchased " + purchasingTickets + " Tickets!");
                e.getWhoClicked().closeInventory();
            }else{
                e.getWhoClicked().sendMessage(ChatColor.RED + "You Don't Have Enough Money");
                e.getWhoClicked().closeInventory();
            }
        }
        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("cancel")){
            e.getWhoClicked().closeInventory();
        }
    }

    private void firstSetup() {
        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", 1, false));
        }
        ticketCounter = createGuiItem(Material.NAME_TAG, "Tickets For Purchase: " + purchasingTickets + " | " + ChatColor.GREEN + "Price: $" + plugin.getConfig().getDouble("price-per-ticket"),"TicketCounter", 1, false);
        if(currentTickets == 0){
            inv.setItem(21, createGuiItem(Material.BARRIER, ChatColor.RESET + "" + ChatColor.GREEN + "Current Tickets: " + ChatColor.WHITE + currentTickets, "CurrentTickets", 1, false));
        }else{
            inv.setItem(21, createGuiItem(Material.NAME_TAG, "Current Tickets: " + currentTickets, "CurrentTickets", currentTickets, true));
        }
        inv.setItem(24, createGuiItem(Material.PAPER, ChatColor.RESET + "" + ChatColor.AQUA + "Max Purchasable Tickets: " + ChatColor.WHITE + (maxTickets - currentTickets), "MaxTickets", (maxTickets - currentTickets), true));
        inv.setItem(43, createGuiItem(Material.REDSTONE_BLOCK, ChatColor.RESET + "" +ChatColor.RED + "CANCEL", "cancel", 1, false));
        inv.setItem(44, createGuiItem(Material.EMERALD_BLOCK, ChatColor.RESET + "" + ChatColor.GREEN + "PURCHASE TICKETS", "purchase", 1, false));
        inv.setItem(4, createGuiItem(Material.CHEST, ChatColor.RESET + "" + ChatColor.GOLD + "Current Prize Pool: " + ChatColor.GREEN + "$" + EzLottery.currentLottery.getPrizePool(), "PrizePool", 1, true ));
        inv.setItem(3, createGuiItem(Material.CLOCK, ChatColor.RESET + "" + ChatColor.WHITE + "Time Remaining: " + ChatColor.GOLD + EzLottery.currentLottery.getLotteryTimerString(), "Timer", 1, false));
        inv.setItem(5, createGuiItem(Material.PLAYER_HEAD, ChatColor.RESET + "" + ChatColor.WHITE + "Players In Lottery: " + ChatColor.GOLD + EzLottery.currentLottery.getParticipantsCount(), "PlayerCount", 1, true));
        inv.setItem(14, createGuiItem(Material.BOOK, ChatColor.RESET + "" + ChatColor.WHITE + "LEFT CLICK (ADD) | RIGHT CLICK (REMOVE)", "tut1", 1, false));
        inv.setItem(15, createGuiItem(Material.BOOK, ChatColor.RESET + "" + ChatColor.WHITE + "MAXIMUM AMOUNT OF PURCHASABLE TICKETS", "tut2", 1, false));
        inv.setItem(12, createGuiItem(Material.BOOK, ChatColor.RESET + "" + ChatColor.WHITE + "CURRENT AMOUNT OF TICKETS", "tut3", 1, false));
        //Setup Number of tickets
    }
}
