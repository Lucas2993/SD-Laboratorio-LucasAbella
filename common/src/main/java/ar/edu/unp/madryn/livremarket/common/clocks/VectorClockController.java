package ar.edu.unp.madryn.livremarket.common.clocks;

import com.javacreed.api.veclock.LongVersion;
import com.javacreed.api.veclock.StringNode;
import com.javacreed.api.veclock.VectorClock;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class VectorClockController {
    private static final int SERVER_ID_INDEX = 0;
    private static final int SERVER_CLOCK_INDEX = 1;

    private String serverID;
    private VectorClock clock;

    public VectorClockController(String serverID) {
        this.serverID = serverID;
        this.clock = VectorClock.first(this.serverID);
    }

    public synchronized boolean updateClocks(Map<String,Long> clocksData){
        VectorClock receivedClock = null;

        Long currentClock = clocksData.remove(this.serverID);

        if(MapUtils.isEmpty(clocksData)){
            return false;
        }

        for(String id : clocksData.keySet()){
            Long version = clocksData.get(id);
            if(receivedClock == null){
                receivedClock = VectorClock.of(StringNode.of(id), LongVersion.of(version));
                continue;
            }
            receivedClock = receivedClock.add(StringNode.of(id), LongVersion.of(version));
        }

        if(receivedClock == null) {
            return false;
        }

        if(currentClock != null) {
            receivedClock = receivedClock.add(StringNode.of(this.serverID), LongVersion.of(currentClock));
        }

        this.clock = this.clock.add(receivedClock);

        return true;
    }

    public synchronized Map<String,Long> getClocks(){
        Map<String,Long> result = new HashMap<>();
        String info = this.clock.toString();

        info = info.replace("[", "");
        info = info.replace("]", "");

        if(StringUtils.isEmpty(info)){
            return null;
        }

        for(String singleClock : info.split(",")){
            String [] clockData = singleClock.split(":");
            result.put(clockData[SERVER_ID_INDEX], NumberUtils.createLong(clockData[SERVER_CLOCK_INDEX]));
        }

        return result;
    }

    public synchronized boolean updateClock(){
        this.clock = this.clock.next();
        return true;
    }
}
