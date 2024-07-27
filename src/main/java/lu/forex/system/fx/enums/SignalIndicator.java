package lu.forex.system.fx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignalIndicator {
  BULLISH(OrderType.BUY), BEARISH(OrderType.SELL), NEUTRAL(OrderType.NOTHING);

  final OrderType orderType;

  public enum OrderType {
    BUY, SELL, NOTHING
  }
}
