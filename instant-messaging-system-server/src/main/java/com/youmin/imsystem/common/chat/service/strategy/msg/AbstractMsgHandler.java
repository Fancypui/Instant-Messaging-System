package com.youmin.imsystem.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.service.adapter.MessageAdapter;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * Message Handler abstract class
 */
public abstract class AbstractMsgHandler<Req> {

    private Class<Req> reqClass;

    @Autowired
    private MessageDao messageDao;

    @PostConstruct
    private void init(){

        ParameterizedType genericSuperclass = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.reqClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getMsgType(), this);
    }

    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid){
        Req body = toBean(request.getBody());
        //validate body info
        AssertUtils.allCheckValidateThrow(body);
        //let subclassess make their own validation logic
        checkMsg(body,request.getRoomId(),uid);
        Message insert = MessageAdapter.buildMessageSave(request, uid);
        //save common detail first
        messageDao.save(insert);
        //let subclasess implement their own save logic
        saveMsg(insert,body);
        return insert.getId();
    }

    public Req toBean(Object body){
        //for base type like string
        if(reqClass.isAssignableFrom(body.getClass())){
            return (Req) body;
        }
        return BeanUtil.toBean(body,this.reqClass);
    }
    public abstract Object showMsg(Message msg);

    protected void checkMsg(Req body,Long roomId,Long uid){

    }

    /**
     * sub classes have their own save message logic
     * @param message
     * @param body
     */
    protected abstract void saveMsg(Message message,Req body);

    abstract MessageTypeEnum getMsgTypeEnum();
}
