package lu.forex.system.fx.requests;

import jakarta.validation.Valid;
import lu.forex.system.fx.dtos.TickDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/ticks")
public interface TickRequests {

  @PutMapping()
  @ResponseStatus(HttpStatus.OK)
  String updateTickAndGetOpenPosition(final @RequestBody @Valid TickDto currentTick);

}
