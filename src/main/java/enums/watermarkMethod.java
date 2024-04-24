package enums;

public enum watermarkMethod {

    LSB("LSB"),
    DCT("DCT");

    String name;

    watermarkMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Sampling: " + name;
    }
}
