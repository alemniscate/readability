package readability;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        String fileName = "in.txt";
        String fileText = ReadText.readAllWithoutEol(fileName);
        Statistics statistics = new Statistics(fileText);
        Score score = new Score(statistics);
 
        statistics.print();

        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        scanner.close();

        System.out.println();
        switch (command) {
            case "ARI":
                score.printARI();
                break; 
            case "FK":
                score.printFK();
                break;
            case "SMOG":
                score.printSMOG();
                break;
            case "CL":
                score.printCL();
                break;
            case "all":
                double avg = 0;
                avg += score.printARI();
                avg += score.printFK();
                avg += score.printSMOG();
                avg += score.printCL();
                avg /= 4.0;
                System.out.println();
                System.out.println(String.format("This text should be understood in average by %.2f year olds.", avg));
            break;
        }
    }
}
class Statistics {
    int sentenceCount;
    int wordCount;
    int charCount;
    int syllableCount;
    int polySyllableCount;
    String fileText;

    Statistics(String fileText) {
        this.fileText = fileText;

        String[] sentences = fileText.split("[.!?]");
        sentenceCount = sentences.length;
        wordCount = 0;
        charCount = 0;
        syllableCount = 0;
        polySyllableCount = 0;
        for (String sentence: sentences) {
            String[] words = sentence.trim().split("\\s+");
            wordCount += words.length;
            for (String word: words) {
                charCount += word.length();
                int syllables = counSyllables(word);
                syllableCount += syllables;
                if (syllables > 2) {
                    polySyllableCount++;
                }
            }
        }
        charCount += sentenceCount - 1;
        if (fileText.endsWith(".") || fileText.endsWith("!") || fileText.endsWith("?")) {
            charCount++;
        }
    }

    int counSyllables(String wordInput) {
        /**
            1. Count the number of vowels in the word.
            2. Do not count double-vowels (for example, "rain" has 2 vowels but only 1 syllable).
            3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" has 1 syllable).
            4. If at the end it turns out that the word contains 0 vowels, then consider this word as a 1-syllable one.
        */
        String word = wordInput.toLowerCase();
        
        int vowels = (int) word
            .chars()
            .filter(c -> c == 'a' || c == 'i' || c == 'u' || c == 'e' || c == 'o' || c == 'y')
            .count();
    
        int doubleVowels = 0;
        doubleVowels += addDVCount(word, "aa");
        doubleVowels += addDVCount(word, "ai");
        doubleVowels += addDVCount(word, "au");
        doubleVowels += addDVCount(word, "ae");
        doubleVowels += addDVCount(word, "ao");
        doubleVowels += addDVCount(word, "ay");
        doubleVowels += addDVCount(word, "ia");
        doubleVowels += addDVCount(word, "ii");
        doubleVowels += addDVCount(word, "iu");
        doubleVowels += addDVCount(word, "ie");
        doubleVowels += addDVCount(word, "io");
        doubleVowels += addDVCount(word, "iy");
        doubleVowels += addDVCount(word, "ua");
        doubleVowels += addDVCount(word, "ui");
        doubleVowels += addDVCount(word, "uu");
        doubleVowels += addDVCount(word, "ue");
        doubleVowels += addDVCount(word, "uo");
        doubleVowels += addDVCount(word, "uy");
        doubleVowels += addDVCount(word, "ea");
        doubleVowels += addDVCount(word, "ei");
        doubleVowels += addDVCount(word, "eu");
        doubleVowels += addDVCount(word, "ee");
        doubleVowels += addDVCount(word, "eo");
        doubleVowels += addDVCount(word, "ey");
        doubleVowels += addDVCount(word, "oa");
        doubleVowels += addDVCount(word, "oi");
        doubleVowels += addDVCount(word, "ou");
        doubleVowels += addDVCount(word, "oe");
        doubleVowels += addDVCount(word, "oo");
        doubleVowels += addDVCount(word, "oy");
        doubleVowels += addDVCount(word, "ya");
        doubleVowels += addDVCount(word, "yi");
        doubleVowels += addDVCount(word, "yu");
        doubleVowels += addDVCount(word, "ye");
        doubleVowels += addDVCount(word, "yo");
        doubleVowels += addDVCount(word, "yy");
        vowels -= doubleVowels;
    /*
        if (word.startsWith("y") && !word.startsWith("ya") && !word.startsWith("yi") && !word.startsWith("yu") && !word.startsWith("ye") && !word.startsWith("yo") && !word.startsWith("yy")) {
            vowels--;
        }
    */
        if (word.endsWith("e") && !word.endsWith("ae") && !word.endsWith("ie") && !word.endsWith("ue") && !word.endsWith("ee") && !word.endsWith("oe") && !word.endsWith("ye")) {
            vowels--;
        }
    
        vowels = Math.max(vowels, 1);
    
        return vowels;
    }
    
