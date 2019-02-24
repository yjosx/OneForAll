package pow.jie.oneforall.db;

import org.litepal.crud.LitePalSupport;

public class Essay extends LitePalSupport {
    private String title;
    private String author;
    private String authorAuth;
    private String content;
    private String guideWord;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorAuth() {
        return authorAuth;
    }

    public void setAuthorAuth(String authorAuth) {
        this.authorAuth = authorAuth;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGuideWord() {
        return guideWord;
    }

    public void setGuideWord(String guideWord) {
        this.guideWord = guideWord;
    }
}
