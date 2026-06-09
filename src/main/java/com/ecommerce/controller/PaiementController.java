package com.ecommerce.controller;

import com.ecommerce.service.KPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {
    
    private final KPayService kpayService;
    
    @PostMapping("/initier")
    public ResponseEntity<Map<String, Object>> initierPaiement(@RequestBody Map<String, Object> request) {
        Double montant = Double.valueOf(request.get("montant").toString());
        String externalId = (String) request.get("externalId");
        String telephone = (String) request.get("telephone");
        String operateur = (String) request.get("operateur");
        
        Map<String, Object> result = kpayService.initierPaiement(montant, externalId, telephone, operateur);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("paymentId", result.get("id"));
        response.put("reference", result.get("reference"));
        response.put("status", result.get("status"));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verifier/{paymentId}")
    public ResponseEntity<Map<String, Object>> verifierPaiement(@PathVariable String paymentId) {
        Map<String, Object> result = kpayService.verifierPaiement(paymentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", result.get("status"));
        response.put("reference", result.get("reference"));
        
        return ResponseEntity.ok(response);
    }
}