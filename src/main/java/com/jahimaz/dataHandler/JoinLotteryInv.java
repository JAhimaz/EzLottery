package com.jahimaz.dataHandler;

import com.jahimaz.EZLottery;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class JoinLotteryInv implements InventoryHolder, Listener {
    private final Inventory inv;

    private EZLottery plugin;

    int purchasingTickets = 1, currentTickets, maxTickets;

    ItemStack ticketCounter;

    public JoinLotteryInv(EZLottery instance, String name, int currentTickets, int maxTickets) {
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
            if(purchasingTickets == 0){
                e.getWhoClicked().sendMessage(ChatColor.RED + "You Need To Purchase Atleast 1 Ticket");
                e.getWhoClicked().closeInventory();
            }

            if(EZLottery.getEconomy().getBalance((Player) e.getWhoClicked()) >= (purchasingTickets * plugin.getConfig().getDouble("price-per-ticket"))){
                EZLottery.getEconomy().withdrawPlayer((Player) e.getWhoClicked(),purchasingTickets * plugin.getConfig().getDouble("price-per-ticket"));
                if(currentTickets == 0){
                    EZLottery.currentLottery.addParticipant();
                }
                //Adding Tickets
                for(int i = 1; i <= purchasingTickets; i++){
                    EZLottery.currentLottery.addTicket(new Ticket(e.getWhoClicked().getName(), EZLottery.currentLottery.getTicketCount() + 1));
                }
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "You Have Purchased " + purchasingTickets + " Tickets!");
            }else{
                e.getWhoClicked().sendMessage(ChatColor.RED + "You Don't Have Enough Money");
            }
            e.getWhoClicked().closeInventory();
        }
        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("cancel")){
            this.purchasingTickets = 0;
            e.getWhoClicked().closeInventory();
        }

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e){
        if (e.getInventory().getHolder() != this) return;

        if(EZLottery.currentLottery.getParticipantsCount() == 0){
            EZLottery.cancelLottery();
        }
    }

    private void firstSetup() {
        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", 1, false));
        }
        ticketCounter = createGuiItem(Material.PAPER, "Tickets For Purchase: " + purchasingTickets + " | " + ChatColor.GREEN + "Price: $" + plugin.getConfig().getDouble("price-per-ticket"),"TicketCounter", 1, false);
        if(currentTickets == 0){
            inv.setItem(21, createGuiItem(Material.BARRIER, ChatColor.RESET + "" + ChatColor.GREEN + "Current Tickets: " + ChatColor.WHITE + currentTickets, "CurrentTickets", 1, false));
        }else{
            inv.setItem(21, createGuiItem(Material.PAPER, "Current Tickets: " + currentTickets, "CurrentTickets", currentTickets, true));
        }
        inv.setItem(24, createGuiItem(Material.BOOK, ChatColor.RESET + "" + ChatColor.AQUA + "Max Purchasable Tickets: " + ChatColor.WHITE + (maxTickets - currentTickets), "MaxTickets", (maxTickets - currentTickets), true));
        inv.setItem(43, createGuiItem(Material.REDSTONE_BLOCK, ChatColor.RESET + "" +ChatColor.RED + "CANCEL", "cancel", 1, false));
        inv.setItem(44, createGuiItem(Material.EMERALD_BLOCK, ChatColor.RESET + "" + ChatColor.GREEN + "PURCHASE TICKETS", "purchase", 1, false));
    }
}
