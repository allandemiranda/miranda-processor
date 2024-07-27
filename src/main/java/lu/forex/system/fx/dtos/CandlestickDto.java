package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.fx.models.Candlestick}
 */
public record CandlestickDto(@NotNull UUID id, @NotNull Symbol symbol, @NotNull TimeFrame timeFrame, @NotNull LocalDateTime timestamp, @NotNull SignalIndicator signalIndicator) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = -5373530704128605673L;
}