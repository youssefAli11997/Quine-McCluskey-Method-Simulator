package quine.mccluskey.method.simulator;

import java.util.ArrayList;
import java.util.Stack;


public class MainClass {
	
	private ArrayList<Term> terms = new ArrayList<Term>();
	private ArrayList<ArrayList<Term> > termsGroups = new ArrayList<ArrayList<Term>>();
	private ArrayList<ArrayList<ArrayList<Term>>> G = new ArrayList<ArrayList<ArrayList<Term>>>(); // I can't find a nice name for this :""D
	private ArrayList<Integer> minterms = new ArrayList<Integer>(); 
	private int maxLength = -100;        // like G', G'', G''' and so on
	private ArrayList<Term> finalPrimeTerms = new ArrayList<Term>();
	private ArrayList<Term> essentials = new ArrayList<Term>();
	private ArrayList<ArrayList<Term>> restPrimes = new ArrayList<ArrayList<Term>>() ;
	private StringBuilder outPut = new StringBuilder();
        int [][] tempChart ;
	public String toBinary(int num){
		return Integer.toBinaryString(num);
	}

	public void addMinterm(int value){
	    Term mt = new Term(toBinary(value), false, value);
	    terms.add(mt);
	    minterms.add(value);
	}

	public void addDontCare(int value){
	    Term mt = new Term(toBinary(value), true, value);
	    terms.add(mt);
	}
	
	public void getMaximumTerm(){  
	    for(int i=0; i<terms.size(); i++){
	        int curr = terms.get(i).getValue().length();
	        if(maxLength < curr)
	            maxLength = curr;
	    }
	}
	
	public void modifyTerms(){
	    getMaximumTerm();
	    for(int i=0; i<terms.size(); i+=1){
	        int diff = maxLength - terms.get(i).getValue().length();
	        for(int j=0; j<diff; j+=1)
	            terms.get(i).setValue("0" + terms.get(i).getValue());
	    }
	}
	
	public void groupingTerms(){
	    modifyTerms();
	    
	    int numOfRows = maxLength + 3; // 3 is extra, it has no meaning :D
	    for(int i=0; i<numOfRows; i++){
	    	termsGroups.add(new ArrayList<Term>());
	    }
	    
	    for(int i=0; i<terms.size(); i++){
	        String t = terms.get(i).getValue();
	        Term newTerm = new Term(t, terms.get(i).isDontCare());
	        newTerm.addNumbersItCovers(terms.get(i).getNumbersItCovers());
	        int toRow = countOnes(t);
			termsGroups.get(toRow).add(newTerm);
	    }
	}

	private int countOnes(String t) {
		int count = 0;
	    for(int i=0; i<t.length(); i++){
	        count += (t.charAt(i) == '1') ? 1 : 0;
	    }
	    return count;
	}
	
	public void callQuine(){
		groupingTerms();	
		G.add(termsGroups);
		RecursiveQuine(1);
	}
	
	public void RecursiveQuine(int level){
		
		int numOfTokens = 0;
		// create a new 2D array for the new level :D
		G.add(new ArrayList<ArrayList<Term>>());
		int numOfRows = maxLength + 3; // Again, 3 is extra, it has no meaning :D
	    for(int i=0; i<numOfRows; i++){
	    	G.get(level).add(new ArrayList<Term>());
	    }
	    int cache1 = G.get(level-1).size() - 1;
	    for(int i=0; i<cache1; i++){
	    	for(int j=0; j<G.get(level-1).get(i).size(); j++){
	    		for(int k=0; k<G.get(level-1).get(i+1).size(); k++){
		    		if(canBeReducedTogether(G.get(level-1).get(i).get(j).getValue(), G.get(level-1).get(i+1).get(k).getValue())){
		    			ReduceThem(level-1,i,j,k);
		    			numOfTokens++;
		    		}
		    	}
	    	}
	    }
	    
	    if(numOfTokens > 0) // recurse
	    	RecursiveQuine(level + 1);
	    
	}
	
	public boolean canBeReducedTogether(String a, String b){
		int countOfDiff = 0;
		for(int i=0; i<a.length(); i++) if(a.charAt(i) != b.charAt(i) )
			countOfDiff++;
			
		return (countOfDiff == 1);
	}
	
