package org.iesjoandaustria.animals.control;
import java.io.*;
public class IOUtils {
    private static BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));
    // retorna un enter llegit de entrada estàndard. -1 si error
    public static int llegeixInt() {
        int resposta = -1;
        try {
            resposta = Integer.parseInt(llegeixStr());
        } catch (Exception e) {}
        return resposta;
    }
    // retorna un String llegit de entrada estàndard. "" si error.
    public static String llegeixStr() {
        String resposta = "";
        try {
            resposta = br.readLine();
        } catch (Exception e) {}
        return resposta;
    }
}
