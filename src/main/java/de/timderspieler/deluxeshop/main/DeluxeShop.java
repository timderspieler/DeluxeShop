package de.timderspieler.deluxeshop.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import com.cryptomorin.xseries.XMaterial;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.timderspieler.deluxeshop.external.Currency;
import de.timderspieler.deluxeshop.external.ShopHook;
import de.timderspieler.deluxeshop.external.ShopTrait;
import de.timderspieler.deluxeshop.listeners.Admin_ShopClick;
import de.timderspieler.deluxeshop.listeners.ItemChange_Shop;
import de.timderspieler.deluxeshop.listeners.Main_ShopClick;
import de.timderspieler.deluxeshop.objects.ShopItem;
import de.timderspieler.deluxeshop.storage.FileManager;

public class DeluxeShop 
extends JavaPlugin {
	
	private static DeluxeShop m;
	
	private FileManager fm;
	private ItemUtils iu;
	
	private Inventory main_shop;
	private Inventory purchase_menu;
	private HashMap<Player, Inventory> opened_shops = new HashMap<Player, Inventory>();
	private HashMap<Integer, ShopItem> items = new HashMap<Integer, ShopItem>();
	private static HashMap<String, ShopHook> hooks = new HashMap<String, ShopHook>();
	
	private String shop_title = "";
	private String no_perm = "";
	private String open_perm = "";
	private String in_shop_lore = "";
	
	@Override
	public void onEnable() {
		
		m = this;
		
		fm = new FileManager();
		iu = new ItemUtils();
		
		loadInventory();
		loadItems();
		loadPurchaseMenu();
		
		shop_title = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Shop-Title"));
		no_perm = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("No-Permission"));
		in_shop_lore = getFM().getDefaultConfig().getString("In-Shop-Lore");
		open_perm = getFM().getDefaultConfig().getString("Shop-Permission");
		
		Bukkit.getPluginManager().registerEvents(new Admin_ShopClick(), this);
		Bukkit.getPluginManager().registerEvents(new ItemChange_Shop(), this);
		Bukkit.getPluginManager().registerEvents(new Main_ShopClick(), this);
		
		getCommand("deluxeshop").setExecutor(new DeluxeShopCommand());
		
		if(getServer().getPluginManager().getPlugin("Citizens") == null || !Objects.requireNonNull(getServer().getPluginManager().getPlugin("Citizens")).isEnabled()) {
			sendConsoleError("Citizens 2.0 not found or not enabled");
			return;
		} else {
			CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ShopTrait.class).withName("deluxeshop"));
		}
		
	}
	
	public void onDisable() {
		saveInventory();
		saveItems();
	}
	
	public static DeluxeShop getPlugin() { return m; }
	public FileManager getFM() { return fm; }
	public ItemUtils getIU() { return iu; }
	public HashMap<Player, Inventory> getOpenedShops() { return opened_shops; }
	public String getShopTitle() { return shop_title; }
	public String getNoPermMSG() { return no_perm; }
	public String getOpenPerm() { return open_perm; }
	public HashMap<Integer, ShopItem> getItems() { return items; }
	public HashMap<String, ShopHook> getHooks() { return hooks; }
	
	public static void sendConsoleError(String string) {
		getPlugin().getLogger().log(Level.SEVERE, "[DeluxeShop] " + string);
	}
	
	public static void sendConsoleMSG(String string) {
		getPlugin().getLogger().log(Level.INFO, "[DeluxeShop] " + string);
	}
	
	public void sendMSG(Player p, boolean prefix, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix ? "&b&lDeluxeShop &r&f| " : "") + msg));
	}
	
	public static void registerHook(Plugin plugin, ShopHook hook) {
		if (plugin != null && hook != null && !hooks.containsKey(plugin.getDescription().getName()) && isValidHook(plugin, hook)) {
			hooks.put(plugin.getDescription().getName(), hook);
			sendConsoleMSG("Accepted " + plugin.getDescription().getName() + " hook!");
		} else {
			sendConsoleMSG("Denied " + plugin.getDescription().getName() + " hook!");
		}
	}
	
	private static boolean isValidHook(Plugin plugin, ShopHook hook) {
		
		if (hook.getCurrencies().isEmpty()) {
			sendConsoleError("Cannot accept hook of " + plugin.getDescription().getName() + ". Reason: Currency List is empty.");
			return false;
		}
		
		if (!((Object) hook.getCurrencies().get(0) instanceof Currency)) {
			sendConsoleError("Cannot accept hook of " + plugin.getDescription().getName() + ". Reason: Plugin did not return List of type 'Currency'.");
			return false;
		}
		
		return true;
		
	}
	
	public ShopItem getItem(int slot) {
		return items.get(slot);
	}
	
	public ShopHook getHook(String plugin) {
		return hooks.get(plugin);
	}
	
	private void saveInventory() {
		getFM().getDataConfig().set("shop-inventory", inventoryToString(main_shop));
		getFM().saveDataFile();
	}
	
	private void saveItems() {
		List<String> save = new ArrayList<String>();
		for (int slots : items.keySet()) {
			save.add(getItem(slots).toString());
		}
		getFM().getDataConfig().set("shop-items", save);
		getFM().saveDataFile();
	}
	
	private void loadInventory() {
		if (getFM().getDataConfig().get("shop-inventory") == null) {
			main_shop = Bukkit.createInventory(null, 54);
		} else {
			try {
				main_shop = stringToInventory(getFM().getDataConfig().getString("shop-inventory"));
			} catch (IOException e) {}
		}
	}
	
	private void loadPurchaseMenu() {
		
		purchase_menu = Bukkit.createInventory(null, 54);
		
		String purchase_name = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Purchase-Item.Name"));
		String purchase_lore = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Purchase-Item.Lore"));
		String cancel_name = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Cancel-Item.Name"));
		String cancel_lore = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Cancel-Item.Lore"));
		String info_name = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Buy-Information.Name"));
		String info_lore = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Buy-Information.Lore"));
		
		purchase_menu.setItem(20, getIU().buildItem(purchase_name, XMaterial.EMERALD_BLOCK.parseMaterial(), purchase_lore));
		purchase_menu.setItem(24, getIU().buildItem(cancel_name, XMaterial.REDSTONE_BLOCK.parseMaterial(), cancel_lore));
		purchase_menu.setItem(31, getIU().buildItem(info_name, XMaterial.PAPER.parseMaterial(), info_lore));
		
	}
	
	private void loadItems() {
		
		if (getFM().getDataConfig().get("shop-items") != null) {
			for (String all : getFM().getDataConfig().getStringList("shop-items")) {
				ShopItem current = new ShopItem(all.split("&"));
				items.put(current.getSlot(), current);
			}
		}
		
	}
	
	public void openShop(Player p) {
		
		Inventory temp = Bukkit.createInventory(null, 54, getShopTitle());
		loadInShopItems(temp);

		for (int i = 0; i < temp.getSize(); i++) {
			if (items.containsKey(i)) {
				ShopItem si = items.get(i);
				ItemStack current = temp.getItem(i);
				String info_lore = "" + in_shop_lore;
				info_lore = info_lore.replaceAll("%currency%", si.getCurrency());
				info_lore = info_lore.replaceAll("%amount%", "" + si.getPrice());

				if (!si.getRequirement_type().equalsIgnoreCase("none")) {
					info_lore = info_lore.replaceAll("%requirement%", si.getRequirement_type());
					info_lore = info_lore.replaceAll("%requirement_value%", si.getRequirement_value());
				} else {
					info_lore = info_lore.replaceAll("%requirement%", ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("None")));
					info_lore = info_lore.replaceAll("%requirement_value%", "");
				}
				getIU().addLores(current, info_lore);
				temp.setItem(i, current);
			}
		}

		opened_shops.put(p, temp);
		
		p.openInventory(temp);
		
	}
	
	public void loadInShopItems(Inventory temp) {
		temp.setContents(main_shop.getContents());
	}
	
	public void overrideShopItems(Inventory temp) {
		main_shop.setContents(temp.getContents());
	}
	
	public void openPurchaseMenu(Player p) {
		
		if (!opened_shops.containsKey(p) || !Main_ShopClick.getActiveBuyers().containsKey(p)) {
			return;
		}
		
		Inventory temp = opened_shops.get(p);
		
		temp.clear();
		temp.setContents(purchase_menu.getContents());
		
		temp.setItem(13, main_shop.getItem(Main_ShopClick.getActiveBuyers().get(p)));
		
		String info_lore = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Buy-Information.Lore"));
		
		ShopItem item = getItem(Main_ShopClick.getActiveBuyers().get(p));
		
		info_lore = info_lore.replaceAll("%currency%", item.getCurrency());
		info_lore = info_lore.replaceAll("%amount%", "" + item.getPrice());
		
		if (!item.getRequirement_type().equalsIgnoreCase("none")) {
			info_lore = info_lore.replaceAll("%requirement%", item.getRequirement_type());
			info_lore = info_lore.replaceAll("%requirement_value%", item.getRequirement_value());
		} else {
			info_lore = info_lore.replaceAll("%requirement%", ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("None")));
			info_lore = info_lore.replaceAll("%requirement_value%", "");
		}
		
		getIU().setLores(temp.getItem(31), info_lore);
		
	}
	
	public void openAdmin(Player p) {
		
		Inventory temp = Bukkit.createInventory(null, 54, "DeluxeShop Itemoptions");
		loadInShopItems(temp);
		
		opened_shops.put(p, temp);
		
		p.openInventory(temp);
		
	}
	
	public void openAdminItemschange(Player p) {
		
		Inventory temp = Bukkit.createInventory(null, 54, "DeluxeShop Itemschange");
		loadInShopItems(temp);
		
		opened_shops.put(p, temp);
		
		p.openInventory(temp);
		
	}
	
	public void openAdminOptions(Player p) {
		
		if (!opened_shops.containsKey(p)) {
			return;
		}
		
		p.openInventory(opened_shops.get(p));
		
	}
	
	public void updateAdminOptions(Player p, boolean all, String whichone) {
		
		Inventory open = opened_shops.get(p);
		
		if (all) {
			open.clear();
		}
		
		open.setItem(13, main_shop.getItem(Admin_ShopClick.getAdminSlots().get(p)));
		open.setItem(43, getIU().buildItem("&cDelete Item", XMaterial.REDSTONE_BLOCK.parseMaterial(), "=&7Delete the item from the config==&7To save the item, simply close=&7this menu."));
		
		ShopItem target = getItem(Admin_ShopClick.getAdminSlots().get(p));
		
		if (all || whichone.equalsIgnoreCase("ADDTOINVENTORY")) {
			open.setItem(10, getIU().buildItem("&eAdd to inventory: " + (target.isAddToInventory() ? "&aYes" : "&cNo"), Material.CHEST, "=&7If enabled, the item gets added to the=&7inventory of the player after he=&7purchased it."));
		}
		
		if (all || whichone.equalsIgnoreCase("COMMANDS")) {
			
			String cmds = "";
			
			for (String cmd : target.getCommands()) {
				cmds += "=&f- &7&o" + cmd;
			}
			
			if (target.getCommands().isEmpty()) {
				cmds = "=&c&oEmpty";
			}
			
			open.setItem(19, getIU().buildItem("&eConsole Commands", XMaterial.COMMAND_BLOCK.parseMaterial(), "=&7Commands, which are executed after purchase=" + cmds));
			
		}
		
		if (all || whichone.equalsIgnoreCase("PRICE")) {
			open.setItem(28, getIU().buildItem("&ePrice: &f" + target.getPrice(), XMaterial.GOLD_INGOT.parseMaterial(), "=&7Price of the item."));
		}
		
		if (all || whichone.equalsIgnoreCase("PLUGIN")) {
			
			String plugins = "";
			
			for (String plugin : hooks.keySet()) {
				if (target.getRequiredHook().equalsIgnoreCase(plugin)) {
					plugins += "=&f- &6>&f" + plugin + "&6<";
				} else {
					plugins += "=&f- " + plugin;
				}
			}
			
			open.setItem(16, getIU().buildItem("&ePlugin", XMaterial.COMPASS.parseMaterial(), "=&7Select the plugin, which will provide the=&7currency or the requirement needed.=" + plugins));
			
			ShopHook hook = hooks.get(target.getRequiredHook());
			
			if (hook != null) {
				
				String requirement_types = "";
				
				for (String requirement : hook.getRequirementTypes()) {
					if (target.getRequirement_type().equalsIgnoreCase(requirement)) {
						requirement_types += "=&f- &6>&f" + requirement + "&6<";
					} else {
						requirement_types += "=&f- " + requirement;
					}
				}
				
				open.setItem(25, getIU().buildItem("&eRequirement Type", XMaterial.IRON_SWORD.parseMaterial(), "=&7Requirement type for the item, the player=&7needs to purchase it.=" + requirement_types));
				
				String currencies = "";
				
				for (Currency currency : hook.getCurrencies()) {
					if (target.getCurrency().equalsIgnoreCase(currency.getName())) {
						currencies += "=&f- &6>&f" + currency.getName() + "&6<";
					} else {
						currencies += "=&f- " + currency.getName();
					}
				}
				
				open.setItem(37, getIU().buildItem("&eCurrency", XMaterial.BEACON.parseMaterial(), "=&7Currency type for the item, the player=&7needs to purchase it.=" + currencies));
				
			}
			
		}
		
		if (all || whichone.equalsIgnoreCase("REQUIREMENT_VALUE")) {
			open.setItem(34, getIU().buildItem("&eRequirement Value: &f" + target.getRequirement_value(), XMaterial.REDSTONE_TORCH.parseMaterial(), "=&7How much (Requirement Type) the player=&7needs to purchase the item."));
		}
		
	}
	
	public String getNextHook(String current) {
		
		List<String> hook = new ArrayList<String>();
		
		for (String all : hooks.keySet()) {
			hook.add(all);
		}
		
		if (!hook.contains(current)) {
			return hook.get(0);
		}
		
		int curr = hook.indexOf(current);
		int size = hook.size() - 1;
		
		if (curr == size) {
			return hook.get(0);
		}
		
		return hook.get(curr + 1);
		
	}
	
	public String getNextRequirement(String plugin, String current) {
		
		List<String> hook = new ArrayList<String>();
		
		for (String req : getHook(plugin).getRequirementTypes()) {
			hook.add(req);
		}
		
		if (!hook.contains(current)) {
			if (hook.size() == 0) {
				return "none";
			}
			return hook.get(0);
		}
		
		int curr = hook.indexOf(current);
		int size = hook.size() - 1;
		
		if (curr == size) {
			return hook.get(0);
		}
		
		return hook.get(curr + 1);
		
	}
	
	public String getNextCurrency(String plugin, String current) {
		
		List<String> hook = new ArrayList<String>();
		
		for (Currency req : getHook(plugin).getCurrencies()) {
			hook.add(req.getName());
		}
		
		if (!hook.contains(current)) {
			if (hook.size() == 0) {
				return "none";
			}
			return hook.get(0);
		}
		
		int curr = hook.indexOf(current);
		int size = hook.size() - 1;
		
		if (curr == size) {
			return hook.get(0);
		}
		
		return hook.get(curr + 1);
		
	}
	
	private Currency getCurrencyByName(String currency) {
		
		for (String plg : getHooks().keySet())  {
			for (Currency all : getHook(plg).getCurrencies()) {
				if (all.getName().equalsIgnoreCase(currency)) {
					return all;
				}
			}
		}
		
		return null;
		
	}
	
	private String getCurrencyName(int amount, Currency currency) {
		return amount != 1 ? currency.getPlural() : currency.getSingular();
	}
	
	public void purchaseItem(Player p, int slot) {
		
		ShopItem item = getItem(slot);
		ItemStack stack = main_shop.getItem(slot);
		ShopHook hook = getHook(item.getRequiredHook());
		Currency curr = getCurrencyByName(item.getCurrency());
		
		p.closeInventory();
		
		if (curr == null) {
			sendMSG(p, true, "&cA fatal error occured. Please inform your administrator!");
			return;
		}
		
		if (!item.getRequirement_type().equalsIgnoreCase("none")) {
			if (!item.getRequirement_value().equalsIgnoreCase("none")) {
				if (!hook.meetRequirement(p, item.getRequirement_type(), item.getRequirement_value())) {
					String message = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Requirement-Not-Meeted"));
					message = message.replaceAll("%requirement%", item.getRequirement_type());
					message = message.replaceAll("%requirement_value%", item.getRequirement_value());
					p.sendMessage(message);
					return;
				}
			}
		}
		
		String curr_name = getCurrencyName(item.getPrice(), curr);
		
		if (!hook.hasEnoughCurrency(p, getCurrencyByName(item.getCurrency()), item.getPrice())) {
			String message = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Not-Enough-Money"));
			message = message.replaceAll("%currency%", curr_name);
			message = message.replaceAll("%amount%", "" + item.getPrice());
			p.sendMessage(message);
			return;
		}
		
		hook.removeCurrency(p, getCurrencyByName(item.getCurrency()), item.getPrice());
		
		String message = ChatColor.translateAlternateColorCodes('&', getFM().getDefaultConfig().getString("Item-Purchased"));
		message = message.replaceAll("%item%", stack.getItemMeta().getDisplayName() == null ? "???" : stack.getItemMeta().getDisplayName().length() <= 1 ? stack.getType().name().replaceAll("_", " ").toLowerCase() : stack.getItemMeta().getDisplayName());
		message = message.replaceAll("%currency%", curr_name);
		message = message.replaceAll("%amount%", "" + item.getPrice());
		p.sendMessage(message);
		
		for (String all : item.getCommands()) {
			String execute = all;
			execute = execute.replaceAll("%player%", p.getName());
			execute = execute.replaceAll("%and%", "&");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), execute);
		}
		
		if (item.isAddToInventory()) {
			p.getInventory().addItem(stack);
		}
		
	}
	
	public boolean isValidItem(int slot) {
		
		if (getItem(slot) == null) {
			return false;
		}
		
		ShopItem item = getItem(slot);
		
		if (item.getRequiredHook().equalsIgnoreCase("none")) {
			return false;
		}
		
		if (item.getCurrency().equalsIgnoreCase("none")) {
			return false;
		}
		
		if (getCurrencyByName(item.getCurrency()) == null) {
			return false;
		}
		
		return true;
		
	}
	
	private String inventoryToString(Inventory inventory) {
		
		try {
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeInt(inventory.getSize());

			for (int i = 0; i < inventory.getSize(); i++) {
				dataOutput.writeObject(inventory.getItem(i));
			}

			dataOutput.close();
			
			return Base64Coder.encodeLines(outputStream.toByteArray());
			
		} catch (Exception e) {
			
			throw new IllegalStateException("Unable to save item stacks.", e);
			
		}
		
	}

	private Inventory stringToInventory(String data) throws IOException {
		
		try {
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			int sizeRecieviedInventory = dataInput.readInt();
			Inventory inventory = Bukkit.getServer().createInventory(null,sizeRecieviedInventory);
			
			for (int i = 0; i < sizeRecieviedInventory; i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());			
			}
			
			dataInput.close();
			return inventory;
			
		} catch (ClassNotFoundException e) {
			
			throw new IOException("Unable to decode class type.", e);
			
		}
		
	}
	
}
