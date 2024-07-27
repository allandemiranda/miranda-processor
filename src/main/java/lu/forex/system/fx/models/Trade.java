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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@ToString
@Table(name = "trade", indexes = {@Index(name = "idx_trade_symbol_time_frame", columnList = "symbol, time_frame, slot_week")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_trade_symbol_time_frame", columnNames = {"symbol", "time_frame", "stop_loss", "take_profit", "slot_week", "slot_start", "slot_end"})})
public class Trade implements Serializable {

  @Serial
  private static final long serialVersionUID = 5378555306820100916L;

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
  @Enumerated(EnumType.STRING)
  @Column(name = "time_frame", nullable = false, updatable = false)
  private TimeFrame timeFrame;

  @Negative
  @Column(name = "stop_loss", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.INTEGER)
  private int stopLoss;

  @Positive
  @Column(name = "take_profit", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.INTEGER)
  private int takeProfit;

  @Enumerated(EnumType.STRING)
  @Column(name = "slot_week", nullable = false, updatable = false)
  private DayOfWeek slotWeek;

  @NonNull
  @Column(name = "slot_start", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotStart;

  @NotNull
  @Column(name = "slot_end", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotEnd;

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
    final Trade trade = (Trade) o;
    return getId() != null && Objects.equals(getId(), trade.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
