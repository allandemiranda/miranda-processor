package lu.forex.system.fx.requests;

import jakarta.validation.Valid;
import lu.forex.system.fx.dtos.InitCandlestickDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/candlesticks")
public interface CandlestickRequest {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  InitCandlestickDto insetInitCandlestick(@RequestBody @Valid InitCandlestickDto newCandlestickDto);

}
