import dispositivos.DispositivoEletrico;
import sensores.SensorEnergia;

public class App {
    public static void main(String[] args) {

        DispositivoEletrico lampada = new DispositivoEletrico("Lampada", 60, 5);

        SensorEnergia sensor = new SensorEnergia("S1", lampada, 127, 0.5);

        sensor.registrarLeitura();
    }
}