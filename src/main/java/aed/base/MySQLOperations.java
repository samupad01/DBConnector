package aed.base;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.CallableStatement;

public class MySQLOperations {

    public static void mostrarTabla(Connection connection) throws SQLException {
    	//INNER JOIN para poder recoger datos de diferentes tablas
        String query = "SELECT P.Codproducto, P.Denoproducto, P.PrecioBase, P.Codfamilia, P.Congelado, F.Denofamilia " +
                       "FROM PRODUCTO P " +
                       "JOIN FAMILIA F ON P.Codfamilia = F.Codfamilia";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        //Formateo de la salida por pantalla de la tabla
        System.out.println("Codproducto | Denoproducto | PrecioBase | Codfamilia | Congelado | Denofamilia");
        System.out.println("------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.println(rs.getInt("Codproducto") + " | " + 
                               rs.getString("Denoproducto") + " | " +
                               rs.getDouble("PrecioBase") + " | " + 
                               rs.getInt("Codfamilia") + " | " + 
                               rs.getBoolean("Congelado") + " | " +
                               rs.getString("Denofamilia"));
        }

        rs.close();
        stmt.close();
    }
    
    public static void insertarProducto(Connection connection, Scanner sc) throws SQLException {
        System.out.println("Introduce los datos del producto:");
        sc.nextLine();  // Limpiar el buffer aquí
        System.out.print("Denoproducto: ");
        String denoproducto = sc.nextLine();

        double precioBase = 0.0;
        boolean precioValido = false;
        while(!precioValido) {
            try {
                System.out.print("PrecioBase: ");
                precioBase = sc.nextDouble();
                precioValido = true;
            } catch (InputMismatchException e) {
                System.out.println("Por favor, introduce un número válido para PrecioBase.");
                sc.next(); // limpiar el buffer
            }
        }
        sc.nextLine();  // Limpiar el buffer

        System.out.print("Codfamilia: ");
        int codfamilia = sc.nextInt();
        sc.nextLine();  // Limpiar el buffer

        System.out.print("Congelado (true o false): ");
        boolean congelado = sc.nextBoolean();
        sc.nextLine();  // Limpiar el buffer

        if (!isValidCodFamilia(connection, codfamilia)) {
            System.out.println("El Codfamilia introducido no existe.");
            return;
        }

        String query = "INSERT INTO PRODUCTO (Denoproducto, PrecioBase, Codfamilia, Congelado) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, denoproducto);
        ps.setDouble(2, precioBase);
        ps.setInt(3, codfamilia);
        ps.setBoolean(4, congelado);
        ps.executeUpdate();
        System.out.println("Producto insertado con éxito.");
    }

    private static boolean isValidCodFamilia(Connection connection, int codfamilia) throws SQLException {
        String query = "SELECT * FROM FAMILIA WHERE Codfamilia = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, codfamilia);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
    public static void eliminarProducto(Connection connection, Scanner sc) {
        try {
            System.out.println("Introduce el código del producto a eliminar:");
            
            // Validación de entrada:
            while (!sc.hasNextInt()) {
                System.out.println("Por favor, introduce un número válido.");
                sc.nextLine();  // Limpia la entrada inválida
            }
            
            int codigoProducto = sc.nextInt();
            sc.nextLine();  // Limpia el buffer
            
            String queryCheckStock = "SELECT COUNT(*) AS total FROM STOCK WHERE Codproducto = ?";
            PreparedStatement pstmtCheckStock = connection.prepareStatement(queryCheckStock);
            pstmtCheckStock.setInt(1, codigoProducto);

            ResultSet rsStock = pstmtCheckStock.executeQuery();
            rsStock.next();
            int stockCount = rsStock.getInt("total");

            if (stockCount > 0) {
                System.out.println("El producto tiene stock asociado. ¿Desea eliminar todos los stocks? (Sí/No)");
                String decision = sc.nextLine().trim().toLowerCase();

                if ("sí".equals(decision) || "si".equals(decision)) {
                    String queryDeleteStock = "DELETE FROM STOCK WHERE Codproducto = ?";
                    PreparedStatement pstmtDeleteStock = connection.prepareStatement(queryDeleteStock);
                    pstmtDeleteStock.setInt(1, codigoProducto);
                    pstmtDeleteStock.executeUpdate();
                } else {
                    System.out.println("No se ha eliminado el producto ya que tiene stock asociado.");
                    return;
                }
            }

            String queryDeleteProduct = "DELETE FROM PRODUCTO WHERE Codproducto = ?";
            PreparedStatement pstmtDeleteProduct = connection.prepareStatement(queryDeleteProduct);
            pstmtDeleteProduct.setInt(1, codigoProducto);

            int affectedRows = pstmtDeleteProduct.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Producto eliminado con éxito.");
            } else {
                System.out.println("No se encontró el producto especificado.");
            }

        } catch (Exception e) {
            System.out.println("Error al intentar eliminar el producto.");
            e.printStackTrace();
        }
        }
        
        public static void modificarProducto(Connection connection, Scanner sc) {
            try {
                System.out.println("Introduce el código del producto a modificar:");
                int codigoProducto = sc.nextInt();
                sc.nextLine();  // Limpia el buffer
                
                String queryCheckProduct = "SELECT * FROM PRODUCTO WHERE Codproducto = ?";
                PreparedStatement pstmtCheckProduct = connection.prepareStatement(queryCheckProduct);
                pstmtCheckProduct.setInt(1, codigoProducto);
                ResultSet rs = pstmtCheckProduct.executeQuery();

                if (rs.next()) {
                    String denoproducto = rs.getString("Denoproducto");
                    BigDecimal precioBase = rs.getBigDecimal("PrecioBase");
                    int codfamilia = rs.getInt("Codfamilia");
                    boolean congelado = rs.getBoolean("Congelado");
                    
                    System.out.println("Datos actuales del producto:");
                    System.out.println("Denoproducto: " + denoproducto);
                    System.out.println("PrecioBase: " + precioBase);
                    System.out.println("Codfamilia: " + codfamilia);
                    System.out.println("Congelado: " + congelado);
                    
                    System.out.println("Introduce los nuevos datos (pulsa enter para no modificar):");
                    
                    System.out.println("Denoproducto:");
                    String newDenoproducto = sc.nextLine().trim();
                    System.out.println("PrecioBase:");
                    String newPrecioBaseStr = sc.nextLine().trim();
                    System.out.println("Codfamilia:");
                    String newCodfamiliaStr = sc.nextLine().trim();
                    System.out.println("Congelado (true/false):");
                    String newCongeladoStr = sc.nextLine().trim().toLowerCase();
                    
                    String updateQuery = "UPDATE PRODUCTO SET Denoproducto = ?, PrecioBase = ?, Codfamilia = ?, Congelado = ? WHERE Codproducto = ?";
                    PreparedStatement pstmtUpdate = connection.prepareStatement(updateQuery);
                    
                    pstmtUpdate.setString(1, newDenoproducto.isEmpty() ? denoproducto : newDenoproducto);
                    pstmtUpdate.setBigDecimal(2, newPrecioBaseStr.isEmpty() ? precioBase : new BigDecimal(newPrecioBaseStr));
                    pstmtUpdate.setInt(3, newCodfamiliaStr.isEmpty() ? codfamilia : Integer.parseInt(newCodfamiliaStr));
                    pstmtUpdate.setBoolean(4, newCongeladoStr.isEmpty() ? congelado : Boolean.parseBoolean(newCongeladoStr));
                    pstmtUpdate.setInt(5, codigoProducto);
                    
                    pstmtUpdate.executeUpdate();
                    System.out.println("Producto modificado con éxito.");
                } else {
                    System.out.println("No se encontró el producto especificado.");
                }

            } catch (Exception e) {
                System.out.println("Error al intentar modificar el producto.");
                e.printStackTrace();
            }
             
            
        }
        
        public static void insertarProductoConVerificacion(Connection connection, Scanner sc) throws SQLException {
            System.out.println("Introduce los datos del producto:");

            int codproducto = solicitInt("Codproducto: ", sc);
            String denoproducto = solicitString("Denoproducto: ", sc);
            double precioBase = solicitDouble("PrecioBase: ", sc);
            int codfamilia = solicitInt("Codfamilia: ", sc);
            int congelado = solicitInt("Congelado (1 para sí, 0 para no): ", sc);

            while (congelado != 0 && congelado != 1) {
                System.out.println("Por favor, introduce 1 para 'congelado' o 0 para 'no congelado'.");
                congelado = solicitInt("Congelado (1 para sí, 0 para no): ", sc);
            }

            if (!isValidCodFamilia(connection, codfamilia)) {
                System.out.println("El Codfamilia introducido no existe. Producto no insertado.");
                return;
            }

            String storedProcedure = "{call insertarProducto(?, ?, ?, ?, ?, ?)}"; // Añadido un parámetro extra para el parámetro de salida
            CallableStatement cs = connection.prepareCall(storedProcedure);
            cs.setInt(1, codproducto);
            cs.setString(2, denoproducto);
            cs.setDouble(3, precioBase);
            cs.setInt(4, codfamilia);
            cs.setInt(5, congelado);
            cs.registerOutParameter(6, Types.INTEGER); // Registro del parámetro de salida

            cs.execute();

            int insercionExitosa = cs.getInt(6); // Recuperar el valor del parámetro de salida

            if (insercionExitosa == 1) {
                System.out.println("Producto insertado con éxito usando el procedimiento almacenado.");
            } else {
                System.out.println("Hubo un problema al insertar el producto usando el procedimiento almacenado.");
            }
        }

        private static int solicitInt(String prompt, Scanner sc) {
            while(true) { 
                try {
                    System.out.print(prompt);
                    int number = sc.nextInt();
                    sc.nextLine();  // Limpiar el buffer
                    return number; 
                } catch (InputMismatchException e) {
                    System.out.println("Por favor, introduce un número válido.");
                    sc.next(); // limpiar el buffer
                }
            }
        }

        private static double solicitDouble(String prompt, Scanner sc) {
            while(true) { 
                try {
                    System.out.print(prompt);
                    double number = sc.nextDouble();
                    sc.nextLine();  // Limpiar el buffer
                    return number; 
                } catch (InputMismatchException e) {
                    System.out.println("Por favor, introduce un número válido.");
                    sc.next(); // limpiar el buffer
                }
            }
        }

        private static String solicitString(String prompt, Scanner sc) {
            System.out.print(prompt);
            return sc.nextLine();
        }
        public static void listarProductosPorFamilia(Connection connection, Scanner sc) throws SQLException {
            System.out.print("Introduce el nombre de la familia: ");
            String familiaDenominacion = sc.nextLine();

            String storedProcedure = "{call ListarProductosPorFamilia(?)}";
            CallableStatement cs = connection.prepareCall(storedProcedure);
            
            // Configurar el parámetro de entrada
            cs.setString(1, familiaDenominacion);

            // Ejecutar el procedimiento almacenado y obtener un ResultSet
            ResultSet rs = cs.executeQuery();

            // Procesar el ResultSet
            while(rs.next()) {
                String denoproducto = rs.getString("Denoproducto");
                double precioBase = rs.getDouble("PrecioBase");
                int congelado = rs.getInt("Congelado");
                
                String congeladoTexto = (congelado == 1) ? "Sí" : "No";
                System.out.printf("Producto: %s | Precio Base: %.2f | Congelado: %s%n", denoproducto, precioBase, congeladoTexto);
            }

            rs.close();
            cs.close();
        }
        
        public static void ObtenerInformacionFamilia(Connection conn) throws SQLException {
            Scanner sc = new Scanner(System.in);
            
            // Solicitar los datos al usuario
            System.out.println("Introduce el nombre de la familia:");
            String denoFamilia = sc.nextLine();
            
            System.out.println("Introduce el precio máximo:");
            double precioMax;
            while (true) {
                try {
                    precioMax = Double.parseDouble(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduce un número válido.");
                }
            }
            
            sc.close();

            // Crear el CallableStatement
            String sql = "{CALL ObtenerInformacionFamilia(?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                
                // Establecer los parámetros de entrada
                cs.setString(1, denoFamilia);
                cs.setDouble(2, precioMax);
                
                // Registrar los parámetros de salida
                cs.registerOutParameter(3, Types.INTEGER); // cantidad total de productos
                cs.registerOutParameter(4, Types.INTEGER); // cantidad de productos bajo precio congelados
                
                // Ejecutar el procedimiento almacenado
                cs.execute();
                
                // Recuperar y mostrar los resultados
                int cantidadTotalProductos = cs.getInt(3);
                int cantidadProductosBajoPrecioCongelados = cs.getInt(4);
                System.out.println("Cantidad total de productos: " + cantidadTotalProductos);
                System.out.println("Cantidad de productos congelados bajo precio: " + cantidadProductosBajoPrecioCongelados);
            }
        }
        
        
        
        
        public static void SumaUnidadesProductoPorCodigoPostal(Connection connection) {
            Scanner sc = new Scanner(System.in);

            // Solicitar datos por teclado
            System.out.print("Introduce el código del producto: ");
            int codProducto = sc.nextInt();

            System.out.print("Introduce el código postal: ");
            String codigoPostal = sc.next();
            sc.close();
            try {
                String query = "SELECT SumaUnidadesProductoPorCodigoPostal(?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, codProducto);
                ps.setString(2, codigoPostal);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int totalUnidades = rs.getInt(1);
                    System.out.println("Total de unidades para el producto " + codProducto + " en el código postal " + codigoPostal + " es: " + totalUnidades);
                } else {
                    System.err.println("No se pudo obtener el resultado de la función.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

}


