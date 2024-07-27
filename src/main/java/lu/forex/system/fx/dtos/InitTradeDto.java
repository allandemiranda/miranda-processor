package lu.forex.system.fx.dtos;

import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.fx.models.Trade}
 */
public record InitTradeDto(UUID id, @NotNull Symbol symbol, @NotNull TimeFrame timeFrame, @Negative int stopLoss, @Positive int takeProfit, DayOfWeek slotWeek, LocalTime slotStart,
                           @NotNull LocalTime slotEnd) implements
    Serializable {
  @Serial
  private static final long serialVersionUID = 3369793932762506761L;

}