package nl.lolmewn.stats.global;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.disposables.CompositeDisposable;
import nl.lolmewn.stats.SharedMain;
import nl.lolmewn.stats.player.PlayerManager;

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
            this.disposable.add(PlayerManager.getInstance().subscribe(player ->
                    this.disposable.add(player.subscribe(statsContainer ->
                            this.disposable.add(statsContainer.subscribe(statTimeEntry -> {
                                System.out.println(String.format("%s updated %s with %d to %d at %d",
                                        player.getUuid().toString(), statsContainer.getStat().getName(),
                                        statTimeEntry.getAmount(), statsContainer.getTotal(), statTimeEntry.getTimestamp()));
                                String message = this.gson.toJson(Map.of(
                                        "serverUuid", SharedMain.getServerUuid(),
                                        "content", Map.of(
                                                "playerUuid", player.getUuid().toString(),
                                                "amount", statTimeEntry.getAmount(),
                                                "metadata", this.gson.toJson(statTimeEntry.getMetadata()),
                                                "timestamp", statTimeEntry.getTimestamp()
                                        ),
                                        "stat", statsContainer.getStat().getName()
                                ));
                                this.channel.basicPublish("", routingKey, null, message.getBytes());
                            }))
                    ))
            ));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void setupRabbitMq() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        this.rabbitMqConnection = factory.newConnection();
        this.channel = this.rabbitMqConnection.createChannel();
        this.channel.queueDeclare("stats.global", true, false, false, null);
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
