package javaPrologNLP;

import java.util.ArrayList;
import java.util.Properties;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

public class prologFromNlp {
	
	Properties props = new Properties();
	StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	public prologFromNlp(ToProlog prolog) {
		
		
		props.setProperty("annotators", "tokenize,pos,lemma");

		
		if (!prolog.noFlag) {

		}
		sentenceKind(prolog);
		
		
	}
	
	public void sentenceKind (ToProlog prolog) {
		
		if (prolog.subordConj == 0 && prolog.coordConj == 0) {
			simpleSentence(prolog, prolog.sentenceTree, prolog.negations);
		}
		else if (prolog.subordConj == 0 && prolog.coordConj > 0) {
			compoundSentence(prolog);
		}
		else if (prolog.subordConj > 0 && prolog.coordConj == 0) {
			complexSentence (prolog);
		}
		else if (prolog.subordConj > 0 && prolog.coordConj > 0) {
			compoundComplexSentence (prolog);
		}
	}
	
	public void simpleSentence (ToProlog prolog, String sentence, int negationCount) {
		String verbPhrase = extractor(sentence, "(VP ");
		
		System.out.println(verbPhrase);
		//https://www.geeksforgeeks.org/stringbuffer-delete-method-in-java-with-examples/
		StringBuffer tmp = new StringBuffer(sentence);
		
		int verbPhraseIndexStart = sentence.indexOf(verbPhrase);
		int verbPhraseIndexEnd = verbPhraseIndexStart + verbPhrase.length() - 1;
		
		tmp.delete(verbPhraseIndexStart, verbPhraseIndexEnd);
		String nounPhrase = tmp.toString();
		
		String subject = "";
		String verb = "";
		String object = "";
		
		int nounPhraseCount = nounPhraseCounter(sentence);
		//int verbPhraseCount = verbPhraseCounter(prolog.sentenceTree);
		
		System.out.println("sentence: " + sentence);
		System.out.println("verbPhrase: " + verbPhrase);
		System.out.println("nounPhrase: " + nounPhrase);
		System.out.println("nounPhraseCount: " + nounPhraseCount);
		
		if (nounPhraseCount == 1) {
			verb = verbPrologify(verbPhrase);
			if(prolog.questions == 0) {
				subject = nounPrologify(nounPhrase);
				
				if(negationCount % 2 == 0) {
					prolog.statementPrologList.add(verb + "(" + subject + ").");
				}
				else {
					prolog.statementPrologList.add("not(" + verb + "(" + subject + ")).");
				}
			}
		}
		else if (nounPhraseCount == 2) {
			System.out.println("enter nounphrasecount 2");
			verb = verbPrologify(verbPhrase);
			if(prolog.questions == 0) {
				subject = nounPrologify(nounPhrase);
				object = nounPrologify(verbPhrase);
				
				if(negationCount % 2 == 0) {
					prolog.statementPrologList.add(verb + "(" + subject + ", " + object + ").");
				}
				else {
					prolog.statementPrologList.add("not(" + verb + "(" + subject + ", " + object + ")).");
				}
			}
		}	
	}
	
