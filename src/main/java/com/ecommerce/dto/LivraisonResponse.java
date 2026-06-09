package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LivraisonResponse {
    private Long id;
    private String reference;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateTerminaison;
    private List<CommandeLivraisonDto> commandes;
    private boolean success;
    private String message;
    
    @Data
    @Builder
    public static class CommandeLivraisonDto {
        private Long id;
        private String reference;
        private Double montantTotal;
        private String clientNom;
        private String clientEmail;
        private String adresseLivraison;
    }
}