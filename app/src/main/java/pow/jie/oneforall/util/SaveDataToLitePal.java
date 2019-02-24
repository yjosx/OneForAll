package pow.jie.oneforall.util;

import android.util.Log;

import org.litepal.LitePal;

import java.util.List;

import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.db.Essay;
import pow.jie.oneforall.db.Question;
import pow.jie.oneforall.db.SerialContent;
import pow.jie.oneforall.gson.ContentItemGson;
import pow.jie.oneforall.gson.EssayGson;
import pow.jie.oneforall.gson.QuestionGson;
import pow.jie.oneforall.gson.SerialContentGson;

public class SaveDataToLitePal {
    public static void SaveToContentList(ContentItemGson contentItemGson) {

        int lastVol = 0;
        //将ContentListBean获取为list
        List<ContentItemGson.DataBean.ContentListBean> list = contentItemGson.getData().getContent_list();

        for (int i = 0; i < list.size(); i++) {
            ContentItem contentItem = new ContentItem();

            contentItem.setItemId(list.get(i).getItem_id());
            contentItem.setCategory(list.get(i).getCategory());

            contentItem.setForward(list.get(i).getForward());
            contentItem.setUrl(list.get(i).getImg_url());
            contentItem.setContent(list.get(i).getContent_id());
            contentItem.setTitle(list.get(i).getTitle());
            contentItem.setVolume(list.get(0).getVolume());
            contentItem.setIdListId(Integer.parseInt(contentItemGson.getData().getId()));
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
            lastVol = contentItem.getVolumeNum() - 10;
        }
        LitePal.deleteAll(ContentItem.class, "volumeNum < ?", String.valueOf(lastVol));
    }

    public static void SaveToEssay(EssayGson essayGson) {
        Essay essay = new Essay();
        essay.setTitle(essayGson.getData().getHp_title());
        essay.setAuthorAuth(essayGson.getData().getAuth_it());
        essay.setAuthor(essayGson.getData().getHp_author());
        essay.setGuideWord(essayGson.getData().getGuide_word());
        essay.setContent(essayGson.getData().getHp_content());
        essay.save();
    }

    public void SaveToQuestion(QuestionGson questionGson) {
        Question question = new Question();
        question.setQuestionTitle(questionGson.getData().getQuestion_title());
        question.setQuestionContent(questionGson.getData().getQuestion_content());
        question.setAnswerContent(questionGson.getData().getAnswer_content());
        question.save();
    }

    public void SaveToSerialContent(SerialContentGson serialContentGson) {
        SerialContent serialContent = new SerialContent();
        serialContent.setNumber(serialContentGson.getData().getNumber());
        serialContent.setTitle(serialContentGson.getData().getTitle());
        serialContent.setContent(serialContentGson.getData().getContent());
        serialContent.save();
    }
}
