package de.timderspieler.deluxeshop.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.timderspieler.deluxeshop.main.DeluxeShop;
import de.timderspieler.deluxeshop.objects.ShopItem;

public class Admin_ShopClick 
implements Listener {
	
	private DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }
	private static HashMap<Player, Integer> admin_triggers = new HashMap<Player, Integer>();
	private static HashMap<Player, String> admin_work = new HashMap<Player, String>();
	
	public static HashMap<Player, String> getAdminWork() { return admin_work; }
	public static HashMap<Player, Integer> getAdminSlots() { return admin_triggers; };
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		ItemStack stack = e.getCurrentItem();
		
		if (!e.getView().getTitle().equals("DeluxeShop Itemoptions")) {
			return;
		}
		
		e.setCancelled(true);
		
		if (!itemCheckValid(stack)) {
			return;
		}
		
		int slot = e.getSlot();
		
		if (admin_triggers.containsKey(p)) {
			
			ShopItem target = getPlugin().getItem(getAdminSlots().get(p));
			
			if (slot == 10) {
				target.setAddToInventory(target.isAddToInventory() ? false : true);
				getPlugin().updateAdminOptions(p, false, "ADDTOINVENTORY");
				return;
			}
			
			if (slot == 16) {
				target.setRequiredHook(getPlugin().getNextHook(target.getRequiredHook()));
				getPlugin().updateAdminOptions(p, false, "PLUGIN");
				return;
			}
			
			if (slot == 19) {
				getAdminWork().put(p, "COMMANDS");
				sendInstructions(p);
				p.closeInventory();
				return;
			}
			
			if (slot == 25) {
				target.setRequirement_type(getPlugin().getNextRequirement(target.getRequiredHook(), target.getRequirement_type()));
				getPlugin().updateAdminOptions(p, false, "PLUGIN");
				return;
			}
			
			if (slot == 28) {
				getAdminWork().put(p, "PRICE");
				sendInstructions(p);
				p.closeInventory();
				return;
			}
			
			if (slot == 34) {
				getAdminWork().put(p, "REQUIREMENT_VALUE");
				sendInstructions(p);
				p.closeInventory();
				return;
			}
			
			if (slot == 37) {
				target.setCurrency(getPlugin().getNextCurrency(target.getRequiredHook(), target.getCurrency()));
				getPlugin().updateAdminOptions(p, false, "PLUGIN");
				return;
			}
			
			if (slot == 43) {
				getPlugin().getItems().remove(getAdminSlots().get(p));
				p.closeInventory();
				getPlugin().sendMSG(p, true, "The item has been removed from the config. It is still available in the &e/deluxeshop editShop &fmenu.");
				return;
			}
			
			return;
		}
		
		if (getPlugin().getItem(slot) == null) {
			getPlugin().getItems().put(slot, new ShopItem(slot));
		}
		
		admin_triggers.put(p, slot);
		getPlugin().updateAdminOptions(p, true, "");
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		
		if (!e.getView().getTitle().equals("DeluxeShop Itemoptions")) {
			return;
		}
		
		Player p = (Player) e.getPlayer();
		
		if (getAdminWork().containsKey(p)) {
			return;
		}
		
		getAdminSlots().remove(p);
		getAdminWork().remove(p);
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
		
		String displayname = check.getItemMeta().getDisplayName();
		
		if (displayname == null) {
			return false;
		}
		
		return true;
		
	}
	
	private void sendInstructions(Player p) {
		
		if (!getAdminWork().containsKey(p)) {
			return;
		}
		
		getPlugin().sendMSG(p, false, "");
		getPlugin().sendMSG(p, false, " &b&lInstructions");
		getPlugin().sendMSG(p, false, "");
		getPlugin().sendMSG(p, false, " &f/deluxeshop setValue (Value)");
		
		String work = getAdminWork().get(p);
		
		if (work.equalsIgnoreCase("PRICE")) {
			getPlugin().sendMSG(p, false, "");
			getPlugin().sendMSG(p, false, "&fPlease set a price for the item.");
		}
		
		if (work.equalsIgnoreCase("COMMANDS")) {
			getPlugin().sendMSG(p, false, " &f/deluxeshop setValue clear");
			getPlugin().sendMSG(p, false, "");
			getPlugin().sendMSG(p, false, "&fPlease add a command for the item. Use the clear command to remove all commands.");
		}
		
		if (work.equalsIgnoreCase("REQUIREMENT_VALUE")) {
			getPlugin().sendMSG(p, false, "");
			getPlugin().sendMSG(p, false, "&fPlease set a requirement, that is needed to purchase the item.");
		}
		
		getPlugin().sendMSG(p, false, "");
		
	}

}
