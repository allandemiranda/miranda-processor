package lu.forex.system.fx.controllers;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.dtos.TradeDto;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.enums.SignalIndicator.OrderType;
import lu.forex.system.fx.exceptions.TickTimestampOlderException;
import lu.forex.system.fx.providers.CandlestickProvider;
import lu.forex.system.fx.providers.OpenPositionProvider;
import lu.forex.system.fx.providers.TickProvider;
import lu.forex.system.fx.providers.TradeProvider;
import lu.forex.system.fx.requests.TickRequests;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickController implements TickRequests {

  private final TickProvider tickProvider;
  private final CandlestickProvider candlestickProvider;
  private final TradeProvider tradeProvider;
  private final OpenPositionProvider openPositionProvider;

  @Override
  public String updateTickAndGetOpenPosition(final @NonNull TickDto currentTick) {
    if (this.getTickProvider().getLastTick(currentTick.symbol()).timestamp().isBefore(currentTick.timestamp())) {
      final TickDto currentTickUpdate = this.getTickProvider().updateTickData(currentTick);
      return this.getCandlestickProvider().updateAndGetCandlesticksNotNeutral(currentTick).stream()
          .map(candlestickDto -> Pair.of(candlestickDto.signalIndicator(), this.getTradeProvider().getTrade(candlestickDto)))
          .filter(signalIndicatorOptionalPair -> signalIndicatorOptionalPair.getSecond().isPresent()).map(signalIndicatorOptionalPair -> {
            final SignalIndicator signalIndicator = signalIndicatorOptionalPair.getFirst();
            final TradeDto trade = signalIndicatorOptionalPair.getSecond().get();
            if (currentTickUpdate.spread().multiply(currentTick.symbol().getPip()).compareTo(BigDecimal.valueOf(trade.stopLoss())) > 0) {
              this.getOpenPositionProvider()
                  .addOpenPosition(trade.id(), signalIndicator.getOrderType(), signalIndicator.getOrderType().equals(OrderType.BUY) ? currentTick.bid() : currentTick.ask(),
                      currentTick.timestamp());
              return String.format("%s %s %s %s %s", currentTick.timestamp(), trade.timeFrame(), signalIndicator.getOrderType().name(), trade.takeProfit(), trade.stopLoss() * (-1));
            } else {
              log.warn("Spread high to open {}", currentTickUpdate.toString());
              return null;
            }
          }).filter(Objects::nonNull).reduce("", (a, b) -> {
            if (a.isEmpty()) {
              return b;
            } else if (b.isEmpty()) {
              return a;
            } else {
              return a.concat(",").concat(b);
            }
          });
    } else {
      // throw new TickTimestampOlderException(currentTick.timestamp(), currentTick.symbol().name());
      return "";
    }
  }
}
