package lu.forex.system.fx.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.CandlestickDto;
import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import lu.forex.system.fx.mappers.CandlestickMapper;
import lu.forex.system.fx.models.AverageDirectionalIndex;
import lu.forex.system.fx.models.Candlestick;
import lu.forex.system.fx.models.CandlestickBody;
import lu.forex.system.fx.models.RelativeStrengthIndex;
import lu.forex.system.fx.providers.CandlestickProvider;
import lu.forex.system.fx.repository.CandlestickRepository;
import lu.forex.system.fx.utils.MathUtils;
import lu.forex.system.fx.utils.TimeFrameUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickService implements CandlestickProvider {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;

  @Override
  public @NonNull Collection<CandlestickDto> getCandlesticks(final @NonNull TickDto currentTick) {
    return Arrays.stream(TimeFrame.values()).map(timeFrame -> {
      final LocalDateTime candlestickTimestamp = TimeFrameUtils.getCandlestickTimestamp(currentTick.timestamp(), timeFrame);
      final BigDecimal price = currentTick.bid();
      final Symbol symbol = currentTick.symbol();

      final Optional<Candlestick> optionalCandlestick = this.getCandlestickRepository().getFirstBySymbolAndTimeFrameAndTimestamp(symbol, timeFrame, candlestickTimestamp);
      if (optionalCandlestick.isPresent()) {
        this.updateCandlestickPrice(optionalCandlestick.get(), price);
      } else {
        this.updateTableSize(symbol, timeFrame);
        this.createNewCandlestick(symbol, timeFrame, candlestickTimestamp, price);
      }
      return this.calculateIndicators(timeFrame, symbol);
    }).filter(candlestick -> !SignalIndicator.NEUTRAL.equals(candlestick.getSignalIndicator())).map(this.getCandlestickMapper()::toDto).toList();

  }

  private @NonNull Candlestick calculateIndicators(final @NonNull TimeFrame timeFrame, final @NonNull Symbol symbol) {
    final Candlestick[] candlesticks = this.getCandlestickRepository().findBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).toArray(Candlestick[]::new);

    //!! paralelo aqui depois
    this.calculateRsi(candlesticks);
    this.calculateAdx(candlesticks);
    //

    final Candlestick currentCandlestick = candlesticks[0];
    final Candlestick lastCandlestick = candlesticks[1];
    final SignalIndicator realLastSignalIndicator = lastCandlestick.getAdx().getSignalIndicator().equals(lastCandlestick.getRsi().getSignalIndicator()) ? lastCandlestick.getAdx().getSignalIndicator() : SignalIndicator.NEUTRAL;
    if (currentCandlestick.getAdx().getSignalIndicator().equals(currentCandlestick.getRsi().getSignalIndicator()) && !realLastSignalIndicator.equals(currentCandlestick.getAdx().getSignalIndicator())) {
      currentCandlestick.setSignalIndicator(currentCandlestick.getAdx().getSignalIndicator());
    } else {
      currentCandlestick.setSignalIndicator(SignalIndicator.NEUTRAL);
    }
    return this.getCandlestickRepository().save(currentCandlestick);
  }

  private void updateTableSize(final @NonNull Symbol symbol, final @NonNull TimeFrame timeFrame) {
    final Candlestick candlestickToRemove = this.getCandlestickRepository().findBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).getLast();
    this.getCandlestickRepository().delete(candlestickToRemove);
  }

  private void createNewCandlestick(final Symbol symbol, final @NonNull TimeFrame timeFrame, final @NonNull LocalDateTime candlestickTimestamp, final @NonNull BigDecimal price) {
    final Candlestick candlestick = new Candlestick();
    candlestick.setSymbol(symbol);
    candlestick.setTimeFrame(timeFrame);
    candlestick.setTimestamp(candlestickTimestamp);

    final CandlestickBody body = new CandlestickBody();
    body.setHigh(price);
    body.setLow(price);
    body.setLow(price);
    body.setClose(price);
    candlestick.setBody(body);

    final AverageDirectionalIndex adx = new AverageDirectionalIndex();
    adx.setSignalIndicator(SignalIndicator.NEUTRAL);
    candlestick.setAdx(adx);

    final RelativeStrengthIndex rsi = new RelativeStrengthIndex();
    rsi.setSignalIndicator(SignalIndicator.NEUTRAL);
    candlestick.setRsi(rsi);

    candlestick.setSignalIndicator(SignalIndicator.NEUTRAL);

    this.getCandlestickRepository().save(candlestick);
  }

  private void updateCandlestickPrice(final @NonNull Candlestick candlestick, final @NonNull BigDecimal price) {
    candlestick.getBody().setClose(price);
    this.getCandlestickRepository().save(candlestick);
  }

  public void calculateRsi(final @NonNull Candlestick @NonNull [] candlesticks) {
    final RelativeStrengthIndex[] technicalIndicators = IntStream.range(0, candlesticks.length < RelativeStrengthIndex.PERIOD ? 1 : RelativeStrengthIndex.PERIOD)
        .mapToObj(i -> candlesticks[i].getRsi()).toArray(RelativeStrengthIndex[]::new);

    if (candlesticks.length >= 2) {
      final BigDecimal currentClosePrice = candlesticks[0].getBody().getClose();
      final BigDecimal lastClosePrice = candlesticks[1].getBody().getClose();
      final BigDecimal gain = currentClosePrice.compareTo(lastClosePrice) > 0 ? currentClosePrice.subtract(lastClosePrice) : BigDecimal.ZERO;
      technicalIndicators[0].setKeyGain(gain);
      final BigDecimal loss = currentClosePrice.compareTo(lastClosePrice) < 0 ? lastClosePrice.subtract(currentClosePrice) : BigDecimal.ZERO;
      technicalIndicators[0].setKeyLoss(loss);

      if (technicalIndicators.length == RelativeStrengthIndex.PERIOD && IntStream.range(0, RelativeStrengthIndex.PERIOD)
          .noneMatch(i -> Objects.isNull(technicalIndicators[i].getKeyGain()) || Objects.isNull(technicalIndicators[i].getKeyLoss()))) {
        if (Objects.isNull(technicalIndicators[1].getKeyAverageGain())) {
          final BigDecimal averageGain = MathUtils.getMed(IntStream.range(0, RelativeStrengthIndex.PERIOD).mapToObj(i -> technicalIndicators[i].getKeyGain()).toList());
          technicalIndicators[0].setKeyAverageGain(averageGain);
          final BigDecimal averageLoss = MathUtils.getMed(IntStream.range(0, RelativeStrengthIndex.PERIOD).mapToObj(i -> technicalIndicators[i].getKeyLoss()).toList());
          technicalIndicators[0].setKeyAverageLoss(averageLoss);
        } else {
          final BigDecimal averageGain = ((technicalIndicators[1].getKeyAverageGain().multiply(BigDecimal.valueOf(RelativeStrengthIndex.PERIOD - 1L))).add(gain)).divide(
              BigDecimal.valueOf(RelativeStrengthIndex.PERIOD), MathUtils.SCALE, MathUtils.ROUNDING_MODE);
          technicalIndicators[0].setKeyAverageGain(averageGain);
          final BigDecimal averageLoss = ((technicalIndicators[1].getKeyAverageLoss().multiply(BigDecimal.valueOf(RelativeStrengthIndex.PERIOD - 1L))).add(loss)).divide(
              BigDecimal.valueOf(RelativeStrengthIndex.PERIOD), MathUtils.SCALE, MathUtils.ROUNDING_MODE);
          technicalIndicators[0].setKeyAverageLoss(averageLoss);
        }
        final BigDecimal rs = technicalIndicators[0].getKeyAverageGain().divide(technicalIndicators[0].getKeyAverageLoss(), MathUtils.SCALE, MathUtils.ROUNDING_MODE);
        final BigDecimal rsi = BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), MathUtils.SCALE, MathUtils.ROUNDING_MODE));
        technicalIndicators[0].setKeyRsi(rsi);
        if (rsi.compareTo(RelativeStrengthIndex.OVERBOUGHT) > 0) {
          technicalIndicators[0].setSignalIndicator(SignalIndicator.BULLISH);
        } else if (rsi.compareTo(RelativeStrengthIndex.OVERSOLD) < 0) {
          technicalIndicators[0].setSignalIndicator(SignalIndicator.BEARISH);
        } else {
          technicalIndicators[0].setSignalIndicator(SignalIndicator.NEUTRAL);
        }
      }
    }
  }

  public void calculateAdx(final @NonNull Candlestick @NonNull [] candlesticks) {
    final AverageDirectionalIndex[] technicalIndicators = IntStream.range(0, candlesticks.length < AverageDirectionalIndex.PERIOD ? 1 : AverageDirectionalIndex.PERIOD)
        .mapToObj(i -> candlesticks[i].getAdx()).toArray(AverageDirectionalIndex[]::new);

    if (candlesticks.length >= 2) {
      // get TR1
      final BigDecimal trOne = MathUtils.getMax(candlesticks[0].getBody().getHigh().subtract(candlesticks[0].getBody().getLow()),
          candlesticks[0].getBody().getHigh().subtract(candlesticks[0].getBody().getClose()), candlesticks[0].getBody().getLow().subtract(candlesticks[1].getBody().getClose()).abs());
      technicalIndicators[0].setKeyTr1(trOne);

      // get +DM1
      final BigDecimal pDmOne =
          candlesticks[0].getBody().getHigh().subtract(candlesticks[1].getBody().getHigh()).compareTo(candlesticks[1].getBody().getLow().subtract(candlesticks[0].getBody().getLow())) > 0
              ? MathUtils.getMax(candlesticks[0].getBody().getHigh().subtract(candlesticks[1].getBody().getHigh()), BigDecimal.ZERO) : BigDecimal.ZERO;
      technicalIndicators[0].setKeyPDm1(pDmOne);

      // get -DM1
      final BigDecimal nDmOne =
          candlesticks[1].getBody().getLow().subtract(candlesticks[0].getBody().getLow()).compareTo(candlesticks[0].getBody().getHigh().subtract(candlesticks[1].getBody().getHigh())) > 0
              ? MathUtils.getMax(candlesticks[1].getBody().getLow().subtract(candlesticks[0].getBody().getLow()), BigDecimal.ZERO) : BigDecimal.ZERO;
      technicalIndicators[0].setKeyNDm1(nDmOne);

      if (technicalIndicators.length == AverageDirectionalIndex.PERIOD && IntStream.range(0, AverageDirectionalIndex.PERIOD)
          .noneMatch(i -> Objects.isNull(technicalIndicators[i].getKeyTr1()) || Objects.isNull(technicalIndicators[i].getKeyPDm1()) || Objects.isNull(technicalIndicators[i].getKeyNDm1()))) {
        // get TR(P)
        final BigDecimal[] trPArray = new BigDecimal[AverageDirectionalIndex.PERIOD];
        IntStream.range(0, AverageDirectionalIndex.PERIOD).parallel().forEach(i -> trPArray[i] = technicalIndicators[i].getKeyTr1());
        final BigDecimal trP = MathUtils.getSum(trPArray);

        // get +DM(P)
        final BigDecimal[] pDmPArray = new BigDecimal[AverageDirectionalIndex.PERIOD];
        IntStream.range(0, AverageDirectionalIndex.PERIOD).parallel().forEach(i -> pDmPArray[i] = technicalIndicators[i].getKeyPDm1());
        final BigDecimal pDmP = MathUtils.getSum(pDmPArray);

        // get -DM(P)
        final BigDecimal[] nDmPArray = new BigDecimal[AverageDirectionalIndex.PERIOD];
        IntStream.range(0, AverageDirectionalIndex.PERIOD).parallel().forEach(i -> nDmPArray[i] = technicalIndicators[i].getKeyNDm1());
        final BigDecimal nDmP = MathUtils.getSum(nDmPArray);

        // get +DI(P)
        final BigDecimal pDiP = MathUtils.getMultiplication(AverageDirectionalIndex.DECIMAL, MathUtils.getDivision(pDmP, trP));
        technicalIndicators[0].setKeyPDiP(pDiP);

        // get -DI(P)
        final BigDecimal nDiP = MathUtils.getMultiplication(AverageDirectionalIndex.DECIMAL, MathUtils.getDivision(nDmP, trP));
        technicalIndicators[0].setKeyNDiP(nDiP);

        // get DI diff
        final BigDecimal diDiff = pDiP.subtract(nDiP).abs();

        // get DI sum
        final BigDecimal diSum = pDiP.add(nDiP);

        // get DX
        final BigDecimal dx = MathUtils.getMultiplication(AverageDirectionalIndex.DECIMAL, MathUtils.getDivision(diDiff, diSum));
        technicalIndicators[0].setKeyDx(dx);

        if (IntStream.range(0, AverageDirectionalIndex.PERIOD).noneMatch(i -> Objects.isNull(technicalIndicators[i].getKeyDx()))) {
          // get ADX
          final BigDecimal[] adxArray = new BigDecimal[AverageDirectionalIndex.PERIOD];
          IntStream.range(0, AverageDirectionalIndex.PERIOD).parallel().forEach(i -> adxArray[i] = technicalIndicators[i].getKeyDx());
          final BigDecimal adx = MathUtils.getMed(adxArray);
          technicalIndicators[0].setKeyAdx(adx);

          // Setting Signal
          if (adx.compareTo(AverageDirectionalIndex.TENDENCY_LINE) > 0) {
            if (pDiP.compareTo(nDiP) > 0) {
              technicalIndicators[0].setSignalIndicator(SignalIndicator.BULLISH);
            } else if (pDiP.compareTo(nDiP) < 0) {
              technicalIndicators[0].setSignalIndicator(SignalIndicator.BEARISH);
            } else {
              technicalIndicators[0].setSignalIndicator(SignalIndicator.NEUTRAL);
            }
          } else {
            technicalIndicators[0].setSignalIndicator(SignalIndicator.NEUTRAL);
          }
        }
      }
    }
  }
}
