package com.thmub.cocobook.model.server;

import com.thmub.cocobook.model.bean.*;
import com.thmub.cocobook.model.bean.packages.*;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;

/**
 * Created by zhouas666 on 17-4-20.
 */

public class RemoteRepository {
    private static final String TAG = "RemoteRepository";

    private static RemoteRepository sInstance;
    private Retrofit mRetrofit;
    private BookApi mBookApi;

    private RemoteRepository() {
        mRetrofit = RemoteHelper.getInstance()
                .getRetrofit();

        mBookApi = mRetrofit.create(BookApi.class);
    }

    public static RemoteRepository getInstance() {
        if (sInstance == null) {
            synchronized (RemoteHelper.class) {
                if (sInstance == null) {
                    sInstance = new RemoteRepository();
                }
            }
        }
        return sInstance;
    }


    /********************************书城推荐*************************************/
    /**
     * 精选轮播
     *
     * @return
     */
    public Single<List<SwipePictureBean>> getSwipePictures() {
        return mBookApi.getSwipePicturePackage()
                .map(bean -> bean.getData());
    }

    /**
     * 精选分类
     *
     * @return
     */
    public Single<List<FeatureBean>> getFeature() {
        return mBookApi.getFeaturePackage()
                .map(bean -> bean.getData());
    }

    /**
     * 精选分类图书
     *
     * @param nodeId
     * @return
     */
    public Single<List<FeatureBookBean>> getFeatureBooks(String nodeId) {
        return mBookApi.getFeatureBookPackage(nodeId)
                .map(bean -> bean.getData());
    }

    /**
     * 精选分类图书
     *
     * @param nodeId
     * @return
     */
    public Single<List<FeatureDetailBean>> getFeatureDetail(String nodeId) {
        return mBookApi.getFeatureDetailPackage(nodeId)
                .map(bean -> bean.getData());
    }

    /**
     * 由性别推荐图书
     *
     * @param gender
     * @return
     */
    public Single<List<CollBookBean>> getRecommendBooksByGender(String gender) {
        return mBookApi.getRecommendBookPackage(gender)
                .map(bean -> bean.getBooks());
    }

    /**
     * 同类图书
     *
     * @param bookId
     * @return
     */
    public Single<List<RankBookBean>> getRecommendBooksByBookId(String bookId) {
        return mBookApi.getRecommendBookPackageByBookId(bookId)
                .map(bean -> bean.getBooks());
    }

    /**
     * 图书章节
     *
     * @param bookId
     * @return
     */
    public Single<List<BookChapterBean>> getBookChapters(String bookId) {
        return mBookApi.getBookChapterPackage(bookId, "chapter")
                .map(bean -> {
                    if (bean.getMixToc() == null) {
                        return new ArrayList<BookChapterBean>(1);
                    } else {
                        return bean.getMixToc().getChapters();
                    }
                });
    }

    /**
     * 注意这里用的是同步请求
     *
     * @param url
     * @return
     */
    public Single<ChapterInfoBean> getChapterInfo(String url) {
        return mBookApi.getChapterInfoPackage(url)
                .map(bean -> bean.getChapter());
    }


    /**********************************书籍分类*******************************************/
    /**
     * 获取书籍的分类
     *
     * @return
     */
    public Single<BookSortPackage> getBookSortPackage() {
        return mBookApi.getBookSortPackage();
    }

    /**
     * 获取书籍的子分类
     *
     * @return
     */
    public Single<BookSubSortPackage> getBookSubSortPackage() {
        return mBookApi.getBookSubSortPackage();
    }

    /**
     * 根据分类获取书籍列表
     *
     * @param gender
     * @param type
     * @param major
     * @param minor
     * @param start
     * @param limit
     * @return
     */
    public Single<List<SortBookBean>> getSortBooks(String gender, String type, String major, String minor, int start, int limit) {
        return mBookApi.getSortBookPackage(gender, type, major, minor, start, limit)
                .map(bean -> bean.getBooks());
    }

    /***************************************排行榜****************************************/

    /**
     * 排行榜的类型
     *
     * @return
     */
    public Single<BillboardPackage> getBillboardPackage() {
        return mBookApi.getBillboardPackage();
    }

    /**
     * 排行榜的书籍
     *
     * @param billId
     * @return
     */
    public Single<List<RankBookBean>> getBillBooks(String billId) {
        return mBookApi.getBillBookPackage(billId)
                .map(bean -> bean.getRanking().getBooks());
    }

    /***********************************书单*************************************/

    /**
     * 获取书单列表
     *
     * @param duration
     * @param sort
     * @param start
     * @param limit
     * @param tag
     * @param gender
     * @return
     */
    public Single<List<BookListBean>> getBookLists(String duration, String sort, int start, int limit,
                                                   String tag, String gender) {
        return mBookApi.getBookListPackage(duration, sort, start + "", limit + "", tag, gender)
                .map(bean -> bean.getBookLists());
    }

    /**
     * 获取书单的标签|类型
     *
     * @return
     */
    public Single<List<BookTagBean>> getBookTags() {
        return mBookApi.getBookTagPackage()
                .map(bean -> bean.getData());
    }

    /**
     * 获取书单的详情
     *
     * @param detailId
     * @return
     */
    public Single<BookListDetailBean> getBookListDetail(String detailId) {
        return mBookApi.getBookListDetailPackage(detailId)
                .map(bean -> bean.getBookList());
    }

    /**********************************书籍详情**********************************************/
    /**
     * 图书详情
     *
     * @param bookId
     * @return
     */
    public Single<BookDetailBean> getBookDetail(String bookId) {
        return mBookApi.getBookDetail(bookId);
    }


    /**
     * 推荐书单
     *
     * @param bookId
     * @param limit
     * @return
     */
    public Single<List<BookListBean>> getRecommendBookList(String bookId, int limit) {
        return mBookApi.getRecommendBookListPackage(bookId, limit + "")
                .map(bean -> bean.getBooklists());
    }


    /********************************书籍搜索*********************************************/
    /**
     * 搜索热词
     *
     * @return
     */
    public Single<List<String>> getHotWords() {
        return mBookApi.getHotWordPackage()
                .map(bean -> bean.getHotWords());
    }

    /**
     * 搜索关键字
     *
     * @param query
     * @return
     */
    public Single<List<String>> getKeyWords(String query) {
        return mBookApi.getKeyWordPacakge(query)
                .map(bean -> bean.getKeywords());

    }

    /**
     * 查询书籍
     *
     * @param query:书名|作者名
     * @return
     */
    public Single<List<SearchBookPackage.BooksBean>> getSearchBooks(String query) {
        return mBookApi.getSearchBookPackage(query)
                .map(bean -> bean.getBooks());
    }
}
