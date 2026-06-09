#!/bin/bash
echo "🚀 Démarrage en mode DEV..."
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run