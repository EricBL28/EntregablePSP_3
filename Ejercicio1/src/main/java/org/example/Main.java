package org.example;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final String ALGORITMO = "AES";
    private static String nombrefichero = "";
    private static final String ficheroEncriptado = "encriptado.zip";

    private static SecretKey key;


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
        secureRandom.setSeed(SEMILLA.getBytes());//Se usa la semilla para generar la clave

        key = generarLlave(secureRandom);
        System.out.println("Clave generada:" + mostrarBytes(key.getEncoded())); //por curiosdad ver la clave que se genera

        encriptarFichero();
        descifrarFichero();
    }

    public static void encriptarFichero() {
        System.out.println("Encriptando el fichero utilizando AES: " + ficheroEncriptado);

        try (FileInputStream inputStream = new FileInputStream(nombrefichero);
             FileOutputStream outputStream = new FileOutputStream(ficheroEncriptado)) {

            int bytesLeidos;

            Cipher cifrador = Cipher.getInstance(ALGORITMO);
            //Se inicializa el cifrador en modo CIFRADO o ENCRIPTACIÓN
            cifrador.init(Cipher.ENCRYPT_MODE, key);

            byte[] buffer = new byte[1000]; //array de bytes
            byte[] bufferCifrado;

            //lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
            bytesLeidos = inputStream.read(buffer, 0, 1000);
            while (bytesLeidos != -1) {//mientras no se llegue al final del fichero
                //pasa texto claro al cifrador y lo cifra, asignándolo a bufferCifrado
                bufferCifrado = cifrador.update(buffer, 0, bytesLeidos);
                outputStream.write(bufferCifrado); //Graba el texto cifrado en fichero
                bytesLeidos = inputStream.read(buffer, 0, 1000);
            }
            bufferCifrado = cifrador.doFinal(); //Completa el cifrado
            outputStream.write(bufferCifrado); //Graba el final del texto cifrado, si lo hay


        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Archivo cifrado con éxito.");

    }

    public static void descifrarFichero() {
        System.out.println("Encriptando el fichero utilizando AES: " + ficheroEncriptado);

        String ficheroDesencriptado = "desencriptado.zip";
        try (FileInputStream inputStream = new FileInputStream(ficheroEncriptado);
             FileOutputStream outputStream = new FileOutputStream(ficheroDesencriptado)) {

            int bytesLeidos;

            Cipher cifrador = Cipher.getInstance(ALGORITMO);
            //3.- Poner cifrador en modo DESCIFRADO o DESENCRIPTACIÓN
            cifrador.init(Cipher.DECRYPT_MODE, key);
            System.out.println("Desencpripto el fichero: " + ficheroEncriptado + ", utilizando AES : " + ficheroDesencriptado);

            byte[] bufferClaro;
            byte[] buffer = new byte[1000]; //array de bytes
            //lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
            bytesLeidos = inputStream.read(buffer, 0, 1000);
            while (bytesLeidos != -1) {//mientras no se llegue al final del fichero
                //pasa texto cifrado al cifrador y lo descifra, asignándolo a bufferClaro
                bufferClaro = cifrador.update(buffer, 0, bytesLeidos);
                outputStream.write(bufferClaro); //Graba el texto claro en fichero
                bytesLeidos = inputStream.read(buffer, 0, 1000);
            }
            bufferClaro = cifrador.doFinal(); //Completa el descifrado
            outputStream.write(bufferClaro); //Graba el final del texto claro, si lo hay


        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Archivo cifrado con éxito.");

    }

    public static SecretKey generarLlave(SecureRandom secureRandom) {
        System.out.println("Genero la clave secreta con AES");
        SecretKey clave = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITMO);
            keyGen.init(128, secureRandom);
            clave = keyGen.generateKey();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        return clave;
    }

    public static String mostrarBytes(byte[] buffer) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(buffer);
        //System.out.write(buffer, 0, buffer.length);
    }
}
