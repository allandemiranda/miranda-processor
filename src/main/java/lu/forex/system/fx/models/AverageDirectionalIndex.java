package lu.forex.system.fx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@ToString
@Table(name = "adx")
public class AverageDirectionalIndex implements Serializable {

  @Serial
  private static final long serialVersionUID = -5334846270937449469L;

  public static final int PERIOD = 14;
  public static final BigDecimal TENDENCY_LINE = BigDecimal.valueOf(50);
  public static final BigDecimal DECIMAL = BigDecimal.valueOf(100);

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @Enumerated
  @Column(name = "signal_indicator", nullable = false)
  private SignalIndicator signalIndicator;

  @PositiveOrZero
  @Column(name = "key_adx", precision = 40, scale = 30)
  private BigDecimal keyAdx;

  @PositiveOrZero
  @Column(name = "key_p_di_p", precision = 40, scale = 30)
  private BigDecimal keyPDiP;

  @PositiveOrZero
  @Column(name = "key_n_di_p", precision = 40, scale = 30)
  private BigDecimal keyNDiP;

  @PositiveOrZero
  @Column(name = "key_tr_one", precision = 40, scale = 30)
  private BigDecimal keyTr1;

  @PositiveOrZero
  @Column(name = "key_p_dm_one", precision = 40, scale = 30)
  private BigDecimal keyPDm1;

  @PositiveOrZero
  @Column(name = "key_n_dm_one", precision = 40, scale = 30)
  private BigDecimal keyNDm1;

  @PositiveOrZero
  @Column(name = "key_dx", precision = 40, scale = 30)
  private BigDecimal keyDx;

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    final AverageDirectionalIndex that = (AverageDirectionalIndex) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
