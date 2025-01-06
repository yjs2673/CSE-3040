package first_assignment;

import java.util.Scanner;

public class problem4 {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String sentence = input.nextLine();
		
		int words = sentence.length();
		for(int i = words - 1; i >= 0; i--) {
			char word = sentence.charAt(i);
			System.out.print(word);
		}
		
		input.close();
	}

}
