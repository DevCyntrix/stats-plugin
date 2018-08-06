package nl.lolmewn.stats.global;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import nl.lolmewn.stats.SharedMain;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class GlobalStats {

    //    private String exchangeName;
    private static final String routingKey = "stats.global";
    private final Gson gson = new Gson();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Connection rabbitMqConnection;
    private Channel channel;

    public GlobalStats() {
        try {
            setupRabbitMq();
            this.disposable.add(PlayerManager.getInstance().subscribe(this.getPlayerConsumer(), Util::handleError));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private Consumer<StatsPlayer> getPlayerConsumer() {
        return player -> {
            player.getContainers().forEach(cont -> // Listen to updates of already-in-place containers
                    this.disposable.add(cont.subscribe(this.getStatTimeEntryConsumer(player, cont), Util::handleError)));
            this.disposable.add(player.subscribe(this.getContainerConsumer(player), Util::handleError)); // Listen to new containers
        };
    }

    private Consumer<StatsContainer> getContainerConsumer(StatsPlayer player) {
        return statsContainer ->
                this.disposable.add(statsContainer.subscribe(this.getStatTimeEntryConsumer(player, statsContainer), Util::handleError));
    }

    private Consumer<StatTimeEntry> getStatTimeEntryConsumer(StatsPlayer player, StatsContainer statsContainer) {
        return statTimeEntry -> {
            SharedMain.debug(String.format("%s updated %s with %d to %d at %d",
                    player.getUuid().toString(), statsContainer.getStat().getName(),
                    statTimeEntry.getAmount(), statsContainer.getTotal(), statTimeEntry.getTimestamp()));
            String message = this.gson.toJson(Map.of(
                    "serverUuid", SharedMain.getServerUuid(),
                    "content", Map.of(
                            "playerUuid", player.getUuid().toString(),
                            "amount", statTimeEntry.getAmount(),
                            "metadata", statTimeEntry.getMetadata(),
                            "timestamp", statTimeEntry.getTimestamp()
                    ),
                    "stat", statsContainer.getStat().getName()
            ));
            SharedMain.debug("Publishing " + message);
            this.channel.basicPublish("", routingKey, null, message.getBytes());
        };
    }

    private void setupRabbitMq() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setUsername("stats5");
        factory.setPassword("stats5");
        factory.setHost("lolmewn.nl");
        factory.setPort(5672);
        this.rabbitMqConnection = factory.newConnection();
        this.channel = this.rabbitMqConnection.createChannel();
    }

    public void shutdown() {
        this.disposable.dispose();
        try {
            this.channel.close();
            this.rabbitMqConnection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
