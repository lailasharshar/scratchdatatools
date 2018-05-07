package com.sharshar.scratchdatatools.repository;

import com.sharshar.scratchdatatools.beans.PriceData;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lsharshar on 3/24/2018.
 */
@Repository
public interface PriceDataRepository extends ElasticsearchRepository<PriceData, Long> {
	Page<PriceData> findTop500ByTickerAndExchangeOrderByUpdateTimeAsc(String ticker, int exchange, Pageable pageable);

	Page<PriceData> findByUpdateTimeBetweenAndExchangeAndTicker(
			DateTime d1, DateTime d2, int exchange, String ticker, Pageable pageable);
}
