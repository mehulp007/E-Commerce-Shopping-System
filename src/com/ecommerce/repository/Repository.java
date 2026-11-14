package com.ecommerce.repository;

import com.ecommerce.exception.DataStoreException;
import java.util.Collection;
import java.util.List;

/** Generic storage for any type of record (products, customers, orders). */
public interface Repository<T> {

    List<T> loadAll() throws DataStoreException;

    void saveAll(Collection<T> items) throws DataStoreException;
}
