package bankingjdbc;
 
import java.text.*; //Input/Output
import java.util.*; //User Input
import java.awt.*;  //Graphics Class
import java.awt.event.*;    //User Interface
import javax.swing.*;   //GUI
import javax.swing.table.DefaultTableModel; //JTable
import java.sql.*;  //Database
 
class BankingJDBC implements ActionListener{

/* BANKING TRANSACTIONS:
    1. Deposit
    2. Cash Withdrawal
    3. Cash Transfer
    4. Bill Payment
    5. Compute Interest
*/
 
JFrame mainframe = new JFrame("TECHNOLOGICAL BANK OF THE PHILIPPINES"); //Sample Bank Name
JLayeredPane mainlayerpane = new JLayeredPane();
JPanel mainpanel = new JPanel(), loginpanel = new JPanel(), account_mainpanel = new JPanel(), transaction_history = new JPanel();
JButton login = new JButton("PROCEED"), deposit = new JButton("DEPOSIT"), withdraw = new JButton("CASH WITHDRAWAL"),
transfer = new JButton("CASH TRANSFER"), pay = new JButton("BILL PAYMENT"), compute = new JButton("COMPUTE INTEREST");
JTextField login_id = new JTextField(); JPasswordField login_pin = new JPasswordField();
JTextField account_holder = new JTextField(), balance = new JTextField();
JLabel id_display = new JLabel(),currency = new JLabel("PHP");
JTabbedPane tabs = new JTabbedPane();
 
DefaultTableModel model = new DefaultTableModel(){ 
  public boolean isCellEditable(int row, int column){ 
  return false;
  }
};
 
JTable table = new JTable(model);
JScrollPane sp = new JScrollPane(table);
int initial_row_count=0, rowcount=0;
 
String[] myinfo = new String[5];
String[] Transaction_info = new String[6];
String using_id="", query="",generatedString="", transaction_type="", Transaction_write="", amount;
String name="", receiver="";
float total;
 
DecimalFormat DCF = new DecimalFormat("0.00");
    
BankingJDBC(){
 
mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
mainframe.setVisible(true);
mainframe.setSize(1000,500);
mainframe.setLayout(null);
mainframe.setResizable(false);
mainframe.setLocationRelativeTo(null);
mainlayerpane.setLayout(null);
mainlayerpane.setBounds(0,0,1000,500);
mainframe.add(mainlayerpane);
 
loginpanel.setBounds(0,0,1000,500);
loginpanel.setLayout(null);
loginpanel.setBackground(Color.cyan);
mainlayerpane.add(loginpanel, Integer.valueOf(0));
    
JLabel id_label = new JLabel("ACCOUNT ID: "),pin_label = new JLabel("PIN NUMBER: ");
JLabel bankname = new JLabel("TECHNOLOGICAL BANK OF THE PHILIPPINES");
bankname.setFont(new Font("Garamond", Font.BOLD,30));
bankname.setBounds(150,100,1000,50);
loginpanel.add(bankname);
loginpanel.setFocusable(false);
login.setBounds(450,250,100,30);
login_id.setBounds(400,150,200,30);
login_pin.setBounds(400,200,200,30);
id_label.setBounds(300,150,100,30);
pin_label.setBounds(300,200,100,30);
loginpanel.add(id_label);
loginpanel.add(pin_label);
loginpanel.add(login_id);
loginpanel.add(login_pin);
loginpanel.add(login);
login.addActionListener(this);
}
 
public void Account(){
  
  Account_Data();
        
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from customer_data where customer_id= "+myinfo[2]+";");
 
        while(rs.next()){
            name = rs.getString(2)+" "+rs.getString(4);
            System.out.println(name);
        }
        
        con.close();
    } 
    
    catch (Exception e){
        System.out.println(e);
    }
        
    mainlayerpane.remove(loginpanel);
        
    tabs.setBounds(2,0,980,495);
    mainlayerpane.add(tabs);
    
    account_mainpanel.setBounds(0,0,980,495);
    account_mainpanel.setLayout(null);
    account_mainpanel.setBackground(Color.cyan);
 
