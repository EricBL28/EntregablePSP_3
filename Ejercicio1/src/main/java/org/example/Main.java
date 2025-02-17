package org.example;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;


public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Introduce el nombre del fichero que deseas cifrar:");
        String nombrefichero= sc.nextLine();
        File fichero=new File(nombrefichero);

        while (!fichero.exists()) {
            System.out.println("El fichero no existe, introduce uno valido");
            nombrefichero= sc.nextLine();
            fichero=new File(nombrefichero);
        }

        System.out.println("El fichero existe");

        System.out.println("Ahora introduce la smeilla");


        try{
            SecureRandom secureRandom = SecureRandom.getInstance("AES");


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }
}