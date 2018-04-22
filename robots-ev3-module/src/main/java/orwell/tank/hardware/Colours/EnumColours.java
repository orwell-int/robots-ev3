package orwell.tank.hardware.Colours;

public enum EnumColours {
    NONE,
    RED,
    GREEN,
    BLUE,
    YELLOW,
    ORANGE,
    PURPLE;

    private static final EnumColours[] vals = values();

    public EnumColours next() {
        return vals[(ordinal() + 1) % vals.length];
    }
}
