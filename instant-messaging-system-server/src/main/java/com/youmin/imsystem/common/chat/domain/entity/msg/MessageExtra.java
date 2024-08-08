package com.youmin.imsystem.common.chat.domain.entity.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;

//    private Map<String, UrlInfo> urlContentMap;
    private MsgRecall msgRecall;
    private List<Long> atUidList;
    private FileMsgDTO fileMsgDTO;
    private ImgMsgDTO imgMsgDTO;
    private SoundMsgDTO soundMsgDTO;
    private VideoMsgDTO videoMsgDTO;
    private EmojiMsgDTO emojiMsgDTO;

}
