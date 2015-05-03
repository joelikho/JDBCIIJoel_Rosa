package org.iesjoandaustria.animals;
import java.sql.*;
public class UsaAnimal {
    private Connection conn = null;    // connexió

    private void connecta() throws SQLException {
        if (conn==null) {
            String usuari   = "joelikho";
            String password = "12345";
            String host     = "192.168.56.101";
            String bd       = "testbd";
            String url      = "jdbc:postgresql://" + host + "/" + bd;
            conn = DriverManager.getConnection(url, usuari, password);
            System.out.println("Connectat amb " + url);
        }
    }

    // tanca la connexió en cas que estigui connectada
    private void desconnecta() {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) {}
            System.out.println("Desconnectat");
            conn = null;
        }
    }

    // crea la taula d'animals
    private void creaTaula() throws SQLException {
        eliminaTaula(); // eliminem si ja existia
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql =
                "CREATE TABLE  ANIMALS (" +
                "       id        SERIAL PRIMARY KEY," +
                "       nom       TEXT,              " +
                "       categoria VARCHAR(40))";
            st.executeUpdate(sql);
            System.out.println("Creada taula ANIMALS");
        } finally {
            if (st != null) { st.close(); }
        }
    }

    // elimina la taula d'animals si existeix
    private void eliminaTaula() throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS ANIMALS";
            st.executeUpdate (sql);
            System.out.println("Eliminada taula ANIMALS");
        } finally {
            if (st != null) { st.close(); }
        }
    }

    // mostra la llista d'animals
    private void consulta() throws SQLException {
        String sql = "SELECT * FROM ANIMALS ORDER BY nom";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int nAnimals = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String cat = rs.getString("categoria");
                Animal animal = new Animal(id, nom, cat);
                System.out.println(animal);
                nAnimals++;
            }
            if (nAnimals==0) System.out.println("Cap animal de moment");
            rs.close();
        } finally {
            if (st != null) { st.close(); }
        }
    }

    // retorna el nombre d'animals que hi ha insertats
    private void comptaAnimals() throws SQLException {
        String SELECT_SQL = "SELECT COUNT(*) FROM Animals";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(SELECT_SQL);
            rs.next();
            System.out.println("El nombre d'animals a la bd és: " + rs.getInt(1));
        } finally {
            if (st != null) { st.close(); }
        }
    }

    // afegeix un animal a la taula i obté l'id generat
    private void afegeixAnimal() throws SQLException {
        // crea l'animal
        Animal a = new Animal("canari", "ocell");
        // crea la comanda
        String sql = "INSERT INTO ANIMALS (nom, categoria) values ('"
            + a.getNom()
            + "', '"
            + a.getCategoria()
            + "')";
        // envia la comanda
        Statement st = null;
        try {
            st = conn.createStatement();
            int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Nombre d'animals afegits: " + num);
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            a.setId(id);        // assignem l'id de l'animal
            System.out.println("Afegit l'animal " + a);
            rs.close();
        } finally {
            if (st != null) { st.close(); }
        }
    }

    // afegeix uns quants animals a la taula
    // Ho fa com a transacció (o tots o cap)
    // Fa servir un PreparedStatement per optimitzar repetides crides

    private void afegeixMoltsAnimals() throws SQLException {
        // plantilla de la sentència d'inserció
        String sql = "INSERT INTO Animals (nom, categoria) values (?,?)";

        // crea els animals
        Animal[] llista = {
            new Animal("estruç", "ocell"),
            new Animal("kiwi", "ocell"),
            new Animal("gos", "mamifer"),
            new Animal("bacallà", "peix"),
            new Animal("dofí", "peix")
        };

        PreparedStatement ps = null;
        // crea la sentència a executar (només un cop!)
        try {
            ps = conn.prepareStatement(sql);
            // obté l'estat anterior de l'autocommit.
            boolean anteriorAutoCommit = conn.getAutoCommit();
            try {
                // fem que no faci autocommit a cada execució
                conn.setAutoCommit(false);

                // afegeix cada animal de la llista
                for (Animal a: llista) {
                    // afegim els valors a insertar
                    ps.setString(1, a.getNom());       // primer camp
                    ps.setString(2, a.getCategoria()); // segon camp
                    ps.executeUpdate();
                    System.out.println("Afegit l'animal " + a);
                }
                // si no hi ha problemes accepta tot
                conn.commit();
            } catch (SQLException e) {
                // trobat problemes amb la inserció: tot enrere
                conn.rollback();
            } finally {
                // tornem l'estat de autocomit tal i com estava
                conn.setAutoCommit(anteriorAutoCommit);
            }
        } finally {
            if (ps != null) { ps.close(); }
        }
    }

    private void corregeixAnimals() throws SQLException {
        // tenim el dofí en una categoria equivocada. Canviem-la a la BD
        String sql = "UPDATE Animals set categoria = 'mamifer' WHERE nom = 'dofí'";
        Statement st = null;
        try {
            st = conn.createStatement();
            int num = st.executeUpdate(sql);
            System.out.println("Modificat animals: " + num);
        } finally {
            if (st != null) { st.close(); }
        }
    }

    private void eliminaOcells() throws SQLException {
        // volem eliminar tots els ocells de la bd
        String DELETE_SQL = "DELETE FROM Animals WHERE categoria = 'ocell'";
        Statement st = null;
        try {
            st = conn.createStatement();
            int num = st.executeUpdate(DELETE_SQL);
            System.out.println("Eliminats ocells: " + num);
        } finally {
            if (st != null) { st.close(); }
        }
    }
}