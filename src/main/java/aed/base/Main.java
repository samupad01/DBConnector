package aed.base;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Seleccione la base de datos:");
        System.out.println("1. MySQL");
        System.out.println("2. SQL Server");

        int option = sc.nextInt();
        sc.nextLine(); // Limpia el buffer

        DatabaseConnector connector = null;
        Connection connection = null;

        try {
            switch (option) {
                case 1:
                    connector = new MySQLConnector();
                    connection = connector.connect();
                    System.out.println("Conexión establecida con éxito a MySQL!");
                    break;
                case 2:
                    connector = new SQLServerConnector();
                    connection = connector.connect();
                    System.out.println("Conexión establecida con éxito a SQL Server!");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return; // Salimos del programa si la opción no es válida
            }

            boolean continuar = true;
            while (continuar) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Mostrar Tabla");
                System.out.println("2. Insertar Producto");
                System.out.println("3. Eliminar Producto");
                System.out.println("4. Modificar Producto");
                System.out.println("5. Insertar Producto con Verificación (PROCEDIMIENTO)");
                System.out.println("6. Listar productos por familia(PROCEDIMIENTO)");
                System.out.println("7. Obtener información por familia(PROCEDIMIENTO)");
                System.out.println("8. FUNCIÓN");
                System.out.println("9. Salir");

                int actionOption = sc.nextInt();
                sc.nextLine(); // Limpia el buffer

                switch (actionOption) {
                    case 1:
                        MySQLOperations.mostrarTabla(connection);
                        break;
                    case 2:
                        MySQLOperations.insertarProducto(connection, sc);
                        break;
                    case 3:
                        MySQLOperations.eliminarProducto(connection, sc);
                        break;
                    case 4:
                        MySQLOperations.modificarProducto(connection, sc);
                        break;
                    case 5:
                        MySQLOperations.insertarProductoConVerificacion(connection, sc);
                        break;
                    case 6:
                        MySQLOperations.listarProductosPorFamilia(connection, sc);
                        break;
                    case 7:
                        MySQLOperations.ObtenerInformacionFamilia(connection);
                        break;
                    case 8:
                        MySQLOperations.SumaUnidadesProductoPorCodigoPostal(connection);
                        break;
                    case 9:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al establecer la conexión.");
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sc.close(); // Cierra el scanner al finalizar
    }
}







