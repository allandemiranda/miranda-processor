package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.fx.enums.Symbol;

/**
 * DTO for {@link lu.forex.system.fx.models.Tick}
 */
public record TickDto(@NotNull UUID id, @NotNull Symbol symbol, @NotNull LocalDateTime timestamp, @NotNull @Positive BigDecimal bid, @NotNull @Positive BigDecimal ask,
                      @NotNull @NegativeOrZero BigDecimal spread) implements Serializable {

  @Serial
  private static final long serialVersionUID = -7183218897964538485L;

}