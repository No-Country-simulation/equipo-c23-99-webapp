package c23_99_m_webapp.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "inventory")
    private List<Resource> resources;

    @OneToOne(mappedBy = "inventory", cascade = CascadeType.ALL)
    private Institution institution;


}
