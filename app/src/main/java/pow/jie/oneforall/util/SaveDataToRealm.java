package pow.jie.oneforall.util;

import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pow.jie.oneforall.databean.ContentItemBean;
import pow.jie.oneforall.db.ContentItemNew;

public class SaveDataToRealm {
    public static void SaveToContentList(final ContentItemBean contentItemBean) {
        //将ContentListBean获取为list
        final List<ContentItemBean.DataBean.ContentListBean> list = contentItemBean.getData().getContent_list();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(realm1 -> {
                int lastVol = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).getCategory().equals("6")) {
                        ContentItemNew contentItem = realm1.createObject(ContentItemNew.class);

                        contentItem.setDate(contentItemBean.getData().getDate());
                        contentItem.setItemId(list.get(i).getItem_id());
                        contentItem.setCategory(list.get(i).getCategory());
                        contentItem.setLikeCount(list.get(i).getLike_count());
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
                                RealmList<String> realmList = new RealmList<>();
                                realmList.addAll(list.get(i).getSerial_list());
                                contentItem.setSerialList(realmList);
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
                        Log.d("SaveToRealm", "SaveToContentList: " + contentItem.getIdListId());
                        lastVol = contentItem.getVolumeNum() - 10;
                    }
                }
                RealmResults<ContentItemNew> realmList = realm1.where(ContentItemNew.class)
                        .lessThan("volumeNum", lastVol).findAll();
                realmList.deleteAllFromRealm();
            });
        }
    }
}
