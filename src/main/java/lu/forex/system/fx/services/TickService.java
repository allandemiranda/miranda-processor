package lu.forex.system.fx.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.enums.Symbol;
import lu.forex.system.fx.exceptions.LastTickNotFound;
import lu.forex.system.fx.mappers.TickMapper;
import lu.forex.system.fx.models.Tick;
import lu.forex.system.fx.providers.TickProvider;
import lu.forex.system.fx.repository.TickRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickService implements TickProvider {

  private final TickRepository tickRepository;
  private final TickMapper tickMapper;

  @Override
  public TickDto getLastTick(final Symbol symbol) {
    return this.getTickRepository().getFirstBySymbol(symbol).map(this.getTickMapper()::toDto).orElseThrow(LastTickNotFound::new);
  }

  @Override
  public void updateTickData(final @NonNull TickDto currentTick) {
    final Symbol symbol = currentTick.symbol();
    final Tick last = this.getTickRepository().getFirstBySymbol(symbol).orElseThrow(LastTickNotFound::new);
    this.getTickRepository().delete(last);
    final Tick current = this.getTickMapper().toEntity(currentTick);
    this.getTickRepository().save(current);
  }

}
