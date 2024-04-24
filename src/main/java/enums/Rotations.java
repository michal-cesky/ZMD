package enums;

public enum Rotations {

    R45("45"),
    R90("90");

    String name;

    Rotations(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Sampling: " + name;
    }
}
