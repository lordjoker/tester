package pl.mw.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static akka.actor.Props.create;

/**
 * Created by mwisniewski.
 */
public class MapActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(MapActor.class);
    public static Props props = create(MapActor.class);

    String[] STOP_WORDS = {"a", "am", "an", "and", "are", "as", "at",
            "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to"};
    List<String> STOP_WORDS_LIST = Arrays.asList(STOP_WORDS);
    
//    public int count = 0;

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(String.class, message -> {
                    sender().tell(evaluateExpression(message), self());
                }).build();
    }

    private MapData evaluateExpression(String line) {
        List<WordCount> dataList = new ArrayList<>();
        StringTokenizer parser = new StringTokenizer(line);
        while (parser.hasMoreElements()) {
            String word = parser.nextToken().toLowerCase();
            if (!STOP_WORDS_LIST.contains(word)) {
                dataList.add(new WordCount(word, 1));
            }
        }
//        if (count++ < 10) {
//            throw new NullPointerException("Test: " + count);
//        }
        return new MapData(dataList);
    }
}
