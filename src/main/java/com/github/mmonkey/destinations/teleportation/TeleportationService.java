package com.github.mmonkey.destinations.teleportation;

import com.github.mmonkey.destinations.Destinations;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.google.common.base.Preconditions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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
        expires = DestinationsConfig.getInstance().get().getNode(DestinationsConfig.TELEPORT_SETTINGS, "expires").getInt(30);
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

        if (optionalCaller.isPresent()) {
            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.WARN, "Your call to "));
            message.append(Text.of(FormatUtil.OBJECT, target.getName()));
            message.append(Text.of(FormatUtil.WARN, " has expired."));
            optionalCaller.get().sendMessage(message.build());
        }

        if (optionalTarget.isPresent()) {
            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.WARN, " The request from "));
            message.append(Text.of(FormatUtil.OBJECT, caller.getName()));
            message.append(Text.of(FormatUtil.WARN, " has expired."));
            optionalTarget.get().sendMessage(message.build());
        }
    }

    public void call(Player caller, Player target) {
        Transaction call = new Transaction(caller, target);
        Timestamp expireTime = new Timestamp(System.currentTimeMillis() + this.expires * 1000);
        this.transactions.put(call, expireTime);
    }

    public void removeCall(Player caller, Player target) {

        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getCaller().getUniqueId().equals(caller.getUniqueId())
                    && call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                this.transactions.remove(call.getKey());
            }
        }
    }

    public Player getFirstCaller(Player target) {

        Player caller = null;
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                caller = call.getKey().getCaller();
            }
        }

        return caller;
    }

    public int getNumCallers(Player target) {

        int callers = 0;
        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                callers++;
            }
        }

        return callers;
    }

    public List<String> getCalling(Player target) {

        List<String> list = new ArrayList<String>();

        for (Map.Entry<Transaction, Timestamp> call : this.transactions.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                list.add(call.getKey().getCaller().getName());
            }
        }

        return list;
    }

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
