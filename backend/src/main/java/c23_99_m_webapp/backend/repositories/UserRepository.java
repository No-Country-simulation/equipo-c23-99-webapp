package c23_99_m_webapp.backend.repositories;

import c23_99_m_webapp.backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String subject);

    Page<User> findByActiveTrue(Pageable pageable);

    User getReferenceByDni(String dni);

    User getReferenceByFullName(String fullName);
}
