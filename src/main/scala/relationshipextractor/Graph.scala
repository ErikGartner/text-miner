package relationshipextractor

import org.graphstream.graph._
import org.graphstream.graph.implementations._
import java.util.Iterator
import scala.collection.JavaConverters._

object Graph {
  val graph: Graph = new SingleGraph("tutorial 1")
  var i: Int = 0
  val styleSheet: String =
    """
    node {
      fill-color: hotpink;
    }

    node.marked {
      fill-color: red;
    }

    node:clicked {
      fill-color: blue;
    }
    """
  graph.addAttribute("ui.stylesheet", styleSheet)
  graph.setAutoCreate(true)
  graph.setNullAttributesAreErrors(true);
  graph.setStrict(false)
  graph.display()

  def addRelation(relation: Relation) {
    val a: Node = addPerson(relation.subject)
    val b: Node = addPerson(relation.obj)
    i = i + 1
    val edge: Edge = graph.addEdge(i.toString, a, b, true)
    try {
      edge.setAttribute("ui.label", relation.relationship)
    } catch {
      case nullEx: NullAttributeException => {}
      case e: Exception => {}
    }
  }

  def addPerson(person: Person): Node = {
    val theNode: Node = graph.addNode(person.hashCode.toString)
    theNode.addAttribute("sentence", person.sentence)
    theNode.addAttribute("ui.label", person.name)
    theNode
  }
}