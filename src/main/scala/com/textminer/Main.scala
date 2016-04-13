package com.textminer
import java.util
import java.util.Properties

import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation
import edu.stanford.nlp.hcoref.CorefCoreAnnotations.CorefMentionsAnnotation
import edu.stanford.nlp.ie.util.RelationTriple
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.ling.{CoreLabel, Sentence}
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations.RelationTriplesAnnotation
import edu.stanford.nlp.naturalli.OpenIE
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP, StanfordCoreNLPClient}
import edu.stanford.nlp.semgraph.SemanticGraph
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.util.CoreMap

import scala.collection.JavaConverters._


object Main extends App {

  val text = "Harry Potter is the son of Jessica Simpson. Eric is likes ice cream. He is the father to Josef."
  val relations = Set("son", "father", "mother", "brother", "sister")

  val props = new Properties()
  //props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, natlog, openie");
  //val pipeline = new StanfordCoreNLPClient(props, "localhost", 9000, 2)
  // val pipeline = new StanfordCoreNLP(props)
  props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref, natlog, openie");
  val pipeline= new StanfordCoreNLP(props)

  // create an empty Annotation just with the given text
  val document = new Annotation(text)

  // run all Annotators on this text
  pipeline.annotate(document)

  // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types

  val sentences = document.get(classOf[SentencesAnnotation]).asScala

  for(sentence:CoreMap <- sentences) {

    val oie = sentence.get(classOf[RelationTriplesAnnotation]).asScala
    var res:Option[RelationTriple] = None

    for(triple:RelationTriple <- oie) {
      for(relation <- relations) {
        if(triple.relationLemmaGloss.toLowerCase.contains(relation)) {
          res = Some(triple)
        }
      }
    }

    if(res.nonEmpty){
      val t = res.get
      println(s"Sentence: ${t} => ${t.subjectLemmaGloss()} - ${t.relationLemmaGloss()} - ${t.objectLemmaGloss()}")
      for (m  <- sentence.get(classOf[CorefMentionsAnnotation]).asScala) {
        println("\t" + m)
      }
    }


  }

  for (cc <- document.get(classOf[CorefChainAnnotation]).values().asScala) {
    println("\t"+cc);
  }


}