	public void ReduceThem(int level, int i, int j, int k){
		String newExpr =  getReducedExpression(G.get(level).get(i).get(j).getValue(), G.get(level).get(i+1).get(k).getValue());
		G.get(level).get(i).get(j).setDeleted(true);
		G.get(level).get(i+1).get(k).setDeleted(true);
		int toRow = countOnes(newExpr);
		if(!isExist(G.get(level+1).get(toRow),newExpr)){ // add it
			Term toBeAdded = new Term(newExpr, false);
			toBeAdded.addNumbersItCovers(G.get(level).get(i).get(j).getNumbersItCovers());
			toBeAdded.addNumbersItCovers(G.get(level).get(i+1).get(k).getNumbersItCovers());
			G.get(level+1).get(toRow).add(toBeAdded);
		}
	}
	
	public String getReducedExpression(String a, String b){
		StringBuilder ret = new StringBuilder();
		for(int i=0; i<a.length(); i++){
			if(a.charAt(i) == b.charAt(i))
				ret.append(a.charAt(i));
			else
				ret.append("-");
		}
		return ret.toString();
	}
	
	public boolean isExist(ArrayList<Term> arr, String expr){
		for(int i=0; i<arr.size(); i++){
			if(arr.get(i).getValue().equals(expr))
				return true;
		}
		return false;
	}
	public void printIt(){
            outPut.append("***Terms:\n");
            for(int i=0; i<termsGroups.size(); i++){
                boolean ok2 = false;
                for(int j=0; j<termsGroups.get(i).size(); j++){
                    ok2 = true;
                    outPut.append(termsGroups.get(i).get(j).getNumbersItCovers() + " ");
                    outPut.append(termsGroups.get(i).get(j).getValue());
                    //outPut.append("  " + termsGroups.get(i).get(j).isDontCare());
                    if(termsGroups.get(i).get(j).isDontCare()){
                        outPut.append(" don't care\n");
                    }else{
                        outPut.append(" minterm\n");
                    }
                }
                if(ok2)
                    outPut.append("---------------------\n");
            }
            // printing our 3D arraylist
            outPut.append("\n***Quine McCluskey Method:\n");
            outPut.append("**Number of Levels: " + G.size() + "\n");
            for(int i=0; i<G.size(); i++){
                    boolean ok1 = false;
                    boolean ok3 = false;
                    for(int j=0; j<G.get(i).size(); j++){
                            boolean ok2 = false;
                            for(int k=0; k<G.get(i).get(j).size(); k++){
                                    ok1 = true;
                                    ok2 = true;
                                    if(!ok3){
                                            outPut.append("*Level " + i + ":\n");
                                            ok3 = true;
                                    }
                                    outPut.append(G.get(i).get(j).get(k).getValue() + "  " + G.get(i).get(j).get(k).getNumbersItCovers().toString());
                                    outPut.append(G.get(i).get(j).get(k).isDeleted()? " token\n":"\n");
                                    //System.out.println(G.get(i).get(j).get(k).getValue() + "  " + G.get(i).get(j).get(k).getNumbersItCovers().toString());
                            }
                            if(ok2 == true)
                                    outPut.append("-------------------------\n");
                                    //System.out.println("-------------------------");
                    }
                    if(ok1 == true)
                            outPut.append("=================\n");
                    //System.out.println("=================");
            }
    }
    public void printPrime(){
            outPut.append("\n***Essential Ones:\n");
            //System.out.println("Essential Ones");
            for(int i=0; i<essentials.size(); i++){
                    outPut.append(toAlpha(essentials.get(i).getValue().toString()) + " ");
                    outPut.append(essentials.get(i).getValue().toString() + "  " + essentials.get(i).getNumbersItCovers().toString()+"\n");
                    //System.out.println(essentials.get(i).getValue().toString() + "  " + essentials.get(i).getNumbersItCovers().toString());
            }
        outPut.append("*************************\n");
            //System.out.println("-------------------------");
    }
    public void printSols(){
            outPut.append("***Petrick Possible Choices:\n");
            if(restPrimes.size() == 0){
                    outPut.append("No Non-essential ... No Choices\n*************************\n");
                    return;
            }
            for(int i=0; i<restPrimes.size(); i++){
                    if(i>0)outPut.append("------------ OR -------------\n");
                    for(int j = 0 ; j < restPrimes.get(i).size() ; j ++){
                    outPut.append(toAlpha(restPrimes.get(i).get(j).getValue().toString()) + " ");
                    //System.out.print(toAlpha(restPrimes.get(i).get(j).getValue().toString()) + " ");
                    outPut.append(restPrimes.get(i).get(j).getValue().toString() + "  " + restPrimes.get(i).get(j).getNumbersItCovers().toString() +"\n");
                    //System.out.println(restPrimes.get(i).get(j).getValue().toString() + "  " + restPrimes.get(i).get(j).getNumbersItCovers().toString());
                    }
            //System.out.println("-------------------------");
                    }
    }

