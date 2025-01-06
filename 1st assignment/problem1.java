package first_assignment;

public class problem1 {

	public static void main(String[] args) {
		int dice1 = 0, dice2 = 0;
		while(dice1 + dice2 != 7) {
			dice1 = (int)(6 * Math.random()) + 1;
			dice2 = (int)(6 * Math.random()) + 1;
			System.out.print("(" + dice1 + "," + dice2 + ") ");
		}
	}
	
}
