// c'est a sauvegarder ou rechercher un utilisateur et verifier si un email existe déjà

package com.ecommerce.repository;

import com.ecommerce.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByFirebaseUid(String firebaseUid);
    
    Optional<Utilisateur> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByFirebaseUid(String firebaseUid);
    
    @Query("SELECT COUNT(u) FROM Utilisateur u")
    long compterUtilisateurs();

    List<Utilisateur> findByRole(String role);

    @Query("SELECT u FROM Utilisateur u WHERE u.role IN :roles")
    List<Utilisateur> findByRoleIn(List<String> roles);
}