	public void compoundSentence (ToProlog prolog) {
		
		ArrayList<String> sentenceList = new ArrayList<String>();
		ArrayList<String> negationList = new ArrayList<String>();
		
		ArrayList<Integer> isNegative = new ArrayList<Integer>();
		
		ArrayList<String> simpleSentenceList = new ArrayList<String>();
		
		String temp = prolog.sentenceTree;
		
		if(prolog.questions == 0) {
			
			String tmp = temp;
			int sentenceCount = recursiveSubstringCount(tmp, "(S ", "");
			
			String buffer = prolog.sentenceTree;
			for(int x = 0; x < sentenceCount; x++) {
				
				System.out.println("beforeExtractor (S ");
				sentenceList.add(extractor(buffer, "(S "));
				
				//https://www.geeksforgeeks.org/stringbuffer-delete-method-in-java-with-examples/
				StringBuffer tm = new StringBuffer(buffer);
				
				int sentenceIndexStart = prolog.sentenceTree.indexOf(sentenceList.get(x));
				int sentenceIndexEnd = prolog.sentenceTree.lastIndexOf(sentenceList.get(x));
				
				tm.delete(sentenceIndexStart, sentenceIndexEnd + 1);
				buffer = tm.toString();
			}
			
			if (prolog.negations > 0) {
				int negativeSentenceCount = recursiveSubstringCount(temp, "(S ", "");
					
				String negativeBuffer = prolog.unmodifiedTree;
				StringBuffer tm = new StringBuffer(negativeBuffer);
				
				for(int x = 0; x < negativeSentenceCount; x++) {
					negationList.add(extractor(negativeBuffer, "(S "));
						
					//https://www.geeksforgeeks.org/stringbuffer-delete-method-in-java-with-examples/
						
					int negativeSentenceIndexStart = prolog.unmodifiedTree.indexOf(negationList.get(x));
					int negativeSentenceIndexEnd = prolog.unmodifiedTree.lastIndexOf(negationList.get(x));
						
					tm.delete(negativeSentenceIndexStart, negativeSentenceIndexEnd + 1);
					negativeBuffer = tmp.toString();
				}
				
				for (int y = 0; y < negationList.size(); y++) {
					isNegative.add(hasNegations(negationList.get(y)));
				}
			}
			else {
				for (int x = 0; x < sentenceList.size(); x++) {
					isNegative.add(0);
				}
			}
			
			System.out.println("after negation before CC ");
			for (int y = 0; y < sentenceList.size(); y++) {
				/*if (sentenceList.get(y).contains("(CC ")) { */
					int coordConjCount = coordConjCounter(sentenceList.get(y));
					int verbPhraseCount = verbPhraseCounter(sentenceList.get(y));
					
					ArrayList<String> verbPhraseList = new ArrayList<String>();
					ArrayList<String> nounPhraseList = new ArrayList<String>();
					
					System.out.println("before extractor this: " + sentenceList.get(y));
					
									

					//CoreNLP sometimes wants to treat two verb phrases like one verb phrase with multiple nouns.
					// "Jerry takes names and chews bubblegum." "works".
					// "Jerry rides bikes and drives cars" does not.
					
					String initialVerbPhrase = extractor(sentenceList.get(y), "(VP ");
						
					System.out.println("verbPhraseCount: " + verbPhraseCount);
					
					if(verbPhraseCount > 1) {
						ArrayList<String> verbSplit = new ArrayList<String>();
						String[] verbSplitCC = initialVerbPhrase.split("(CC )");
							
						for (int z = 0; z < verbSplitCC.length; z++) {
							System.out.println("in the verbSplitCC for loop");
							
							String[] verbSplitCCcomma = verbSplitCC[z].split("(, ,)");
							
							for (int a = 0; a < verbSplitCCcomma.length; a++) {
								System.out.println("in the verbSplitCCcomma for loop");
								verbSplit.add(verbSplitCCcomma[a]);
							}
						}
							
						for (int a = 0; a < verbSplit.size(); a++) {
							System.out.println("verbPraseList.add for loop");
							System.out.println(verbSplit.get(a));
							if (a == 0) {
								verbSplit.set(a, verbSplit.get(a).substring(4));
							}
							verbPhraseList.add(extractor(verbSplit.get(a), "(VP "));
						}
					}
					else {
						verbPhraseList.add(initialVerbPhrase);
					}
					
					System.out.println("above initialNounPhrase");
					
					//This will need to change in the future. We really need to test for
					//NPs that are not within VPs.
					//There could be NPs within VPs that come first in a sentence.
					//Looking back on this I am not sure what I did. I think I made the above mentioned mistake.
					String initialNounPhrase = extractor(sentenceList.get(y), "(NP ");
					
					coordConjCount = coordConjCounter(initialNounPhrase);
					
					System.out.println("initialNounPhrase: " + initialNounPhrase);
					System.out.println("coordConjCount: " + coordConjCount);
					
					if (coordConjCount > 0) {
						
						ArrayList<String> nounSplit = new ArrayList<String>();
						String[] nounSplitCC = initialNounPhrase.split("(CC )");
						
						for (int z = 0; z < nounSplitCC.length; z ++) {
							System.out.println("in nounSplitCC for loop");
							String[] nounSplitCCcomma = nounSplitCC[z].split("(, ,)");
							
							for (int a = 0; a < nounSplitCCcomma.length; a++) {
								System.out.println("in nounSplitCCcomma for loop");
								nounSplit.add(nounSplitCCcomma[a]);
							}
						}
						
						for (int a = 0; a < nounSplit.size(); a++) {
							
							//Check for noun within verb phrase
							String nounBuffer = extractor(nounSplit.get(a), "(NN");
							
							if (!initialVerbPhrase.contains(nounBuffer)) {
								nounPhraseList.add("(NP " + nounBuffer + ")");
							}
						}
						
					} else {
						System.out.println("enter else nounPhraseList.add");
						nounPhraseList.add(extractor(initialNounPhrase, "(NP "));
					}
					for (int z = 0; z < nounPhraseList.size(); z++) {
						for (int a = 0; a < verbPhraseList.size(); a++) {
							System.out.println("enter nested foor loop simpleSentenceList.add");

							simpleSentenceList.add(nounPhraseList.get(z) + " " + verbPhraseList.get(a));
						}
					}
					
					for (int a = 0; a < simpleSentenceList.size(); a++) {
						simpleSentence(prolog, simpleSentenceList.get(a), isNegative.get(y));
					}
				}
			}
		}
	
