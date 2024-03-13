package enums;

public enum PsnrType {

    Red("Red"),
    Green("Green"),
    Blue("Blue"),
    Y("Y"),
    Cb("Cb"),
    Cr("Cr"),
    RGB("RGB"),
    YCbCr("YCbCr");

    String name;

    PsnrType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

