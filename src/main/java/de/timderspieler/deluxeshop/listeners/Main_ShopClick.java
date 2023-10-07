package de.timderspieler.deluxeshop.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import de.timderspieler.deluxeshop.main.DeluxeShop;

public class Main_ShopClick 
implements Listener {
	
	private DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }
	
	private static HashMap<Player, Integer> active_buyers = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> getActiveBuyers() { return active_buyers; }
	
	@EventHandler
	public void onUserClick(InventoryClickEvent e) {
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		ItemStack stack = e.getCurrentItem();
		
		if (!getRawName(e.getView().getTitle()).equals(getRawName(getPlugin().getShopTitle()))) {
			return;
		}
		
		int slot = e.getSlot();
		
		e.setCancelled(true);
		
		if (!itemCheckValid(stack)) {
			return;
		}
		
		if (active_buyers.containsKey(p)) {
			
			if (slot == 24) {
				getPlugin().loadInShopItems(getPlugin().getOpenedShops().get(p));
				active_buyers.remove(p);
			}
			
			if (slot == 20) {
				getPlugin().getOpenedShops().remove(p);
				getPlugin().purchaseItem(p, active_buyers.get(p));
			}
			
			return;
		}
		
		if (!getPlugin().isValidItem(slot)) {
			return;
		}
		
		active_buyers.put(p, slot);
		getPlugin().openPurchaseMenu(p);
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		
		if (!getRawName(e.getView().getTitle()).equals(getRawName(getPlugin().getShopTitle()))) {
			return;
		}
		
		Player p = (Player) e.getPlayer();
		
		active_buyers.remove(p);
		getPlugin().getOpenedShops().remove(p);
		
	}
	
	private boolean itemCheckValid(ItemStack check) {
		
		if (check == null) {
			return false;
		}
		
		if (check.getType() == Material.AIR) {
			return false;
		}
		
		if (check.getItemMeta() == null) {
			return false;
		}
		
		return true;
		
	}
	
	private String getRawName(String color_name) {
		return ChatColor.stripColor(color_name);
	}

}