	public void complexSentence (ToProlog prolog) {
		//Todo
	}
	
	public void compoundComplexSentence (ToProlog prolog) {
		//Todo
	}
	
	public int nounPhraseCounter (String nlp) {
		String temp = nlp;
		return recursiveSubstringCount(temp, "(NP ", "");
	}
	
	public int verbPhraseCounter (String nlp) {
		String temp = nlp;
		return recursiveSubstringCount(temp, "(VP ", "");
	}
	
	public int nounCounter (String nlp) {
		String temp = nlp;
		int counter = recursiveSubstringCount(temp, "(NN ", "");
		counter += recursiveSubstringCount(temp, "(NNS ", "");
		counter += recursiveSubstringCount(temp, "(NNP ", "");
		counter += recursiveSubstringCount(temp, "(NNPS ", "");
		
		return counter;
	}
	
	public int verbCounter (String nlp) {
		String temp = nlp;
		int counter = recursiveSubstringCount(temp, "(VB ", "");
		counter += recursiveSubstringCount(temp, "(VBD ", "");
		counter += recursiveSubstringCount(temp, "(VBG ", "");
		counter += recursiveSubstringCount(temp, "(VBN ", "");
		counter += recursiveSubstringCount(temp, "(VBP ", "");
		counter += recursiveSubstringCount(temp, "(VBZ ", "");
		
		return counter;
	}
	
	//https://webapps.towson.edu/ows/sentences.htm
	private int coordConjCounter(String nlp) {
		int coorConCount = 0;
		String temp = nlp;
		coorConCount += recursiveSubstringCount(temp, "(CC and)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC but)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC yet)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC or)", "");
		if (coorConCount > 0) {
			coorConCount += recursiveSubstringCount(temp, "(, ,)", "");
		}

		return coorConCount;
	}
	
	public String nounPrologify (String nlp) {
		String temp = nlp;
		int nounCount = nounCounter(temp);
		String nounHolder = "";
		
		while (nounCount > 0) {
			
			if (!nounHolder.isEmpty()) {
				nounHolder += "_";
			}
			
			int nounIndexStart = 0;
			int nounWordStart = 0;
			int nounIndexEnd = 0;
			
			//https://www.w3schools.com/jsref/jsref_lastindexof_array.asp
			if (temp.contains("(NN ")) {
				nounIndexStart = temp.indexOf("(NN ") + 1;
				nounWordStart = temp.indexOf("(NN ") + 3;
			}
			else if (temp.contains("(NNS ")) {
				nounIndexStart = temp.indexOf("(NNS ") + 1;
				nounWordStart = temp.indexOf("(NNS ") + 4;
			}
			else if (temp.contains("(NNP ")) {
				nounIndexStart = temp.indexOf("(NNP ") + 1;
				nounWordStart = temp.indexOf("(NNP ") + 4;
			}
			else if (temp.contains("(NNPS ")) {
				nounIndexStart = temp.indexOf("(NNPS ") + 1;
				nounWordStart = temp.indexOf("(NNPS ") + 4;
			}
			else if (temp.contains("(PRP ")) {
				nounIndexStart = temp.indexOf("(PRP ") + 1;
				nounWordStart = temp.indexOf("(PRP ") + 4;
			}
			
			nounIndexEnd = temp.indexOf(")", nounIndexStart);
			String buffer = temp.substring(nounWordStart, nounIndexEnd);
			
			//https://nlp.stanford.edu/nlp/javadoc/javanlp-3.5.0/edu/stanford/nlp/process/Morphology.html
			//https://stanfordnlp.github.io/CoreNLP/lemma.html
			CoreDocument document = pipeline.processToCoreDocument(buffer);
		    for (CoreLabel tok : document.tokens()) {
		        nounHolder += tok.lemma().toString();
		      }
			
			//https://www.geeksforgeeks.org/stringbuffer-delete-method-in-java-with-examples/
			StringBuffer tmp = new StringBuffer(temp);
			tmp.delete(nounIndexStart, nounIndexEnd + 1);
			temp = tmp.toString();
			
			nounCount--;
		}
		
		nounHolder = nounHolder.toLowerCase();
		
		System.out.println("nounHolder: " + nounHolder);
		
		return nounHolder;
		
	}
	
