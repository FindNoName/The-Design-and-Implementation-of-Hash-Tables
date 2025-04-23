class Contact {
    private static final long serialVersionUID = 1L;
    private String phoneNumber;
    private String userName;
    private String address;
    private int hashAddress; // 新增字段，存储哈希地址

    public Contact(String phoneNumber, String userName, String address) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.address = address;
    }

    // 添加 getter 和 setter
    public int getHashAddress() {
        return hashAddress;
    }

    public void setHashAddress(int hashAddress) {
        this.hashAddress = hashAddress;
    }
    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", address='" + address + '\'' +
                ",HashAddress="+hashAddress+'\''+
                '}';
    }
}
