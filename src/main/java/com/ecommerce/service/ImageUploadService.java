package com.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ImageUploadService {
    
    private final String uploadDir = "uploads/produits/";
    
    public ImageUploadService() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    public String uploadImage(MultipartFile file, String nomProduit) throws IOException {
        // Nettoyer le nom
        String nomPropre = nomProduit.toLowerCase()
                .replaceAll("[éèêë]", "e")
                .replaceAll("[àâä]", "a")
                .replaceAll("[ôö]", "o")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
        
        // Date et heure
        LocalDateTime maintenant = LocalDateTime.now();
        String date = maintenant.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String heure = maintenant.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        // Récupérer l'extension
        String extension = "";
        String nomOriginal = file.getOriginalFilename();
        if (nomOriginal != null && nomOriginal.contains(".")) {
            extension = nomOriginal.substring(nomOriginal.lastIndexOf("."));
        } else {
            extension = ".jpg";
        }
        
        // Nom du fichier
        String nomFichier = nomPropre + "_" + date + "_" + heure + extension;
        
        // Sauvegarder
        Path chemin = Paths.get(uploadDir + nomFichier);
        Files.write(chemin, file.getBytes());
        
        return "/uploads/produits/" + nomFichier;
    }
}