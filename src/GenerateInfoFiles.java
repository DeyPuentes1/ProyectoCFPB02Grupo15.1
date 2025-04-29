import utils.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Clase encargada de generar archivos de prueba para un sistema de gestión de ventas.
 * Genera vendedores, productos y archivos de ventas (CSV y serializados).
 **/
public class GenerateInfoFiles {

    private static final Random random = new Random();

    private static final String[] vendorNames = {
            "Maria Perez", "Juan Martinez", "Ana Gomez", "Luis Rodriguez",
            "Carmen Sanchez", "Pedro Garcia", "Laura Díaz", "Jorge Hernandez",
            "Sofía Lopez", "Miguel Torres"
    };

    private static final String[] productNames = {
            "Celular", "Tablet", "Computador", "Monitor", "Impresora",
            "Teclado", "Mouse", "Audifonos", "Camara", "Router"
    };
    private static final Map<Long, String> vendedoresMap = new HashMap<>();
    private static final Set<String> productosValidos = new HashSet<>();

    /**
     * Método principal que genera todos los archivos de prueba necesarios.
     **/
    public static void main(String[] args) {
        try {
            createProductsFile(10);
            createSalesManInfoFile(10);

            cargarVendedores();
            cargarProductos();

            for (Map.Entry<Long, String> entry : vendedoresMap.entrySet()) {
                long id = entry.getKey();
                createSalesMenFile(5 + random.nextInt(5), id);
                createSerializedSalesFile(5 + random.nextInt(5),  id);
            }

            System.out.println("Archivos de prueba generados exitosamente");
        } catch (Exception e) {
            System.err.println("Error al generar archivos de prueba, detalle: " + e.getMessage());
        }
    }

    /**
     * Genera un archivo CSV con información de vendedores.
     * @param quantitySellers Número de vendedores a generar.
     * @throws IOException si ocurre un error de escritura.
     **/
    public static void createSalesManInfoFile(int quantitySellers) throws IOException {
        Path filePath = Constants.DATA_FOLDER.resolve("vendedores.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("TipoDocumento;NumeroDocumento;Nombres;Apellidos\n");
            Set<Long> salesmenIds = new HashSet<>();

            for (int i = 0; i < quantitySellers; i++) {
                long id;
                do {
                    id = 100000 + random.nextInt(900000);
                } while (!salesmenIds.add(id));

                String[] nameComplete = vendorNames[i % vendorNames.length].split(" ");
                writer.write(String.format("%s;%d;%s;%s\n", Constants.DOCUMENT_TYPE, id, nameComplete[0], nameComplete[1]));
            }

            System.out.println("Archivo vendedores generado exitosamente");
        }
    }

    /**
     * Genera un archivo CSV con productos ficticios.
     * @param quantityProducts Cantidad de productos a generar.
     * @throws IOException si ocurre un error de escritura.
     **/
    public static void createProductsFile(int quantityProducts) throws IOException {
        Path filePath = Constants.DATA_FOLDER.resolve("productos.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("ProductoId;Nombre;Precio\n");
            Set<String> products = new HashSet<>();

            for (int i = 0; i < quantityProducts; i++) {
                String idProduct;
                do {
                    idProduct = "P" + (100 + random.nextInt(900));
                } while (!products.add(idProduct));

                int price = 10000 + random.nextInt(990000);
                writer.write(String.format("%s;%s;%d\n", idProduct, productNames[i % productNames.length], price));
            }

            System.out.println("Archivo productos generado exitosamente");
        }
    }

