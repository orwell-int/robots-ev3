package orwell.tank.hardware.Colours;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public enum EnumColours {
    NONE,
    RED,
    GREEN,
    BLUE;

    private static EnumColours[] vals = values();

    public EnumColours next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
