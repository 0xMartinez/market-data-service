package com.crpt.Crypto.Repository.Model;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OpenOrderRepository extends CassandraRepository<OpenOrder, OpenOrderKey> {

    @Query("SELECT * FROM open_orders WHERE timestamp >= ?0 AND timestamp <= ?1 AND symbol == ?2")
    List<OpenOrder> findFuturesOpenOrdersByTimestampBetweenAndSymbol(Instant startTime, Instant endTime, String symbol);
}
