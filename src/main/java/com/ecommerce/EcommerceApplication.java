//fonctionne comme le bouton de demarrage de mon backend

package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
        System.out.println(" Backend démarré sur http://localhost:8080");
    }
}