    public String printFormattedSols(){
            StringBuilder form = new StringBuilder();
            StringBuilder essen = new StringBuilder();
            form.append("***Solutions:\n\n");
            for(int i=0; i<essentials.size(); i++){
                    if(i>0) essen.append(" + ");
                    essen.append(toAlpha(essentials.get(i).getValue().toString()));
            }
            if(restPrimes.size() == 0)
                    form.append("F = " + essen);
            for(int i=0; i<restPrimes.size(); i++){
                    form.append("F = ");
                    form.append(essen);
                    if(essen.length() > 0 && restPrimes.get(i).size() > 0)
                            form.append(" + ");
                    for(int j = 0 ; j < restPrimes.get(i).size() ; j ++){
                            if(j>0) form.append(" + ");
                            form.append(toAlpha(restPrimes.get(i).get(j).getValue().toString()));
                    }
                    form.append("\n*************************\n");
            }
            return form.toString();
	}
	
	private String toAlpha(String s) {
		String res = "";
		boolean flag = false;
		for(int i=0; i<s.length(); i++){
			char ch = s.charAt(i);
			if(ch !='-'){
				flag = true;
				res += (char) ('A' + i);
				if(ch == '0')
					res += "'"; // ( ' )
			}
		}
		if(!flag)
                    res = "1";
		return res;
	}
	
	public String finalString() {
		return  "***Solution Steps:\n\n" + outPut.toString();
	}

	public ArrayList<Term> processChart(int [][] primeChart){
		ArrayList<Term> primes = new ArrayList<Term>();
		int width = primeChart[0].length ;
		int height = primeChart.length ;
		boolean [] termsDeleted = new boolean [width];
		boolean [] primeDeleted = new boolean [height];
		for(int i = 0 ; i < termsDeleted.length ; i ++)
			termsDeleted[i] = false ;
		for(int i = 0 ; i < primeDeleted.length ; i ++)
			primeDeleted[i] = false ;
		boolean flag = true ;
		while (flag){
			flag = false ;
			int count = 0 ;
			int i , j ;
			int temp = 0 ;
			for(i = 0 ; i < width ; i ++){
				if(termsDeleted[i] == true)
					continue ;
				count = 0;
				for(j = 0 ; j < height ; j ++){
					if(primeDeleted[j] == true )
						continue ;
					if(primeChart[j][i] == 1)
						temp = j ;
					count += primeChart[j][i];}
				if(count == 1){
					flag = true ;
					break ;}
				}
			if(flag == true){
				primes.add(finalPrimeTerms.get(temp));
				primeDeleted[temp] =true ;
				for(int z = 0 ; z < width ; z++)
					if(primeChart[temp][z] == 1)
						termsDeleted[z] = true ;
			}
			
		}
		boolean f = false ;
		for(int v = 0 ; v <termsDeleted.length ; v ++){
			if(termsDeleted[v] == false){
				f=true ;
				break;}}
		if(f){
		ArrayList<String> temp = petrickMethod(termsDeleted , primeDeleted ,primeChart );
		ArrayList<String> poss = processMultiplication(temp);
		for(int i = 0 ; i < poss.size() ; i ++){
			restPrimes.add(new ArrayList<Term>());
				for(int j = 0 ; j < poss.get(i).length() ; j ++){
					restPrimes.get(i).add(finalPrimeTerms.get((int)poss.get(i).charAt(j)-48));}}}
		return primes ;
	}
	
