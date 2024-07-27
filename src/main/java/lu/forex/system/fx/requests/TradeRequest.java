package lu.forex.system.fx.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.fx.dtos.InitTradeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/trades")
public interface TradeRequest {

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  InitTradeDto insertTrade(final @NotNull @Valid InitTradeDto initTradeDto);

}
