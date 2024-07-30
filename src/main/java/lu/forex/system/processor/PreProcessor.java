package lu.forex.system.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.processor.enums.Symbol;
import lu.forex.system.processor.enums.TimeFrame;
import lu.forex.system.processor.models.Candlestick;
import lu.forex.system.processor.models.Tick;
import lu.forex.system.processor.models.Trade;
import lu.forex.system.processor.services.CandlestickService;
import lu.forex.system.processor.services.TickService;
import lu.forex.system.processor.services.TradeService;
import lu.forex.system.processor.utils.XmlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Log4j2
@UtilityClass
public class PreProcessor {

  public static Map<lu.forex.system.fx.models.Tick, Collection<lu.forex.system.fx.models.Candlestick>> getExternalizingCollection(final @NonNull File inputFolder) {
    return Arrays.stream(Objects.requireNonNull(inputFolder.listFiles())).parallel()
        .filter(file -> Arrays.stream(Symbol.values()).anyMatch(symbol -> symbol.name().equals(file.getName().split("_")[0])))
        .flatMap(inputFile -> Arrays.stream(TimeFrame.values()).parallel().map(timeFrame -> {
          final Symbol symbol = Symbol.valueOf(inputFile.getName().split("_")[0]);

          final AtomicReference<Tick> lastTick = new AtomicReference<Tick>();
          final AtomicReference<List<lu.forex.system.fx.models.Candlestick>> candlesticksMemory = new AtomicReference<>();

          final Runnable lastTickRunnable = () -> lastTick.set(lastTickMemoryExternalizing(inputFile));
          final Runnable candlesticksMemoryRunnable = () -> {
            final Collection<Candlestick> memoryCandlesticks = CandlestickService.getCandlesticksMemory(inputFile, timeFrame, symbol);
            candlesticksMemory.set(memoryCandlesticks.stream().map(candlestick -> getCandlestickToFx(candlestick, symbol, timeFrame)).toList());
          };
          Stream.of(lastTickRunnable, candlesticksMemoryRunnable).parallel().map(Thread::new).peek(Thread::start).forEach(thread -> {
            try {
              thread.join();
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          });

          final lu.forex.system.fx.models.Tick tickToFx = getTickToFx(lastTick.get(), symbol);
          return new Externalizing(candlesticksMemory.get(), tickToFx);
        })).collect(Collectors.toMap(Externalizing::getLastTick, Externalizing::getCandlesticksMemory, CollectionUtils::union));
  }

  public static Collection<lu.forex.system.fx.models.Trade> getExternalizingTrades(final @NonNull File inputFolder) {
    return Arrays.stream(Objects.requireNonNull(inputFolder.listFiles())).parallel()
        .filter(file -> Arrays.stream(Symbol.values()).anyMatch(symbol -> symbol.name().equals(file.getName().split("_")[0])))
        .flatMap(inputFile -> Arrays.stream(TimeFrame.values()).parallel().flatMap(timeFrame -> {
          try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            final Symbol symbol = Symbol.valueOf(inputFile.getName().split("_")[0]);
            final Collection<Trade> trades = TradeService.getTrades(inputFile, bufferedReader, timeFrame, symbol);
            printTradesExcel(trades, timeFrame, symbol, inputFolder);
            return trades.stream().parallel().map(trade -> getTradeToFx(trade, symbol, timeFrame));
          } catch (IOException e) {
            throw new IllegalStateException(e);
          }
        })).toList();
  }

  private static lu.forex.system.fx.models.@NonNull Tick getTickToFx(final @NonNull Tick tick, final @NonNull Symbol symbol) {
    final lu.forex.system.fx.models.Tick tickFx = new lu.forex.system.fx.models.Tick();
    tickFx.setSymbol(lu.forex.system.fx.enums.Symbol.valueOf(symbol.name()));
    tickFx.setTimestamp(tick.getDateTime());
    tickFx.setBid(tick.getBid());
    tickFx.setAsk(tick.getAsk());
    return tickFx;
  }

