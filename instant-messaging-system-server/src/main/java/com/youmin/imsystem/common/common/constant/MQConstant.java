package com.youmin.imsystem.common.common.constant;

public class MQConstant {

    /**
     * SEND MESSAGE TOPIC
     */
    public static final String SEND_MSG_EXCHANGE = "SEND_MSG_EXCHANGE";
    public static final String SEND_MSG_QUEUE = "SEND_MSG_QUEUE";
    public static final String SEND_MSG_ROUTING_KEY = "SEND_MSG_ROUTING_KEY";

    /**
     * Push Message to all cluster
     */
    public static final String PUSH_MSG_EXCHANGE = "PUSH_MSG_EXCHANGE";
    public static final String PUSH_MSG_QUEUE_1 = "PUSH_MSG_QUEUE_1";
    //no routing key, because the exchange type is fanout, where each springboot instance handle its own push_msg_queue
}
