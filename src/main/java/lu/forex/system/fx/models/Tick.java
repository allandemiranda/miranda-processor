package lu.forex.system.fx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.fx.enums.Symbol;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tick", indexes = {@Index(name = "idx_tick_symbol", columnList = "symbol")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_tick_symbol_timestamp", columnNames = {"symbol", "timestamp"})})
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8438859761579040733L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "symbol", nullable = false, updatable = false)
  private Symbol symbol;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @NotNull
  @Positive
  @Column(name = "bid", nullable = false, updatable = false, precision = 20, scale = 10)
  private BigDecimal bid;

  @NotNull
  @Positive
  @Column(name = "ask", nullable = false, updatable = false, precision = 20, scale = 10)
  private BigDecimal ask;

  @NotNull
  @NegativeOrZero
  public BigDecimal getSpread() {
    return this.getBid().subtract(this.getAsk());
  }

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
    final Tick tick = (Tick) o;
    return getId() != null && Objects.equals(getId(), tick.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
