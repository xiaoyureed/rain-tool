package org.eu.rainx0.raintool.core.starter.data.jpa.x;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eu.rainx0.raintool.core.common.exception.BizException;
import org.eu.rainx0.raintool.core.starter.data.jpa.x.entity.AbstractIdEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * This base service interface can be used to store common methods
 *
 */
public interface IBaseService<T extends AbstractIdEntity, ID> {

    IBaseRepository<T, ID> getRepository();

    default Optional<T> findById(ID id) {
        return this.getRepository().findById(id);
    }

    default List<T> findAll() {
        return this.getRepository().findAll();
    }

    default T save(T t) {
        return this.getRepository().saveAndFlush(t);
    }

    default void deleteById(ID id) {
        this.getRepository().deleteById(id);
    }

    default void deleteByIds(List<ID> ids) {
        this.getRepository().deleteAllByIdInBatch(ids);
    }

    @Transactional
    default void updateById(ID id, Consumer<T> update) {
        T en = this.findById(id).orElseThrow(() -> new BizException("数据不存在"));
        update.accept(en);
        this.save(en);
    }

    @Modifying
    @Query("update Account u set u.username = :name where u.id = :id")
    int updateNameById(@Param("id") String id, @Param("name") String name);

    @Modifying
    @Query(value = "UPDATE account SET name = ?2 WHERE id = ?1", nativeQuery = true)
    int updateNameNative(String id, String name);

}