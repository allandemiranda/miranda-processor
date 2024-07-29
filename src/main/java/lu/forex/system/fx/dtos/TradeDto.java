package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.NonNull;
import lu.forex.system.fx.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.fx.models.Trade}
 */
public record TradeDto(@NonNull UUID id, @Negative int stopLoss, @Positive int takeProfit, @NotNull TimeFrame timeFrame) implements Serializable {

  @Serial
  private static final long serialVersionUID = -4427739296747905087L;
}