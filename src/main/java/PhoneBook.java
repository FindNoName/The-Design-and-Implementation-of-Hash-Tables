import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhoneBook {

    public final static String filePath = "PhoneBook.xlsx";
    private final List<Contact> contacts;
    public static HashTable nameHashTable = null; // 基于用户名的哈希表
    public static HashTable phoneHashTable = null; // 基于电话号码的哈希表

    public PhoneBook() {
        contacts = new ArrayList<>();
        nameHashTable = new HashTable(100); // 初始化哈希表，设定容量
        phoneHashTable = new HashTable(100);
//        loadContacts(filePath); // 从文件加载联系人并添加到哈希表
    }


    public void addContact(String phoneNumber, String userName, String address) {
        Contact contact = new Contact(phoneNumber, userName, address);

        // 插入联系人时自动计算并保存哈希地址
        nameHashTable.insert(contact, "username");
        phoneHashTable.insert(contact, "phone");

        contacts.add(contact);
    }



    // 搜索联系人，允许使用用户名或电话号码作为搜索键
    public Contact searchByUsername(String userName) {
        return nameHashTable.search(userName, "username"); // 明确指定 keyType
    }

    public Contact searchByPhoneNumber(String phoneNumber) {
        return phoneHashTable.search(phoneNumber, "phone"); // 明确指定 keyType
    }

    // 删除联系人，允许使用用户名或电话号码作为删除键
    public void deleteContact(String userName, String phoneNumber) {
        // 使用用户名查找联系人
        Contact contact = nameHashTable.search(userName, "username");
        // 检查联系人是否存在并且电话号码匹配
        if (contact != null && contact.getPhoneNumber().equals(phoneNumber)) {
            nameHashTable.delete(userName, "username"); // 使用用户名删除
            phoneHashTable.delete(phoneNumber, "phone"); // 使用电话号码删除
            contacts.remove(contact); // 从列表中移除
        } else {
            System.out.println("联系人不存在或电话号码不匹配。");
        }
    }


    // 显示哈希表中的所有联系人
    public void displayContacts() {
        System.out.println("哈希表中的联系人：");
        nameHashTable.display();
    }


    public void saveToFile(String filePath,boolean isUsingNameHashTable) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contacts");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Phone Number");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("Address");
        headerRow.createCell(3).setCellValue(isUsingNameHashTable ? "Name Hash Address" : "Phone Hash Address");

        // 填充联系人信息
        int rowNum = 1;
        for (Contact contact : getAllContacts()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contact.getPhoneNumber());
            row.createCell(1).setCellValue(contact.getUserName());
            row.createCell(2).setCellValue(contact.getAddress());
            row.createCell(3).setCellValue(contact.getHashAddress());
        }

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            System.out.println("Contacts saved to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 从文件中加载联系人
    public void loadContacts(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 假设联系人信息在第一个工作表
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行

                // 读取每行的单元格内容
                Cell phoneCell = row.getCell(0);
                Cell nameCell = row.getCell(1);
                Cell addressCell = row.getCell(2);
                Cell HashAddressCell=row.getCell(3);

                // 将单元格内容转为字符串
                String phoneNumber = phoneCell.getStringCellValue();
                String userName = nameCell.getStringCellValue();
                String address = addressCell.getStringCellValue();
                //String HashAddress=HashAddressCell.getStringCellValue();

                // 创建新的 Contact 对象并添加到 PhoneBook 重新计算hash
                addContact(phoneNumber, userName, address);
            }

            System.out.println("联系人数据已加载");
        } catch (IOException e) {
            System.err.println("加载联系人时出错: " + e.getMessage());
        }


        //随机生成
        //addRandomContacts(30);
    }

    // 随机生成 count 条联系人数据
    public void addRandomContacts(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String phoneNumber = String.valueOf(1000000000L + random.nextInt(900000000));
            String userName = "User" + i;
            String address = "Address" + (random.nextInt(100) + 1);
            addContact(phoneNumber, userName, address);
        }
    }

    public List<Contact> getAllContacts() {
        return contacts;
    }

    public int getHashAddress(String key, String keyType) {
        // 根据 keyType 决定使用哪个哈希表
        if (keyType.equals("username")) {
            return nameHashTable.getHashAddress(key, keyType);
        } else {
            return phoneHashTable.getHashAddress(key, keyType);
        }
    }

}
