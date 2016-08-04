package pl.mw.akka;

import java.util.List;

/**
 * Created by mwisniewski.
 */
public class MapData {
    
    private final List<WordCount> dataList;

    public MapData(List<WordCount> dataList) {
        this.dataList = dataList;
    }

    public List<WordCount> getDataList() {
        return dataList;
    }
}
