import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;

public class MainForm {
    private JPanel mainPanel;
    private JFormattedTextField maxValueField;
    private JFormattedTextField setTriesField;
    private JButton okButton;
    private JFormattedTextField answerField;
    private JButton answerButton;
    private JLabel expressionLabel;
    private JPanel settings;
    private JPanel game;
    private JPanel failed;
    private JButton oneMoreTimeButton;

    private int maxNumber;
    private Timer timer;
    private int currentNumber;
    private String currentLayoutName;
    private int elapsedSeconds;
    private String session;
    private int currentTry;
    private int currentSession;
    public MainForm() {
        currentSession = 1;
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        currentLayoutName = "Settings";

        setTriesField.addKeyListener(new FieldsKeyListener());

        maxValueField.addKeyListener(new FieldsKeyListener());

        answerField.addKeyListener(new FieldsKeyListener());

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                session = "";
                maxNumber = Integer.parseInt(maxValueField.getText());

                currentTry = Integer.parseInt(setTriesField.getText());

                timer = new Timer (1000, new TimerListener());
                timer.start();

                generate();

                switchLayout("Game");

                answerField.requestFocus();
            }
        });
        answerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                currentTry--;

                session += "Потребовалось " + elapsedSeconds + " секунд " + expressionLabel.getText() + " " + answerField.getText();

                if (Integer.parseInt(answerField.getText()) == currentNumber) session += " ОТВЕТ ВЕРНЫЙ!" + System.getProperty("line.separator");
                else session += " ОТВЕТ НЕВЕРНЫЙ!" + System.getProperty("line.separator");

                System.out.print(session);

                if (currentTry > 0) {
                    timer = new Timer(1000, new TimerListener());
                    timer.start();

                    generate();

                    answerField.requestFocus();
                }
                else {
                    try {
                        FileWriter writer = new FileWriter("Сессия" + currentSession + ".txt");
                        writer.write(session);
                        writer.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    switchLayout("Failed");
                }
            }
        });

        oneMoreTimeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentSession++;
                switchLayout("Settings");

            }

        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }



    public void generate() {

        int number_a, number_b;
        String sign;

        answerField.setText("");

        currentNumber = ((int) (Math.random()* 1000 * maxNumber))%maxNumber;
        number_a = ((int) (Math.random()* 1000 * maxNumber))%maxNumber;
        number_b = currentNumber - number_a;

        if (number_b >= 0) sign = "+";
        else sign = "-";

        System.out.print(number_a + " " + number_b + " " + currentNumber + "\n");
        expressionLabel.setText(number_a + " " + sign + " " + Math.abs(number_b) + " = ");

    }

    class TimerListener implements ActionListener{

        public TimerListener () {
            elapsedSeconds = 0;
        }

        public void actionPerformed(ActionEvent evt){

            elapsedSeconds++;

        }

    }

    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                switch (currentLayoutName) {
                    case "Settings":
                        okButton.doClick();
                        break;
                    case "Game":
                        answerButton.doClick();
                        break;
                    case "Failed":
                        oneMoreTimeButton.doClick();
                        break;
                }
            }
            return false;
        }
    }

    private class FieldsKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (!Character.isDigit(e.getKeyChar())) e.consume();
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private void switchLayout(String name) {
        CardLayout mainLayout = (CardLayout) mainPanel.getLayout();
        mainLayout.show(mainPanel, name);
        currentLayoutName = name;
    }
}
