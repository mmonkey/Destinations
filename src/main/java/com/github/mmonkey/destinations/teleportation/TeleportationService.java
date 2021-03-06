package com.github.mmonkey.destinations.teleportation;

import com.github.mmonkey.destinations.Destinations;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import com.google.common.base.Preconditions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TeleportationService {

    public static final TeleportationService instance = new TeleportationService();
    private final Map<Transaction, Timestamp> transactions = new ConcurrentHashMap<>();
    private final int expires;

    /**
     * TeleportationService constructor
     */
    private TeleportationService() {
        expires = DestinationsConfig.getTeleportRequestExpiration();
        this.startCleanupTask();
    }

    /**
     * TeleportationService.Transaction class
     */
    private class Transaction {
        private Player caller;
        private Player target;

        /**
         * @return Player
         */
        Player getCaller() {
            return this.caller;
        }

        /**
         * @return Player
         */
        Player getTarget() {
            return this.target;
        }

        /**
         * CallModel constructor
         *
         * @param caller Player
         * @param target Player
         */
        Transaction(Player caller, Player target) {
            Preconditions.checkNotNull(caller);
            Preconditions.checkNotNull(target);

            this.caller = caller;
            this.target = target;
        }
    }

    /**
     * Start the teleportationCleanupTask
     */
    private void startCleanupTask() {
        Sponge.getScheduler().createTaskBuilder()
                .interval(1, TimeUnit.SECONDS)
                .name("teleportationCleanupTask")
                .execute(this::cleanup)
                .submit(Destinations.getInstance());
    }

    /**
     * Cleanup teleportation transactions
     */
    private void cleanup() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (now.after(call.getValue())) {
                this.expiredNotification(call.getKey().getCaller(), call.getKey().getTarget());
                this.transactions.remove(call.getKey());
            }
        }
    }

    /**
     * Send teleportation request expiration notices to players
     *
     * @param caller Player
     * @param target Player
     */
    private void expiredNotification(Player caller, Player target) {
        Optional<Player> optionalCaller = Sponge.getServer().getPlayer(caller.getUniqueId());
        Optional<Player> optionalTarget = Sponge.getServer().getPlayer(target.getUniqueId());

        optionalCaller.ifPresent(player -> player.sendMessage(MessagesUtil.warning(player, "call.request_expire_to", target.getName())));
        optionalTarget.ifPresent(player -> player.sendMessage(MessagesUtil.warning(player, "call.request_expire_from", caller.getName())));
    }

    /**
     * Add a call transaction
     *
     * @param caller Player
     * @param target Player
     */
    public void call(Player caller, Player target) {
        Transaction call = new Transaction(caller, target);
        Timestamp expireTime = new Timestamp(System.currentTimeMillis() + this.expires * 1000);
        this.transactions.put(call, expireTime);
    }

    /**
     * Remove a call transaction
     *
     * @param caller Player
     * @param target Player
     */
    public void removeCall(Player caller, Player target) {
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getCaller().getUniqueId().equals(caller.getUniqueId())
                    && call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                this.transactions.remove(call.getKey());
            }
        }
    }

    /**
     * Get the first caller from this player's current calls
     *
     * @param target Player
     * @return Player
     */
    public Player getFirstCaller(Player target) {
        Player caller = null;
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                caller = call.getKey().getCaller();
            }
        }

        return caller;
    }

    /**
     * Get the total number of current callers
     *
     * @param target Player
     * @return int
     */
    public int getNumCallers(Player target) {
        int callers = 0;
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                callers++;
            }
        }

        return callers;
    }

    /**
     * Get the current list of callers
     *
     * @param target Player
     * @return List<String>
     */
    public List<String> getCalling(Player target) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                list.add(call.getKey().getCaller().getName());
            }
        }

        return list;
    }

    /**
     * Get whether or not a Player is currently waiting for a call reply
     *
     * @param caller Player
     * @param target Player
     * @return bool
     */
    public boolean isCalling(Player caller, Player target) {

        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())
                    && call.getKey().getCaller().getUniqueId().equals(caller.getUniqueId())) {
                return true;
            }
        }

        return false;
    }
}
