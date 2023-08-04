package eu.knoker.iploc.enrichers.shodan;

import com.fooock.shodan.ShodanRestApi;
import com.fooock.shodan.model.host.Host;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.entities.ShodanData;
import eu.knoker.iploc.repositories.ShodanDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Shodan {

    @Value("${shodan.apiKey}")
    private ShodanRestApi api;

    @Value("${shodan.refeshTimeout}")
    private long timeOut;

    @Autowired
    private ShodanDataRepository repository;

    private Host getIpInfo(String ip) {
        return api.hostByIp(ip).blockingFirst();
    }

    //@EventListener()
    public void enrich(Access access) {

        if(access.getShodanData().getLastUpdated()==null ||
                access.getShodanData().getLastUpdated() - System.currentTimeMillis() > this.timeOut){

            Host h = getIpInfo(access.getIp());

            ShodanData shodanData = access.getShodanData();
            shodanData.setAsn(h.getAsn());
            shodanData.setHostnames(List.of(h.getHostnames()));
            shodanData.setLatitude(h.getLatitude());
            shodanData.setLongitude(h.getLongitude());
            shodanData.setPorts(h.getPorts());
            shodanData.setTags(List.of(h.getTags()));
            shodanData.setCity(h.getCity());
            shodanData.setCountryCode(h.getCountryCode());
            shodanData.setCountryName(h.getCountryName());
            shodanData.setIsp(h.getIsp());
            shodanData.setLastUpdated(System.currentTimeMillis());

            repository.save(shodanData);
        }
    }
}
