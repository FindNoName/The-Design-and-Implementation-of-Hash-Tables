import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

import static java.lang.System.exit;

public class PhoneBookGUI extends JFrame {
    private boolean isUsingNameHashTable = true; // 是否使用名称哈希表
    private JButton toggleHashTableButton;
    private JPanel panel;
    private static PhoneBook phoneBook;  // 电话簿对象
    private JTable contactTable;   // 表格
    private DefaultTableModel tableModel; // 表格模型
    private JTextField phoneField, nameField, addressField, searchField; // 输入字段
    public static final String filePath = "PhoneBook.xlsx"; // 文件保存路径
    private JLabel hashAddressFormulaLabel; // 用于显示哈希地址公式

    public PhoneBookGUI() {
        phoneBook = new PhoneBook();  // 初始化PhoneBook对象
        panel = new JPanel(new BorderLayout());


        // 设置窗口
        setTitle("Phone Book Manager");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // 在GUI初始化时添加哈希地址公式标签
        hashAddressFormulaLabel = new JLabel("Current Hash Table: " + (isUsingNameHashTable ? "Name Hash Table" : "Phone Hash Table") +
                " | Hash Address Formula: hash(key_ASCII) % table.length");
        hashAddressFormulaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(hashAddressFormulaLabel, BorderLayout.NORTH);
        // 表格初始化，增加 "Hash Address" 列
        tableModel = new DefaultTableModel(new Object[]{"Phone Number", "Username", "Address", "Hash Address"}, 0);
        contactTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // 控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(8, 2, 5, 5));

        controlPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        controlPanel.add(phoneField);

        controlPanel.add(new JLabel("Username:"));
        nameField = new JTextField();
        controlPanel.add(nameField);

        controlPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        controlPanel.add(addressField);

        controlPanel.add(new JLabel("Search:"));
        searchField = new JTextField();
        controlPanel.add(searchField);

        // 按钮
        JButton addButton = new JButton("Add Contact");
        JButton deleteButton = new JButton("Delete Contact");
        JButton searchByNameButton = new JButton("Search by Username Hash Address");
        JButton searchByPhoneButton = new JButton("Search by Phone Number Address");
        //JButton saveButton = new JButton("Save Contacts");
        JButton viewAllButton = new JButton("View All Contacts"); // 新增查看所有记录的按钮
        JButton saveAsButton = new JButton("Save As..."); // 新增的“另存为”按钮
        JButton exitButton = new JButton("Exit"); // 新增的“另存为”按钮
        toggleHashTableButton = new JButton("Switch PhoneNum\\Name");

        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(searchByNameButton);
        controlPanel.add(searchByPhoneButton);
        //controlPanel.add(saveButton);
        controlPanel.add(viewAllButton); // 将新按钮添加到控制面板
        controlPanel.add(saveAsButton); // 添加“另存为”按钮到界面
        controlPanel.add(exitButton);
        controlPanel.add(toggleHashTableButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // 按钮事件
        addButton.addActionListener(e -> addContact());
        deleteButton.addActionListener(e -> deleteContact());
        searchByNameButton.addActionListener(e -> searchByUsername());
        searchByPhoneButton.addActionListener(e -> searchByPhoneNumber());
        viewAllButton.addActionListener(e -> refreshTable()); // 查看所有记录时刷新表格
        saveAsButton.addActionListener(e -> saveAsContactsToFile()); // 添加“另存为”按钮的事件
        exitButton.addActionListener(e -> exitPrograme());
        toggleHashTableButton.addActionListener(e -> toggleHashTable());

        refreshTable(); // 初始化表格内容
        loadContactsFromFile(); // 加载已有联系人
    }

    private void exitPrograme() {
        exit(0);
    }

    private void refreshTable() {
        // 清空现有的表格数据
        tableModel.setRowCount(0);

        // 从 PhoneBook 中获取联系人并填充表格
        for (Contact contact : phoneBook.getAllContacts()) {
            // 根据当前哈希表模式获取哈希地址
            int hashAddress = isUsingNameHashTable ?
                    phoneBook.getHashAddress(contact.getUserName(), "username") :
                    phoneBook.getHashAddress(contact.getPhoneNumber(), "phone");

            tableModel.addRow(new Object[]{
                    contact.getPhoneNumber(),
                    contact.getUserName(),
                    contact.getAddress(),
                    hashAddress // 显示哈希地址
            });
        }
    }




    private void loadContactsFromFile() {
        //phoneBook.addRandomContacts(30);
        phoneBook.loadContacts(filePath); // 从文件加载联系人
        refreshTable(); // 刷新表格
    }

    private void addContact() {
        String phoneNumber = phoneField.getText();
        String userName = nameField.getText();
        String address = addressField.getText();

        if (!phoneNumber.isEmpty() && !userName.isEmpty() && !address.isEmpty()) {
            Contact contact = new Contact(phoneNumber, userName, address);

            // 根据当前选择的哈希表计算哈希地址
            if (isUsingNameHashTable) { // 假设 isUsingPhoneHashTable 是一个布尔变量，指示当前选择的哈希表
                PhoneBook.phoneHashTable.insert(contact, "phone"); // 使用电话哈希表
            } else {
                PhoneBook.nameHashTable.insert(contact, "username"); // 使用用户名哈希表
            }

            // 将联系人添加到联系人列表
            phoneBook.addContact(phoneNumber, userName, address);

            // 刷新表格并清空输入字段
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
        }
    }


    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            String userName = tableModel.getValueAt(selectedRow, 1).toString();
            String phoneNumber = tableModel.getValueAt(selectedRow, 0).toString();
            phoneBook.deleteContact(userName, phoneNumber);
            refreshTable(); // 删除后刷新表格
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete!");
        }
    }

    private void searchByUsername() {
        String userName = searchField.getText();
        Contact contact = phoneBook.searchByUsername(userName);
        if (contact != null) {
            tableModel.setRowCount(0); // 清空现有数据
            tableModel.addRow(new Object[]{contact.getPhoneNumber(), contact.getUserName(), contact.getAddress()});
        } else {
            JOptionPane.showMessageDialog(this, "Contact not found!");
            refreshTable(); // 找不到时刷新所有数据
        }
    }

    private void searchByPhoneNumber() {
        String phoneNumber = searchField.getText();
        Contact contact = phoneBook.searchByPhoneNumber(phoneNumber);
        if (contact != null) {
            tableModel.setRowCount(0); // 清空现有数据
            tableModel.addRow(new Object[]{contact.getPhoneNumber(), contact.getUserName(), contact.getAddress()});
        } else {
            JOptionPane.showMessageDialog(this, "Contact not found!");
            refreshTable(); // 找不到时刷新所有数据
        }
    }


    private void clearFields() {
        phoneField.setText("");
        nameField.setText("");
        addressField.setText("");
        searchField.setText("");
    }

    private void saveContactsToFileWithoutExit() {
        phoneBook.saveToFile(filePath,isUsingNameHashTable); // 保存联系人到文件
        JOptionPane.showMessageDialog(this, "Contacts saved successfully!");
        System.out.println("退出程序。");
        Scanner scanner = new Scanner(System.in);
        scanner.close();

    }

    private void saveAsContactsToFile() {
        saveContactsToFileWithoutExit();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("PhoneBook.xlsx")); // 默认文件名

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            // 确保文件路径包含 .xlsx 扩展名
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            phoneBook.saveToFile(filePath,isUsingNameHashTable);
            JOptionPane.showMessageDialog(this, "Contacts saved to: " + filePath);
        }
        exitPrograme();
    }

    private void toggleHashTable() {
        // 切换哈希表模式
        isUsingNameHashTable = !isUsingNameHashTable;

        // 更新按钮文本
        toggleHashTableButton.setText(isUsingNameHashTable ? "Switch to Phone HashTable" : "Switch to Name HashTable");

        // 更新哈希表标签文本
        hashAddressFormulaLabel.setText("Current Hash Table: " + (isUsingNameHashTable ? "Name Hash Table" : "Phone Hash Table") +
                " | Hash Address Formula: hash(key) % table.length");

        // 刷新表格以显示切换后的哈希地址
        refreshTable();
    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PhoneBookGUI frame = new PhoneBookGUI();
            frame.setVisible(true);
        });
    }
}