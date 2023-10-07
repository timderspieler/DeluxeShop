package de.timderspieler.deluxeshop.external;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import de.timderspieler.deluxeshop.main.DeluxeShop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;

public class ShopTrait extends Trait {

	public ShopTrait() {
		super("deluxeshop");
	}

	private DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }

	@EventHandler
	public void click(NPCRightClickEvent event){
		//Handle a click on a NPC. The event has a getNPC() method. 
		//Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

		if (event.getNPC() != getNPC()) {
			return;
		}

		Player p = event.getClicker();

		if (!p.hasPermission(getPlugin().getOpenPerm())) {
			return;
		}

		getPlugin().openShop(p);

	}

	// Called every tick
	@Override
	public void run() {
	}

	//Run code when your trait is attached to a NPC. 
	//This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	//This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
	}

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

}
