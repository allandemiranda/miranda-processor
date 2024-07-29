package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.fx.enums.SignalIndicator.OrderType;

/**
 * DTO for {@link lu.forex.system.fx.models.OpenPosition}
 */
public record OpenPositionDto(@NotNull UUID tradeId, @NotNull OrderType orderType, @NotNull @Positive BigDecimal openPrice, @NotNull LocalDateTime openTime) implements Serializable {

  @Serial
  private static final long serialVersionUID = -6795738815543015209L;
}