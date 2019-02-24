package pow.jie.oneforall.db;

import org.litepal.crud.LitePalSupport;
import java.util.List;

public class ContentItem extends LitePalSupport {
    public int getVolumeNum() {
        return volumeNum;
    }

    public void setVolumeNum(int volumeNum) {
        this.volumeNum = volumeNum;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public String getAuthorPicUrl() {
        return authorPicUrl;
    }

    public void setAuthorPicUrl(String authorPicUrl) {
        this.authorPicUrl = authorPicUrl;
    }

    public List<String> getSerialList() {
        return serialList;
    }

    public void setSerialList(List<String> serialList) {
        this.serialList = serialList;
    }

    public int getIdListId() {
        return idListId;
    }

    public void setIdListId(int idListId) {
        this.idListId = idListId;
    }

    private int idListId;
    private int volumeNum;
    private String itemId;
    private String category;
    private String title;//作品标题
    private String tagTitle;//栏目标题
    private String content;
    private String forward;
    private String url;
    private String volume;
    private String author;
    private String authorPicUrl;
    private List<String> serialList;

}
