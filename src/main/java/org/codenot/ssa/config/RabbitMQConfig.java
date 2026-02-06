package org.codenot.ssa.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String ORBIT_UPDATE_QUEUE = "orbit.update.queue";
    public static final String ORBIT_EXCHANGE = "orbit.exchange";
    public static final String ROUTING_KEY = "orbit.update";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
