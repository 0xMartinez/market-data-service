package com.crpt.Crypto.Repository;

import com.crpt.Crypto.Repository.Model.CandlestickDb;
import com.crpt.Crypto.Repository.Model.CandlestickKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandlestickRepository extends CassandraRepository<CandlestickDb, CandlestickKey> {


    Optional<CandlestickDb> findByKeySymbolAndKeyCloseTime(String symbol, Instant closeTime);
    List<CandlestickDb> findAllByKeySymbol(String symbol);
}