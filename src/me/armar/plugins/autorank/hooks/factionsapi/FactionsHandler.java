package me.armar.plugins.autorank.hooks.factionsapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UPlayer;

/**
 * Handles all connections with Factions
 * <p>
 * Date created:  21:01:50
 * 15 mrt. 2014
 * @author Staartvin
 *
 */
public class FactionsHandler implements DependencyHandler {

	private final Autorank plugin;
	private Factions api;

	public FactionsHandler(final Autorank instance) {
		plugin = instance;
	}


	public double getFactionPower(final Player player) {
		if (!isAvailable()) return 0.0d;
		
		final UPlayer uPlayer = UPlayer.get(player);

		if (!uPlayer.hasFaction()) {
			return 0.0d;
		}

		return uPlayer.getFaction().getPower();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("Factions");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Factions)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup() {
		if (!isInstalled()) {
			plugin.getLogger().info("Factions has not been found!");
			return false;
		} else {
			api = (Factions) get();

			if (api != null) {
				plugin.getLogger()
						.info("Factions has been found and can be used!");
				return true;
			} else {
				plugin.getLogger().info(
						"Factions has been found but cannot be used!");
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		Factions plugin = (Factions) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}
}
