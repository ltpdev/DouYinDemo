package com.gdcp.douyindemo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recycler;
    private ViewPagerLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private List<VideosBean.V9LG4B3A0Bean> v9LG4B3A0BeanList;
    private int num=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        getDataFromServer();
    }

    private void showDatas(VideosBean videosBean) {
            if (videosBean==null){
                System.out.println("----没有获取到服务器的新闻数据");
                return;
            }
            if (videosBean.getV9LG4B3A0()==null){
                return;
            }

            for (int i = 0; i <videosBean.getV9LG4B3A0().size() ; i++) {
                v9LG4B3A0BeanList.add(videosBean.getV9LG4B3A0().get(i));
            }

        mAdapter.notifyDataSetChanged();

    }

    private void getDataFromServer() {
        final String url = URLManager.getLoadMoreVideoURL(num);
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url,new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                System.out.println("----服务器返回的json数据:" + json);
                Gson gson = new Gson();
                VideosBean videosBean = gson.fromJson(json, VideosBean.class);
                // 显示服务器数据
                showDatas(videosBean);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
            }
        });
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext,int position) {
                Log.e(TAG,"释放位置:"+position +" 下一页:"+isNext);
                int index = 0;
                if (isNext){
                    index = 0;
                }else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position,boolean isBottom) {
                Log.e(TAG,"选中位置:"+position+"  是否是滑动到底部:"+isBottom);
                if (isBottom){
                    num=num+10;
                    getDataFromServer();
                }else {
                    if (v9LG4B3A0BeanList.size()>0)
                        playVideo(0);
                }
            }

            @Override
            public void onLayoutComplete() {
                if (v9LG4B3A0BeanList.size()>0)
                playVideo(0);
            }

        });
    }

    private void playVideo(int index) {
        View itemView = recycler.getChildAt(index);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final RelativeLayout rootView = itemView.findViewById(R.id.root_view);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                mediaPlayer[0] = mp;
                Log.e(TAG,"onInfo");
                mp.setLooping(true);
                imgThumb.animate().alpha(0).setDuration(200).start();
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG,"onPrepared");

            }
        });


        itemView.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()){
                    Log.e(TAG,"isPlaying:"+videoView.isPlaying());
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                }else {
                    Log.e(TAG,"isPlaying:"+videoView.isPlaying());
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        View itemView = recycler.getChildAt(index);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        videoView.stopPlayback();
        imgThumb.animate().alpha(1).start();
        imgPlay.animate().alpha(0f).start();
    }

    private void initView() {
        v9LG4B3A0BeanList=new ArrayList<>();
        recycler = (RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new ViewPagerLayoutManager(MainActivity.this, OrientationHelper.VERTICAL);
        mAdapter = new MyAdapter();
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(mAdapter);
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        public MyAdapter(){

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            VideosBean.V9LG4B3A0Bean bean=v9LG4B3A0BeanList.get(position);
            Glide.with(MainActivity.this).load(bean.getCover()).into(holder.img_thumb);
            holder.videoView.setVideoPath(bean.getMp4_url());
        }

        @Override
        public int getItemCount() {
            return v9LG4B3A0BeanList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView img_thumb;
            VideoView videoView;
            ImageView img_play;
            RelativeLayout rootView;
            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoView = itemView.findViewById(R.id.video_view);
                img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
            }
        }
    }
}
