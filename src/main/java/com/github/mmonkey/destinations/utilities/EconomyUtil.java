package com.github.mmonkey.destinations.utilities;

import com.github.mmonkey.destinations.exceptions.EconomyServiceNotFoundException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Optional;

public class EconomyUtil {

    public static final EconomyUtil instance = new EconomyUtil();

    private EconomyService service;
    private Currency currency;

    /**
     * Charge a player
     *
     * @param player Player
     * @param amount BigDecimal
     * @return whether or not the charge was successful
     */
    public boolean chargePlayer(Player player, BigDecimal amount) throws EconomyServiceNotFoundException {
        if (this.service == null) {
            Optional<EconomyService> optional = Sponge.getServiceManager().provide(EconomyService.class);
            if (optional.isPresent()) {
                this.service = optional.get();
                this.currency = optional.get().getDefaultCurrency();
            } else {
                throw new EconomyServiceNotFoundException("Economy Service not loaded.");
            }
        }

        UniqueAccount account = this.getPlayerAccount(player);
        if (account != null) {
            BigDecimal balance = account.getBalance(this.currency);
            if (balance.compareTo(amount) >= 0) {
                account.withdraw(this.currency, amount, Cause.of(NamedCause.owner(player)));

                String chargedAmount = this.currency.format(amount, 2).toPlain();
                String newBal = this.currency.format(account.getBalance(this.currency), 2).toPlain();
                player.sendMessage(MessagesUtil.success(player, "economy.charge", chargedAmount, newBal));
                return true;
            }
        }
        return false;
    }

    /**
     * Get a player's account
     *
     * @param player Player
     * @return UniqueAccount|null
     */
    private UniqueAccount getPlayerAccount(Player player) {
        Optional<UniqueAccount> optional = this.service.getOrCreateAccount(player.getUniqueId());
        return optional.orElse(null);
    }

    public static void alertPlayerOfCost(Player player, String command) {
        player.sendMessage(MessagesUtil.warning(player, "economy.cost"));
    }

}