    /**
     * Genera un archivo CSV de ventas por vendedor.
     *
     * @param ventasAleatorias Cantidad de ventas a generar.
     * @param id Identificador del vendedor.
     * @throws IOException si ocurre un error de escritura.
     **/
    public static void createSalesMenFile(int ventasAleatorias, long id) throws IOException {
        if (!vendedoresMap.containsKey(id)) {
            System.err.println("Vendedor no registrado: " + id);
            return;
        }

        Path filePath = Constants.DATA_FOLDER.resolve("ventas_" + id + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("ProductoId;Cantidad\n");

            for (int i = 0; i < ventasAleatorias; i++) {
                String idProducto;
                do {
                    idProducto = "P" + (100 + random.nextInt(900));
                } while (!productosValidos.contains(idProducto));

                int cantidad = 1 + random.nextInt(50);
                writer.write(idProducto + ";" + cantidad + "\n");
            }

            System.out.println("Archivo ventas_" + id + ".csv generado con éxito");
        }
    }

    /**
     * Genera un archivo serializado de ventas por vendedor.
     * @param randomSales Cantidad de ventas a generar aleatoriamente.
     * @param id Identificador del vendedor.
     * @throws IOException si ocurre un error de escritura.
     **/
    public static void createSerializedSalesFile(int randomSales,  long id) throws IOException {
        if (!vendedoresMap.containsKey(id)) {
            System.err.println("Vendedor no registrado: " + id);
            return;
        }

        Path filePath = Constants.DATA_FOLDER.resolve("ventas_" + id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            List<String> lines = new ArrayList<>();
            lines.add(Constants.DOCUMENT_TYPE + ";" + id);

            for (int i = 0; i < randomSales; i++) {
                String idProducto;
                do {
                    idProducto = "P" + (100 + random.nextInt(900));
                } while (!productosValidos.contains(idProducto));

                int cantidad = 1 + random.nextInt(20);
                lines.add(idProducto + ";" + cantidad);
            }

            oos.writeObject(lines);
        }
    }

