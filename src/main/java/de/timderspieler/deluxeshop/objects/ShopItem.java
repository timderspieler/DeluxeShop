package de.timderspieler.deluxeshop.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.timderspieler.deluxeshop.external.ShopHook;
import de.timderspieler.deluxeshop.main.DeluxeShop;

public class ShopItem {
	
	private int slot;
	
	private String requiredHook = "none";
	private String requirement_type = "none";
	private String requirement_value = "none";
	
	private ArrayList<String> commands = new ArrayList<String>();
	private boolean addToInventory;
	
	private int price;
	private String currency = "none";
	
	public ShopItem(int slot, String requiredHook, String requirement_type, String requirement_value, ArrayList<String> commands, boolean addToInventory, int price, String currency) {
		
		this.slot = slot;
		this.requiredHook = requiredHook;
		this.requirement_type = requirement_type;
		this.requirement_value = requirement_value;
		this.commands = commands;
		this.addToInventory = addToInventory;
		this.price = price;
		this.currency = currency;
		
	}
	
	public ShopItem(int slot) {
		
		this.slot = slot;
		
		Map.Entry<String, ShopHook> entry = DeluxeShop.getPlugin().getHooks().entrySet().iterator().next();
		
		this.requiredHook = entry.getKey();
		this.currency = entry.getValue().getCurrencies().get(0).getName();
		
	}

	public int getSlot() { return slot; }
	public int getPrice() { return price; }
	
	public String getRequiredHook() { return requiredHook; }
	public String getRequirement_type() { return requirement_type; }
	public String getRequirement_value() { return requirement_value; }
	public String getCurrency() { return currency; }
	
	public ArrayList<String> getCommands() { return commands; }
	
	public boolean isAddToInventory() { return addToInventory; }
	
	public void setRequiredHook(String requiredHook) { this.requiredHook = requiredHook; }
	public void setRequirement_type(String requirement_type) { this.requirement_type = requirement_type; }
	public void setRequirement_value(String requirement_value) { this.requirement_value = requirement_value; }
	public void setAddToInventory(boolean addToInventory) { this.addToInventory = addToInventory; }
	public void setPrice(int price) { this.price = price; }
	public void setCurrency(String currency) { this.currency = currency; }
	public void clearCommands() { this.commands.clear(); }
	public void addCommand(String cmd) { this.commands.add(cmd); }
	public void removeCommand(int pointer) { this.commands.remove(pointer); }
	
	@SuppressWarnings("unchecked")
	public ShopItem(String[] data) {
		
		this.slot = Integer.parseInt(data[0]);
		setRequiredHook(data[1]);
		setRequirement_type(data[2]);
		setRequirement_value(data[3]);
		this.commands = new Gson().fromJson(data[4], ArrayList.class);
		setAddToInventory(Boolean.parseBoolean(data[5]));
		setPrice(Integer.parseInt(data[6]));
		setCurrency(data[7]);
		
	}
	
	public String toString() {
		
		return getSlot() + "&" + getRequiredHook() + "&" + getRequirement_type() + "&" + getRequirement_value() + "&"
				+ new Gson().toJson(getCommands()) + "&" + isAddToInventory() + "&" + getPrice() + "&" + getCurrency();
		
	}

}
