package lu.forex.system.fx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.fx.enums.SignalIndicator;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@ToString
@Table(name = "rsi")
public class RelativeStrengthIndex implements Serializable {

  public static final int PERIOD = 14;
  public static final BigDecimal OVERBOUGHT = BigDecimal.valueOf(70.0);
  public static final BigDecimal OVERSOLD = BigDecimal.valueOf(30.0);
  @Serial
  private static final long serialVersionUID = 2820716796228647965L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "signal_indicator", nullable = false)
  private SignalIndicator signalIndicator;

  @PositiveOrZero
  @Column(name = "key_gain", precision = 40, scale = 30)
  private BigDecimal keyGain;

  @PositiveOrZero
  @Column(name = "key_loss", precision = 40, scale = 30)
  private BigDecimal keyLoss;

  @PositiveOrZero
  @Column(name = "key_rsi", precision = 40, scale = 30)
  private BigDecimal keyRsi;

  @PositiveOrZero
  @Column(name = "key_average_gain", precision = 40, scale = 30)
  private BigDecimal keyAverageGain;

  @PositiveOrZero
  @Column(name = "key_average_loss", precision = 40, scale = 30)
  private BigDecimal keyAverageLoss;

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    final RelativeStrengthIndex that = (RelativeStrengthIndex) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
