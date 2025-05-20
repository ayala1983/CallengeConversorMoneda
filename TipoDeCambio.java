public class TipoDeCambio {
    private final String monedaOrigen;
    private final String monedaDestino;
    private final double tasaCambio;

    public TipoDeCambio(String monedaOrigen, String monedaDestino, double tasaCambio) {
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
        this.tasaCambio = tasaCambio;
    }

    public double convertir(double cantidad) {
        return cantidad * tasaCambio;
    }
}
