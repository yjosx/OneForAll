package pow.jie.oneforall.util;

import android.util.Log;

import org.litepal.LitePal;

import java.util.List;

import pow.jie.oneforall.MainActivity;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.db.Essay;
import pow.jie.oneforall.db.Question;
import pow.jie.oneforall.db.SerialContent;
import pow.jie.oneforall.databean.ContentItemBean;
import pow.jie.oneforall.databean.EssayBean;
import pow.jie.oneforall.databean.QuestionBean;
import pow.jie.oneforall.databean.SerialContentBean;

public class SaveDataToLitePal {
    public static void SaveToContentList(ContentItemBean contentItemBean) {

        int lastVol = 0;
        //将ContentListBean获取为list
        List<ContentItemBean.DataBean.ContentListBean> list = contentItemBean.getData().getContent_list();

        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getCategory().equals("6")) {
                ContentItem contentItem = new ContentItem();

                contentItem.setItemId(list.get(i).getItem_id());
                contentItem.setCategory(list.get(i).getCategory());

                contentItem.setForward(list.get(i).getForward());
                contentItem.setUrl(list.get(i).getImg_url());
                contentItem.setContent(list.get(i).getContent_id());
                contentItem.setTitle(list.get(i).getTitle());
                contentItem.setVolume(list.get(0).getVolume());
                contentItem.setIdListId(Integer.parseInt(contentItemBean.getData().getId()));
                //以下是volume字符串获取为纯数字
                StringBuilder numStr = new StringBuilder();
                for (char each : list.get(0).getVolume().toCharArray()) {
                    if (each >= '0' && each <= '9')
                        numStr.append(each);
                }
                contentItem.setVolumeNum(Integer.parseInt(numStr.toString()));

                switch (list.get(i).getCategory()) {
                    case "0"://摄影
                        contentItem.setAuthor(list.get(i).getPic_info());
                        contentItem.setTitle(list.get(i).getWords_info());//作品标题
                        contentItem.setTagTitle(list.get(i).getTitle());
                        break;
                    case "1"://文章
                        if (!String.valueOf(list.get(i).getTag_list()).equals("[]")) {
                            Log.d("SaveDataToLitePal", String.valueOf(list.get(i).getTag_list()));
                            contentItem.setTagTitle(list.get(i).getTag_list().get(0).getTitle());
                        } else {
                            Log.d("SaveDataToLitePal", String.valueOf(list.get(i).getTag_list()));
                            contentItem.setTagTitle("阅读");
                        }
                        contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                        contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                        break;
                    case "2"://连载
                        contentItem.setSerialList(list.get(i).getSerial_list());
                        contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                        contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                        contentItem.setTagTitle(list.get(i).getSubtitle());//栏目标题
                        break;
                    case "3"://问答
                        contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                        contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                        contentItem.setTagTitle("问答");
                        break;
                    case "4"://音乐
                        contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                        contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                        contentItem.setTagTitle("音乐");
                        break;
                    case "5"://电影
                        contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                        contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                        contentItem.setTagTitle("电影");
                        break;
                }
                contentItem.save();
                Log.d("SaveToLitePal", "SaveToContentList: " + contentItem.getIdListId());
                lastVol = contentItem.getVolumeNum() - 10;
            }
        }
        LitePal.deleteAll(ContentItem.class, "volumeNum < ?", String.valueOf(lastVol));
    }

    public static void SaveToEssay(EssayBean essayBean) {
        Essay essay = new Essay();
        essay.setTitle(essayBean.getData().getHp_title());
        essay.setAuthorAuth(essayBean.getData().getAuth_it());
        essay.setAuthor(essayBean.getData().getHp_author());
        essay.setGuideWord(essayBean.getData().getGuide_word());
        essay.setContent(essayBean.getData().getHp_content());
        essay.save();
    }

    public void SaveToQuestion(QuestionBean questionBean) {
        Question question = new Question();
        question.setQuestionTitle(questionBean.getData().getQuestion_title());
        question.setQuestionContent(questionBean.getData().getQuestion_content());
        question.setAnswerContent(questionBean.getData().getAnswer_content());
        question.save();
    }

    public void SaveToSerialContent(SerialContentBean serialContentBean) {
        SerialContent serialContent = new SerialContent();
        serialContent.setNumber(serialContentBean.getData().getNumber());
        serialContent.setTitle(serialContentBean.getData().getTitle());
        serialContent.setContent(serialContentBean.getData().getContent());
        serialContent.save();
    }
}
