package lu.forex.system.fx.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.exceptions.TradeNotFoundException;
import lu.forex.system.fx.models.OpenPosition;
import lu.forex.system.fx.models.Trade;
import lu.forex.system.fx.providers.OpenPositionProvider;
import lu.forex.system.fx.repository.OpenPositionRepository;
import lu.forex.system.fx.repository.TradeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class OpenPositionService implements OpenPositionProvider {

  private final OpenPositionRepository openPositionRepository;
  private final TradeRepository tradeRepository;

  @Override
  public void addOpenPosition(final @NonNull UUID tradeId, final @NonNull SignalIndicator.OrderType orderType, final @NonNull BigDecimal openPrice, final @NonNull LocalDateTime openTime) {
    final OpenPosition openPosition = new OpenPosition();
    final Trade trade = this.getTradeRepository().findById(tradeId).orElseThrow(() -> new TradeNotFoundException(tradeId));
    openPosition.setTrade(trade);
    openPosition.setOrderType(orderType);
    openPosition.setOpenPrice(openPrice);
    openPosition.setOpenTime(openTime);
    this.getOpenPositionRepository().save(openPosition);
  }
}
