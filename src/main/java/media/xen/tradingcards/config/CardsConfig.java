package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class CardsConfig extends SimpleConfig {
	public CardsConfig(final TradingCards plugin) {
		super(plugin, "cards.yml");
	}

	public boolean deleteRarity(final Player player, final String rarity) {
		if (!plugin.isRarity(rarity).equals("None")) {
			ConfigurationSection cards = plugin.getConfig().getConfigurationSection("Cards." + plugin.isRarity(rarity));
			Set<String> cardKeys = cards.getKeys(false);
			int numCardsCounter = 0;

			for (final String key : cardKeys) {
				plugin.debug("deleteRarity iteration: " + numCardsCounter);

				if (plugin.hasShiny(player, key, rarity) && plugin.hasCard(player, key, rarity) == 0) {
					plugin.debug("Deleted: Cards." + key + ".key2");

					plugin.deleteCard(player, key, rarity);
					++numCardsCounter;
				}

				if (plugin.hasCard(player, key, rarity) > 0) {
					plugin.debug("Deleted: Cards." + key + ".key2");

					plugin.deleteCard(player, key, rarity);
					++numCardsCounter;
				}
			}
			return true;
		}

		return false;
	}
}
