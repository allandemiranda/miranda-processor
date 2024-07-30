package lu.forex.system.fx.services;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.models.Tick;
import lu.forex.system.fx.models.Trade;
import lu.forex.system.fx.providers.PreProcessorProvider;
import lu.forex.system.fx.repository.CandlestickRepository;
import lu.forex.system.fx.repository.TickRepository;
import lu.forex.system.fx.repository.TradeRepository;
import lu.forex.system.processor.PreProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class PreProcessorService implements PreProcessorProvider {

  private final CandlestickRepository candlestickRepository;
  private final TradeRepository tradeRepository;
  private final TickRepository tickRepository;

  @Override
  public void startDataSystem(final @NonNull File inputFolder) {
    final AtomicReference<Tick> lastTick = new AtomicReference<>();
    PreProcessor.getExternalizingCollection(inputFolder).forEach((tick, candlesticks) -> {
      lastTick.set(tick);
      this.getCandlestickRepository().saveAll(candlesticks);
    });
    if (Objects.nonNull(lastTick.get())) {
      this.getTickRepository().save(lastTick.get());
    }
  }

  @Override
  public void startTradeDataSystem(final @NonNull File inputFolder) {
    final Collection<Trade> externalizingTrades = PreProcessor.getExternalizingTrades(inputFolder);
    this.getTradeRepository().saveAll(externalizingTrades);
  }
}