    int addDVCount(String word, String doubleVowel) {
        if (word.matches(".*" + doubleVowel + ".*")) {
                return 1;
        }
        return 0;
    }

    void print() {
        System.out.println("The text is:");
        System.out.println(fileText);
        System.out.println();
        System.out.println("Words: " + wordCount);
        System.out.println("Sentences: " + sentenceCount);
        System.out.println("Characters: " + charCount);
        System.out.println("Syllables: " + syllableCount);
        System.out.println("Polysyllables: " + polySyllableCount);
    }
}

class Score {

    Statistics st;
    double score;
    String age;
    String type;
    String title;
//    List<String> ageList = List.of("5-6","6-7","7-9","9-10","10-11","11-12","12-13","13-14","14-15","15-16","16-17","17-18","18-24","24+");
    List<String> ageList = List.of("6","7","9","10","11","12","13","14","15","16","17","18","24","24+");

    Score(Statistics st) {
        this.st = st;
    }

    double printARI() {
        title = "Automated Readability Index";
        score = 4.71 * ((double) st.charCount / (double) st.wordCount) + 0.5 * ((double) st.wordCount / (double) st.sentenceCount) - 21.43;
        age = calcAge(score);
        print();
        return Double.parseDouble(age);
    }

    double printFK() {
        title = "Flesch–Kincaid readability tests";
        score = 0.39 * ((double) st.wordCount / (double) st.sentenceCount)
                + 11.8 * ((double) st.syllableCount / (double) st.wordCount)
                - 15.59;
        age = calcAge(score);        
        print();
        return Double.parseDouble(age);
    }

    double printSMOG() {
        title = "Simple Measure of Gobbledygook";
        score = 1.043 * Math.sqrt((double) st.polySyllableCount * 30.0 / (double) st.sentenceCount) + 3.1291;
        age = calcAge(score);        
        print();
        return Double.parseDouble(age);
    }
    
    double printCL() {
        title = "Coleman–Liau index";
        double L = (double) st.charCount * 100.0 / (double) st.wordCount;
        double S = (double) st.sentenceCount * 100.0 / (double) st.wordCount;
        score = 0.0588 * L - 0.296 * S - 15.8;
        age = calcAge(score);        
        print();
        return Double.parseDouble(age);
    }

    String calcAge(double score) {
        int ageIndex = (int) Math.floor(score + 0.5) - 1;
        ageIndex = Math.min(ageIndex, ageList.size() - 1);
        return ageList.get(ageIndex);
    }

    void print() {
        System.out.println(String.format("%s: %.2f (about %s year olds).", title, score, age));
    }
}

class ReadText {

    static boolean isExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    static String getAbsolutePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    static String readAllWithoutEol(String fileName) {
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));   
            text =  br.lines().collect(Collectors.joining());        
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return text;
    }

    static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));   
            lines =  br.lines().collect(Collectors.toList());        
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lines;
    }

    static String readAll(String fileName) {
        char[] cbuf = new char[4096];
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));           
            while (true) {
                int length = br.read(cbuf, 0, cbuf.length);
                if (length != -1) {
                    sb.append(cbuf, 0, length);
                }
                if (length < cbuf.length) {
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}

class WriteText {

    static void writeAll(String fileName, String text) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(text, 0, text.length());
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
