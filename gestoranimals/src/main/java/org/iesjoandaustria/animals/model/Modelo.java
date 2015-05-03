package org.iesjoandaustria.animals.model;
import org.iesjoandaustria.animals.vista.*;
import org.iesjoandaustria.animals.control.*;
import java.sql.*;
import java.util.ArrayList;
public class Modelo {
    private Connection conn = null;    // connexió

    public Modelo(){
        try{
            connecta();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

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

    public void list() throws SQLException{
        String sql = "SELECT DISTINCT categoria FROM ANIMALS ORDER BY categoria";
        Statement st = null;
        Statement an = null;
        int nCategoria = 0;
        int nAnimals = 0;
        ArrayList<Animal> array = null;
        Animal a = null;
        int i = 0;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                array = new ArrayList<Animal>();
                an = conn.createStatement();
                System.out.println("* "+rs.getString("categoria")+":");
                sql = "SELECT nom FROM ANIMALS WHERE categoria = '"+rs.getString("categoria")+"' ORDER BY nom";
                ResultSet animals = an.executeQuery(sql);
                while(animals.next()){
                    a = new Animal(animals.getString("nom"),rs.getString("categoria"));
                    array.add(a);
                    nAnimals++;
                }
                //System.out.println("\t -"+animals.getString("nom")+"");
                if (nAnimals==0){
                   System.out.println(Vista.categoriaNoTrobada); 
                }else{
                    for (i=0; i<array.size();i++) {
                        System.out.println("\t -"+array.get(i).getNom()+""); 
                    }
                }
                
                nCategoria++;
            }
            if (nCategoria==0) System.out.println(Vista.animalNoTrobat);
            rs.close();
        } finally {
            if (st != null) { st.close(); }
        }
    }
    public void list_cat(String s) throws SQLException{
        String sql = "SELECT nom,categoria FROM ANIMALS where categoria='"+s+"'ORDER BY nom";
        Statement st = null;
        ArrayList<Animal> array = null;
        Animal a = null;
        int i = 0;
        try {
            array = new ArrayList<Animal>();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            System.out.println("* "+s+":");
            int nAnimals=0;
            while (rs.next()) {
                a = new Animal(rs.getString("nom"),rs.getString("categoria"));
                array.add(a);
                nAnimals++;
            }
            if (nAnimals==0){
                System.out.println("Cap animal");
            }else{
                for (i=0; i<array.size();i++) {
                    System.out.println("\t -"+array.get(i).getNom()+"");
                }
            }
            rs.close();
        } finally {
            if (st != null) { st.close(); }
        }
    }
    public void add_animal(String n,String c) throws SQLException{
        String sql_comprova = "SELECT * from ANIMALS where nom = '"+n+"' AND categoria = '"+c+"'";
        int animals_dins =0;
        Statement comprova = null;
        try{
            comprova = conn.createStatement();
            ResultSet rs = comprova.executeQuery(sql_comprova);
            while(rs.next()){
                animals_dins++;
            }
            if (animals_dins>0) {
                System.out.println(Vista.animalNomRepetit);
            }else{
                Animal animal = new Animal(n,c);
                String sql = "INSERT INTO ANIMALS(nom,categoria) VALUES ('"+animal.getNom()+"','"+animal.getCategoria()+"')";
                Statement st = null;
                try {
                    st = conn.createStatement();
                    st.executeUpdate(sql);
                } finally {
                    if (st != null) { st.close(); }
                } 
            }
            
        }finally{
           if (comprova != null) { comprova.close(); } 
        }
        
    }
    public void del_animal(String n,String c) throws SQLException{
        System.out.println("Es procedirà a eliminar l'animal "+n+" de la categoria "+c+" Segur? (S|n):");
        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
        if (segur.equals("S")|| segur.equals("s")) {
            String sql = "DELETE FROM ANIMALS WHERE nom = '"+n+"' AND categoria = '"+c+"'";
            Statement st = null;
            try {
                st = conn.createStatement();
                st.executeUpdate(sql);
            } finally {
                if (st != null) { st.close(); }
            }System.out.println(Vista.respostaSiIdentificatUnicAnimali);
        }else{
            System.out.println(Vista.respostaAltraIdentificatUnicAnimali);
        }

    }
    public void del_animal(String n) throws SQLException{
        String sql = "SELECT * FROM ANIMALS where nom = '"+n+"'";
        Statement num_animals = null;
        String categoria = null;
            try {
                num_animals = conn.createStatement();
                ResultSet num = num_animals.executeQuery(sql);
                int count=0;
                while (num.next()){
                    count++;
                    categoria = num.getString("categoria");
                }
                if (count>1) {
                    System.out.println("S'ha trobat aquest nom d'animal en les següents categories:");
                    sql = "SELECT categoria FROM ANIMALS where nom = '"+n+"'";
                    Statement categories = null;
                    categories = conn.createStatement();
                    ResultSet cat = categories.executeQuery(sql);
                    while (cat.next()) {
                        System.out.println("- "+cat.getString("categoria"));
                    }
                    System.out.println("Per favor, expliciteu la categoria:");
                    String resposta = IOUtils.llegeixStr();
                    sql = "SELECT * FROM ANIMALS where nom = '"+n+"' AND categoria = '"+resposta+"'";
                    Statement animalF = null;
                    animalF = conn.createStatement();
                    ResultSet animal = animalF.executeQuery(sql);
                    int nAnimalsF=0;
                    String nom=null;
                    categoria=null;
                    while (animal.next()) {
                        nAnimalsF++;
                        nom = animal.getString("nom");
                        categoria = animal.getString("categoria");
                    }
                    if (nAnimalsF==0) {
                        System.out.println(Vista.animalNoTrobat);
                    }else{
                        System.out.println(Vista.retornaAnimalIdentificat(nom, categoria));
                        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
                        if (segur.equals("S")|| segur.equals("s")) {
                            sql = "DELETE FROM ANIMALS WHERE nom = '"+nom+"' AND categoria = '"+categoria+"'";
                            Statement st = null;
                            try {
                                st = conn.createStatement();
                                st.executeUpdate(sql);
                            } finally {
                                if (st != null) { st.close(); }
                            }
                            System.out.println(Vista.respostaSiIdentificatUnicAnimali);
                        }else{
                            System.out.println(Vista.respostaAltraIdentificatUnicAnimali);
                        }
                    }
                }else{
                    
                    System.out.println(Vista.retornaAnimalIdentificat(n, categoria));
                        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
                        if (segur.equals("S")|| segur.equals("s")) {
                            sql = "DELETE FROM ANIMALS WHERE nom = '"+n+"' AND categoria = '"+categoria+"'";
                            Statement st = null;
                            try {
                                st = conn.createStatement();
                                st.executeUpdate(sql);
                            } finally {
                                if (st != null) { st.close(); }
                            }
                            System.out.println(Vista.respostaSiIdentificatUnicAnimali);
                        }else{
                            System.out.println(Vista.respostaAltraIdentificatUnicAnimali);
                        }
                }
            } finally {
                if (num_animals != null) { num_animals.close(); }
            }
    }
    public void mod_animal(String n,String c,String c2) throws SQLException{
        
        System.out.println(Vista.retornaAnimalIdentificat(n,c,c2));
        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
        if (segur.equals("S")|| segur.equals("s")) {
            String sql = "UPDATE ANIMALS SET categoria = '"+c2+"' WHERE categoria = '"+c+"' AND nom = '"+n+"' ";
            Statement st = null;
            try {
                st = conn.createStatement();
                st.executeUpdate(sql);
            } finally {
                if (st != null) { st.close(); }
            }
            System.out.println(Vista.respostaSiAnimalIdentificat);
        }else{
            System.out.println(Vista.respostaAltraAnimalIdentificat);
        }
    }
    public void mod_animal(String n,String c) throws SQLException{
        String resposta=null;
        String sql = "SELECT * FROM ANIMALS where nom = '"+n+"'";
        Statement num_animals = null;
            try {
                num_animals = conn.createStatement();
                ResultSet num = num_animals.executeQuery(sql);
                int count= 0;
                while (num.next()){
                    count++;
                    resposta = num.getString("categoria");
                }
                if (count>1) {
                    System.out.println("S'ha trobat aquest nom d'animal en les següents categories:");
                    sql = "SELECT categoria FROM ANIMALS where nom = '"+n+"'";
                    Statement categories = null;
                    categories = conn.createStatement();
                    ResultSet cat = categories.executeQuery(sql);
                    while (cat.next()) {
                        System.out.println("- "+cat.getString("categoria"));
                    }
                    System.out.println("Per favor, expliciteu la categoria:");
                    resposta = IOUtils.llegeixStr();
                    sql = "SELECT * FROM ANIMALS where nom = '"+n+"' AND categoria = '"+resposta+"'";
                    Statement animalF = null;
                    animalF = conn.createStatement();
                    ResultSet animal = animalF.executeQuery(sql);
                    int nAnimalsF=0;
                    String nom=null;
                    String categoria=null;
                    while (animal.next()) {
                        nAnimalsF++;
                        nom = animal.getString("nom");
                        categoria = animal.getString("categoria");
                    }
                    if (nAnimalsF==0) {
                        System.out.println(Vista.animalNoTrobat);
                    }else{
                        System.out.println(Vista.retornaAnimalIdentificat(n,resposta,c));
                        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
                        if (segur.equals("S")|| segur.equals("s")) {
                            sql = "UPDATE ANIMALS SET categoria = '"+c+"' WHERE categoria = '"+resposta+"' AND nom = '"+n+"' ";
                            Statement st = null;
                            try {
                                st = conn.createStatement();
                                st.executeUpdate(sql);
                            } finally {
                                if (st != null) { st.close(); }
                            }
                            System.out.println(Vista.respostaSiAnimalIdentificat);
                        }else{
                            System.out.println(Vista.respostaAltraAnimalIdentificat);
                        }
                    }
                }else{
                    System.out.println(Vista.retornaAnimalIdentificat(n,resposta,c));
                        String segur = org.iesjoandaustria.animals.control.IOUtils.llegeixStr();
                        if (segur.equals("S")|| segur.equals("s")) {
                            sql = "UPDATE ANIMALS SET categoria = '"+c+"' WHERE categoria = '"+resposta+"' AND nom = '"+n+"' ";
                            Statement st = null;
                            try {
                                st = conn.createStatement();
                                st.executeUpdate(sql);
                            } finally {
                                if (st != null) { st.close(); }
                            }
                            System.out.println(Vista.respostaSiAnimalIdentificat);
                        }else{
                            System.out.println(Vista.respostaAltraAnimalIdentificat);
                        }
                }
            } finally {
                if (num_animals != null) { num_animals.close(); }
            }
        }
}
