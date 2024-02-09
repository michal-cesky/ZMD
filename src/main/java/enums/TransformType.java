package enums;

public enum TransformType {

    DCT("DCT"),
    WHT("WHT");

    String name;

    TransformType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Transform: "+ name;
    }
}
