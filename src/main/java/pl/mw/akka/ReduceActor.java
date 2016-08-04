package pl.mw.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static akka.actor.Props.create;

/**
 * Created by mwisniewski.
 */
public class ReduceActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(ReduceActor.class);
    public static Props props = create(ReduceActor.class);

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(MapData.class, message -> {
                    sender().tell(reduce(message.getDataList()), self());
                }).build();
    }

    private ReduceData reduce(List<WordCount> dataList) {
        Map<String, Integer> reducedMap = new HashMap<>();
        for (WordCount wordCount : dataList) {
            if (reducedMap.containsKey(wordCount.getWord())) {
                Integer value = reducedMap.get(wordCount.getWord());
                value++;
                reducedMap.put(wordCount.getWord(), value);
            } else {
                reducedMap.put(wordCount.getWord(), 1);
            }
        }
        return new ReduceData(reducedMap);
    }
}
