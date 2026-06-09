package com.ecommerce.dto;

import lombok.Data;

@Data
public class PaiementRequest {
    private String modePaiement;
    private String numeroTelephone;
    private String referencePaiement;
}