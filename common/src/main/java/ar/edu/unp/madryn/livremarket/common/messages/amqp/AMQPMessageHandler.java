package ar.edu.unp.madryn.livremarket.common.messages.amqp;

import ar.edu.unp.madryn.livremarket.common.messages.MessageDelivery;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class AMQPMessageHandler implements MessageServer {
    private static final String EXCHANGE_TYPE = "topic";

    private String url;
    private String exchangeName;

    private Connection connection;

    public AMQPMessageHandler(String url, String exchangeName) {
        this.url = url;
        this.exchangeName = exchangeName;
        this.connection = null;
    }

    @Override
    public boolean connect() {
        if (!(this.connection == null)) {
            return false;
        }

        if (StringUtils.isEmpty(this.url)) {
            return false;
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.url);

        try {
            this.connection = factory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnect() {
        if (this.connection == null) {
            return false;
        }

        try {
            this.connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        this.connection = null;
        return true;
    }

    @Override
    public boolean registerProcessor(String bindingKey, MessageDelivery messageProcessor) {
        if (this.connection == null) {
            return false;
        }

        if(StringUtils.isEmpty(this.exchangeName)){
            return false;
        }

        try {
            Channel channel = this.connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();

            //TODO Si la exchange no esta creada falla el binding...
            channel.queueBind(queueName, this.exchangeName, bindingKey);

            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                messageProcessor.processMessage(delivery.getEnvelope().getRoutingKey(), message);
            }, consumerTag -> {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean sendMessage(String routingKey, String message) {
        if (this.connection == null) {
            return false;
        }

        if(StringUtils.isEmpty(this.exchangeName)){
            return false;
        }

        try {
            Channel channel = this.connection.createChannel();

            channel.exchangeDeclare(this.exchangeName, EXCHANGE_TYPE);

            channel.basicPublish(this.exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}
