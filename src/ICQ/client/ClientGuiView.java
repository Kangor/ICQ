package ICQ.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class ClientGuiView {
    private final ClientGuiController controller;
    private JFrame frame = new JFrame("Чат");
    private JTextField textField = new JTextField(50);
    private JTextArea messages = new JTextArea(10, 50);
    private JTextArea users = new JTextArea(10, 10);

    public ClientGuiView(ClientGuiController controller) {
        this.controller = controller;
        this.initView();
    }

    private void initView() {
        this.textField.setEditable(false);
        this.messages.setEditable(false);
        this.users.setEditable(false);
        this.frame.getContentPane().add(this.textField, "North");
        this.frame.getContentPane().add(new JScrollPane(this.messages), "West");
        this.frame.getContentPane().add(new JScrollPane(this.users), "East");
        this.frame.pack();
        this.frame.setDefaultCloseOperation(3);
        this.frame.setVisible(true);
        this.textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientGuiView.this.controller.sendTextMessage(ClientGuiView.this.textField.getText());
                ClientGuiView.this.textField.setText("");
            }
        });
    }

    public String getServerAddress() {
        return JOptionPane.showInputDialog(this.frame, "Введите адрес сервера:", "Конфигурация клиента", 3);
    }

    public int getServerPort() {
        while(true) {
            String port = JOptionPane.showInputDialog(this.frame, "Введите порт сервера:", "Конфигурация клиента", 3);

            try {
                return Integer.parseInt(port.trim());
            } catch (Exception var3) {
                JOptionPane.showMessageDialog(this.frame, "Был введен некорректный порт сервера. Попробуйте еще раз.", "Конфигурация клиента", 0);
            }
        }
    }

    public String getUserName() {
        return JOptionPane.showInputDialog(this.frame, "Введите ваше имя:", "Конфигурация клиента", 3);
    }

    public void notifyConnectionStatusChanged(boolean clientConnected) {
        this.textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(this.frame, "Соединение с сервером установлено", "Чат", 1);
        } else {
            JOptionPane.showMessageDialog(this.frame, "Клиент не подключен к серверу", "Чат", 0);
        }

    }

    public void refreshMessages() {
        this.messages.append(this.controller.getModel().getNewMessage() + "\n");
    }

    public void refreshUsers() {
        ClientGuiModel model = this.controller.getModel();
        StringBuilder sb = new StringBuilder();
        Iterator var3 = model.getAllUserNames().iterator();

        while(var3.hasNext()) {
            String userName = (String)var3.next();
            sb.append(userName).append("\n");
        }

        this.users.setText(sb.toString());
    }
}