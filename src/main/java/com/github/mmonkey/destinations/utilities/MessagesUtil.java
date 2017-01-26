package com.github.mmonkey.destinations.utilities;

import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ResourceBundle;

public class MessagesUtil {

    /**
     * Get the message associated with this key, for this player's locale
     *
     * @param player Player
     * @param key    String
     * @param args   String.format args
     * @return Text formatting of message
     */
    public static Text get(Player player, String key, Object... args) {
        ResourceBundle messages = PlayerCache.instance.getResourceCache(player);
        return messages.containsKey(key) ? Text.of(String.format(messages.getString(key), args)) : Text.EMPTY;
    }

    /**
     * Get the message associated with this key, for this player's locale.
     * Message colored as a success (green).
     *
     * @param player Player
     * @param key    String
     * @param args   String.format args
     * @return Text formatting of message
     */
    public static Text success(Player player, String key, Object... args) {
        return Text.of(TextColors.GREEN, get(player, key, args));
    }

    /**
     * Get the message associated with this key, for this player's locale.
     * Message colored as a warning (gold).
     *
     * @param player Player
     * @param key    String
     * @param args   String.format args
     * @return Text formatting of message
     */
    public static Text warning(Player player, String key, Object... args) {
        return Text.of(TextColors.GOLD, get(player, key, args));
    }

    /**
     * Get the message associated with this key, for this player's locale.
     * Message colored as an error (red).
     *
     * @param player Player
     * @param key    String
     * @param args   String.format args
     * @return Text formatting of message
     */
    public static Text error(Player player, String key, Object... args) {
        return Text.of(TextColors.RED, get(player, key, args));
    }

}
