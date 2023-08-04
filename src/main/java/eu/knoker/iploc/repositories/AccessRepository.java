package eu.knoker.iploc.repositories;

import eu.knoker.iploc.entities.Access;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccessRepository extends JpaRepository<Access, UUID>{
    Access findByIp(String ip);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE access set count = count + 1, last_seen = unixepoch() WHERE id = :id", nativeQuery = true)
    void increment(@Param("id") UUID id);
}