    transaction_history.setBounds(0,0,980,495);
    transaction_history.setLayout(null);
    transaction_history.setBackground(Color.cyan);
 
    id_display.setBounds(15,60,150,50);
    id_display.setText("ACCOUNT ID: "+using_id);
    account_mainpanel.add(id_display, Integer.valueOf(1));
 
    account_holder.setBounds(10,10,420,100);
    account_holder.setEditable(false);
    account_holder.setOpaque(false);
    account_holder.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
    account_holder.setFont(new Font("Garamond", Font.BOLD,40));
    account_holder.setText(name);
 
    currency.setBounds(510,10,100,100);
    currency.setFont(new Font("Garamond", Font.BOLD,40));
    account_mainpanel.add(currency, Integer.valueOf(1));
 
    balance.setBounds(500,10,420,100);
    balance.setEditable(false);
    balance.setOpaque(false);
    balance.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
    balance.setFont(new Font("Garamond", Font.BOLD,40));
    balance.setText(myinfo[4]);
    balance.setHorizontalAlignment(JTextField.RIGHT);
    
    account_mainpanel.add(deposit);
    deposit.setBounds(275,125,195,95);
    deposit.setFocusable(false);
    deposit.addActionListener(this);
    account_mainpanel.add(withdraw);
    withdraw.setBounds(475,125,195,95);
    withdraw.setFocusable(false);
    withdraw.addActionListener(this);
    account_mainpanel.add(transfer);
    transfer.setBounds(275,225,195,95);
    transfer.setFocusable(false);
    transfer.addActionListener(this);
    account_mainpanel.add(pay);
    pay.setBounds(475,225,195,95);
    pay.setFocusable(false);
    pay.addActionListener(this);
    account_mainpanel.add(compute);
    compute.setBounds(275,325,395,95);
    compute.setFocusable(false);
    compute.addActionListener(this);
 
    account_mainpanel.add(account_holder);
    account_mainpanel.add(balance);
    tabs.add("ACCOUNT INFORMATION", account_mainpanel);
    tabs.add("ACCOUNT TRANSACTION HISTORY", transaction_history);
 
    transaction_history.add(sp);
    sp.setBounds(0,0,1000,500);
    model.addColumn("TRANSACTION ID");
    model.addColumn("SENDER ID");
    model.addColumn("RECIEVER ID");
    model.addColumn("TRANSACTION DATE");
    model.addColumn("TRANSACTION TYPE");
    model.addColumn("AMOUNT");
    previous_history();
}
    
public boolean Account_Security(){
  
  boolean verify=false;
 
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select account_id,account_pass from account_data where account_id="+login_id.getText()+";");
 
        while(rs.next()){
            String[] user_pass = {login_id.getText(),String.valueOf(login_pin.getPassword())};
        
            if((rs.getString(1).equals(user_pass[0])) && (rs.getString(2).equals(user_pass[1]))){
                using_id = user_pass[0];
                verify=true;
            }
            
            else{
                JOptionPane.showMessageDialog(mainframe, "INCORRECT ACCOUNT ID OR PIN NUMBER", "INVALID INPUT", JOptionPane.ERROR_MESSAGE);
            }
        }
            
        con.close();
    }
    
    catch (Exception e){
        System.out.println(e);
    }
  
    return verify;
}
    
public void Account_Data(){
  
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from account_data "+"where account_id= "+using_id+";");
        
        while(rs.next()){
            for(int x = 0; x < 5; x++){
                myinfo[x] = rs.getString(x+1);
            }
        }
    
        con.close();
    } 
    
    catch (Exception e){
        System.out.println(e);
    }    
}
    
