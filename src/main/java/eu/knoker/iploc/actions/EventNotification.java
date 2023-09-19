
package eu.knoker.iploc.enrichers;

import eu.knoker.iploc.entities.Access;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventNotification {
    private String type;
    private Access access;
}
