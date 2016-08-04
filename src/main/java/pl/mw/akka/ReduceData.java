package pl.mw.akka;

import java.util.Map;

/**
 * Created by mwisniewski.
 */
public class ReduceData {
    
    private final Map<String, Integer> reduceDataList;

    public ReduceData(Map<String, Integer> reduceDataList) {
        this.reduceDataList = reduceDataList;
    }

    public Map<String, Integer> getReduceDataList() {
        return reduceDataList;
    }
}
