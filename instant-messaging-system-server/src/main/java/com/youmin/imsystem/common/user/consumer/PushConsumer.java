package com.youmin.imsystem.common.user.consumer;

import com.rabbitmq.client.Channel;
import com.youmin.imsystem.common.common.constant.MQConstant;
import com.youmin.imsystem.common.common.domain.dto.PushMessageDTO;
import com.youmin.imsystem.common.user.enums.WSPushTypeEnum;
import com.youmin.imsystem.common.user.service.WebsocketService;
import com.youmin.imsystem.common.utils.JsonUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PushConsumer {

    @Autowired
    private WebsocketService websocketService;

    @RabbitListener(queues = MQConstant.PUSH_MSG_QUEUE_1)
    public void processPushMessage(Channel channel, Message message){
        PushMessageDTO pushMessageDTO = JsonUtils.toObj(new String(message.getBody()), PushMessageDTO.class);
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(pushMessageDTO.getPushType());
        switch(wsPushTypeEnum){
            case ALL:
                this.websocketService.sendToAll(pushMessageDTO.getWsRespBase());
                break;
            case USER:
                pushMessageDTO.getUidList().forEach(uid->{
                    websocketService.sendToUid(pushMessageDTO.getWsRespBase(),uid);
                });
                break;
        }
    }
}
