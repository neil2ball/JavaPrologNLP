package javaPrologNLP;

import java.util.ArrayList;

public class ToProlog {
	
	public String unmodifiedTree;
	public String sentenceTree;
	
	public int statements;
	public int questions;
	public int negations;
	public int coordConj;
	public int subordConj;
	
	public boolean noFlag = false;
	
	ArrayList<String> statementPrologList = new ArrayList<String>();
	ArrayList<String> questionPrologList = new ArrayList<String>();
	
	public ToProlog(String tree) {
		this.unmodifiedTree = tree;
		this.sentenceTree = tree;
		this.statements = hasStatements();
		this.questions = hasQuestions();
		this.negations = hasNegations();
		this.coordConj = hasCoordinatingConjunctions();
		this.subordConj = hasSubordinatingConjunctions();
		outputProlog();
		
		prologFromNlp prologOutput = new prologFromNlp(this);
	}
	
	private void outputProlog() {
		System.out.println("Statements: " + this.statements);
		System.out.println("Questions: " + this.questions);
		System.out.println("Negations: " + this.negations);
		System.out.println("Coordinating Conjunctions: " + this.coordConj);
		System.out.println("Subordinating Conjunctions: " + this.subordConj);
	}
	
	private int hasStatements() {
		String temp = this.sentenceTree;
		int statementCount = 0;
		
		statementCount += recursiveSubstringCount(temp, "(S ", "");
		statementCount += recursiveSubstringCount(temp, "(SBAR ", "");
		
		return statementCount;
	}
	
	private int hasQuestions() {
		String temp = this.sentenceTree;
		int questionCount = 0;
		
		questionCount += recursiveSubstringCount(temp, "(SQ ", "");
		questionCount += recursiveSubstringCount(temp, "(SBARQ ", "");
		if (questionCount == 0) {
			questionCount += recursiveSubstringCount(temp, "(. ?)", "");
		}

		
		return questionCount;
	}
	
	//https://webapps.towson.edu/ows/sentences.htm
	private int hasCoordinatingConjunctions() {
		int coorConCount = 0;
		String temp = this.sentenceTree;
		coorConCount += recursiveSubstringCount(temp, "(CC and)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC but)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC yet)", "");
		coorConCount += recursiveSubstringCount(temp, "(CC or)", "");
		if (coorConCount > 0) {
			coorConCount += recursiveSubstringCount(temp, "(, ,)", "");
		}
		return coorConCount;
	}
	
