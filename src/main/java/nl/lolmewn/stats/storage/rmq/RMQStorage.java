package nl.lolmewn.stats.storage.rmq;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import nl.lolmewn.stats.SharedMain;
import nl.lolmewn.stats.Util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class RMQStorage {

    private final Gson gson = new Gson();
    private CompositeDisposable disposable;
    private Connection connection;
    private Channel channel;
    private String exchange;

    public RMQStorage(File configFile) throws IOException, TimeoutException {
        this.connect(configFile);
        this.setupSink();
    }

    private void setupSink() {
        this.disposable = new CompositeDisposable();
        this.disposable.add(Util.statUpdate((player, container, entry) ->
                this.disposable.add(Observable.just(player).subscribeOn(Schedulers.io()).subscribe((p) -> {
                    String message = this.gson.toJson(Util.of(
                            "content", Util.of(
                                    "playerUuid", player.getUuid().toString(),
                                    "amount", entry.getAmount(),
                                    "metadata", entry.getMetadata(),
                                    "timestamp", entry.getTimestamp()
                            ),
                            "stat", container.getStat().getName()
                    ));
                    SharedMain.debug("Publishing to RMQ: " + message);
                    String routingKey = (container.getStat().getName().replace(" ", "_") + "."
                            + player.getUuid().toString()).toLowerCase();
                    this.channel.exchangeDeclare(this.exchange, "direct");
                    this.channel.queueDeclare("stats5", true, false, true, null);
                    this.channel.queueBind("stats5", this.exchange, routingKey);
                    this.channel.basicPublish(this.exchange, routingKey, null, message.getBytes());
                }))
        ));
    }

    private void connect(File config) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.load(config.getPath());
        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(config)) {
            properties.load(reader);
            this.exchange = properties.getProperty("exchange", "");
        }
    }

    public void shutdown() {
        this.disposable.dispose();
        try {
            this.channel.close();
            this.connection.close();
        } catch (IOException | TimeoutException ignored) {
            // Fail silently
//            e.printStackTrace();
        }
    }
}
