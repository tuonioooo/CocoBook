package com.thmub.cocobook.presenter.contract;

import com.thmub.cocobook.model.bean.*;
import com.thmub.cocobook.base.BaseContract;

import java.util.List;

/**
 * Created by zhouas666 on 18-2-4.
 */

public interface BookDetailContract {
    interface View extends BaseContract.BaseView{
        void finishRefresh(BookDetailBean bean);
        void finishRecommendBookList(List<BookListBean> beans);
        void finishRecommendBooks(List<RankBookBean> beans);

        void waitToBookShelf();
        void errorToBookShelf();
        void succeedToBookShelf();
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshBookDetail(String bookId);
        //添加到书架上
        void addToBookShelf(CollBookBean collBook);
    }
}
