package lu.forex.system.fx.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.fx.dtos.CandlestickDto;
import lu.forex.system.fx.dtos.InitCandlestickDto;
import lu.forex.system.fx.dtos.TickDto;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface CandlestickProvider {

  @NonNull
  @Transactional()
  Collection<CandlestickDto> getCandlesticks(final @NonNull TickDto currentTick);

  @NotNull
  @Transactional()
  InitCandlestickDto insertInitCandlestick(final @NonNull InitCandlestickDto initCandlestickDto);

}
