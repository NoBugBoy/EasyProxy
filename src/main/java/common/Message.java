package common;
/**
 * Author yujian
 * Description 二进制消息体
 * Date 2021/2/2
 */
public class Message {
  private   int magic;
  private   int type;
  private   int length;
  private   String data;

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" + "magic=" + magic + ", type=" + type + ", length=" + length + ", data='" + data + '\'' + '}';
    }
}
