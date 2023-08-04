package eu.knoker.iploc.repositories;

import eu.knoker.iploc.entities.AbuseIPDBData;
import eu.knoker.iploc.entities.ShodanData;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AbuseIPDBDataRepository extends JpaRepository<AbuseIPDBData, String> {
}
