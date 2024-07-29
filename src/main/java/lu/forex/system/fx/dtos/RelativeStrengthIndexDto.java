package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lu.forex.system.fx.enums.SignalIndicator;

/**
 * DTO for {@link lu.forex.system.fx.models.RelativeStrengthIndex}
 */
public record RelativeStrengthIndexDto(UUID id, @NotNull SignalIndicator signalIndicator, @PositiveOrZero BigDecimal keyGain, @PositiveOrZero BigDecimal keyLoss,
                                       @PositiveOrZero BigDecimal keyRsi, @PositiveOrZero BigDecimal keyAverageGain, @PositiveOrZero BigDecimal keyAverageLoss) implements
    Serializable {
  @Serial
  private static final long serialVersionUID = -3917643993776606546L;
}