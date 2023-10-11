package javaPrologNLP;

import java.io.*;

//import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.trees.*;

import edu.stanford.nlp.io.IOUtils;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
//import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.logging.Redwood;


import java.util.*;
import java.util.concurrent.TimeUnit;

public class javaPrologNLP {

	private static final Redwood.RedwoodChannels log = Redwood.channels(javaPrologNLP.class);


	public static void main(String[] args) {
		
		ArrayList<String> prologSentences = new ArrayList<String>();
		
		Properties props = new Properties();
		props.put("annotators", "tokenize, pos, lemma, ner, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Welcome to coreNLP to Prolog! This is definitely an *academic* work in progress.");
		System.out.println("It will take a very long time to generate the appropriate output. Please be patient.");
		System.out.println("Currently, only simple and compound sentences are supported (and only nouns and verbs).");
		System.out.println("The sentence types are to build on top of each other to all be broken down into simple sentences.");
		System.out.println("Yet the code is written with complex and compound-complex sentences in mind.");
		System.out.println("So when that is feasible, then it can be implemented (although probably NOT in Java!)");
		System.out.println("Limited support for negation is also available. This also needs more work.");
		System.out.println("This was done as a proof of concept for extracting truth values, facts, and eventually inferernces from rules.");
		System.out.println("(OH THE POSSIBILITIES!!!!)");
		System.out.println("While the work is immature, it has the potential for growth.");
		System.out.println("Threading would obviously help, but is not currently implemented.\n");
		System.out.println("Also coreNLP is not always accurate. Please keep that in mind. Verbs have a tendency to be nouns in there.\n");
		System.out.println("Also coreNLP is A HUGE MEMORY HOG!!!! You have been warned.\n");
		System.out.println("Your feedback is greatly appreciated.\n");
		
		System.out.print("Please enter a sentence: ");
		String sentence = input.nextLine();
		String sentenceTree = "";
		
		while(!sentence.equals("")) {
			sentenceTree = coreNlpRawOutput(sentence, pipeline);
			

			//https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			if (!sentenceTree.contains("(. ")) {
				System.out.println("Your sentence does not contain punctuation. Please try again.");
				System.out.println(sentenceTree);
			} else {
				ToProlog prolog = new ToProlog(sentenceTree);
				System.out.println(prolog.sentenceTree);
				for (int x = 0; x < prolog.statementPrologList.size(); x++) {
					prologSentences.add(prolog.statementPrologList.get(x));
					System.out.println(prolog.statementPrologList.get(x));
				}
			}
			
			System.out.print("Please enter a sentence: ");
			sentence = input.nextLine();
		}
		System.out.print("Thanks! Writing to file.");
		
		//https://tedblob.com/write-arraylist-to-file-java/
		FileWriter writer = null;
		try {
			writer = new FileWriter("resources/javaToProlog.pl");
					for(String str: prologSentences) {
						writer.write(str + System.lineSeparator());
					}
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		input.close();
	}
	
	public static String coreNlpRawOutput(String text, StanfordCoreNLP pipeline) {

		
		//https://stanfordnlp.github.io/CoreNLP/parse.html
		 Annotation annotation =
			        //new Annotation("The small red car turns very quickly around the corner.");
				 new Annotation(text);
			    // annotate
			    pipeline.annotate(annotation);
			    // get tree
			    Tree tree =
			        annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(TreeCoreAnnotations.TreeAnnotation.class);
			    System.out.println(tree);
			    Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
			    for (Constituent constituent : treeConstituents) {
			      if (constituent.label() != null &&
			          (constituent.label().toString().equals("VP") || constituent.label().toString().equals("NP"))) {
			        System.err.println("found constituent: "+constituent.toString());
			        System.err.println(tree.getLeaves().subList(constituent.start(), constituent.end()+1));
			      }
			    }
			    
			    //https://github.com/stanfordnlp/CoreNLP/blob/main/src/edu/stanford/nlp/parser/nndep/demo/DependencyParserCoreNLPDemo.java
			    /*for (CoreMap sent : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			      SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
			      log.info(IOUtils.eolChar + sg.toString(SemanticGraph.OutputFormat.LIST));
			    }*/
			    
			    return tree.toString();
	}
}
