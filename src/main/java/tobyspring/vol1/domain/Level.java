package tobyspring.vol1.domain;

public enum Level {
  GOLD(3, null),
  SILVER(2, GOLD),
  BASIC(1, SILVER);


  private final int value;
  private final Level next;

  Level(final int value, final Level next) {
    this.value = value;
    this.next = next;
  }

  public int intValue() {
    return value;
  }

  public Level nextLevel() {
    return this.next;
  }

  public static Level valueOf(int value) {
    return switch (value) {
      case 1 -> BASIC;
      case 2 -> SILVER;
      case 3 -> GOLD;
      default -> throw new AssertionError("Unknown Level value: " + value);
    };
  }

}
