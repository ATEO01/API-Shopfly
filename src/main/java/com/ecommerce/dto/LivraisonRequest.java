package com.ecommerce.dto;

import java.util.List;

public class LivraisonRequest {
    private List<Long> commandesIds;

    public List<Long> getCommandesIds() {
        return commandesIds;
    }
}