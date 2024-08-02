package lu.forex.system.fx.enums;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum Symbol {
  //@formatter:off
  EURUSD(BigDecimal.valueOf(0.00001)),
  USDJPY(BigDecimal.valueOf(0.001)),
  GBPUSD(BigDecimal.valueOf(0.00001)),
  USDCAD(BigDecimal.valueOf(0.00001)),
  AUDUSD(BigDecimal.valueOf(0.00001)),
  USDCHF(BigDecimal.valueOf(0.00001)),
  NZDUSD(BigDecimal.valueOf(0.00001)),
  USDHKD(BigDecimal.valueOf(0.00001)),
  ;
  //@formatter:on
  @NonNull
  final BigDecimal pip;
}
