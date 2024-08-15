package com.youmin.imsystem.transaction.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.youmin.imsystem.common.utils.JsonUtils;
import com.youmin.imsystem.transaction.annotation.SecureInvoke;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

public class MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


//    @SecureInvoke()
    public void sendSecureMsg(String exchangeName, String routingKey, Object body){
        Message message = MessageBuilder
                .withBody(JsonUtils.toStr(body).getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        rabbitTemplate.convertAndSend(exchangeName,routingKey,message);
    }

    public void sendMsg(String exchangeName, String routingKey, Object body){
        Message message = MessageBuilder
                .withBody(JsonUtils.toStr(body).getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        rabbitTemplate.convertAndSend(exchangeName,routingKey,message);
    }
}
