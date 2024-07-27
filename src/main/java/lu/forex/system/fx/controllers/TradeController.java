package lu.forex.system.fx.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.InitTradeDto;
import lu.forex.system.fx.providers.TradeProvider;
import lu.forex.system.fx.requests.TradeRequest;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeController implements TradeRequest {

  private final TradeProvider tradeProvider;

  @Override
  public InitTradeDto insertTrade(final InitTradeDto initTradeDto) {
    return this.getTradeProvider().initTrade(initTradeDto);
  }
}