	public String verbPrologify (String nlp) {
		String temp = nlp;
		String verbHolder = "";
		
		int verbIndexStart = 0;
		int verbWordStart = 0;
		int verbIndexEnd = 0;
			
		//https://www.w3schools.com/jsref/jsref_lastindexof_array.asp
		if (temp.contains("(VB ")) {
			verbIndexStart = temp.indexOf("(VB ") + 1;
			verbWordStart = temp.indexOf("(VB ") + 3;
		}
		else if (temp.contains("(VBD ")) {
			verbIndexStart = temp.indexOf("(VBD ") + 1;
			verbWordStart = temp.lastIndexOf("(VBD ") + 4;
		}
		else if (temp.contains("(VBG ")) {
			verbIndexStart = temp.indexOf("(VBG ") + 1;
			verbWordStart = temp.lastIndexOf("(VBG ") + 4;
		}
		else if (temp.contains("(VBN ")) {
			verbIndexStart = temp.indexOf("(VBN ") + 1;
			verbWordStart = temp.lastIndexOf("(VBN ") + 4;
		}
		else if (temp.contains("(VBP ")) {
			verbIndexStart = temp.indexOf("(VBP ") + 1;
			verbWordStart = temp.lastIndexOf("(VBP ") + 4;
		}
		else if (temp.contains("(VBZ ")) {
			verbIndexStart = temp.indexOf("(VBZ ") + 1;
			verbWordStart = temp.lastIndexOf("(VBZ ") + 4;
		}

		verbIndexEnd = temp.indexOf(")", verbIndexStart);
		String buffer = temp.substring(verbWordStart, verbIndexEnd);
			
		//https://nlp.stanford.edu/nlp/javadoc/javanlp-3.5.0/edu/stanford/nlp/process/Morphology.html
		//https://stanfordnlp.github.io/CoreNLP/lemma.html
		CoreDocument document = pipeline.processToCoreDocument(buffer);
		for (CoreLabel tok : document.tokens()) {
			verbHolder += tok.lemma().toString();
		}
		
		verbHolder = verbHolder.toLowerCase();
		
		verbHolder += "_"; //This is to avoid conflict with built-in Prolog functions.
		
		System.out.println("verbHolder: " + verbHolder);
		return verbHolder;
		
	}


	public String extractor (String nlp, String sign) {
		String temp = nlp;
		
		int StartIndex = nlp.indexOf(sign);
		
		if (StartIndex > 0) {
			temp = nlp.substring(StartIndex);
			StartIndex = 0;
		}
		
		while (temp.charAt(temp.length() - 1) != ')') {
			temp = temp.substring(0, temp.length() - 2);
		}
		
		int EndIndex = temp.length();

		int BufferIndex = 1;
		int parenthesesCount = 1;
		while (parenthesesCount > 0) {
			
			if (temp.indexOf("(", BufferIndex) > temp.indexOf(")", BufferIndex)) {
				BufferIndex = temp.indexOf(")", BufferIndex) + 1;
				parenthesesCount--;
			}
			else {
				BufferIndex = temp.indexOf("(", BufferIndex) + 1;
				parenthesesCount++;
			}
			
			if (parenthesesCount == 0) {
				EndIndex = BufferIndex;
			}
			BufferIndex++;
		}
		
		return temp.substring(StartIndex, EndIndex - 1);
	}
	
