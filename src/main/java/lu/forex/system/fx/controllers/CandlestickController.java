package lu.forex.system.fx.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.InitCandlestickDto;
import lu.forex.system.fx.providers.CandlestickProvider;
import lu.forex.system.fx.requests.CandlestickRequest;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickRequest {

  private final CandlestickProvider candlestickProvider;

  @Override
  public InitCandlestickDto insetInitCandlestick(final InitCandlestickDto newCandlestickDto) {
    return this.getCandlestickProvider().insertInitCandlestick(newCandlestickDto);
  }
}
