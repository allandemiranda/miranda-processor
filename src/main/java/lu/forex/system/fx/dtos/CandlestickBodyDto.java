package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link lu.forex.system.fx.models.CandlestickBody}
 */
public record CandlestickBodyDto(@NotNull @Positive BigDecimal high, @NotNull @Positive BigDecimal low, @NotNull @Positive BigDecimal open, @NotNull @Positive BigDecimal close) implements
    Serializable {
  @Serial
  private static final long serialVersionUID = -3763960388938832861L;
}