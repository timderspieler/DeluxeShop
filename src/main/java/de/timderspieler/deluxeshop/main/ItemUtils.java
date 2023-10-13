package de.timderspieler.deluxeshop.main;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	@SuppressWarnings("unused")
	public ItemStack copyItem(ItemStack is) {
		
		ItemStack result = new ItemStack(is.getType());
		ItemMeta meta = result.getItemMeta();
		
//		List<String> lores = is.getItemMeta().getLore();
//		String displayname = is.getItemMeta().getDisplayName();
//		
//		if (displayname != null) {
//			meta.setDisplayName(displayname);
//		}
//		
//		if (lores != null) {
//			meta.setLore(lores);
//		}
//		
//		if (!is.getItemMeta().getEnchants().isEmpty()) {
//			for (Enchantment all : is.getItemMeta().getEnchants().keySet()) {
//				result.addEnchantment(all, is.getItemMeta().getEnchants().get(all));
//			}
//		}
		
		result.setItemMeta(is.getItemMeta());
		
		return result;
		
	}
	
	public ItemStack buildItem(String name, Material mat, int amount, String lore) {
		
		ItemStack result = buildItem(name, mat, lore);
		result.setAmount(amount);
		
		return result;
		
	}
	
	public ItemStack buildItem(String name, Material mat, String lore) {
		
		if (mat == null) {
			mat = Material.STONE;
		}
		
		ItemStack result = new ItemStack(mat);
		ItemMeta result_meta = result.getItemMeta();
		
		result_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		if (lore != null) {
			result_meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\\=")));
		}
		
		result.setItemMeta(result_meta);
		
		return result;
		
	}
	
	public void setLores(ItemStack is, String lore) {
		
		ItemMeta result = is.getItemMeta();
		result.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\\=")));
		is.setItemMeta(result);
		
	}

	public void addLores(ItemStack is, String line) {
		ItemMeta result = is.getItemMeta();
		List<String> lores = result.getLore();
		if (lores == null) {
			lores = new ArrayList<>();
		}
		lores.addAll(Arrays.asList(ChatColor.translateAlternateColorCodes('&', line).split("\\=")));
		result.setLore(lores);
		is.setItemMeta(result);
	}
	
	@SuppressWarnings("deprecation")
	public void applyShort(ItemStack is, short data) {
		is.setDurability(data);
	}
	
	public void setAmount(ItemStack is, int amount) {
		is.setAmount(amount);
	}
	
	public void addEnchantment(ItemStack is, Enchantment ench, int level) {
		is.addEnchantment(ench, level);
	}
	
	public void removeEnchantment(ItemStack is, Enchantment ench) {
		is.removeEnchantment(ench);
	}
	
	public void addGlow(ItemStack is) {
		is.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
	}
	
	public void removeGlow(ItemStack is) {
		removeEnchantment(is, Enchantment.OXYGEN);
	}
	
	public void setDisplayName(ItemStack is, String name) {
		
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		is.setItemMeta(meta);
		
	}
}
