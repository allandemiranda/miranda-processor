package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lu.forex.system.fx.enums.SignalIndicator;

/**
 * DTO for {@link lu.forex.system.fx.models.AverageDirectionalIndex}
 */
public record AverageDirectionalIndexDto(UUID id, @NotNull SignalIndicator signalIndicator, @PositiveOrZero BigDecimal keyAdx, @PositiveOrZero BigDecimal keyPDiP,
                                         @PositiveOrZero BigDecimal keyNDiP, @PositiveOrZero BigDecimal keyTr1, @PositiveOrZero BigDecimal keyPDm1, @PositiveOrZero BigDecimal keyNDm1,
                                         @PositiveOrZero BigDecimal keyDx) implements
    Serializable {
  @Serial
  private static final long serialVersionUID = 2526042457426155484L;

}