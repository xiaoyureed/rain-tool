package org.eu.rainx0.raintool.core.starter.data.jpa.x;

import org.eu.rainx0.raintool.core.starter.data.jpa.x.entity.AbstractIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/*
 * Inside this base repository, you can place the shared data access method here
 *
 * JpaSpecificationExecutor is dedicated to conduct complex query
 */
@NoRepositoryBean// Flag as a none Repository interface (which means no instantiation happen)
public interface IBaseRepository<T extends AbstractIdEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}