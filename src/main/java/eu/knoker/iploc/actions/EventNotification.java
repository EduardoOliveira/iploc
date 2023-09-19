
package eu.knoker.iploc.actions;

import eu.knoker.iploc.entities.Access;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class EventNotification {
    public static final String CHANGE_ACCESS_EVENT = "change.access";

    @NonNull
    private String type;
    private Access access;
}