	private int hasNegations(String nlp) {
		int negCount = 0;
		

		if (nlp.contains("(DT Neither)") || nlp.contains("(DT neither)")
			|| nlp.contains("(CC Nor)") || nlp.contains("(CC nor)")) {
			
			negCount++;
			
			int ignoreCount = recursiveSubstringCount(nlp, "(DT Neither)", "(DT Either)");
			ignoreCount = recursiveSubstringCount(nlp, "(DT neither)", "(DT either)");
			
			ignoreCount = recursiveSubstringCount(nlp, "(CC Nor)", "(CC Or)");
			ignoreCount = recursiveSubstringCount(nlp, "(CC nor)", "(CC or)");
		}
		if (nlp.contains("(DT No)") || nlp.contains("(DT no)")) {
			negCount += recursiveSubstringCount(nlp, "(DT No)", "(DT Any)");
			negCount += recursiveSubstringCount(nlp, "(DT no)", "(DT any)");
		}
		if (nlp.contains("(NN Nothing)") || nlp.contains("(NN nothing)")) {
			negCount += recursiveSubstringCount(nlp, "(NN Nothing)", "(NN Anything)");
			negCount += recursiveSubstringCount(nlp, "(NN nothing)", "(NN anything)");
		}
		if (nlp.contains("(NN None)") || nlp.contains("(NN none)")) {
			negCount += recursiveSubstringCount(nlp, "(NN None)", "(NN Any)");
			negCount += recursiveSubstringCount(nlp, "(NN none)", "(NN any)");
		}
		if (nlp.contains("(NN Nobody)") || nlp.contains("(NN nobody)")) {
			negCount += recursiveSubstringCount(nlp, "(NN Nobody)", "(NN Anybody)");
			negCount += recursiveSubstringCount(nlp, "(NN nobody)", "(NN anybody)");
		}
		if (nlp.contains("(RB Nowhere)") || nlp.contains("(RB nowhere)")) {
			negCount += recursiveSubstringCount(nlp, "(RB Nowhere)", "(RB Anywhere)");
			negCount += recursiveSubstringCount(nlp, "(RB nowhere)", "(RB anywhere)");
		}
		if (nlp.contains("(RB Not)") || nlp.contains("(RB not)")) {
			negCount += recursiveSubstringCount(nlp, "(RB Not)", "");
			negCount += recursiveSubstringCount(nlp, "(RB not)", "");
			
			nlp = nlp.replaceAll("  ", " ");	
		}
		
		if (nlp.contains("(RB Never)") || nlp.contains("(RB never)")) {
			negCount += recursiveSubstringCount(nlp, "(RB Never)", "(RB Always)");
			negCount += recursiveSubstringCount(nlp, "(RB never)", "(RB always)");
		}
		
		if (nlp.contains("(RB n't)")) {
			negCount += recursiveSubstringCount(nlp, "(RB n't)", " ");
			
			nlp = nlp.replaceAll("(MD ca)", "MD can");
			nlp = nlp.replaceAll("(MD Ca)", "MD Can");
			
			nlp = nlp.replaceAll("(MD wo)", "MD will");
			nlp = nlp.replaceAll("(MD Wo)", "MD Will");
			
			nlp = nlp.replaceAll("(VB sha)", "MD shall");
			//todo: Shan't
			
			nlp = nlp.replaceAll("  ", "");
		}
		
		return negCount;
	}
	
	//https://www.tutorialspoint.com/count-occurrences-of-a-substring-recursively-in-java
 	private int recursiveSubstringCount(String input, String subOut, String subIn) {
 		String temp = input;
 		System.out.println("input " + input);
		if (input.contains(subOut)){
			System.out.println("containsSubOut " + subOut);
			StringBuffer tmp = new StringBuffer(temp);
			
			int indexStart = temp.indexOf(subOut);
			int indexEnd = indexStart + subOut.length();
			
			temp = tmp.delete(indexStart, indexEnd).toString();
			System.out.println("inputNow: " + temp);
	         return 1 + recursiveSubstringCount(temp, subOut, subIn);
	      }
	      return 0;
	}
	
}
