package first_assignment;

public class problem2 {

	public static void main(String[] args) {
		for(int i = 1; i <= 34; i++) {
			if(i % 2 == 0) {
				System.out.println();
				continue;
			}
			
			for(int j = 1; j <= 17; j++) {
				if(i / 2 + 1== j) System.out.print("*");
				else if(i / 2 + 1 + j == 18) System.out.print("*");
				else System.out.print(" ");
			}
			System.out.println();
		}
	}

}
