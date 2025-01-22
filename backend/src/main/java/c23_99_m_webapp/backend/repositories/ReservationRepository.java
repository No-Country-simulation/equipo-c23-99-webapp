package c23_99_m_webapp.backend.repositories;


import c23_99_m_webapp.backend.models.Reservation;
import c23_99_m_webapp.backend.models.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

//    //buscar reservacion por fecha

    @Query("SELECT r FROM Reservation r WHERE startDate = :startDate")
    List<Reservation> findReservationByStartDate(@Param("startDate") LocalDate startDate, Pageable pageable);
//
//    //buscar reservacion por status
//    @Query("SELECT r FROM Reservation r WHERE reservationStatus = :reservationStatus")
//    Reservation findReservationByStatus(@Param("reservationStatus")ReservationStatus reservationStatus);

    @Query("SELECT r FROM Reservation r WHERE r.deleted = true")
    List<Reservation> findAllDeleted();

    boolean existsByResource(Resource resource);
}
