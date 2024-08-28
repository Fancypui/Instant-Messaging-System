package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.vo.req.IdRespVO;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.dao.UserEmojiDao;
import com.youmin.imsystem.common.user.domain.entity.UserEmoji;
import com.youmin.imsystem.common.user.domain.vo.req.UserEmojiRequest;
import com.youmin.imsystem.common.user.domain.vo.resp.UserEmojiResp;
import com.youmin.imsystem.common.user.service.IUserEmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEmojiServiceImpl implements IUserEmojiService {

    @Autowired
    private UserEmojiDao userEmojiDao;


    @Override
    public List<UserEmojiResp> getEmojiList(Long uid) {
        return userEmojiDao.listByUid(uid)
                .stream()
                .map(emoji->
                     UserEmojiResp.builder()
                            .id(emoji.getId())
                            .expressionUrl(emoji.getExpressionUrl())
                            .build()
                ).collect(Collectors.toList());
    }

    @Override
    public void deleteEmoji(long emojiId, Long uid) {
        UserEmoji delete = userEmojiDao.getById(emojiId);
        AssertUtils.isNotEmpty(delete,"Emoji not found");
        AssertUtils.equal(delete.getId(),uid,"Fail deleting emoji");
        userEmojiDao.removeById(emojiId);
    }

    @Override
    @RedissonLock(key = "#uid")
    public IdRespVO insert(UserEmojiRequest request, Long uid) {
        Integer count = userEmojiDao.countByUid(uid);
        AssertUtils.isTrue(count<30,"Emoji bag is full");
        Integer existsCount = userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, request.getExpressionUrl())
                .eq(UserEmoji::getUid, uid)
                .count();
        AssertUtils.isFalse(existsCount > 0, "emoji is already exist~~");

        UserEmoji insert = new UserEmoji();
        insert.setUid(uid);
        insert.setExpressionUrl(request.getExpressionUrl());
        userEmojiDao.save(insert);
        return IdRespVO.builder().id(insert.getId()).build();
    }
}
