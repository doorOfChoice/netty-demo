package com.dawndevil.netty.demo5.protocol;

public class EasyProtocol {

    private int magic;

    private int count;

    private String content;

    public EasyProtocol() {
    }

    public EasyProtocol(int count, String content) {
        this.count = count;
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }
}
