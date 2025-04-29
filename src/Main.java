import java.io.IOException;
import java.util.Map;

/**
 * Clase principal encargada de ejecutar la lógica del programa:
 * 1. Generar archivos de prueba.
 * 2. Procesar y validar archivos de ventas (CSV y serializados).
 **/
public class Main {

    /**
     * Método principal de ejecución.
     * ejecuta los métodos de generación de datos de prueba de la clase GenerateInfoFiles.
     * @param args no se utilizan argumentos desde consola.
     **/
    public static void main(String[] args) {
        try {
            ejecutarMenuGeneracionArchivosPrueba();
            System.out.println("Procesamiento finalizado exitosamente");

        } catch (Exception e) {
            System.err.println("Error inesperado, detalle: " + e.getMessage());
        }
    }

    /**
     * Ejecuta la generación de archivos de prueba pseudoaleatorios.
     * Este método llama a los métodos definidos en la clase GenerateInfoFiles.
     **/
    private static void ejecutarMenuGeneracionArchivosPrueba() {
        try {
            GenerateInfoFiles.createProductsFile(5);
            GenerateInfoFiles.createSalesManInfoFile(5);
            GenerateInfoFiles.cargarVendedores();
            GenerateInfoFiles.cargarProductos();

            // Genera archivos de ventas para todos los vendedores registrados
            for (Map.Entry<Long, String> vendedor : GenerateInfoFiles.getVendedoresMap().entrySet()) {
                long id = vendedor.getKey();

                GenerateInfoFiles.createSalesMenFile(3, id);
                GenerateInfoFiles.createSerializedSalesFile(3, id);
            }

            // Valida archivos generados y visualizar los archivos generados en consola
            GenerateInfoFiles.procesarArchivosVentas();
            GenerateInfoFiles.procesarArchivosVentasSerializados();
            GenerateInfoFiles.visualizarArchivosSerializados();

            System.out.println("Archivos de prueba generados y validados correctamente");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error generando o procesando archivos de prueba, detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

