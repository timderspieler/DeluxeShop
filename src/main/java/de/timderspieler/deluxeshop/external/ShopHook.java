package de.timderspieler.deluxeshop.external;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class ShopHook {
	
	public abstract void removeCurrency(Player p, Currency currency, int amount);
	public abstract void addCurrency(Player p, Currency currency, int amount);
	public abstract void setCurrency(Player p, Currency currency, int amount);
	
	public abstract boolean meetRequirement(Player p, String requirement_type, String value);
	public abstract boolean hasEnoughCurrency(Player p, Currency currency, int amount);
	
	public abstract List<String> getRequirementTypes();
	public abstract List<Currency> getCurrencies();
	
}