    public static void cargarVendedores() throws IOException {
        Path path = Constants.DATA_FOLDER.resolve("vendedores.csv");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                long id = Long.parseLong(parts[1]);
                String name = parts[2] + " " + parts[3];
                vendedoresMap.put(id, name);
            }
        }
    }

    public static void cargarProductos() throws IOException {
        Path path = Constants.DATA_FOLDER.resolve("productos.csv");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                productosValidos.add(parts[0]);
            }
        }
    }

    public static void procesarArchivosVentas() throws IOException {
        File folder = Constants.DATA_FOLDER.toFile();
        File[] files = folder.listFiles((dir, name) -> name.startsWith("ventas_") && name.endsWith(".csv"));
        if (files == null) return;

        for (File file : files) {
            System.out.println("Procesando: " + file.getName());
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine(); // Saltar encabezado: ProductoId;Cantidad

                // Extrae ID del nombre del archivo, ej: ventas_123456.csv
                String fileName = file.getName();
                String idStr = fileName.substring("ventas_".length(), fileName.indexOf(".csv"));

                long numeroDocumento;
                try {
                    numeroDocumento = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    System.err.println("Nombre de archivo inválido: " + fileName);
                    continue;
                }

                if (!vendedoresMap.containsKey(numeroDocumento)) {
                    System.err.println("Vendedor no registrado: " + numeroDocumento);
                    continue;
                }

                String venta;
                while ((venta = reader.readLine()) != null) {
                    String[] ventaParts = venta.split(";");
                    if (ventaParts.length != 2) {
                        System.err.println("Formato inválido en línea: " + venta + " en archivo: " + fileName);
                        continue;
                    }

                    String productoId = ventaParts[0];
                    int cantidad;

                    if (!productosValidos.contains(productoId)) {
                        System.err.println("Producto no registrado: " + productoId + " en archivo: " + fileName);
                        continue;
                    }

                    try {
                        cantidad = Integer.parseInt(ventaParts[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Cantidad inválida: " + ventaParts[1] + " en archivo: " + fileName);
                        continue;
                    }

                    // Aquí podrías almacenar, acumular o mostrar la venta
                    System.out.printf("Venta procesada - Vendedor: %d, Producto: %s, Cantidad: %d%n",
                            numeroDocumento, productoId, cantidad);
                }

                System.out.println("Archivo procesado correctamente: " + fileName);
            }
        }
    }


    public static void procesarArchivosVentasSerializados() throws IOException, ClassNotFoundException {
        File folder = Constants.DATA_FOLDER.toFile();
        File[] files = folder.listFiles((dir, name) -> name.startsWith("ventas_") && name.endsWith(".ser"));
        if (files == null) return;

        for (File file : files) {
            System.out.println("Procesando archivo serializado: " + file.getName());
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (!(obj instanceof List)) {
                    System.err.println("Contenido inválido en archivo: " + file.getName());
                    continue;
                }

                List<?> lines = (List<?>) obj;
                if (lines.size() < 2) {
                    System.err.println("Encabezado faltante en archivo: " + file.getName());
                    continue;
                }

                String tipoDocLine = lines.get(0).toString();
                String nombreLine = lines.get(1).toString();

                String[] docParts = tipoDocLine.split(";");
                String[] nameParts = nombreLine.split(";");

                if (docParts.length != 2 || nameParts.length != 2) {
                    System.err.println("Encabezado inválido en archivo: " + file.getName());
                    continue;
                }

                long numeroDocumento = Long.parseLong(docParts[1]);

                if (!vendedoresMap.containsKey(numeroDocumento)) {
                    System.err.println("Vendedor no encontrado: " + numeroDocumento);
                    continue;
                }

                for (int i = 2; i < lines.size(); i++) {
                    String[] ventaParts = lines.get(i).toString().split(";");
                    if (ventaParts.length != 2 || !productosValidos.contains(ventaParts[0])) {
                        System.err.println("Venta inválida o producto no registrado en archivo: " + file.getName());
                    }
                }

                System.out.println("Archivo serializado procesado correctamente: " + file.getName());
            }
        }
    }

    /**
     * Muestra el contenido de todos los archivos serializados (.ser) en consola.
     **/
    public static void visualizarArchivosSerializados() {
        File folder = Constants.DATA_FOLDER.toFile();
        File[] files = folder.listFiles((dir, name) -> name.startsWith("ventas_") && name.endsWith(".ser"));
        if (files == null) return;

        for (File file : files) {
            System.out.println("**** Contenido de " + file.getName() + " *****");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    List<?> lines = (List<?>) obj;
                    for (Object line : lines) {
                        System.out.println(line);
                    }
                } else {
                    System.out.println("El contenido no es una lista valida");
                }
            } catch (Exception e) {
                System.err.println("Error leyendo " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    // Mapa global para almacenar la cantidad de productos vendidos
    private static final Map<String, Integer> productosVendidos = new HashMap<>();

    /**
     * Genera un archivo CSV con el reporte de ventas por vendedor, ordenado por la cantidad de dinero recaudado.
     * @throws IOException si ocurre un error de escritura.
     **/
    public static void generateVendorSalesReport() throws IOException {
        Path filePath = Constants.DATA_FOLDER.resolve("reporte_vendedores.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("Vendedor;TotalRecaudado\n");

            // Calcula la cantidad total recaudada por cada vendedor
            Map<Long, Double> ventasPorVendedor = new HashMap<>();
            File folder = Constants.DATA_FOLDER.toFile();
            File[] files = folder.listFiles((dir, name) -> name.startsWith("ventas_") && name.endsWith(".csv"));
            if (files != null) {
                for (File file : files) {
                    long idVendedor = Long.parseLong(file.getName().substring("ventas_".length(), file.getName().indexOf(".csv")));
                    double totalVentas = 0;
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        reader.readLine(); // Saltar encabezado
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(";");
                            String productoId = parts[0];
                            int cantidad = Integer.parseInt(parts[1]);
                            double precio = getProductPrice(productoId);
                            totalVentas += cantidad * precio;

                            // Actualizar cantidad de productos vendidos
                            productosVendidos.put(productoId, productosVendidos.getOrDefault(productoId, 0) + cantidad);
                        }
                    }

                    ventasPorVendedor.put(idVendedor, totalVentas);
                }
            }

            // Ordenar vendedores por total recaudado (de mayor a menor)
            List<Map.Entry<Long, Double>> vendedorList = new ArrayList<>(ventasPorVendedor.entrySet());
            vendedorList.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

            // Escribir los datos en el archivo CSV
            for (Map.Entry<Long, Double> entry : vendedorList) {
                writer.write(String.format("%s;%.2f\n", vendedoresMap.get(entry.getKey()), entry.getValue()));
            }

            System.out.println("Reporte de vendedores generado con éxito");
        }
    }

    public static void generateProductSalesReport() throws IOException {
        // Mapa para almacenar la cantidad total vendida de cada producto
        Map<String, Integer> productosVendidos = new HashMap<>();

        // Procesar los archivos de ventas para acumular la cantidad vendida por producto
        File folder = Constants.DATA_FOLDER.toFile();
        File[] files = folder.listFiles((dir, name) -> name.startsWith("ventas_") && name.endsWith(".csv"));
        if (files == null) return;

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine(); // Saltar encabezado: ProductoId;Cantidad
                String venta;
                while ((venta = reader.readLine()) != null) {
                    String[] ventaParts = venta.split(";");
                    if (ventaParts.length != 2) {
                        System.err.println("Formato inválido en línea: " + venta + " en archivo: " + file.getName());
                        continue;
                    }

                    String productoId = ventaParts[0];
                    int cantidad;
                    try {
                        cantidad = Integer.parseInt(ventaParts[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Cantidad inválida: " + ventaParts[1] + " en archivo: " + file.getName());
                        continue;
                    }

                    // Acumular las ventas por producto
                    productosVendidos.put(productoId, productosVendidos.getOrDefault(productoId, 0) + cantidad);
                }
            }
        }

        // Crear el archivo de reporte CSV
        Path filePath = Constants.DATA_FOLDER.resolve("reporte_productos.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("ProductoId;Nombre;CantidadVendida;Precio\n");

            // Obtener los productos y ordenarlos por cantidad vendida de mayor a menor
            List<Map.Entry<String, Integer>> listaProductos = new ArrayList<>(productosVendidos.entrySet());
            listaProductos.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())); // Orden descendente por cantidad

            // Escribir los productos al archivo CSV
            for (Map.Entry<String, Integer> entry : listaProductos) {
                String productoId = entry.getKey();
                int cantidadVendida = entry.getValue();

                // Buscar el nombre y el precio del producto usando el ID
                String nombreProducto = getProductNameById(productoId);
                int precioProducto = getProductPriceById(productoId);

                writer.write(String.format("%s;%s;%d;%d\n", productoId, nombreProducto, cantidadVendida, precioProducto));
            }

            System.out.println("Reporte de productos generado exitosamente");
        }
    }

    // Método para obtener el nombre del producto por su ID
    private static String getProductNameById(String productId) throws IOException {
        Path path = Constants.DATA_FOLDER.resolve("productos.csv");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine(); // Saltar encabezado: ProductoId;Nombre;Precio
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(productId)) {
                    return parts[1];
                }
            }
        }
        return "Desconocido";
    }

    // Método para obtener el precio del producto por su ID
    private static int getProductPriceById(String productId) throws IOException {
        Path path = Constants.DATA_FOLDER.resolve("productos.csv");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine(); // Saltar encabezado: ProductoId;Nombre;Precio
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(productId)) {
                    return Integer.parseInt(parts[2]);
                }
            }
        }
        return 0;
    }


    /**
     * Obtiene el precio del producto basado en su ID.
     * @param productoId El ID del producto.
     * @return El precio del producto.
     **/
    private static double getProductPrice(String productoId) {
        return 10000 + random.nextInt(990000);
    }


    public static Map<Long, String> getVendedoresMap() {
        return vendedoresMap;
    }

}
