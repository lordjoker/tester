package pl.mw.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import static akka.actor.Props.create;

/**
 * Created by mwisniewski.
 */
public class MasterActor extends AbstractActor {

    public static Props props = create(MasterActor.class);
    
    ActorRef mapActor = getContext().actorOf(MapActor.props.withRouter(new RoundRobinPool(5)), "map");
    ActorRef reduceActor = getContext().actorOf(ReduceActor.props.withRouter(new RoundRobinPool(5)), "reduce");
    ActorRef aggregateActor = getContext().actorOf(AggregateActor.props, "aggregate");

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(String.class, message -> {
                    mapActor.tell(message, self());
                })
                .match(MapData.class, message -> {
                    reduceActor.tell(message, self());
                })
                .match(ReduceData.class, message -> {
                    aggregateActor.tell(message, self());
                })
                .match(Result.class, message -> {
                    aggregateActor.forward(message, context());
                })
                .matchAny(message -> sender().tell(new Status.Failure(new Exception("Unknown message " + message)), self()))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(10, Duration.apply("1 minute"), t -> {
            if (t instanceof NullPointerException) {
                return SupervisorStrategy.restart();
            } else {
                return SupervisorStrategy.escalate();
            }
        });
    }
}
