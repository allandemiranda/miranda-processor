package lu.forex.system.fx.enums;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum Symbol {
  EURUSD(BigDecimal.valueOf(0.00001)), GBPUSD(BigDecimal.valueOf(0.00001));

  @NonNull
  final BigDecimal pip;
}
