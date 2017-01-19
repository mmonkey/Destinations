package com.github.mmonkey.destinations.commands.elements;

import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.SelectorCommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HomeCommandElement extends SelectorCommandElement {

    /**
     * HomeCommandElement constructor
     *
     * @param key Text
     */
    public HomeCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {

        if (!(source instanceof Player)) {
            return null;
        }

        Player player = (Player) source;
        List<String> list = new CopyOnWriteArrayList<>();
        PlayerCache.instance.get(player).getHomes().forEach(home -> list.add(home.getName()));

        return list;
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return choice;
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        final StringBuilder stringBuilder = new StringBuilder(args.next());
        while (args.hasNext()) {
            stringBuilder.append(' ').append(args.next());
        }
        return stringBuilder.toString();
    }

}
