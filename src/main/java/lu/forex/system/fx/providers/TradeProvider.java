package lu.forex.system.fx.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lu.forex.system.fx.dtos.CandlestickDto;
import lu.forex.system.fx.dtos.TradeDto;
import org.springframework.transaction.annotation.Transactional;

public interface TradeProvider {

  @NotNull
  @Transactional(readOnly = true)
  Optional<@NotNull TradeDto> getTrade(final @NotNull CandlestickDto candlestickDto);

}
