package de.timderspieler.deluxeshop.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.timderspieler.deluxeshop.listeners.Admin_ShopClick;

public class DeluxeShopCommand 
implements CommandExecutor {
	
	private static DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }
	
	private boolean hasPermission(Player p, String permission, boolean sendMSG) {
		if (!p.hasPermission(permission)) {
			if (sendMSG) {
				sendMSG(p, false, getPlugin().getNoPermMSG());
			}
			return false;
		}
		return true;
	}
	
	public void sendMSG(Player p, boolean prefix, String msg) {
		getPlugin().sendMSG(p, prefix, msg);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		
		if (!(sender instanceof Player)) {
			DeluxeShop.sendConsoleMSG("This command cannot be executed via the console!");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (args.length == 0) {
			
			if (!hasPermission(p, "deluxeshop.admin", true)) {
				return false;
			}
			
			sendMSG(p, false, "");
			sendMSG(p, false, " &b&lDeluxeShop");
			sendMSG(p, false, "");
			sendMSG(p, false, " &f/deluxeshop hooks &e- Lists all hooked plugins");
			
			if (!getPlugin().getHooks().isEmpty()) {
				sendMSG(p, false, " &f/deluxeshop editShop &e- Edit the shop items order");
				sendMSG(p, false, " &f/deluxeshop editItems &e- Edit the item options");
				sendMSG(p, false, " &f/deluxeshop open &e- Open the shop");
			} else {
				sendMSG(p, false, "");
				sendMSG(p, false, " &c&l!WARNING!");
				sendMSG(p, false, " &fNo plugin is installed, which hooks into DeluxeShop.");
				sendMSG(p, false, " &fThat means, you cannot use plugin!");
			}
			
			sendMSG(p, false, "");
			
			return false;
		}
		
		if (args.length == 1) {
			
			if (args[0].equalsIgnoreCase("hooks")) {
				
				if (!hasPermission(p, "deluxeshop.admin", true)) {
					return false;
				}
				
				sendMSG(p, false, "");
				sendMSG(p, false, " &b&lDeluxeShop Hooks");
				sendMSG(p, false, "");
				sendMSG(p, false, " &fFollowing plugins are hooked into DeluxeShop:");
				
				List<String> hooks = new ArrayList<String>();
				for (String all : getPlugin().getHooks().keySet()) {
					hooks.add(" &f- " + all);
				}
				
				if (hooks.isEmpty()) {
					hooks.add(" &f- &c&oNo plugins are hooked.");
				}
				
				for (String all : hooks) {
					sendMSG(p, false, all);
				}
				
				sendMSG(p, false, "");
				
				return false;
			}
			
			if (args[0].equalsIgnoreCase("editShop")) {
				
				if (!hasPermission(p, "deluxeshop.admin", true)) {
					return false;
				}
				
				if (getPlugin().getHooks().isEmpty()) {
					sendMSG(p, true, "&cNo plugin is installed, which hooks into DeluxeShop! Please read the resource page for further information.");
					return false;
				}
				
				getPlugin().openAdminItemschange(p);
				
				return false;
			}
			
			if (args[0].equalsIgnoreCase("editItems")) {
				
				if (!hasPermission(p, "deluxeshop.admin", true)) {
					return false;
				}
				
				if (getPlugin().getHooks().isEmpty()) {
					sendMSG(p, true, "&cNo plugin is installed, which hooks into DeluxeShop! Please read the resource page for further information.");
					return false;
				}
				
				getPlugin().openAdmin(p);
				
				return false;
			}
			
			if (args[0].equalsIgnoreCase("open")) {
				
				if (!hasPermission(p, getPlugin().getOpenPerm(), true)) {
					return false;
				}
				
				if (getPlugin().getHooks().isEmpty()) {
					sendMSG(p, true, "&cNo plugin is installed, which hooks into DeluxeShop! Please read the resource page for further information.");
					return false;
				}
				
				getPlugin().openShop(p);
				
				return false;
			}
			
			return false;
		}
		
		if (args.length > 1) {
			
			if (args[0].equalsIgnoreCase("setValue")) {
				
				if (!Admin_ShopClick.getAdminWork().containsKey(p)) {
					return false;
				}
				
				String work = Admin_ShopClick.getAdminWork().get(p);
				
				if (args[1].equalsIgnoreCase("cancel")) {
					getPlugin().openAdminOptions(p);
					return false;
				}
				
				if (work.equalsIgnoreCase("COMMANDS")) {
					String cmd_to_add = String.join(" ", args).replaceAll(args[0] + " ", "").replaceAll("&", "%and%");
					
					if (cmd_to_add.equalsIgnoreCase("clear")) {
						getPlugin().getItem(Admin_ShopClick.getAdminSlots().get(p)).clearCommands();
					} else {
						getPlugin().getItem(Admin_ShopClick.getAdminSlots().get(p)).addCommand(cmd_to_add);
					}
					getPlugin().openAdminOptions(p);
					getPlugin().updateAdminOptions(p, false, "COMMANDS");
					Admin_ShopClick.getAdminWork().remove(p);
					return false;
				}
				
				if (work.equalsIgnoreCase("PRICE")) {
					if (!isANumber(args[1])) {
						sendMSG(p, true, "&c" + args[1] + "&r&c is not a number!");
						return false;
					}
					getPlugin().getItem(Admin_ShopClick.getAdminSlots().get(p)).setPrice(Integer.parseInt(args[1]));
					getPlugin().openAdminOptions(p);
					getPlugin().updateAdminOptions(p, false, "PRICE");
					Admin_ShopClick.getAdminWork().remove(p);
					return false;
				}
				
				if (work.equalsIgnoreCase("REQUIREMENT_VALUE")) {
					getPlugin().getItem(Admin_ShopClick.getAdminSlots().get(p)).setRequirement_value(args[1]);
					getPlugin().openAdminOptions(p);
					getPlugin().updateAdminOptions(p, false, "REQUIREMENT_VALUE");
					Admin_ShopClick.getAdminWork().remove(p);
					return false;
				}
				
				sendMSG(p, true, "&7You can cancel the input with &c/deluxeshop setValue cancel");
				return false;
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean isANumber(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

}
