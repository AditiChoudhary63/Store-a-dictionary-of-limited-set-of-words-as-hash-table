import java.io.*;
import java.util.*;
import java.util.regex.*;

class Spelling {
    private HashMap<String, Integer> nWords;
    public Spelling(String file) throws IOException {
        nWords = new HashMap<String, Integer>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        // This pattern matches any word character (letters or digits)
        Pattern p = Pattern.compile("\\w+");
        for(String temp = ""; temp != null; temp = in.readLine()){
            Matcher m = p.matcher(temp.toLowerCase());
            while(m.find())
                nWords.put((temp = m.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
        }
        in.close();
    }
    private ArrayList<String> edits(String word) {
        ArrayList<String> result = new ArrayList<String>();
        // All deletes of a single letter
        for(int i=0; i < word.length(); ++i)
            result.add(word.substring(0, i) + word.substring(i+1));
       // All swaps of adjacent letters
        for(int i=0; i < word.length()-1; ++i)
            result.add(word.substring(0, i) + word.substring(i+1, i+2) +
                    word.substring(i, i+1) + word.substring(i+2));
        // All replacements of a letter
        for(int i=0; i < word.length(); ++i)
            for(char c='a'; c <= 'z'; ++c)
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
        // All insertions of a letter
        for(int i=0; i <= word.length(); ++i)
            for(char c='a'; c <= 'z'; ++c)
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
        return result;
    }
   public String correct(String word) {
        // If in the dictionary, return it as correctly spelled
        if(nWords.containsKey(word))
            return word;
        ArrayList<String> list = edits(word);  // Everything edit distance 1 from word
        HashMap<Integer, String> candidates = new HashMap<Integer, String>();
        // Find all things edit distance 1 that are in the dictionary.  Also remember
        //   their frequency count from nWords.
        // (Note if equal frequencies the last one will be the one remembered.)
        for(String s : list)
            if(nWords.containsKey(s))
                candidates.put(nWords.get(s),s);
        // If found something edit distance 1 return the most frequent word
        if(candidates.size() > 0)
            return candidates.get(Collections.max(candidates.keySet()));
        // Find all things edit distance 1 from everything of edit distance 1.  These
        // will be all things of edit distance 2 (plus original word).  Remember frequencies
        for(String s : list)
            for(String w : edits(s))
                if(nWords.containsKey(w))
                    candidates.put(nWords.get(w),w);
        // If found something edit distance 2 return the most frequent word.
        // If not return the word with a "?" prepended.(Original just returned the word.)
        return candidates.size() > 0 ?
                candidates.get(Collections.max(candidates.keySet())) : "?" + word;
    }
   public void checkFile(String inputFilename) {
        try {
           //inputFilename = "C:\\Users\\user\\Downloads\\test.txt";
            FileWriter fw = new FileWriter("corrected " + inputFilename);
            Scanner sc = new Scanner(new File(inputFilename));
            while (sc.hasNext()) {
                fw.write(correct(sc.next()) + " ");
            }
            fw.close();
        }catch(IOException e) {
            System.out.println("FNF");
        }
   } 
    public static void main(String args[]) throws IOException {
        try {
            Spelling corrector = new Spelling("en-US.txt");
            Scanner input = new Scanner(System.in);
            System.out.print("Enter input filename : ");
            String inputFilename = input.next();
            corrector.checkFile(inputFilename);
        }catch(FileNotFoundException e){
            System.out.println("FNF");
        } 
    }
}

