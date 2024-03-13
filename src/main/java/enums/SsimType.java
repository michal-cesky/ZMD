package enums;

public enum SsimType {
    Y("Y"),
    Cb("Cb"),
    Cr("Cr");

    String name;

    SsimType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
