package system;

/**
 * 连接rabbitmq枚举类
 */
public enum RabitMqEnum {
    IP_ADDRESS("47.101.159.36", "ip address of rabbitmq server"),
    USERNAME("hassan", "a common user in rabbitmq server"),
    PASSWORD("hassan", "the password of hassan"),
    PORT("5672", "port of rabbitmq server");


    RabitMqEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;
    private String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
