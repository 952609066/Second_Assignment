package cn.lidan.start;

import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import com.mysql.cj.jdbc.Driver;
import lindan.cn.util.Const;

public class Calculator extends JFrame implements ActionListener{
	//North component:
	private JPanel jp_north = new JPanel();
	private JTextField input_text = new JTextField();
	private JButton c_Button = new JButton("C");
	
	//Central component:
	private JPanel jp_centre = new JPanel();
	String content = "";
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/dbtest1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";//This is the database where it contains the wanted table
   // The USER ID and PASSPORT:
   static final String USER = "root";
   static final String PASS = "ycr.020910";
   //Written by Chengrui Yuan
   //Date:2023.10.21
   
	public Calculator () throws HeadlessException{
		this.init();
		this.addNorthComponent();
		this.addCentreButton();
	}
	//Doing initialization:
	public void init() {
		this.setTitle(Const.TITLE);
		this.setSize(Const.FRAME_H,Const.FRAME_W);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//Adding north component:
	public void addNorthComponent() {
		//Setting basic attribute of this calculator.
		this.input_text.setPreferredSize(new Dimension(300,50));
		jp_north.add(input_text);
		this.c_Button.setForeground(Color.black);
		jp_north.add(c_Button);
		c_Button.setFont(new Font("粗体",Font.BOLD,16));
		c_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				input_text.setText("");
				content = "";
			}
		});
		this.add(jp_north,BorderLayout.NORTH);
	}
	//Adding central component:
	public void addCentreButton(){
		String [] txt = {"1","2","3","+","ans","history","4","5","6","-","log","sin","7","8","9","*","^","cos","0",".","=","/","%","tan"};
		String reg = "[\\+\\-\\*\\/\\.\\=\\^\\%]";
		this.jp_centre.setLayout(new GridLayout(4,6));
		for (int i = 0;i<24;i++) {
			String temp = txt[i];
			JButton button = new JButton();
			button.setText(temp);
			if(temp.matches(reg)) {
				button.setFont(new Font("粗体",Font.BOLD,18));
				button.setForeground(Color.BLACK);
			}else{
				button.setFont(new Font("粗体",Font.BOLD,18));
				button.setForeground(Color.GRAY);
			}
			button.addActionListener(this);
			jp_centre.add(button);
		}
		this.add(jp_centre,BorderLayout.CENTER);
	}
	
	//Main function:
	public static void main(String[] args) {
		   Calculator calculator = new Calculator();
		   calculator.setVisible(true);
	}

	private String preInput = null;
	private String operator = null; 
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String clickStr = e.getActionCommand();
		if(".0123456789".indexOf(clickStr) != -1) {
			//if enter the numbers on the calculator
			this.input_text.setText(input_text.getText() + clickStr);
			this.input_text.setHorizontalAlignment(JTextField.RIGHT);
			//Record the input into a string.
			content+=clickStr;
		}else if(clickStr.matches("[\\+\\-\\*\\/\\^\\%]")||clickStr.matches("log")|| clickStr.matches("sin") || clickStr.matches("cos") || clickStr.matches("tan")) {
			//if click the operator.
			operator = clickStr;
			preInput = this.input_text.getText();
			this.input_text.setText("");
			//Record the operator into the string.
			content+=clickStr;
		}
		else if(clickStr.equals("=")) {
			//if the clicked button is "=";
			content += clickStr;
			 Double preValue = 0.0;
	         Double latValue = 0.0;
	            if (preInput.matches("-?\\d+(\\.\\d+)?")){
	                preValue = Double.valueOf(preInput);
	            }
	            if (this.input_text.getText().matches("-?\\d+(\\.\\d+)?")) {
	                latValue = Double.valueOf(this.input_text.getText());
	            }
	        Double result = null;
			boolean flag = true;
			String error = null;
			switch(operator) {
			//doing calculation:
			case "+":
					result = preValue + latValue;
					break;
			case "-":
					result = preValue - latValue;
					break;
			case "*":
					result = preValue * latValue;
					break;
			case "/":
					if(latValue != 0) {
						result = preValue/latValue;
						break;
					}else {
						flag = false;
						error = "Invalid Operation!";
						break;
					}
			case"^":
					result = Math.pow(preValue, latValue);
					break;
			case"%":
				if(latValue != 0) {
					result = preValue%latValue;
					break;
				}else {
					flag = false;
					error = "Invalid Operation!";
					break;
				}
			 case"log":
					result = Math.log(preValue)/Math.log(latValue);
					break;
			 case "sin":
                 	BigDecimal   SINresult   =   new   BigDecimal(Math.sin(Math.toRadians(latValue)));
                 	result = SINresult.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                 	break;
             case "cos":
                 	BigDecimal   COSresult   =   new   BigDecimal(Math.cos(Math.toRadians(latValue)));
                 	result = COSresult.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                 	break;
             case "tan":
                 	BigDecimal   TANresult   =   new   BigDecimal(Math.tan(Math.toRadians(latValue)));
                 	result = TANresult.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                 	break;
					}
			content += result;
			
			//Display the result into the given column.
			if(flag)
				this.input_text.setText(result.toString());
			else
				this.input_text.setText(error);
			
			
			 try {
	                Class.forName(JDBC_DRIVER);
	                Connection conn = DriverManager.getConnection(DB_URL,USER, PASS);    //construct the connection to MySQL database
	                String histContent = "'" + content + "'";           //storing the previous operation
	                int countNum = 1;
	                String mySql = "";                              //initialize the MySql sentence
	                Statement statement = conn.createStatement();
	                mySql = "update calculator_history set CONTENT = " + result + " where ID = " + countNum;
	                statement.executeUpdate(mySql);                 //execute MySql sentence to update the latest result on first row in database
	                countNum++;
	                mySql = "select ID,CONTENT from calculator_history";
	                Statement stmt = conn.createStatement();
	                ResultSet res = stmt.executeQuery(mySql);           //Get messages from database and record in res
	                res.next();                                         //Ignore the first row and get the latest answer from database.
	                mySql = "update calculator_history set CONTENT = " + histContent + " where ID = " + countNum;           //Get the previous operations to following rows in database
	                while (res.next()) {                         		//if there are exist next row in database
	                    histContent = "'" + res.getString("CONTENT") + "'";          //remember the current row for it will be rewrote by following code
	                    statement.executeUpdate(mySql);
	                    countNum++;
	                    mySql = "update calculator_history set CONTENT = " + histContent + " where ID = " + countNum;
	                }
	                //release obtained resources
	                statement.close();
	                stmt.close();
	                conn.close();
	            } catch (SQLException ex) {
	                throw new RuntimeException(ex);
	            } catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		else if (clickStr.equals("ans")) {
            try {
                Class.forName(JDBC_DRIVER);
                Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
                Statement statement = connection.createStatement();
                String mySql = "select ID,CONTENT from calculator_history";
                ResultSet res = statement.executeQuery(mySql);
                Statement stmt = connection.createStatement();
                res.next();
                /*if (!res.next()) {  //ResultSet 为空 
                	}else{//ResultSet 不为空
                	}*/
                //obtain the first row in content column of database.
                String preResult = res.getString("CONTENT"); 
                
                //print the message obtained in display window.
                this.input_text.setText(preResult);                        
                res.close();
                stmt.close();
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
			
			else if(clickStr.equals("history")) {
				//initialize a string to conserve the information from database.
				String[] hisContent = new String[10];     
	            //Driver registration.
	            try {
	                Class.forName(JDBC_DRIVER);
	                Connection conn = DriverManager.getConnection(DB_URL,USER, PASS);
	                Statement statement = conn.createStatement();
	                String mySql = "select ID,CONTENT from calculator_history";
	                ResultSet res = statement.executeQuery(mySql);
	                Statement stmt = conn.createStatement();
	                int countNum = 0;
	                //skip to second row in database
	                res.next();                               
	                while (res.next()) {
	                	//receive the message from database and store them in String array for examining.
	                    hisContent[countNum] = res.getString("CONTENT");          
	                    countNum++;
	                }
	                res.close();
	                stmt.close();
	                conn.close();

	            } catch (SQLException e1) {
	                e1.printStackTrace();
	            } catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            			//construct another window in java to show history operations
	    					JFrame j_frame = new JFrame();
	    		            j_frame.setTitle("HISTORY_CONTENT");
	    		            JPanel p = new JPanel();
	    		            JTextField Text[]= new JTextField[10];
	    		            for(int i = 0; i < 10; i++) {
	    		                Text[i] = new JTextField();
	    		                Text[i].setEditable(false);
	    		                Text[i].setHorizontalAlignment(JTextField.CENTER);
	    		                Text[i].setPreferredSize(new Dimension(200,100));
	    		              //set the blocks in window to represent the information retain in String array
	    		                Text[i].setText(hisContent[i]);                  
	    		                Font font=new Font("",Font.BOLD,35);
	    		                Text[i].setFont(font);
	    		                p.add(Text[i],BorderLayout.NORTH);
	    		            }
	    		            j_frame.add(p);
	    		            j_frame.setBounds(600, 200, 500, 600);
	    		            j_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		            j_frame.setVisible(true);
	    		}
			}

}


