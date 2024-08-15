package com.youmin.imsystem.common.common.config;

import com.youmin.imsystem.common.common.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure all the exchanges, queues
 *
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue sendMsgQueue(){
        /**
         * set queue's metadata and messages persistent
         */
        return QueueBuilder.durable(MQConstant.SEND_MSG_QUEUE).maxLength(100).build();
    }

    @Bean
    public Exchange sendMsgExchange(){
        /**
         * direct exchange for SEND MSG
         */
        return ExchangeBuilder.directExchange(MQConstant.SEND_MSG_EXCHANGE).build();
    }

    /**
     * build relationship between sendMsgQueue and sendMsgExchange
     * todo configure dead letter if message exceed (future)
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding sendMsgBinding(@Qualifier("sendMsgQueue") Queue queue,@Qualifier("sendMsgExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MQConstant.SEND_MSG_ROUTING_KEY).noargs();
    }

    /**
     * fanout exchange for pushing/routing message in cluster
     * @return
     */
    @Bean
    public Exchange pushMsgExchange(){
        return new FanoutExchange(MQConstant.PUSH_MSG_EXCHANGE);
    }

    /**
     * queue that receive message from pushMsgExhcange (FanoutExchange)
     * @return
     */
    @Bean
    public Queue pushMsgQueue1(){
        return QueueBuilder.durable(MQConstant.PUSH_MSG_QUEUE_1).maxLength(100).build();
    }

    /**
     * binding relationship between pushMsgQueue and pushMsgExchange
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding pushMsgBinding(@Qualifier("pushMsgQueue1") Queue queue,@Qualifier("pushMsgExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }


}
