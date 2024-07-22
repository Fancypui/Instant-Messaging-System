package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.common.domain.enums.IdempotentEnum;

public interface IUserBackpackService {

    /**
     *
     * @param uid who will receive the item
     * @param itemId item id
     * @param idempotentEnum Idempotent enum
     * @param businessId business id unique key
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
