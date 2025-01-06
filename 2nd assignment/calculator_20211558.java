package chap6;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class calculator_20211558 {
	private Frame frame;
	private TextField display;
	private String curr = ""; // 현재 입력 문자
	private String total = ""; // 전체 수식 저장
	private int flag = 0; // 처음과 AC를 눌렀을 때 나오는 0을 처리하기 위한 플래그 변

	public calculator_20211558() {
		frame = new Frame("calculator_20211558");
		frame.setSize(700, 500);
		frame.setLayout(new BorderLayout());
		
		display = new TextField("0");
		display.setFont(new Font("Arial", Font.BOLD, 40));
		display.setEditable(false);
		
		frame.add(display, BorderLayout.NORTH);
		
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new GridLayout(5, 6, 6, 6));

		String[] buttons = {
				"", "x!", "(", ")", "%", "AC",
				"sin", "ln", "7", "8", "9", "/",
				"cos", "log", "4", "5", "6", "*",
				"tan", "√", "1", "2", "3", "-",
				"", "^", "0", ".", "=", "+" };

		for (int i = 0; i < buttons.length; i++) {
			String text = buttons[i];	
			Button button = new Button(text);
			button.setFont(new Font("Arial", Font.BOLD, 28));
			
			if (text.isEmpty()) button.setVisible(false);
			
			if (text.equals("=") || text.equals("AC")) button.setForeground(Color.BLUE);
			else if (text.matches("[0-9.]")) button.setForeground(Color.BLACK);
			else button.setForeground(Color.GRAY);
			
			button.addActionListener(new ButtonClickListener());
			buttonPanel.add(button);
		}

		frame.add(buttonPanel, BorderLayout.CENTER);

		WindowDestroy listener = new WindowDestroy(); 
		frame.addWindowListener(listener); 
		frame.setVisible(true);
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(flag == 0) { // 처음과 AC 눌렀을 때 0으로 시작
				curr += "0";
				total += "0";
				display.setText("0");
			}
            
			if ("0123456789".contains(command)) {
				if(total.length() == 1) { // AC 이후 0만 남았을 때 바로 0 사용 가능
					if(total.charAt(0) == '0') {
						curr = command;
						total = command;
						display.setText(total);
					}
					else {
						curr += command;
						total += command;
						display.setText(total);
					}
				}
				else {
					curr += command;
					total += command;
					display.setText(total);
				}
			} 
			else if (command.equals(".")) {
				if (!curr.contains(".")) {
					curr += ".";
					total += ".";
					display.setText(total);
				}
			} 
			else if (command.equals("AC")) {
				curr = "0";
				total = "0";
				display.setText("0");
				flag = 0;
			} 
			else if ("+-*/^".contains(command)) Op(command);
			else if (command.equals("(") || command.equals(")")) {
				total += " " + command + " ";
				display.setText(total);
			} 
			else if (command.equals("=")) {
				try {
					double result = Cal(total);
					if(isInt(result)) { // 계산 결과가 정수면 정수 형태로 출력
						int num = (int)result;
						display.setText(String.valueOf(num));
						total = String.valueOf(num);
					}
					else {
						result = Math.round(result * 100000000) / 100000000.0;
						display.setText(String.valueOf(result));
						total = String.valueOf(result);
					}
					curr = "";
				} 
				catch (Exception ex) {
					display.setText("Error");
				}
			} 
			else if (command.equals("%")) {
				total += " % ";
				display.setText(total);
			}
			else if (command.equals("√")) {
				total += " √ ";
				display.setText(total);
			} 
			else if (command.equals("x!")) {
				if (!curr.isEmpty()) {
					total += " !";
						display.setText(total);
						curr = "";
				}
			} 
			else if (command.equals("ln")) {
				total += " ln ";
				display.setText(total);
			} 
			else if (command.equals("log")) {
				total += " log ";
				display.setText(total);
			} 
			else if (command.equals("sin")) {
				total += " sin ";
				display.setText(total);
			} 
			else if (command.equals("cos")) {
				total += " cos ";
				display.setText(total);
			}
			else if (command.equals("tan")) {
				total += " tan ";
				display.setText(total);
			}

			flag = 1; // 계속 수식을 입력할 때 0과 관련된 초기화 과정을 따라가지 않음
		}
	}

	private void Op(String command) {
		total = total.trim();
		char last = total.charAt(total.length() - 1);

		if ("+-*/^".indexOf(last) >= 0) { // - 제외 중복된 연산자 처리
			int flag = 0;
			
			if(total.length() > 1) { // - 와 다른 연산자 중복 입력하면 연산자 계속 입력되는 현상 방지
				char last_before;
				last_before = total.charAt(total.length() - 3);
				if ("+-*/^".indexOf(last_before) >= 0) flag = 1;
			}
			
			if(flag == 0) {
				if(last != '-' && last != '+' && command == "-") total += " " + command + " ";    			
				else total = total.substring(0, total.length() - 1) + command + " ";
			}		
		} 
		else total += " " + command + " ";

		curr = "";
		display.setText(total);
	}

	// 계산 수행
	private double Cal(String total) {
		String[] tokens = total.split(" ");
		Stack<Double> values = new Stack<>();
		Stack<String> ops = new Stack<>();

		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.isEmpty()) continue;

			if (isNum(token)) values.push(Double.parseDouble(token));	
			else if (token.equals("(")) ops.push(token); // 괄호 연산 처리
			else if (token.equals(")")) {
				while (!ops.isEmpty() && !ops.peek().equals("(")) {
					values.push(apply(ops.pop(), values.pop(), values.pop()));
				}
				ops.pop();
			} 
			else if (isOp(token)) {
				// 연산자 2개가 연속으로 있고 뒤에가 - 일 때, 이어지는 숫자를 음수로써 계산
				if (token.equals("-") && (i == 0 || isOp(tokens[i - 1]) || tokens[i - 1].equals("("))) {
					if (i + 1 < tokens.length && isNum(tokens[i + 1])) {
						values.push(-Double.parseDouble(tokens[++i]));
						continue;
					}
				}

				while (!ops.isEmpty() && seq(ops.peek()) >= seq(token)) {
					values.push(apply(ops.pop(), values.pop(), values.pop()));
				}
				ops.push(token);
			}
			else if (token.equals("%")) {
				double result = values.pop() * 0.01;
				values.push(result);
			}
			else if (token.equals("√")) values.push(Math.sqrt(values.pop()));
			else if (token.equals("ln")) {
				double result = values.pop();
				if(result <= 0) throw new ArithmeticException("Error"); // 0 or 음수일 때 예외 처리
				else values.push(Math.log(result));
				
			}
			else if (token.equals("log")) {
				double result = values.pop();
				if(result <= 0) throw new ArithmeticException("Error"); // 0 or 음수일 때 예외 처리
				else values.push(Math.log10(result));
			}
			else if (token.equals("!")) values.push((double)factorial(values.pop().intValue()));
			else if (token.equals("sin")) {
				double result = Math.sin(Math.toRadians(values.pop()));
				values.push(Math.round(result * 100000000) / 100000000.0);
			} 
			else if (token.equals("cos")) {
				double result = Math.cos(Math.toRadians(values.pop()));
				values.push(Math.round(result * 100000000) / 100000000.0);
			} 
			else if (token.equals("tan")) {
				double num = values.pop();
				if(num % 90 == 0); // 발산하는 경우는 에러처리
				else {
					double result = Math.tan(Math.toRadians(num));
					values.push(Math.round(result * 100000000) / 100000000.0);
				}
			}
		}

		while (!ops.isEmpty()) values.push(apply(ops.pop(), values.pop(), values.pop()));

		return values.pop();
	}
    
	private int factorial(int n) {
		if (n == 0 || n == 1) return 1;
		return n * factorial(n - 1);
	}
    
	private boolean isNum(String token) {
		try {
			Double.parseDouble(token);
			return true;
		} 
		catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isInt(double value) {
		return value == Math.floor(value) && !Double.isInfinite(value);
	}
	
	private boolean isOp(String token) {
		return "+-*/^".contains(token);
	}

	private int seq(String op) {
		if (op.equals("+") || op.equals("-")) return 1;
		if (op.equals("*") || op.equals("/")) return 2;
		return 0;
	}

	private double apply(String op, double b, double a) {
		switch (op) {
			case "+": return a + b;
			case "-": return a - b;
			case "*": return a * b;
			case "/": {
				if (b == 0) throw new ArithmeticException("Error"); // 0으로 나눌 때 예외 처리
	            return a / b;
			}
			case "^": return Math.pow(a, b);
			default: return 0;
		}
	}

	public static void main(String[] args) {
		new calculator_20211558();
	}
}

class WindowDestroy extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
}
