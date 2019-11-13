package ar.edu.unp.madryn.livremarket.common.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

public class Conditions {
    public static <K> boolean isMapBooleanTrue(Map<? super K,?> map, K key){
        if(MapUtils.isEmpty(map)){
            return false;
        }

        return BooleanUtils.isTrue(MapUtils.getBoolean(map, key));
    }

    public static <K> boolean isMapBooleanFalse(Map<? super K,?> map, K key){
        if(MapUtils.isEmpty(map)){
            return false;
        }

        return BooleanUtils.isFalse(MapUtils.getBoolean(map, key));
    }

    public static <K> boolean mapContainsKey(Map<? super K,?> map, K key){
        if(MapUtils.isEmpty(map)){
            return false;
        }

        return map.containsKey(key);
    }
}
