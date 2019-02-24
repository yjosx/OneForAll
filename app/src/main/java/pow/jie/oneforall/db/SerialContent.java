package pow.jie.oneforall.db;

import org.litepal.crud.LitePalSupport;

public class SerialContent extends LitePalSupport {
    private String title;
    private String number;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
