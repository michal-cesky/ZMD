package enums;

public enum PsnrTypeMethod {

    MAE("MAE"),
    MSE("MSE"),
    PSNR("PSNR"),
    SAE("SAE");

    String name;

    PsnrTypeMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
