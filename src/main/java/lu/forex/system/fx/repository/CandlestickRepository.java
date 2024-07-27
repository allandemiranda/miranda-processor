package lu.forex.system.fx.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import lu.forex.system.fx.models.Candlestick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NonNull
  @Transactional(readOnly = true)
  List<Candlestick> findBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  Optional<Candlestick> getFirstBySymbolAndTimeFrameAndTimestamp(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame, @NonNull LocalDateTime timestamp);
}