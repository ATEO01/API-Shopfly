package com.ecommerce.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KPayService {
    
    @Value("${kpay.api.url}")
    private String apiUrl;
    
    @Value("${kpay.api.key}")
    private String apiKey;
    
    @Value("${kpay.api.secret.key}")
    private String secretKey;
    
    private final RestTemplate restTemplate;
    
    public KPayService() {
        this.restTemplate = new RestTemplate();
    }
    
    //Début de la construction de la requête http
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");//Indique que le corps est au format JSON
        headers.set("X-API-Key", apiKey);
        headers.set("X-Secret-Key", secretKey);
        return headers;
    }
    
    //Finition de la construction de la requête http et envoie de la requête
    public Map<String, Object> initierPaiement(Double montant, String externalId, String telephone, String operateur) {
        String url = apiUrl + "/payments/init";
        //Finition de la requête
        Map<String, Object> body = Map.of(
            "amount", montant,
            "currency", "XAF",
            "phoneNumber", telephone,
            "paymentMethod", operateur,
            "externalId", externalId
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        //Envoie de la requête a l'API externe avec restTemplate.exchange
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);//le dernier paramètre correspond au type attendu
        //Récupération et renvoie de la réponse
        return response.getBody();
    }
    
    public Map<String, Object> verifierPaiement(String paymentId) {
        String url = apiUrl + "/payments/" + paymentId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }
}