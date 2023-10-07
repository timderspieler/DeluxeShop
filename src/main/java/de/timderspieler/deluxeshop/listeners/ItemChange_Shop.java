package de.timderspieler.deluxeshop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.timderspieler.deluxeshop.main.DeluxeShop;

public class ItemChange_Shop 
implements Listener {
	
	private DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		
		if (!e.getView().getTitle().equals("DeluxeShop Itemschange")) {
			return;
		}
		
		Player p = (Player) e.getPlayer();
		
		if (!p.hasPermission("deluxeshop.admin")) {
			getPlugin().sendMSG(p, true, getPlugin().getNoPermMSG());
			return;
		}
		
		if (!getPlugin().getOpenedShops().containsKey(p)) {
			getPlugin().sendMSG(p, true, "&cSomething went wrong while saving the shop. Try again!");
			return;
		}
		
		getPlugin().overrideShopItems(getPlugin().getOpenedShops().get(p));
		getPlugin().sendMSG(p, true, "&aShop has been successfully saved.");
		
	}

}
