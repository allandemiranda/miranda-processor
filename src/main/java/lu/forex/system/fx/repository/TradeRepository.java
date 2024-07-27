package lu.forex.system.fx.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import lu.forex.system.fx.models.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface TradeRepository extends JpaRepository<Trade, UUID>, JpaSpecificationExecutor<Trade> {

  @NonNull
  @Transactional(readOnly = true)
  @Query("select t from Trade t where t.symbol = ?1 and t.timeFrame = ?2 and t.slotWeek = ?3 and ?4 between t.slotStart and t.slotEnd")
  Optional<Trade> getTrade(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame, @NonNull DayOfWeek slotWeek, @NonNull LocalTime slotTime);


}