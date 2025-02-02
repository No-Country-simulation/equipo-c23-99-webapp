package c23_99_m_webapp.backend.controllers;

import c23_99_m_webapp.backend.exceptions.MyException;
import c23_99_m_webapp.backend.models.User;
import c23_99_m_webapp.backend.models.dtos.DataAnswerReservation;
import c23_99_m_webapp.backend.models.dtos.DataAnswerReserveByDniUser;
import c23_99_m_webapp.backend.models.dtos.PageResponse;
import c23_99_m_webapp.backend.models.dtos.ReservationDto;
import c23_99_m_webapp.backend.models.enums.ReservationShiftStatus;
import c23_99_m_webapp.backend.models.enums.ReservationStatus;
import c23_99_m_webapp.backend.services.ReservationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/reservations")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto reservationDto) {

        try {
            DataAnswerReservation reservationDto1 = reservationService.createdReservation(reservationDto);

            return ResponseEntity.ok(Map.of("status",
                    "success", "message",
                    "Reserva creada con éxito","data",
                    reservationDto1));

        } catch (MyException e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status",
                    "error", "message",
                    e.getMessage()));
        }catch (RuntimeException e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status",
                    "error", "message",
                    e.getMessage()));
        }
    }

    @GetMapping("/allReservations") //agregar restriccion de borradas
    public ResponseEntity<List<ReservationDto>> getAllReservations() throws MyException {

            List<ReservationDto> reservationList = reservationService.getReservations();
            return ResponseEntity.ok().body(reservationList);
    }

    @GetMapping("/getId/{id}") //agregar restriccion de borradas
    public ResponseEntity<Optional<ReservationDto>> getReservationById(@PathVariable Long id) throws MyException {
        return ResponseEntity.ok().body(reservationService.findReservationById(id));
    }

    //update para el usuario
    @PatchMapping("/update/{id}")
    public ResponseEntity<Optional<ReservationDto>> updateById(@PathVariable Long id,
                                                               @RequestBody ReservationDto reservationDto) throws MyException {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.updateById(id, reservationDto));
    }

    //update para el admin
    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestBody ReservationStatus newStatus) {
        try {
            ReservationDto updatedReservation = reservationService.updateReservationStatusByAdmin(id, newStatus);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Estado de la reserva actualizado con éxito",
                    "data", updatedReservation
            ));
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error interno del servidor"
            ));
        }
    }

    @DeleteMapping("/deleteReserve/{id}") //borrado completo (fisico)
    public void delete(@PathVariable Long id) throws MyException {
        reservationService.deleteReserve(id);
    }

    @PutMapping("/delete/{id}") //borrado logico
    public void deleteReservation(@PathVariable Long id) throws MyException {
        reservationService.deleteReservationById(id);
    }

    @GetMapping("/allDeleted")
    public List<ReservationDto> deleted()throws MyException{
        return ResponseEntity.ok().body(reservationService.getDeletedReservations()).getBody();
    }

    @PutMapping("/restore/{id}")
    public void restore(@PathVariable Long id) {
        try {
            reservationService.restoreReservation(id);
        }catch (MyException e) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error",
                    "message", e.getMessage()));
        }catch (RuntimeException e){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status",
                    "error", "message",
                    e.getMessage()));
        }
    }

    @GetMapping("/byUser/{dni}") //agregar restriccion de borradas
    public ResponseEntity<?> reserveByUser(@PathVariable("dni") String dni,
                                           @PageableDefault(size = 10) Pageable pageable){
        try{
            Page<DataAnswerReserveByDniUser> answerReservationPage = reservationService.findByUserDni(dni, pageable);
            PageResponse<DataAnswerReserveByDniUser> response = new PageResponse<>(
                    answerReservationPage.getContent(),
                    answerReservationPage.getTotalPages(),
                    answerReservationPage.getTotalElements(),
                    answerReservationPage.getSize(),
                    answerReservationPage.getNumber()
            );
            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Filtrado realizado con éxito.",
                    "data", response));
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error",
                    "message", e.getMessage()));
        } catch (RuntimeException e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status",
                    "error", "message",
                    e.getMessage()));
        }
    }

    @GetMapping("/byDate/{date}")
    public ResponseEntity<?> listByDate(@PathVariable("date") LocalDate startDate,
                                         @PageableDefault(size = 10) Pageable pageable){
        try {
            Page<DataAnswerReservation> reservationDtoPage = reservationService.findByDate(startDate, pageable);
            PageResponse<DataAnswerReservation> response = new PageResponse<>(
                    reservationDtoPage.getContent(),
                    reservationDtoPage.getTotalPages(),
                    reservationDtoPage.getTotalElements(),
                    reservationDtoPage.getSize(),
                    reservationDtoPage.getNumber()
            );

            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Filtrado realizado con éxito.",
                    "data", response));
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error",
                    "message", e.getMessage()));
        }catch (RuntimeException e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status",
                    "error", "message",
                    e.getMessage()));
        }
    }
    @GetMapping("/byStatus/{status}")
    public ResponseEntity<?> listByStatusReserve(@PathVariable("status") ReservationStatus status,
                                                 @PageableDefault(size = 10) Pageable pageable){
        try {
            Page<DataAnswerReservation> reservationDtoPage = reservationService.findByStatus(status, pageable);
            PageResponse<DataAnswerReservation> response = new PageResponse<>(
                    reservationDtoPage.getContent(),
                    reservationDtoPage.getTotalPages(),
                    reservationDtoPage.getTotalElements(),
                    reservationDtoPage.getSize(),
                    reservationDtoPage.getNumber()
            );
            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Filtrado realizado con éxito.",
                    "data", response));

        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
    @GetMapping("/byShiftStatus/{reservationShiftStatus}")
    public ResponseEntity<?> getByShiftStatus(@PathVariable("reservationShiftStatus") ReservationShiftStatus reservationShiftStatus,
                                              @PageableDefault(size = 10) Pageable pageable){
        try {
            Page<DataAnswerReservation> reservationDtoPage = reservationService.findByShiftStatus(reservationShiftStatus, pageable);
            PageResponse<DataAnswerReservation> response = new PageResponse<>(
                    reservationDtoPage.getContent(),
                    reservationDtoPage.getTotalPages(),
                    reservationDtoPage.getTotalElements(),
                    reservationDtoPage.getSize(),
                    reservationDtoPage.getNumber()
            );
            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Filtrado realizado con éxito.",
                    "data", response));

        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}