public void Deposit(){
    
    try{    
        amount = JOptionPane.showInputDialog(mainframe, "DEPOSIT AMOUNT:");
        if(amount == null){
            System.out.println("TRANSACTION CANCELLED");
            return;
        }
            
        else if(Double.parseDouble(amount) <= 0){
            JOptionPane.showMessageDialog(mainframe, "CANNOT DEPOSIT LESS THAN PHP 1.00", "MINIMUM DEPOSIT NOT REACHED", JOptionPane.ERROR_MESSAGE);
            Deposit();
            return;
        }
        
        total = Float.parseFloat(myinfo[4]) + Float.parseFloat(amount);
        query = "update account_data set balance="+total+" where account_id= "+using_id+";";
        Transaction_id_Generator();
        Transaction();
        transaction_type="DEPOSIT";
        receiver= using_id;
        JOptionPane.showMessageDialog(mainframe, "DEPOSIT SUCCESSFUL", "TRANSACTION", JOptionPane.PLAIN_MESSAGE);
        WriteHistory();
    } 
    
    catch (Exception e){
        System.out.println("TRANSACTION CANCELLED");
    }    
}
    
public void Cash_Withdrawal(){
  
    try{    
        amount = JOptionPane.showInputDialog(mainframe, "WITHDRAWAL AMOUNT:");
     
        if(Double.parseDouble(amount) > Double.parseDouble(myinfo[4])){
            JOptionPane.showMessageDialog(mainframe, "WITHDRAWAL AMOUNT IS GREATER THAN ACCOUNT BALANCE", "INSUFFICIENT ACCOUNT BALANCE", JOptionPane.ERROR_MESSAGE);
            Cash_Withdrawal();
            return;
        }
  
        else if(amount == null){
            System.out.println("TRANSACTION CANCELLED");
            return;
        }
 
        total = Float.parseFloat(myinfo[4]) - Float.parseFloat(amount);
        query = "update account_data set balance="+total+" where account_id= "+using_id+";";
        Transaction_id_Generator();
        Transaction();
        transaction_type="CASH WITHDRAWAL";
        receiver= using_id;
        JOptionPane.showMessageDialog(mainframe, "WITHDRAWAL SUCCESSFUL", "TRANSACTION", JOptionPane.PLAIN_MESSAGE);
        WriteHistory();
    }
    
    catch (Exception e){
        System.out.println("TRANSACTION CANCELLED");
    }
    
}
 
public void Cash_Transfer(){
  
    receiver = JOptionPane.showInputDialog(mainframe, "ENTER RECIPIENT ACCOUNT NUMBER:");
       
    try{
        if(receiver.equals(using_id)){
            JOptionPane.showMessageDialog(mainframe, "CANNOT TRANSFER TO SAME ACCOUNT", "INVALID TRANSACTION", JOptionPane.WARNING_MESSAGE);
            return;
        }
            
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select account_id, balance from account_data where account_id= "+receiver+";");
    
        while(rs.next()){
            if(rs.getInt(1) == Integer.parseInt(receiver)){
                amount = JOptionPane.showInputDialog(mainframe, "CASH TRANSFER AMOUNT:");
                    
                if((Double.parseDouble(amount) < Double.parseDouble(myinfo[4])) && (Double.parseDouble(amount) > 0)){
                    double reciever_new_bal = Double.parseDouble(amount) + rs.getDouble(2);
                    total = Float.parseFloat(myinfo[4]) - Float.parseFloat(amount);
                    transaction_type = "CASH TRANSFER";
                    query = "update account_data set balance="+myinfo[4]+" where account_id= "+using_id+";";
                    Transaction_id_Generator();
                    Transaction();
                    WriteHistory();
                    JOptionPane.showMessageDialog(mainframe, "CASH TRANSFER SUCCESSFUL", "TRANSACTION", JOptionPane.PLAIN_MESSAGE);
                    query= "update account_data set balance="+reciever_new_bal+" where account_id= "+receiver+";";
                    transaction_type="DEPOSIT";
                    Transaction_id_Generator();
                    Transaction();
                }
            }
        }    
    } 
    
    catch (Exception e){
        System.out.println("TRANSACTION CANCELLED");
    }
    }
 
