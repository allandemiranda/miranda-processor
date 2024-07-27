package lu.forex.system.fx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum TimeFrame {
  //@formatter:off
  M15(15, Frame.MINUTE),
  M30(2 * M15.getTimeValue(), Frame.MINUTE),
  H1(1, Frame.HOUR),
  H2(2 * H1.getTimeValue(), Frame.HOUR),
  H4(4 * H1.getTimeValue(), Frame.HOUR),
  H8(8 * H1.getTimeValue(), Frame.HOUR)
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
