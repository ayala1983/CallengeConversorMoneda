import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ConsultaTipoDeMoneda {
    private static final String API_KEY = "4916c649d4499f02adcf5d7c";

    public double obtenerCambio(String from, String to) throws TipoDeMonedaNoEncontradaException {
        try {
            String urlStr = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", API_KEY, from);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder resultado = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                resultado.append(linea);
            }
            reader.close();

            JSONObject json = new JSONObject(resultado.toString());
            if (!json.getString("result").equals("success")) {
                throw new TipoDeMonedaNoEncontradaException("No se encontró la moneda: " + from);
            }
            JSONObject rates = json.getJSONObject("conversion_rates");
            if (!rates.has(to)) {
                throw new TipoDeMonedaNoEncontradaException("No se encontró la moneda destino: " + to);
            }
            return rates.getDouble(to);
        } catch (IOException e) {
            logError("Error de conexión: " + e.getMessage());
            throw new TipoDeMonedaNoEncontradaException("Error al conectar con la API.");
        }
    }

    public void logError(String mensaje) {
        try (FileWriter fw = new FileWriter("logs/errores.log", true)) {
            fw.write(LocalDateTime.now() + " - " + mensaje + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el error en el log.");
        }
    }

    public void guardarHistorial(String from, String to, double montoOriginal, double montoConvertido, double tasa) {
        try {
            // Asegura que la carpeta 'logs' exista
            File carpeta = new File("logs");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            FileWriter writer = new FileWriter("logs/historial.txt", true); // modo append
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String ahora = dtf.format(LocalDateTime.now());
            String linea = String.format("[%s] %.2f %s => %.2f %s | Tasa: %.4f\n",
                    ahora, montoOriginal, from, montoConvertido, to, tasa);
            writer.write(linea);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            logError("No se pudo escribir en el historial: " + e.getMessage());
        }
    }

}
