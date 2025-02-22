package org.example;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final String ALGORITMO = "RSA";
    private static String nombrefichero = "";
    private static final String ficheroEncriptado = "encriptado.zip";

    private static final String USO ="RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static PrivateKey clavePrivada;
    private static PublicKey clavePublica;


    public static void main(String[] args)  {
        System.out.println("Introduce el nombre del fichero que deseas cifrar:");
        nombrefichero = sc.nextLine();
        File fichero = new File(nombrefichero);

        while (!fichero.exists()) {
            System.out.println("El fichero no existe, introduce uno válido:");
            nombrefichero = sc.nextLine();
            fichero = new File(nombrefichero);
        }

        System.out.println("El fichero existe. Introduce la semilla:");
        String SEMILLA = sc.nextLine();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(SEMILLA.getBytes());

        generarLlave(secureRandom);

        encriptarFichero();
        descifrarFichero();
        System.out.println("Cifrado asimetrico completado con éxito.");
    }

    public static void encriptarFichero() {
        System.out.println("Encriptando el fichero utilizando " + USO + ": " + ficheroEncriptado);

        try (FileInputStream inputStream = new FileInputStream(nombrefichero);
             FileOutputStream outputStream = new FileOutputStream(ficheroEncriptado)) {

            Cipher cifrador = Cipher.getInstance(USO);
            cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);

            byte[] buffer = new byte[62]; // Tamaño máximo permitido
            int bytesLeidos;

            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                byte[] bufferCifrado = cifrador.doFinal(buffer, 0, bytesLeidos);
                outputStream.write(bufferCifrado);
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

            Cipher cifrador = Cipher.getInstance(USO);
            cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);

            byte[] buffer = new byte[128];
            int bytesLeidos;

            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                byte[] bufferClaro = cifrador.doFinal(buffer, 0, bytesLeidos);
                outputStream.write(bufferClaro);
            }

        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Archivo descifrado con éxito.");
    }


    public static void generarLlave(SecureRandom secureRandom) {
        System.out.println("Genero la claves privada y publica con RSA");
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITMO);
            keyGen.initialize(1024,secureRandom);
            KeyPair key = keyGen.generateKeyPair();

            clavePrivada= key.getPrivate();
            clavePublica= key.getPublic();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

}
