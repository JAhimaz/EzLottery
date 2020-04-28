package com.jahimaz.dataHandler;

import com.jahimaz.EZLottery;
import com.jahimaz.lotteryHandler.Lottery;
import com.jahimaz.lotteryHandler.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
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

import java.util.Arrays;

public class JoinLotteryInv implements InventoryHolder, Listener {
    private final Inventory inv;

    int purchasingTickets = 1, currentTickets, maxTickets;

    ItemStack ticketCounter;

    public JoinLotteryInv(String name, int currentTickets, int maxTickets) {
        System.out.println("Current Tickets: " + currentTickets);
        this.currentTickets = currentTickets;
        this.maxTickets = maxTickets;

        inv = Bukkit.createInventory(this, 45, ChatColor.GOLD + "Purchase Lottery Tickets");
        ticketCounter = createGuiItem(Material.PAPER, "Tickets For Purchase: " + purchasingTickets + " | " + ChatColor.GREEN + "Price: $","TicketCounter", 1, false);
        if(currentTickets == 0){
            inv.setItem(21, createGuiItem(Material.BARRIER, "Current Tickets: " + currentTickets, "CurrentTickets", 1, false));
        }else{
            inv.setItem(21, createGuiItem(Material.PAPER, "Current Tickets: " + currentTickets, "CurrentTickets", currentTickets, true));
        }
        inv.setItem(24, createGuiItem(Material.BOOK, "Max Tickets: " + (maxTickets - currentTickets), "MaxTickets", (maxTickets - currentTickets), true));
        inv.setItem(43, createGuiItem(Material.REDSTONE_BLOCK, "CANCEL", "cancel", 1, false));
        inv.setItem(44, createGuiItem(Material.EMERALD_BLOCK, "PURCHASE TICKETS", "purchase", 1, false));
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
            updateName.setDisplayName("Tickets For Purchase: " + purchasingTickets);
            ticketCounter.setItemMeta(updateName);
            openInventory(e.getWhoClicked());
        }

        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("purchase")){
            if(purchasingTickets == 0){
                e.getWhoClicked().sendMessage(ChatColor.RED + "You Need To Purchase Atleast 1 Ticket");
                e.getWhoClicked().closeInventory();
            }

            //Add Participant If necessary
            if(currentTickets == 0){
                EZLottery.currentLottery.addParticipant();
            }
            //Adding Tickets
            for(int i = 1; i <= purchasingTickets; i++){
                EZLottery.currentLottery.addTicket(new Ticket(e.getWhoClicked().getName(), EZLottery.currentLottery.getTicketCount() + 1));
            }
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "You Have Purchased " + purchasingTickets + " Tickets!");
            e.getWhoClicked().closeInventory();
        }
        if(clickedItem.getItemMeta().getLocalizedName().equalsIgnoreCase("cancel")){
            this.purchasingTickets = 0;
            if(EZLottery.currentLottery.getParticipantsCount() == 0){
                EZLottery.cancelLottery();
            }
            e.getWhoClicked().closeInventory();
        }
    }

    public int getPurchasingTickets() {
        return purchasingTickets;
    }
}
