package enums;

public enum SsimTypeMtehod {

    SSIM("SSIM"),
    MSSIM("MSSIM");

    String name;

    SsimTypeMtehod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
