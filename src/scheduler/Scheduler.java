/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import scheduler.Scheduler.updateTime;

/**
 *
 * @author Ashutosh
 */
public class Scheduler extends JFrame{

    /**
     * @param args the command line arguments
     */
    private Scheduler mainWindow;
    static String title = new String("Awesome Scheduler");
    private JLabel titleLabel = new JLabel(this.title);
    private JLabel currentTimeLabel;
    private JButton fileChooserButton;
    private File choosenFile = null;
    private JButton addProgramExecutionButton;
    
    public Scheduler(){
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400,400));
        setResizable(false);      
        setTitle(this.title);
        
        setLayout(new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
        
        // add title
        titleLabel.setFont(new Font(titleLabel.getFont().getName(),titleLabel.getFont().getStyle(),40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        add(this.titleLabel,gbc);
        
        // show current Time
        currentTimeLabel = new JLabel();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date).toString());
        currentTimeLabel.setText(dateFormat.format(date).toString());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        add(this.currentTimeLabel,gbc);
        
        // timer to regularly update time
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new updateTime(), 0, 1000, TimeUnit.MILLISECONDS);

        //select a program to be run at specified time
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel temp1 = new JLabel("Choose a Program to shcedule execution:  ");
        this.fileChooserButton = new JButton();
        this.fileChooserButton.setText("Choose a Program");
        this.fileChooserButton.setMaximumSize(new Dimension(200,100));
        this.fileChooserButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                int returnVal = jfc.showOpenDialog(mainWindow);        
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    choosenFile = jfc.getSelectedFile();
                    String name = choosenFile.getName();
                    if(name.length()<=15){
                       fileChooserButton.setText(name); 
                    }
                    else{
                        fileChooserButton.setText(name.substring(0,12)+"...");
                    }                    
                }
            }            
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(temp1,gbc);
        gbc.gridx = 1;
        add(fileChooserButton,gbc);
        
        // specify execution time
        JLabel temp2 = new JLabel("Execution Time:  ");
        gbc.gridy =6;
        gbc.gridx = 0;
        add(temp2,gbc);
        gbc.gridx = 1;        
        JSpinner timeSpinner = new JSpinner( new SpinnerDateModel() );
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "dd/MM/yyyy HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(date);
        add(timeSpinner,gbc);
        
        // ad the schedule button
        this.addProgramExecutionButton = new JButton("Schedule");
        this.addProgramExecutionButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Date scheduleDate = (Date) timeSpinner.getValue();             
                try{
                    Task newTask = new Task(scheduleDate,choosenFile);
                }
                catch(Exception e2){
                    JOptionPane.showMessageDialog(null, "No file specified for execution! Please choose a file");
                }
            }
            
        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(this.addProgramExecutionButton,gbc);
        
        setSize(250,250);
        setVisible(true);
        pack();
        
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        Scheduler mainWindow = new Scheduler();
    }
    
    class updateTime implements Runnable{
        // class used to regularly update current time
        @Override
        public void run() {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            currentTimeLabel.setText(dateFormat.format(date).toString());
        }
        
    }
    
    class Task{
        // main tasking class
        private Date date = null;
        private File file = null;
        
        public Task(Date date,File file){
            this.date = date;
            this.file = file;
            Timer timer;
            timer = new Timer();
            long x = this.getDifference(date);
            if(x<=0){
                x=5000;
            }
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date1 = new Date();
            System.out.println(file.getName()+" scheduled to executed after "+x/1000+" seconds after "+dateFormat.format(date1).toString());
            timer.schedule(new schedule(file), x);            
        }
        
        public long getDifference(Date date1)
        {
            long curr = System.currentTimeMillis();
            long diff = -curr +date1.getTime();
            return diff;
        }
    }
    
    class schedule extends TimerTask{
        
        private File file = null;
        
        public schedule(File file){
            this.file = file;
        }

        @Override
        public void run() {
            System.out.println("Executing "+file.getName());
            try{
                Runtime.getRuntime().exec("cmd /C \"\""+file.getAbsolutePath()+"\"\"");                
            }
            catch(Exception e){
                System.out.println("An exception occured \n"+e.getMessage());   
            }
        }
       
        
    }
    
}
