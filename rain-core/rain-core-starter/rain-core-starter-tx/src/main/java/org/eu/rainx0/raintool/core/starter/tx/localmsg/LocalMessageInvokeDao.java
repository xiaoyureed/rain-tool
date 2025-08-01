package org.eu.rainx0.raintool.core.starter.tx.localmsg;

import java.util.List;

import org.eu.rainx0.raintool.core.starter.tx.localmsg.model.LocalMessage;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:49
 */
public interface LocalMessageInvokeDao<ID, T extends LocalMessage<ID>> {

    void save(T msg);
    void updateById(T msg);
    void remove(ID id);

    List<T> getRetryMessages();

}
