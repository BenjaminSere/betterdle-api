package betterdle.api.core.repository;

import betterdle.api.core.model.GlobalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, String> {
}
