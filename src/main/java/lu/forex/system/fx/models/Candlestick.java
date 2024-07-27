package lu.forex.system.fx.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.fx.enums.SignalIndicator;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.enums.TimeFrame;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@ToString
@Table(name = "candlestick", indexes = {@Index(name = "idx_candlestick_symbol", columnList = "symbol, time_frame")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_candlestick_symbol", columnNames = {"symbol", "time_frame", "timestamp"})})
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 4315986352855862872L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @Enumerated
  @Column(name = "symbol", nullable = false, updatable = false)
  private Symbol symbol;

  @NotNull
  @Enumerated
  @Column(name = "time_frame", nullable = false, updatable = false)
  private TimeFrame timeFrame;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @NotNull
  @Embedded
  @Column(name = "body", nullable = false, updatable = false)
  private CandlestickBody body;

  @NotNull
  @OneToOne(cascade = CascadeType.PERSIST, optional = false, orphanRemoval = true)
  @JoinColumn(name = "adx_id", nullable = false, unique = true, updatable = false)
  private AverageDirectionalIndex adx;

  @NotNull
  @OneToOne(cascade = CascadeType.PERSIST, optional = false, orphanRemoval = true)
  @JoinColumn(name = "rsi_id", nullable = false, unique = true)
  private RelativeStrengthIndex rsi;

  @NotNull
  @Enumerated
  @Column(name = "signal_indicator", nullable = false)
  private SignalIndicator signalIndicator;

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
    final Candlestick that = (Candlestick) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}