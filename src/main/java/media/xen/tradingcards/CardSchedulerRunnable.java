package media.xen.tradingcards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Set;


public class CardSchedulerRunnable extends BukkitRunnable {
    private final TradingCards plugin;

    public CardSchedulerRunnable(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.debug(getClass().getSimpleName() + " task running");
        //check this before the task is registered.
        if (!plugin.getConfig().getBoolean("General.Schedule-Cards"))
            return;

        if (plugin.getConfig().getBoolean("General.Schedule-Cards-Natural")) {
            String mob = plugin.getConfig().getString("General.Schedule-Card-Mob");
            if (plugin.isMob(mob.toUpperCase())) {
                plugin.giveawayNatural(EntityType.valueOf(mob.toUpperCase()), null);
                return;
            }
            plugin.getLogger().info("Error! schedule-card-mob is an invalid mob?");
            return;
        }

        final ConfigurationSection rarities = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards");
        final Set<String> rarityKeys = rarities.getKeys(false);
        String keyToUse = "";

        for (final String key : rarityKeys) {
            plugin.debug("Rarity key: " + key);
            if (key.equalsIgnoreCase(plugin.getConfig().getString("General.Schedule-Card-Rarity"))) {
                keyToUse = key;
            }
        }
        plugin.debug("keyToUse: " + keyToUse);
        if (keyToUse.isEmpty())
            return;

        Bukkit.broadcastMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().scheduledGiveaway));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            String cardName = getRandomCardName(keyToUse);
            CardUtil.dropItem(p, CardManager.getCard(cardName, keyToUse, false));
        }

    }
    private String getRandomCardName(final String keyToUse) {
        ConfigurationSection cards = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + keyToUse);
        Set<String> cardKeys = cards.getKeys(false);
        var rIndex = plugin.getRandom().nextInt(cardKeys.size());
        var i = 0;
        var cardName = "";
        for (Iterator<String> var11 = cardKeys.iterator(); var11.hasNext(); ++i) {
            String theCardName = var11.next();
            if (i == rIndex) {
                return theCardName;
            }
        }
        return cardName;
    }


}