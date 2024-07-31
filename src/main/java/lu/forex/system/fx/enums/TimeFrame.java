package lu.forex.system.fx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum TimeFrame {
  //@formatter:off
  M15(15, Frame.MINUTE),
  M30(30, Frame.MINUTE),
  H1(1, Frame.HOUR),
  ;
  //@formatter:on

  private final int timeValue;
  @NonNull
  private final Frame frame;

  @Getter
  @AllArgsConstructor
  public enum Frame {
    MINUTE(1), HOUR(60 * MINUTE.minutes);

    private final int minutes;
  }
}
