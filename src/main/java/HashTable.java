class HashTable {
    private final Contact[] table;
    private int size;

    public HashTable(int capacity) {
        table = new Contact[capacity];
        size = 0;
    }

    // 通用哈希函数，根据传入的键（可以是用户名或电话号码）生成哈希值
    private int hash(String key) {
        int hash = key.hashCode() % table.length;
        return (hash < 0) ? hash + table.length : hash; // 若为负数，加上 table.length 以确保非负
    }

    // 检查哈希表中是否存在相同的用户名或电话号码
    public boolean contains(String key, String type) {
        // 遍历哈希表中的所有槽
        for (Contact contact : table) {
            if (contact != null) {
                boolean match = type.equals("username") ?
                        contact.getPhoneNumber().equals(key) :
                        contact.getUserName().equals(key);
                if (match) {
                    return true; // 如果找到匹配的键，返回 true
                }
            }
        }
        return false; // 如果遍历完哈希表都没有找到，返回 false
    }

//    // 实现 containsKey 方法，检查哈希表中是否存在该键
//    public boolean containsKey(String key) {
//        int index = hash(key);  // 根据键计算哈希值得到桶的位置
//        int originalIndex = index;
//
//        // 线性探测查找指定的键
//        while (table[index] != null) {
//            // 如果找到了匹配的键
//            if (table[index].getUserName().equals(key) || table[index].getPhoneNumber().equals(key)) {
//                return true;
//            }
//            index = (index + 1) % table.length;  // 线性探测
//            if (index == originalIndex) break; // 防止无限循环
//        }
//
//        return false;  // 没有找到匹配的键
//    }

    public void insert(Contact contact, String keyType) {
        String key = keyType.equals("username") ? contact.getUserName() : contact.getPhoneNumber();
        int index = hash(key);

        // 设置计算的哈希地址
        contact.setHashAddress(index);

        // 线性探测法处理冲突
        while (table[index] != null) {
            if (table[index].getUserName().equals(contact.getUserName())) {
                table[index] = contact; // 如果用户存在，则覆盖
                return;
            }
            index = (index + 1) % table.length; // 线性探测
        }
        table[index] = contact;
        size++;
    }


    // 根据 keyType 查找联系人
    public Contact search(String key, String keyType) {
        int index = hash(key);
        int probeCount = 0; // 记录查找长度

        while (table[index] != null) {
            probeCount++;
            boolean match = keyType.equals("username") ?
                    table[index].getUserName().equals(key) :
                    table[index].getPhoneNumber().equals(key);
            if (match) {
                System.out.println("查找长度: " + probeCount);
                return table[index]; // 找到联系人
            }
            index = (index + 1) % table.length; // 线性探测
        }
        System.out.println("查找长度: " + probeCount);
        return null; // 联系人未找到
    }

    // 根据 keyType 删除联系人
    public boolean delete(String key, String keyType) {
        int index = hash(key);
        while (table[index] != null) {
            boolean match = keyType.equals("username") ?
                    table[index].getUserName().equals(key) :
                    table[index].getPhoneNumber().equals(key);
            if (match) {
                table[index] = null; // 删除联系人
                size--;
                rehash(index); // 重新哈希处理
                return true;
            }
            index = (index + 1) % table.length; // 线性探测
        }
        return false; // 联系人未找到
    }

    private void rehash(int deletedIndex) {
        int index = (deletedIndex + 1) % table.length;
        while (table[index] != null) {
            Contact contactToRehash = table[index];
            table[index] = null; // 先删除
            size--; // 暂时减少 size
            String key = contactToRehash.getUserName(); // 默认用用户名重新插入
            insert(contactToRehash, "username");
            index = (index + 1) % table.length; // 线性探测
        }
    }

    public void display() {
        for (Contact contact : table) {
            if (contact != null) {
                System.out.println(contact);
            }
        }
    }


    public int getHashAddress(String key, String keyType) {
        int index = hash(key);
        int originalIndex = index;

        // 线性探测获取对应的哈希地址
        while (table[index] != null) {
            boolean match = keyType.equals("username") ?
                    table[index].getUserName().equals(key) :
                    table[index].getPhoneNumber().equals(key);
            if (match) {
                return index; // 返回找到的哈希地址
            }
            index = (index + 1) % table.length;
            if (index == originalIndex) break; // 防止无限循环
        }
        return -1; // 如果未找到，则返回 -1 表示无效地址
    }


}


