package org.eu.rainx0.raintool.ex.matchengin.repository;

import java.util.List;

import org.eu.rainx0.raintool.ex.matchengin.entity.EntrustOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xiaoyu
 * @time 2025/7/20 21:27
 */
@Repository
public interface EntrustOrderRepository extends CrudRepository<EntrustOrder, String> {
    // @Query("SELECT * FROM customer WHERE first_name LIKE :prefix || '%'")
    // List<Customer> findByFirstNameStartsWith(@Param("prefix") String prefix);

    List<EntrustOrder> findEntrustOrdersByStatusOrderByCreatedAtDesc(Integer status);
}
