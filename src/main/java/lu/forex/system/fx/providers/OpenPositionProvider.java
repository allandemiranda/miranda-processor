package lu.forex.system.fx.providers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NonNull;
import lu.forex.system.fx.enums.SignalIndicator;
import org.springframework.transaction.annotation.Transactional;

public interface OpenPositionProvider {

  @Transactional()
  void addOpenPosition(final @NonNull UUID tradeId, final @NonNull SignalIndicator.OrderType orderType, final @NonNull BigDecimal openPrice, final @NonNull LocalDateTime openTime);

}
