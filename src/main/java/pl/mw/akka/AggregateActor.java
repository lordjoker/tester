package pl.mw.akka;

import akka.actor.AbstractActor;
import akka.actor.IllegalActorStateException;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwisniewski.
 */
public class AggregateActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(AggregateActor.class);
    public static Props props = Props.create(AggregateActor.class);
    private static int hit = 0;
    private final Map<String, Integer> finalReduceMap = new HashMap<>();

    @Override
    public PartialFunction<Object, BoxedUnit> receive() throws IllegalActorStateException {
        return ReceiveBuilder
                .match(ReduceData.class, message -> {
                    log.info("reduce: " + (++hit));
                    aggregateInMemoryReduce(message.getReduceDataList());
                })
                .match(Result.class, message -> {
                    log.info("result");
                    sender().tell(finalReduceMap.toString(), self());
                }).build();
    }

    private void aggregateInMemoryReduce(Map<String, Integer> reduceDataList) {
        reduceDataList.forEach((k, v) -> {
            if (finalReduceMap.containsKey(k)) {
                finalReduceMap.put(k, v + finalReduceMap.get(k));
            } else {
                finalReduceMap.put(k, v);
            }
        });
    }
}
