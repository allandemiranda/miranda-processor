package lu.forex.system.fx.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.dtos.TradeDto;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.exceptions.TickTimestampOlderException;
import lu.forex.system.fx.providers.CandlestickProvider;
import lu.forex.system.fx.providers.TickProvider;
import lu.forex.system.fx.providers.TradeProvider;
import lu.forex.system.fx.requests.TickRequests;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickController implements TickRequests {

  private final TickProvider tickProvider;
  private final CandlestickProvider candlestickProvider;
  private final TradeProvider tradeProvider;

  @Override
  public String updateTickAndGetOpenPosition(final @NonNull TickDto currentTick) {
    final TickDto lastTickDto = this.getTickProvider().getLastTick(currentTick.symbol());
    if (lastTickDto.timestamp().isBefore(currentTick.timestamp())) {
      final String mt5Answer = this.getCandlestickProvider().getCandlesticks(currentTick).stream()
          .map(candlestickDto -> Pair.of(candlestickDto.signalIndicator(), this.getTradeProvider().getTrade(candlestickDto)))
          .filter(signalIndicatorOptionalPair -> signalIndicatorOptionalPair.getSecond().isPresent()).map(signalIndicatorOptionalPair -> {
            final SignalIndicator signalIndicator = signalIndicatorOptionalPair.getFirst();
            final TradeDto trade = signalIndicatorOptionalPair.getSecond().get();
            return String.format("%s %s %s %s %s", currentTick.timestamp(), trade.timeFrame(), signalIndicator.getOrderType().name(), trade.takeProfit(), trade.stopLoss());
          }).reduce("", (a, b) -> {
            if (a.isEmpty()) {
              return b;
            } else if (b.isEmpty()) {
              return a;
            } else {
              return a.concat(",").concat(b);
            }
          });
      this.getTickProvider().updateTickData(currentTick);
      return mt5Answer;
    } else {
      throw new TickTimestampOlderException(currentTick.timestamp(), currentTick.symbol().name());
    }
  }

  @Override
  public TickDto createTick(final TickDto currentTick) {
    return this.getTickProvider().insertInitTick(currentTick);
  }
}
