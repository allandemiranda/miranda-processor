package lu.forex.system.fx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Embeddable
public class CandlestickBody implements Serializable {

  @Serial
  private static final long serialVersionUID = -487371877339591223L;

  @NotNull
  @Positive
  @Column(name = "high", nullable = false, updatable = false, precision = 20, scale = 10)
  private BigDecimal high;

  @NotNull
  @Positive
  @Column(name = "low", nullable = false, precision = 20, scale = 10)
  private BigDecimal low;

  @NotNull
  @Positive
  @Column(name = "open", nullable = false, precision = 20, scale = 10)
  private BigDecimal open;

  @NotNull
  @Positive
  @Column(name = "close", nullable = false, precision = 20, scale = 10)
  private BigDecimal close;
}