#!/bin/bash
echo "🚀 Démarrage en mode PROD..."
export SPRING_PROFILES_ACTIVE=prod
export DB_PASSWORD="votre_mot_de_passe_ici"
java -jar target/ecommerce-backend-1.0.0.jar