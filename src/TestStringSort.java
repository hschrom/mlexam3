import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TestStringSort {

	public TestStringSort() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String c[] = {"CLASSIFIER.08",
		"CLASSIFIER.07",
		"CLASSIFIER.05",
		"CLASSIFIER.04",
		"CLASSIFIER.03",
		"CLASSIFIER.02",
		"CLASSIFIER.01",
		"ClASSIFIER.06"};
		
		List<String> list =new ArrayList<String>();
		for(String s: c) {
			System.out.println(s);
			list.add(s);
		}
		
		System.out.println("\n");
		 Collections.sort(list, Collator.getInstance());
		// Collections.sort(list );
		 for(String s: list)
			 System.out.println(s);
		
		 String s1 = "ClASSIFIER.08";
		 String s2 = "ClASSIFIER.06";
		 
		 System.out.println(s1.compareTo(s2));
	}

}