	private int hasSubordinatingConjunctions() {
		int subConCount = 0;
		String temp = this.sentenceTree;
		

			if (temp.contains("(WHNP ")) {
					if (temp.contains("(WP ")) {
						subConCount += recursiveSubstringCount(temp, "(WP Who)", "");
						subConCount += recursiveSubstringCount(temp, "(WP What)", "");
						
						subConCount += recursiveSubstringCount(temp, "(WP who)", "");
						subConCount += recursiveSubstringCount(temp, "(WP what)", "");
					} else if (temp.contains("(WDT ")) {
						subConCount += recursiveSubstringCount(temp, "(WDT Which)", "");
						subConCount += recursiveSubstringCount(temp, "(WDT which)", "");
					}
			} else if (temp.contains("(WHADVP ")){
				subConCount += recursiveSubstringCount(temp, "(WRB When)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB Where)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB Why)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB How)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB Whenever)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB Wherever)", "");
				
				subConCount += recursiveSubstringCount(temp, "(WRB when)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB where)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB why)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB how)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB whenever)", "");
				subConCount += recursiveSubstringCount(temp, "(WRB wherever)", "");
			}
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN After)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN after)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Although)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN although)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN As)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN as)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Because)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN because)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Before)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN before)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN If)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN if)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Lest)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN lest)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Since)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN since)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN so)", ""); //"So" is considered part of an ADVP.
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Though)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN though)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (RB Even) (IN though)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (RB even) (IN though)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN till)", ""); //"Till" is considered part of a PP.
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Until)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN until)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Unless)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN unless)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Whereas)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN whereas)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN Whether)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN whether)", "");
			
			subConCount += recursiveSubstringCount(temp, "SBAR (IN While)", "");
			subConCount += recursiveSubstringCount(temp, "SBAR (IN while)", "");

		return subConCount;
	}
	
	private int hasNegations() {
		int negCount = 0;
		
		if(this.sentenceTree.equals("(ROOT (INTJ (UH No) (. .)))")) {
			
			statementPrologList.add("no.");
			noFlag = true;
			return 0;
		}
		if (this.sentenceTree.contains("(DT Neither)") || this.sentenceTree.contains("(DT neither)")
			|| this.sentenceTree.contains("(CC Nor)") || this.sentenceTree.contains("(CC nor)")) {
			
			negCount++;
			
			int ignoreCount = recursiveSubstringChange("(DT Neither)", "(DT Either)");
			ignoreCount = recursiveSubstringChange("(DT neither)", "(DT either)");
			
			ignoreCount = recursiveSubstringChange("(CC Nor)", "(CC Or)");
			ignoreCount = recursiveSubstringChange("(CC nor)", "(CC or)");
		}
		if (this.sentenceTree.contains("(DT No)") || this.sentenceTree.contains("(DT no)")) {
			negCount += recursiveSubstringChange("(DT No)", "(DT Any)");
			negCount += recursiveSubstringChange("(DT no)", "(DT any)");
		}
		if (this.sentenceTree.contains("(NN Nothing)") || this.sentenceTree.contains("(NN nothing)")) {
			negCount += recursiveSubstringChange("(NN Nothing)", "(NN Anything)");
			negCount += recursiveSubstringChange("(NN nothing)", "(NN anything)");
		}
		if (this.sentenceTree.contains("(NN None)") || this.sentenceTree.contains("(NN none)")) {
			negCount += recursiveSubstringChange("(NN None)", "(NN Any)");
			negCount += recursiveSubstringChange("(NN none)", "(NN any)");
		}
		if (this.sentenceTree.contains("(NN Nobody)") || this.sentenceTree.contains("(NN nobody)")) {
			negCount += recursiveSubstringChange("(NN Nobody)", "(NN Anybody)");
			negCount += recursiveSubstringChange("(NN nobody)", "(NN anybody)");
		}
		if (this.sentenceTree.contains("(RB Nowhere)") || this.sentenceTree.contains("(RB nowhere)")) {
			negCount += recursiveSubstringChange("(RB Nowhere)", "(RB Anywhere)");
			negCount += recursiveSubstringChange("(RB nowhere)", "(RB anywhere)");
		}
		if (this.sentenceTree.contains("(RB Not)") || this.sentenceTree.contains("(RB not)")) {
			negCount += recursiveSubstringChange("(RB Not)", "");
			negCount += recursiveSubstringChange("(RB not)", "");
			
			this.sentenceTree = this.sentenceTree.replaceAll("  ", " ");	
		}
		
		if (this.sentenceTree.contains("(RB Never)") || this.sentenceTree.contains("(RB never)")) {
			negCount += recursiveSubstringChange("(RB Never)", "(RB Always)");
			negCount += recursiveSubstringChange("(RB never)", "(RB always)");
		}
		
		if (this.sentenceTree.contains("(RB n't)")) {
			negCount += recursiveSubstringChange("(RB n't)", " ");
			
			this.sentenceTree = this.sentenceTree.replaceAll("(MD ca)", "MD can");
			this.sentenceTree = this.sentenceTree.replaceAll("(MD Ca)", "MD Can");
			
			this.sentenceTree = this.sentenceTree.replaceAll("(MD wo)", "MD will");
			this.sentenceTree = this.sentenceTree.replaceAll("(MD Wo)", "MD Will");
			
			this.sentenceTree = this.sentenceTree.replaceAll("(VB sha)", "MD shall");
			//todo: Shan't
			
			this.sentenceTree = this.sentenceTree.replaceAll("  ", "");
		}
		
		return negCount;
	}
	
	//https://www.tutorialspoint.com/count-occurrences-of-a-substring-recursively-in-java
 	private int recursiveSubstringCount(String input, String subOut, String subIn) {
		if (input.contains(subOut)){
			StringBuffer tmp = new StringBuffer(input);
			
			int indexStart = input.indexOf(subOut);
			int indexEnd = indexStart + subOut.length();
			
			input = tmp.delete(indexStart, indexEnd).toString();
	         return 1 + recursiveSubstringCount(input, subOut, subIn);
	      }
	      return 0;
	}
	
	//https://www.tutorialspoint.com/count-occurrences-of-a-substring-recursively-in-java
	private int recursiveSubstringChange(String subOut, String subIn) {
		if (this.sentenceTree.contains(subOut)){
			this.sentenceTree = this.sentenceTree.replace(subOut, subIn);
	         return 1 + recursiveSubstringChange(subOut, subIn);
	      }
	      return 0;
	}
}
