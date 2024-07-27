package lu.forex.system.fx.exceptions;

import java.time.LocalDateTime;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TickTimestampOlderException extends RuntimeException {

  public TickTimestampOlderException(final LocalDateTime timestamp, final String symbolName) {
    super(String.format("Tick %s with %s is older than last one", symbolName, timestamp));
  }
}