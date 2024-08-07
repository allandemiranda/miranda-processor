package lu.forex.system.processor.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Tick {

  private final LocalDateTime dateTime;
  private final BigDecimal bid;
  private final BigDecimal ask;

  @NonNull
  public BigDecimal getSpread() {
    return this.getBid().subtract(this.getAsk());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Tick tick = (Tick) o;
    return Objects.equals(getDateTime(), tick.getDateTime());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDateTime());
  }
}
