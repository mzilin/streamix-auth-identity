package com.mariuszilinskas.streamix.auth.identity.config;

import com.mariuszilinskas.streamix.auth.identity.properties.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(props.exchange());
    }

    @Bean
    public Queue createCredentialsQueue() {
        return new Queue(props.queues().createCredentials(), true);
    }

    @Bean
    public Binding createCredentialsBinding() {
        return BindingBuilder.bind(createCredentialsQueue())
                .to(exchange())
                .with(props.routingKeys().createCredentials());
    }

    @Bean
    public Queue resetPasscodeQueue() {
        return new Queue(props.queues().resetPasscode(), true);
    }

    @Bean
    public Binding resetPasscodeBinding() {
        return BindingBuilder.bind(resetPasscodeQueue())
                .to(exchange())
                .with(props.routingKeys().resetPasscode());
    }

    @Bean
    public Queue deleteUserDataQueue() {
        return new Queue(props.queues().deleteUserData(), true);
    }

    @Bean
    public Binding deleteUserDataBinding() {
        return BindingBuilder.bind(deleteUserDataQueue())
                .to(exchange())
                .with(props.routingKeys().deleteUserData());
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

}
