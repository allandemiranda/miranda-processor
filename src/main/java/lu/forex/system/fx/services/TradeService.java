package lu.forex.system.fx.services;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.CandlestickDto;
import lu.forex.system.fx.dtos.TradeDto;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import lu.forex.system.fx.mappers.TradeMapper;
import lu.forex.system.fx.providers.TradeProvider;
import lu.forex.system.fx.repository.TradeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeService implements TradeProvider {

  private final TradeRepository tradeRepository;
  private final TradeMapper tradeMapper;

  @Override
  public Optional<@NotNull TradeDto> getTrade(final @NonNull CandlestickDto candlestickDto) {
    final Symbol symbol = candlestickDto.symbol();
    final TimeFrame timeFrame = candlestickDto.timeFrame();
    final DayOfWeek dayOfWeek = candlestickDto.timestamp().getDayOfWeek();
    final LocalTime timestamp = candlestickDto.timestamp().toLocalTime();
    return this.getTradeRepository().getTrade(symbol, timeFrame, dayOfWeek, timestamp).map(this.getTradeMapper()::toDto);
  }
}
