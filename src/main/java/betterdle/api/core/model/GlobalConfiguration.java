package betterdle.api.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "global_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfiguration {
    @Id
    private String confKey;
    private String confValue;
}
