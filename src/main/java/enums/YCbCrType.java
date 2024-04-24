package enums;

public enum YCbCrType {
    Y("Y"),
    Cb("Cb"),
    Cr("Cr");

    String name;

    YCbCrType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
