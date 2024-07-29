package lu.forex.system.fx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.fx.enums.SignalIndicator.OrderType;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@ToString
@Table(name = "open_position")
public class OpenPosition implements Serializable {

  @Serial
  private static final long serialVersionUID = -8972037557065076781L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  private UUID id;

  @Exclude
  @NonNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "trade_id", nullable = false, updatable = false)
  private Trade trade;

  @NotNull
  @Enumerated
  @Column(name = "order_type", nullable = false, updatable = false)
  private OrderType orderType;

  @NotNull
  @Positive
  @Column(name = "open_price", nullable = false, updatable = false, precision = 20, scale = 10)
  private BigDecimal openPrice;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "open_time", nullable = false)
  private LocalDateTime openTime;

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
    final OpenPosition that = (OpenPosition) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
