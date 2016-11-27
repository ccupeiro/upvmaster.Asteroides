package org.masterupv.carloscupeiro.asteroides.servicios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 27/11/2016.
 */

public class ServidorPuntuaciones {

    public static void main(String args[]) {
        List<String> puntuaciones = new ArrayList<String>();
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando conexiones...");
            while (true) {
                Socket cliente = s.accept();
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(cliente.getInputStream()));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(cliente.getOutputStream()), true);
                String datos = entrada.readLine();
                if (datos.equals("PUNTUACIONES")) {
                    for (int n = 0; n < puntuaciones.size(); n++) {
                        salida.println(puntuaciones.get(n));
                    }
                } else {
                    puntuaciones.add(0, datos);
                    salida.println("OK");
                }
                cliente.close();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
