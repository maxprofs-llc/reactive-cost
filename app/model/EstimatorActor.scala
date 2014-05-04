package model

import akka.actor.{ActorRefFactory, Actor, Props, ActorRef}
import play.api.Logger
import akka.event.LoggingReceive

class EstimatorActor(cacheFactory: ActorRefFactory => ActorRef,
                      retrieveFactory: ActorRefFactory => ActorRef) extends Actor {
  def this() = this(
    _.actorOf(Props[CacheActor], "cache"),
    _.actorOf(Props[RetrieverActor], "retriever"))

    val cacheActor = cacheFactory(context)
    val retrieverActor = retrieveFactory(context)
    
    lazy val log = Logger("application." + this.getClass.getName)
    
    type UrlSubscribers = Map[String, Set[ActorRef]]
    
    var subscribers: UrlSubscribers = Map.empty 
  
    def subscribersFor(url: String) = subscribers.get(url).getOrElse(Set.empty)

    def append(sender: ActorRef, url: String) {
      subscribers += (url -> (subscribersFor(url) + sender))
    }

    def removeUrl(url: String) {
      subscribers -= url
    }

    override def receive = LoggingReceive {
        case Estimate(url) =>
            val alreadySent = subscribers.contains(url)
            append(sender, url)
            if (!alreadySent) cacheActor ! PullFromCache(url)    
        case CacheFound(url, values) =>
            val result = EstimateResult(url, values, true)
            subscribersFor(url).foreach(_ ! result)
            removeUrl(url)
        case NoCacheFound(url) => retrieverActor ! Retrieve(url)
        case m@Retrieved(url, values, isFinal) =>
            val result = EstimateResult(url, values, isFinal)
            subscribersFor(url).foreach(_ ! result)
            if(isFinal) {
              removeUrl(url)
              cacheActor ! PushToCache(url, values)
            }
    }
}

case class Estimate(url: String) extends AwaitResponseMessage with EstimatorMessage

trait UrlResponseMessage extends ResponseMessage {
    def url: String
    
    def responseFor = Estimate(url)
}

case class EstimateResult(url: String, values: Map[ResultPartId, ResultPartValue], isFinal: Boolean = true) extends UrlResponseMessage

case class PullFromCache(url: String)

case class PushToCache(url: String, values: Map[ResultPartId, ResultPartValue])

case class CacheFound(url: String, values: Map[ResultPartId, ResultPartValue])

case class NoCacheFound(url: String)

case class Retrieve(url: String)

case class Retrieved(url: String, values: Map[ResultPartId, ResultPartValue], isFinal: Boolean)