	public ArrayList<String> processMultiplication(ArrayList<String> temp){
		int minimumlength = 10000000 ;
		ArrayList<String> possibles = new ArrayList<String>(); 
		for(int i = 0 ; i < temp.size() ; i ++)
			if(temp.get(i).length() < minimumlength)
				minimumlength = temp.get(i).length() ;
		for(int i = 0 ; i < temp.size() ; i ++)
			if(temp.get(i).length() == minimumlength)
				possibles.add(temp.get(i));
		return possibles;
	}
	public ArrayList<String> petrickMethod(boolean [] termsDeleted ,boolean [] primeDeleted , int [][] primeChart ){
		Stack<ArrayList<String>> multi = new Stack<ArrayList<String>>() ;
		for(int i = 0 ; i < termsDeleted.length ; i ++){
			if(termsDeleted[i] == true)
				continue ;
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0 ; j < primeDeleted.length ;  j ++){
				if(primeChart[j][i] == 1)
					temp.add(Integer.toString(j));
			}
			multi.push(temp);
			}
		while(true){
			ArrayList<String> cache = new ArrayList<String>();
			ArrayList<String> term1 = multi.pop();
			if(multi.size() == 0)
				return term1 ;
			ArrayList<String> term2 = multi.pop();
			for(int i = 0 ; i < term1.size() ; i ++){
				for(int j = 0 ;j < term2.size() ; j ++){
					StringBuilder res = new StringBuilder();
					res.append(term1.get(i));
					if(res.lastIndexOf(term2.get(j)) == -1)
						res.append(term2.get(j));
					cache.add(res.toString());
				}}
			multi.push(cache);
		}
				
	}
	public void getPrimeImplicants(){
		for(int i=0; i<G.size(); i++){
			for(int j=0; j<G.get(i).size(); j++){
				for(int k=0; k<G.get(i).get(j).size(); k++){
					if(!G.get(i).get(j).get(k).isDeleted() && !G.get(i).get(j).get(k).isDontCare())
						finalPrimeTerms.add(G.get(i).get(j).get(k));
				}
			}
		}
	}
	public void createChart (){
		int [] [] primeChart = new int [finalPrimeTerms.size()][minterms.size()] ;
		for(int i = 0 ; i < primeChart.length ; i ++){
			for(int j = 0 ; j < primeChart[0].length ; j ++){
				primeChart[i][j] = 0 ;
			}}
		for(int i = 0 ; i < finalPrimeTerms.size() ; i ++){
			for(int j = 0 ; j < minterms.size() ; j ++){
				for(int k = 0 ; k < finalPrimeTerms.get(i).getNumbersItCovers().size() ; k ++)
					if(finalPrimeTerms.get(i).getNumbersItCovers().get(k).intValue() == minterms.get(j).intValue())
						primeChart[i][j] = 1 ;
			}
		}
                tempChart = primeChart ;
		essentials = processChart(primeChart);
	}
        
	public void printChart(){
            outPut.append("\n***Prime Chart:\n");
            for(int i = 0 ; i < minterms.size() ; i ++)
                outPut.append(minterms.get(i)+" ");
            outPut.append("\n");
            for(int i = 0 ; i < tempChart.length ; i ++){
                for(int j = 0 ; j < tempChart[0].length ; j++){
                        outPut.append(tempChart[i][j]);
                        if(minterms.get(j) > 10) outPut.append(" ");
                        if(minterms.get(j) == 1 || minterms.get(j) == 0){
                        outPut.append(" ");}
                        else{
                        if(Math.log10(minterms.get(j))== (int)Math.log10(minterms.get(j)) && minterms.get(j)!=1)
                            outPut.append(" ");
                        for(int g=0; g<Math.log10(minterms.get(j)); g++)
                           outPut.append(" ");}
                }
                outPut.append(" ");
                outPut.append(toAlpha(finalPrimeTerms.get(i).getValue()));
                outPut.append("\n");
            }
            outPut.append("\n=================n");
        }
}
	