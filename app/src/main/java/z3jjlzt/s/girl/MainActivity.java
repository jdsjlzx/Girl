package z3jjlzt.s.girl;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import z3jjlzt.s.girl.z3jjlzt.s.girl.acitivity.BaseActivity;
import z3jjlzt.s.girl.z3jjlzt.s.girl.adapter.MyRecycleViewAdapter;
import z3jjlzt.s.girl.z3jjlzt.s.girl.entity.GirlEntity;
import z3jjlzt.s.girl.z3jjlzt.s.girl.entity.GirlJsonData;
import z3jjlzt.s.girl.z3jjlzt.s.girl.entity.ZhuangbiImg;
import z3jjlzt.s.girl.z3jjlzt.s.girl.interfaces.IgankApi;
import z3jjlzt.s.girl.z3jjlzt.s.girl.utils.NetUtils;

public class MainActivity extends BaseActivity {
    @Bind(R.id.rv_main)
    RecyclerView recyclerView;
    private List<GirlEntity> girlEntityList;
    private MyRecycleViewAdapter myRecycleViewAdapter;

    @Override
    protected void initVariables() {
        girlEntityList = new ArrayList<>();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void loadData() {
        // new getImageTask().execute();
        //   Rxjavatest();
        // getImg();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecycleViewAdapter = new MyRecycleViewAdapter(this, girlEntityList);
        recyclerView.setAdapter(myRecycleViewAdapter);
        recyclerView.setLayoutManager(layoutManager);
        Subscriber<List<String>> stringSubscriber = new Subscriber<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> s1) {
                for (String s : s1) {
                    loge(s);
                }
            }
        };
        Observable.zip(NetUtils.getIgankApi().getG(10, 2), NetUtils.getZhuangBiApi().getZhuanbBiImg("装逼"), new Func2<GirlJsonData, List<ZhuangbiImg>, List<String>>() {
            @Override
            public List<String> call(GirlJsonData girlJsonData, List<ZhuangbiImg> zhuangbiImgs) {
                List<String> list = new ArrayList<>();
                List<GirlEntity> gllist = girlJsonData.getResults();
                for (GirlEntity g : gllist) {
                    list.add(g.getUrl());
                }
                for (ZhuangbiImg z : zhuangbiImgs) {
                    loge(zhuangbiImgs.size() + "");
                    list.add(z.getUrl());
                }

                return list;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringSubscriber);
    }

    private void getImg() {
        NetUtils.getIgankApi().getG(10, 2).flatMap(new Func1<GirlJsonData, Observable<List<GirlEntity>>>() {
            @Override
            public Observable<List<GirlEntity>> call(GirlJsonData girlJsonData) {
                return Observable.just(girlJsonData.getResults());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<GirlEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<GirlEntity> girlEntities) {
                        girlEntityList.addAll(girlEntities);
                        loge(girlEntityList.get(1).getUrl() + "");
                        myRecycleViewAdapter.notifyDataSetChanged();
                    }
                });

    }


    //    map flatmap转换实例
//    Observable.create(new Observable.OnSubscribe<List<GirlEntity>>() {
//            @Override
//            public void call(Subscriber<? super List<GirlEntity>> subscriber) {
//                NetUtils.getImage(girlEntityList, 8, 1);
//                subscriber.onNext(girlEntityList);
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<List<GirlEntity>, Observable<GirlEntity>>() {
//                    @Override
//                    public Observable<GirlEntity> call(List<GirlEntity> girlEntities) {
//                        return Observable.from(girlEntities);
//                    }
//                }).map(new Func1<GirlEntity, String>() {
//            @Override
//            public String call(GirlEntity girlEntity) {
//                return girlEntity.getUrl();
//            }
//        }).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                loge(s);
//            }
//        });

    /**
     * 测试rxjava
     */
    private void Rxjavatest() {

        Observable.create(new Observable.OnSubscribe<List<GirlEntity>>() {
            @Override
            public void call(Subscriber<? super List<GirlEntity>> subscriber) {
                //   NetUtils.getImage(girlEntityList, 20, 2);
                subscriber.onNext(girlEntityList);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())//非UI操作
                .observeOn(AndroidSchedulers.mainThread())//结果在主线程处理
                .subscribe(new Subscriber<List<GirlEntity>>() {
                    @Override
                    public void onCompleted() {
                        loge("error");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<GirlEntity> gg) {
                        myRecycleViewAdapter.notifyDataSetChanged();
                    }
                });
    }


}