  private static lu.forex.system.fx.models.@NonNull Trade getTradeToFx(final @NonNull Trade trade, final @NonNull Symbol symbol, final @NonNull TimeFrame timeFrame) {
    final lu.forex.system.fx.models.Trade tradeFx = new lu.forex.system.fx.models.Trade();
    tradeFx.setSymbol(lu.forex.system.fx.enums.Symbol.valueOf(symbol.name()));
    tradeFx.setTimeFrame(lu.forex.system.fx.enums.TimeFrame.valueOf(timeFrame.name()));
    tradeFx.setStopLoss(trade.getStopLoss());
    tradeFx.setTakeProfit(trade.getTakeProfit());
    tradeFx.setSlotWeek(trade.getSlotWeek());
    tradeFx.setSlotStart(LocalTime.of(trade.getSlotStart() * timeFrame.getSlotTimeH(), 0, 0));
    tradeFx.setSlotEnd(LocalTime.of(trade.getSlotStart() * timeFrame.getSlotTimeH(), 0, 0).plusHours(timeFrame.getSlotTimeH()).minusSeconds(1));
    tradeFx.setOrdersTotal(trade.getOrdersTotal());
    tradeFx.setTakeProfitTotal(trade.getTakeProfitTotal());
    tradeFx.setStopLossTotal(trade.getStopLossTotal());
    tradeFx.setHitPercentage(trade.getHitPercentage());
    tradeFx.setProfitTotal(trade.getProfitTotal());
    return tradeFx;
  }

  private static lu.forex.system.fx.models.@NonNull Candlestick getCandlestickToFx(final @NonNull Candlestick candlestick, final @NonNull Symbol symbol, final @NonNull TimeFrame timeFrame) {
    final lu.forex.system.fx.models.Candlestick candlestickFx = new lu.forex.system.fx.models.Candlestick();
    candlestickFx.setSymbol(lu.forex.system.fx.enums.Symbol.valueOf(symbol.name()));
    candlestickFx.setTimeFrame(lu.forex.system.fx.enums.TimeFrame.valueOf(timeFrame.name()));
    candlestickFx.setTimestamp(candlestick.getTimestamp());
    final lu.forex.system.fx.models.CandlestickBody bodyFx = new lu.forex.system.fx.models.CandlestickBody();
    bodyFx.setClose(candlestick.getBody().getClose());
    bodyFx.setOpen(candlestick.getBody().getOpen());
    bodyFx.setHigh(candlestick.getBody().getHigh());
    bodyFx.setLow(candlestick.getBody().getLow());
    candlestickFx.setBody(bodyFx);
    final lu.forex.system.fx.models.AverageDirectionalIndex adxFx = new lu.forex.system.fx.models.AverageDirectionalIndex();
    adxFx.setSignalIndicator(lu.forex.system.fx.enums.SignalIndicator.valueOf(candlestick.getAdx().getSignal().name()));
    adxFx.setKeyAdx(candlestick.getAdx().getKeyAdx());
    adxFx.setKeyPDiP(candlestick.getAdx().getKeyPDiP());
    adxFx.setKeyNDiP(candlestick.getAdx().getKeyNDiP());
    adxFx.setKeyTr1(candlestick.getAdx().getKeyTr1());
    adxFx.setKeyPDm1(candlestick.getAdx().getKeyPDm1());
    adxFx.setKeyNDm1(candlestick.getAdx().getKeyNDm1());
    adxFx.setKeyDx(candlestick.getAdx().getKeyDx());
    candlestickFx.setAdx(adxFx);
    final lu.forex.system.fx.models.RelativeStrengthIndex rsiFx = new lu.forex.system.fx.models.RelativeStrengthIndex();
    rsiFx.setSignalIndicator(lu.forex.system.fx.enums.SignalIndicator.valueOf(candlestick.getRsi().getSignal().name()));
    rsiFx.setKeyGain(candlestick.getRsi().getKeyGain());
    rsiFx.setKeyLoss(candlestick.getRsi().getKeyLoss());
    rsiFx.setKeyAverageGain(candlestick.getRsi().getKeyAverageGain());
    rsiFx.setKeyAverageLoss(candlestick.getRsi().getKeyAverageLoss());
    rsiFx.setKeyRsi(candlestick.getRsi().getKeyRsi());
    candlestickFx.setRsi(rsiFx);
    candlestickFx.setSignalIndicator(lu.forex.system.fx.enums.SignalIndicator.valueOf(candlestick.getSignalIndicator().name()));
    return candlestickFx;
  }

