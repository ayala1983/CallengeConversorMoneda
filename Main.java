import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsultaTipoDeMoneda consulta = new ConsultaTipoDeMoneda();

        while (true) {
            System.out.println("===== CONVERSOR DE MONEDAS =====");
            System.out.println("Bienvenido seleccione una opcion");
            System.out.println("1) Dólar => Peso Argentino");
            System.out.println("2) Peso Argentino => Dólar");
            System.out.println("3) Dólar => Real Brasileño");
            System.out.println("4) Real Brasileño => Dólar");
            System.out.println("5) Dólar => Peso Colombiano");
            System.out.println("6) Peso Colombiano => Dólar");
            System.out.println("7) Salir");
            System.out.println("8) Ver historial");
            System.out.print("Elija una opción válida: ");

            int opcion = scanner.nextInt();

            if (opcion == 7) {
                System.out.println("Gracias por usar el conversor. ¡Hasta luego!");
                break;
            }

            if (opcion == 8) {
                try (BufferedReader reader = new BufferedReader(new FileReader("logs/historial.txt"))) {
                    System.out.println("\n===== HISTORIAL DE CONVERSIONES =====");
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        System.out.println(linea);
                    }
                    System.out.println("======================================\n");
                } catch (IOException e) {
                    System.out.println("No se pudo leer el historial.");
                    consulta.logError("Error al leer historial: " + e.getMessage());
                }
                continue;
            }

            String from = "";
            String to = "";

            switch (opcion) {
                case 1 -> { from = "USD"; to = "ARS"; }
                case 2 -> { from = "ARS"; to = "USD"; }
                case 3 -> { from = "USD"; to = "BRL"; }
                case 4 -> { from = "BRL"; to = "USD"; }
                case 5 -> { from = "USD"; to = "COP"; }
                case 6 -> { from = "COP"; to = "USD"; }
                default -> {
                    System.out.println("Opción no válida. Intente nuevamente.");
                    continue;
                }
            }

            try {
                double tasa = consulta.obtenerCambio(from, to);
                System.out.print("Ingrese el monto a convertir: ");
                double monto = scanner.nextDouble();

                TipoDeCambio cambio = new TipoDeCambio(from, to, tasa);
                double resultado = cambio.convertir(monto);

                System.out.printf("Resultado: %.2f %s = %.2f %s\n", monto, from, resultado, to);
                consulta.guardarHistorial(from, to, monto, resultado, tasa);

            } catch (TipoDeMonedaNoEncontradaException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error inesperado.");
                consulta.logError("Error inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
