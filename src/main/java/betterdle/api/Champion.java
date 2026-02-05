package betterdle.api;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Champion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String gender;
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    private String role;
    @Column(length = 2000)
    private String description;
    private String species;
    private String resource;
    private String attackRangeType;
    private String region;
    
    private String iconURL;         
    private String passiveIconURL;  

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "champion_id")
    private List<Spell> spells;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "champion_id")
    private List<Skin> skins;

    @Data
    @Entity
    public static class Spell implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;
        @Column(length = 2000)
        private String description;
        private String cooldown;
        private String imageUrl; 
    }

    @Data
    @Entity
    public static class Skin implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;
        private int num;
        private String splashUrl;  // Sera : /skins/splash/{num}.webp
        private String loadingUrl; // Sera : /skins/loading/{num}.webp
    }
}