package media.xen.tradingcards.api.card;

import de.tr7zw.nbtapi.NBTItem;
import media.xen.tradingcards.CardManager;
import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.config.TradingCardsConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TradingCard {
    private final TradingCards plugin;
    private final String cardName;
    private String rarity;
    private boolean isShiny = false;
    private boolean isPlayerCard = false;
    private String rarityColour;
    private String prefix;
    private CardInfo series;
    private CardInfo about;
    private CardInfo type;
    private CardInfo info;
    private String shinyPrefix = null;
    private String cost;

    public TradingCard(TradingCards plugin, final String cardName) {
        this.plugin = plugin;
        this.cardName = cardName;
    }

    public TradingCard isShiny(boolean isShiny) {
        this.isShiny = isShiny;
        return this;
    }

    public TradingCard rarityColour(String rarityColour) {
        this.rarityColour = rarityColour;
        return this;
    }

    public TradingCard prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public TradingCard series(String name, String colour, String display) {
        this.series = new CardInfo(name, colour, display);
        return this;
    }

    public TradingCard about(String name, String colour, String display) {
        this.about = new CardInfo(name, colour, display);
        return this;
    }

    public TradingCard type(String name, String colour, String display) {
        this.type = new CardInfo(name, colour, display);
        return this;
    }

    public TradingCard info(String name, String colour, String display) {
        this.info = new CardInfo(name, colour, display);
        return this;
    }

    public TradingCard shinyPrefix(String shinyPrefix) {
        this.shinyPrefix = shinyPrefix;
        return this;
    }

    public TradingCard cost(String cost) {
        this.cost = cost;
        return this;
    }

    public TradingCard rarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public TradingCard isPlayerCard(boolean isPlayerCard) {
        this.isPlayerCard = isPlayerCard;
        return this;
    }

    /**
     * Saves the card to disk.
     *
     * @return Returns false if the card already exists, or if the rarity doesn't exist
     */
    public boolean save(TradingCards plugin) {
        //Checks should happen in the commands... this should be a stupid method, towny hooks should handle this as well.
        //This method should only return false if there was an io exception or something like that.
        if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + cardName)) {
            plugin.debug("Card already exists. Try a different name?");
            return false;
        }
        if (!plugin.getCardsConfig().getConfig().contains("Cards." + rarity)) {
            plugin.debug("No such rarity, try /cards rarities");
            return false;
        }

        //checks for formatting should be done through the command.
        return true;
    }


    private ItemStack buildItem() {
        ItemStack card = TradingCardsConfig.getBlankCard(1).clone();
        ItemMeta cardMeta = TradingCardsConfig.getBlankCard(1).getItemMeta();
        cardMeta.setDisplayName(formatDisplayName(isPlayerCard, isShiny, prefix, rarityColour, cardName.replace('_', ' '), cost, shinyPrefix));
        cardMeta.setLore(formatLore());
        if (isShiny) {
            cardMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        }
        if (plugin.getMainConfig().hideEnchants) {
            cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        card.setItemMeta(cardMeta);

        return card;
    }


    public ItemStack build() {
        NBTItem nbtItem = new NBTItem(buildItem());
        nbtItem.setBoolean("isCard", true);
        return nbtItem.getItem();
    }

    private List<String> formatLore() {
        List<String> lore = new ArrayList<>();
        lore.add(plugin.cMsg(type.getColour() + type.getDisplay() + ": &f" + type.getName()));
        if (!"None".equals(info.getName()) && !"".equals(info.getName())) {
            lore.add(plugin.cMsg(info.getColour() + info.getDisplay() + ":"));
            lore.addAll(plugin.wrapString(info.getName()));
        } else {
            lore.add(plugin.cMsg(info.getColour() + info.getDisplay() + ": &f" + info.getName()));
        }

        lore.add(plugin.cMsg(series.getColour() + series.getDisplay() + ": &f" + series.getName()));
        if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + cardName + ".About")) {
            lore.add(plugin.cMsg(about.getColour() + about.getDisplay() + ": &f" + about.getName()));
        }

        if (isShiny) {
            lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + plugin.getConfig().getString("General.Shiny-Name") + " " + rarity.replace('_', ' ')));
        } else {
            lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + rarity.replace('_', ' ')));
        }

        return lore;
    }

    @NotNull
    private String formatDisplayName(boolean isPlayerCard, boolean isShiny, String prefix, String rarityColour, String cardName, String cost, String shinyPrefix) {
        final String[] shinyPlayerCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%"};
        final String[] shinyCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%", "_"};

        final String[] cardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "_"};
        final String[] playerCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%"};


        final String shinyTitle = plugin.getConfig().getString("DisplayNames.Cards.ShinyTitle");
        final String title = plugin.getConfig().getString("DisplayNames.Cards.Title");
        if (isShiny && shinyPrefix != null) {
            if (isPlayerCard) {
                return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyPlayerCardFormat, new String[]{prefix, rarityColour, cardName, cost, shinyPrefix}));
            }
            return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyCardFormat, new String[]{prefix, rarityColour, cardName, cost, shinyPrefix, " "}));
        }
        if (isPlayerCard) {
            return plugin.cMsg(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix, rarityColour, cardName, cost}));
        }
        return plugin.cMsg(StringUtils.replaceEach(title, cardFormat, new String[]{prefix, rarityColour, cardName, cost, " "}));
    }

}