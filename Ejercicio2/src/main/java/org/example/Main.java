package org.example;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    // Algoritmo de cifrado
    private static final String ALGORITMO = "RSA";
    private static String nombrefichero = "";

    // Nombre del archivo cifrado
    private static final String ficheroEncriptado = "encriptado.zip";
    private static final String USO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // Claves RSA
    private static PrivateKey clavePrivada;
    private static PublicKey clavePublica;

    public static void main(String[] args)  {
        // Pedir al usuario el nombre del fichero a cifrar
        System.out.println("Introduce el nombre del fichero que deseas cifrar:");
        nombrefichero = sc.nextLine();
        File fichero = new File(nombrefichero);

        // Comprobar si el fichero existe, si no, pedir otro
        while (!fichero.exists()) {
            System.out.println("El fichero no existe, introduce uno válido:");
            nombrefichero = sc.nextLine();
            fichero = new File(nombrefichero);
        }

        // Pedir una semilla para generar las claves RSA
        System.out.println("El fichero existe. Introduce la semilla:");
        String SEMILLA = sc.nextLine();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(SEMILLA.getBytes());

        // Generar las claves pública y privada con la semilla dada
        generarLlave(secureRandom);


        encriptarFichero();
        descifrarFichero();

        System.out.println("Cifrado asimétrico completado con éxito.");
    }

    public static void encriptarFichero() {
        System.out.println("Encriptando el fichero utilizando " + USO + ": " + ficheroEncriptado);

        try (FileInputStream inputStream = new FileInputStream(nombrefichero);
             FileOutputStream outputStream = new FileOutputStream(ficheroEncriptado)) {

            // Crear el cifrador con el algoritmo especificado
            Cipher cifrador = Cipher.getInstance(USO);

            // Inicializarlo en modo ENCRIPTACIÓN con la clave pública
            cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);

            byte[] buffer = new byte[62]; // Tamaño máximo permitido para RSA con OAEP y 1024 bits
            int bytesLeidos;

            // Leer el archivo en bloques de 62 bytes y cifrar cada bloque
            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                byte[] bufferCifrado = cifrador.doFinal(buffer, 0, bytesLeidos);
                outputStream.write(bufferCifrado); // Guardar el bloque cifrado en el archivo de salida
            }

        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Archivo cifrado con éxito.");
    }

    public static void descifrarFichero() {
        System.out.println("Desencriptando el fichero utilizando " + USO + ": " + ficheroEncriptado);

        String ficheroDesencriptado = "desencriptado.zip";
        try (FileInputStream inputStream = new FileInputStream(ficheroEncriptado);
             FileOutputStream outputStream = new FileOutputStream(ficheroDesencriptado)) {

            // Crear el cifrador con el mismo algoritmo
            Cipher cifrador = Cipher.getInstance(USO);

            // Inicializarlo en modo DESCIFRADO con la clave privada
            cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);

            byte[] buffer = new byte[128]; // Tamaño máximo para RSA con 1024 bits
            int bytesLeidos;

            // Leer el archivo cifrado en bloques y descifrarlo
            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                byte[] bufferClaro = cifrador.doFinal(buffer, 0, bytesLeidos);
                outputStream.write(bufferClaro); // Guardar el bloque descifrado en el archivo de salida
            }

        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Archivo descifrado con éxito.");
    }

    public static void generarLlave(SecureRandom secureRandom) {
        System.out.println("Generando las claves privada y pública con RSA");
        try {
            // Crear un generador de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITMO);

            // Configurar el tamaño de clave en 1024 bits y la semilla
            keyGen.initialize(1024, secureRandom);

            // Generar las claves
            KeyPair key = keyGen.generateKeyPair();

            // Guardar la clave privada y pública en variables globales
            clavePrivada = key.getPrivate();
            clavePublica = key.getPublic();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
