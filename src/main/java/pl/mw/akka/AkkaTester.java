package pl.mw.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static akka.pattern.Patterns.ask;

/**
 * Created by mwisniewski.
 */
public class AkkaTester {

    public static final String SENTENCE_SEPARATOR = "(?<=[\\.!;\\?])(?<!Mr\\.|Mrs\\.|Dr\\.|Ms\\.|St\\.)\\s+";

    public static void main(String[] args) throws Exception {
        Timeout timeout = new Timeout(Duration.create(10, "seconds"));

        ActorSystem system = ActorSystem.create("MapReduceApp");
        ActorRef masterActor = system.actorOf(MasterActor.props, "master");

        InputStream in = new FileInputStream(AkkaTester.class.getResource("/sentences.txt").getFile());

        try (Scanner scanner = new Scanner(in)) {
            int count = 0;
            int limit = 10;
            while (scanner.hasNext() && count < limit) {
                scanner.useDelimiter(SENTENCE_SEPARATOR);
                String sentence = scanner.next();
                masterActor.tell(sentence, ActorRef.noSender());
                count++;
            }
        }

        Thread.sleep(2000);

        Future<Object> future = ask(masterActor, new Result(), timeout);
        String result = (String) Await.result(future, timeout.duration());
        System.out.println(result);

        system.shutdown();
    }

}