public void Bill_Payment(){
    
    try{
        receiver = JOptionPane.showInputDialog(mainframe, "ENTER COMPANY NAME TO BE PAYED:");
        
        if(receiver != null){
            amount = JOptionPane.showInputDialog(mainframe, "PAYMENT AMOUNT:");
        }
     
        if((Float.parseFloat(amount) > 0) && (Float.parseFloat(amount) < Float.parseFloat(myinfo[4]))){
            total = Float.parseFloat(myinfo[4]) - Float.parseFloat(amount);
            transaction_type="Bills";
            Transaction();
            JOptionPane.showMessageDialog(mainframe, "BILL PAYMENT SUCCESSFUL", "TRANSACTION", JOptionPane.PLAIN_MESSAGE);
            WriteHistory();
        }
   
        else if(amount == null){
            JOptionPane.showMessageDialog(mainframe, "TRANSACTION CANCELLED");
        }
        
        else{
            JOptionPane.showMessageDialog(mainframe, "TRANSACTION INVALID","INSUFFICIENT BALANCE",JOptionPane.ERROR_MESSAGE);
        }
    } 
    
    catch (Exception e) {
        System.out.println("TRANSACTION CANCELLED");
    }
}
 
public void Compute_Interest(){
  
    double interestadd = Double.parseDouble(myinfo[4]) * 0.0001;
    double interestBal = interestadd + Double.parseDouble(myinfo[4]);
    JOptionPane.showMessageDialog(mainframe, "INTEREST RATE: 0.001, per 15th of the Month\nExpected Interest for 12 Months: PHP"+DCF.format(interestadd)+"\nEXPECTED ACCOUNT BALANCE: PHP"+DCF.format(interestBal), "INTEREST COMPUTATION", JOptionPane.PLAIN_MESSAGE);
}
 
public void Transaction(){
  
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        con.close();
        myinfo[4]=Float.toString(total);
        balance.setText(DCF.format(total));
    }
    
    catch (Exception e) {
        System.out.println(e);
    }
}
 
public void WriteHistory(){
  
    query = "insert into transaction_table values('"+generatedString+"',"+using_id+",'"+receiver+"',CURRENT_TIMESTAMP,'"+transaction_type+"',"+amount+","+myinfo[4]+");";
    Transaction();
    String trid="",accid="",rcvrid="",time="",type="",tramount="",new_balance="";
  
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from transaction_table where account_id="+using_id+" and transaction_id='"+generatedString+"';");
     
        while(rs.next()){
            trid = rs.getString(1);accid = rs.getString(2);
            rcvrid= rs.getString(3);time= rs.getString(4);
            type= rs.getString(5);tramount= rs.getString(6);
            new_balance= rs.getString(7);
        }
            
        model.addRow(new Object[]{trid,accid,rcvrid,time,type,tramount,new_balance});
        con.close();
    }
    
    catch (Exception e){
        System.out.println("TRANSACTION CANCELLED [WriteHistory();]");
    }
    }
 
public void previous_history(){    
    
    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDB", "root", "1010");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from transaction_table where account_id="+using_id+" OR reciever="+using_id+" order by transaction_date desc;");
        
        while(rs.next()){
            model.addRow(new Object[]{});
                
            for(int write_table = 0; write_table < 6; write_table++){
                if(write_table < 6){
                    model.setValueAt(rs.getString(write_table+1), rowcount, write_table);
                }
            }
                rowcount++;
            }
  
        con.close();
        
        }
    
    catch (Exception e) {
        System.out.println(e);
    }
}
 
public void Transaction_id_Generator(){
  
    int leftLimit = 97; 
    int rightLimit = 122; 
    int targetStringLength = 7;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    
    for (int i = 0; i < targetStringLength; i++){
        int randomLimitedInt = leftLimit + (int) 
        (random.nextFloat() * (rightLimit - leftLimit + 1));
        buffer.append((char) randomLimitedInt);
    }
    
    generatedString = buffer.toString();
}
    
public static void main(String[] args){
  
  new BankingJDBC();
}
  @Override
 
public void actionPerformed(ActionEvent e){
  
    if(e.getSource() == login){
        if(Account_Security() == true){
            Account();
        }
    }
    
    else if(e.getSource() == deposit){
        Deposit();
    }
    
    else if(e.getSource() == withdraw){
        Cash_Withdrawal();
    }
        
    else if(e.getSource() == transfer){
        Cash_Transfer();   
    }
        
    else if(e.getSource() == pay){
        Bill_Payment();
    }
    
    else if(e.getSource() == compute){
        Compute_Interest();
    }   
}
}