  @SneakyThrows
  private static Tick lastTickMemoryExternalizing(final @NonNull File inputFile) {
    log.info("Last Tick Memory");
    try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
      final AtomicReference<Tick> tick = new AtomicReference<>(new Tick(LocalDateTime.MIN, BigDecimal.valueOf(-1d), BigDecimal.valueOf(-1d)));
      TickService.getTicks(bufferedReader).forEach(t -> tick.set(t.getKey()));
      return tick.get();
    }
  }

  @SneakyThrows
  private static void printTradesExcel(final @NonNull Collection<Trade> tradesCollection, final @NonNull TimeFrame timeFrame, final @NonNull Symbol symbol,
      final @NonNull File outputFolder) {
    log.info("Printing Trades Excel for symbol {} at timeframe {}", symbol.name(), timeFrame.name());
    final DayOfWeek[] dayOfWeeks = Arrays.stream(DayOfWeek.values()).filter(dayOfWeek -> !DayOfWeek.SATURDAY.equals(dayOfWeek) && !DayOfWeek.SUNDAY.equals(dayOfWeek))
        .toArray(DayOfWeek[]::new);
    final int[] times = IntStream.range(0, 24 / timeFrame.getSlotTimeH()).toArray();
    try (final Workbook workbook = new XSSFWorkbook()) {
      Stream.of("TP", "SL", "TOTAL", "PERCENTAGE_TP", "BALANCE").forEach(sheetName -> {
        final Sheet sheet = workbook.createSheet(sheetName);
        final Row headerRow = sheet.createRow(0);
        IntStream.range(0, dayOfWeeks.length).forEach(i -> XmlUtils.setCellValue(dayOfWeeks[i], headerRow.createCell(i + 1)));
        IntStream.range(0, times.length).forEach(i -> XmlUtils.setCellValue(LocalTime.of(i * timeFrame.getSlotTimeH(), 0, 0), sheet.createRow(i + 1).createCell(0)));

        IntStream.range(0, dayOfWeeks.length).forEach(i -> {
          final DayOfWeek week = dayOfWeeks[i];
          IntStream.range(0, times.length).forEach(j -> {
            final int hour = times[j];
            tradesCollection.stream().filter(trade -> trade.getSlotWeek().equals(week) && trade.getSlotStart() == hour).findFirst()
                .ifPresent(trade -> XmlUtils.setCellValue(switch (sheetName) {
                  case "TP" -> trade.getTakeProfitTotal();
                  case "SL" -> trade.getStopLossTotal();
                  case "TOTAL" -> trade.getOrdersTotal();
                  case "PERCENTAGE_TP" -> trade.getHitPercentage().multiply(BigDecimal.valueOf(100d));
                  case "BALANCE" -> trade.getProfitTotal();
                  default -> throw new IllegalStateException("Unexpected value: " + sheetName);
                }, sheet.getRow(j + 1).createCell(i + 1)));
          });
        });
      });

      final Sheet sheet = workbook.createSheet("TRADES");
      final Row headerRow = sheet.createRow(0);
      final String[] header = new String[]{"TIME_START", "TIME_END", "WEEK", "TP", "SL", "PERCENTAGE_TP", "ORDERS_TP", "ORDERS_SL", "ORDERS_TOTAL", "ORDERS_PROFIT"};
      IntStream.range(0, header.length).forEach(i -> headerRow.createCell(i).setCellValue(header[i]));
      final AtomicInteger i = new AtomicInteger(1);
      tradesCollection.forEach(trade -> {
        final Row row = sheet.createRow(i.getAndIncrement());
        IntStream.range(0, header.length).forEach(j -> {
          final Cell cell = row.createCell(j);
          switch (j) {
            case 0 -> XmlUtils.setCellValue(LocalTime.of(trade.getSlotStart() * timeFrame.getSlotTimeH(), 0, 0), cell);
            case 1 -> XmlUtils.setCellValue(LocalTime.of(trade.getSlotStart() * timeFrame.getSlotTimeH(), 0, 0).plusHours(timeFrame.getSlotTimeH()).minusSeconds(1), cell);
            case 2 -> XmlUtils.setCellValue(trade.getSlotWeek(), cell);
            case 3 -> XmlUtils.setCellValue(trade.getTakeProfit(), cell);
            case 4 -> XmlUtils.setCellValue(trade.getStopLoss(), cell);
            case 5 -> XmlUtils.setCellValue(trade.getHitPercentage().multiply(BigDecimal.valueOf(100d)), cell);
            case 6 -> XmlUtils.setCellValue(trade.getTakeProfitTotal(), cell);
            case 7 -> XmlUtils.setCellValue(trade.getStopLossTotal(), cell);
            case 8 -> XmlUtils.setCellValue(trade.getOrdersTotal(), cell);
            case 9 -> XmlUtils.setCellValue(trade.getProfitTotal(), cell);
            default -> throw new IllegalStateException("Unexpected value: " + trade.toString());
          }
        });
      });
      workbook.write(new FileOutputStream(new File(outputFolder, symbol.name().concat(timeFrame.name()).concat("_trades.xlsx"))));
    }
    log.info("Trades Excel for symbol {} at timeframe {} printed", symbol.name(), timeFrame.name());
  }

  @Getter
  @AllArgsConstructor
  private class Externalizing {

    private final Collection<lu.forex.system.fx.models.Candlestick> candlesticksMemory;
    private final lu.forex.system.fx.models.Tick lastTick;
  }

}
